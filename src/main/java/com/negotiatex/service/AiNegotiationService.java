package com.negotiatex.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class AiNegotiationService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public AiDecision generateAiResponse(BigDecimal buyerOffer, BigDecimal aiTargetPrice, BigDecimal basePrice, String productName, int currentRound) {
        // if buyerOffer >= aiTargetPrice -> auto accept
        if (buyerOffer.compareTo(aiTargetPrice) >= 0) {
            return new AiDecision(true, buyerOffer, "I am happy to accept your offer of $" + buyerOffer + ". It's a deal!", false);
        }

        // if too many rounds, escalate
        if (currentRound >= 3) {
            return new AiDecision(false, aiTargetPrice, "I cannot accept this offer. I will escalate this directly to the human seller who will review it shortly.", true);
        }

        // Otherwise, ask LLM
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = "You are a master professional sales negotiator for NegotiateX, acting on behalf of the seller to sell a '" + productName + "'. " +
                "Base price: $" + basePrice + ". Target minimum price: $" + aiTargetPrice + ". " +
                "Buyer's offer: $" + buyerOffer + ". " +
                "Research shows master negotiators make small, incremental concessions to maximize profit. " +
                "DO NOT immediately drop your price to the target price! Start with a counter-offer very close to the Base price. " +
                "Generate a JSON response: {\"price\": <number>, \"message\": \"<persuasive negotiation message>\"}. " +
                "Your counter-offer MUST be higher than the buyer's offer, and must leave room for future rounds. " +
                "The message should sound like a highly professional, polite human seller explaining the value of the product. " +
                "Output ONLY valid JSON.";

        String requestBody = "{" +
                "\"contents\": [{\"parts\":[{\"text\": \"" + prompt.replace("\"", "\\\"") + "\"}]}]," +
                "\"generationConfig\": {\"temperature\": 0.7}" +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        try {
            String response = restTemplate.postForObject(url, entity, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String textResponse = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            // Text response might have markdown ```json ... ```
            textResponse = textResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            
            JsonNode jsonResponse = mapper.readTree(textResponse);
            BigDecimal counterPrice = new BigDecimal(jsonResponse.get("price").asText());
            String message = jsonResponse.get("message").asText();
            
            // safety check
            if (counterPrice.compareTo(aiTargetPrice) < 0) {
                counterPrice = aiTargetPrice;
            }
            
            return new AiDecision(false, counterPrice, message, false);

        } catch (Exception e) {
            e.printStackTrace();
            // fallback
            BigDecimal fallbackPrice = aiTargetPrice.add(new BigDecimal("10")); // just a dummy fallback
            return new AiDecision(false, fallbackPrice, "I can offer it to you for $" + fallbackPrice + ". This is a great deal!", false);
        }
    }

    public static class AiDecision {
        private boolean accept;
        private BigDecimal counterPrice;
        private String message;
        private boolean escalate;

        public AiDecision(boolean accept, BigDecimal counterPrice, String message, boolean escalate) {
            this.accept = accept;
            this.counterPrice = counterPrice;
            this.message = message;
            this.escalate = escalate;
        }

        public boolean isAccept() { return accept; }
        public BigDecimal getCounterPrice() { return counterPrice; }
        public String getMessage() { return message; }
        public boolean isEscalate() { return escalate; }
    }
}
