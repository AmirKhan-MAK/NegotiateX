package com.negotiatex.service;

import com.negotiatex.dto.request.CounterOfferRequest;
import com.negotiatex.dto.request.CreateOfferRequest;
import com.negotiatex.dto.response.OfferResponse;
import com.negotiatex.dto.response.OfferRoundResponse;
import com.negotiatex.entity.*;
import com.negotiatex.repository.OfferRepository;
import com.negotiatex.repository.OfferRoundRepository;
import com.negotiatex.repository.ProductRepository;
import com.negotiatex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferService {

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private OfferRoundRepository offerRoundRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private ChatService chatService;

	@Autowired
	private com.negotiatex.repository.UserBehaviorEventRepository behaviorRepo;

	@Autowired
	private AiNegotiationService aiNegotiationService;

	@Transactional
	public OfferResponse createOffer(CreateOfferRequest req, String userEmail) {
		User buyer = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
		if (buyer.getRole() != Role.BUYER) {
			throw new RuntimeException("Only buyers can create offers");
		}

		Product product = productRepository.findById(req.getProductId())
				.orElseThrow(() -> new RuntimeException("Product not found"));

		if (product.getSeller().getId().equals(buyer.getId())) {
			throw new RuntimeException("Cannot make offer on your own product");
		}

		Offer offer = new Offer();
		offer.setProduct(product);
		offer.setBuyer(buyer);
		offer.setSeller(product.getSeller());
		offer.setQuantity(req.getQuantity() != null ? req.getQuantity() : 1);
		offer.setInitialPrice(req.getPrice());
		offer.setCurrentPrice(req.getPrice());
		offer.setBuyerMessage(req.getMessage());

		// Validate against floor price - Automated Pricing Engine
		if (req.getPrice().compareTo(product.getFloorPrice()) < 0) {
			offer.setStatus(OfferStatus.REJECTED); // Auto Reject!
		} else {
			offer.setStatus(OfferStatus.PENDING);
		}

		offer.setRounds(1);
		offer = offerRepository.save(offer);
		
		chatService.createChatRoomForOffer(offer);

		// AI Metric Tracking
		com.negotiatex.entity.UserBehaviorEvent event = new com.negotiatex.entity.UserBehaviorEvent();
		event.setUser(buyer);
		event.setProduct(product);
		event.setEventType("OFFER_MADE");
		behaviorRepo.save(event);

		recordRound(offer, buyer, req.getPrice(), OfferAction.OFFER, req.getMessage());

		// AI Negotiator Logic
		if (product.getAiNegotiationEnabled() != null && product.getAiNegotiationEnabled() && product.getAiTargetPrice() != null) {
			handleAiNegotiation(offer);
		}

		return mapToResponse(offer);
	}

	@Transactional
	public OfferResponse counterOffer(String offerId, CounterOfferRequest req, String userEmail) {
		Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"));

		User actor = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

		if (offer.getStatus() != OfferStatus.PENDING && offer.getStatus() != OfferStatus.COUNTERED) {
			throw new RuntimeException("Offer is not in a negotiable state");
		}

		if (offer.getRounds() >= 3) {
			offer.setStatus(OfferStatus.EXPIRED);
			offerRepository.save(offer);
			throw new RuntimeException("Maximum negotiation rounds (3) reached.");
		}

		offer.setRounds(offer.getRounds() + 1);
		offer.setCurrentPrice(req.getPrice());
		offer.setStatus(OfferStatus.COUNTERED);

		// Auto-reject if buyer counters below floor price
		if (actor.getRole() == Role.BUYER && req.getPrice().compareTo(offer.getProduct().getFloorPrice()) < 0) {
			offer.setStatus(OfferStatus.REJECTED);
		}

		offer = offerRepository.save(offer);
		recordRound(offer, actor, req.getPrice(), OfferAction.COUNTER, req.getMessage());
		
		// AI Negotiator Logic
		if (actor.getRole() == Role.BUYER && offer.getProduct().getAiNegotiationEnabled() != null && offer.getProduct().getAiNegotiationEnabled() && offer.getProduct().getAiTargetPrice() != null) {
			handleAiNegotiation(offer);
		}

		return mapToResponse(offer);
	}

	@Transactional
	public OfferResponse acceptOffer(String offerId, String userEmail) {
		Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"));
		User actor = userRepository.findByEmail(userEmail).orElseThrow();

		offer.setStatus(OfferStatus.ACCEPTED);
		offer.setFinalPrice(offer.getCurrentPrice());
		offerRepository.save(offer);

		recordRound(offer, actor, offer.getCurrentPrice(), OfferAction.ACCEPT, "Offer Accepted");
		
		eventPublisher.publishEvent(new com.negotiatex.event.OfferAcceptedEvent(this, offer.getId()));
		
		return mapToResponse(offer);
	}

	@Transactional
	public OfferResponse rejectOffer(String offerId, String userEmail) {
		Offer offer = offerRepository.findById(offerId).orElseThrow();
		User actor = userRepository.findByEmail(userEmail).orElseThrow();

		offer.setStatus(OfferStatus.REJECTED);
		offerRepository.save(offer);

		recordRound(offer, actor, offer.getCurrentPrice(), OfferAction.REJECT, "Offer Rejected manually");
		return mapToResponse(offer);
	}

	public List<OfferResponse> getMyOffers(String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElseThrow();
		List<Offer> offers;
		if (user.getRole() == Role.SELLER) {
			offers = offerRepository.findBySellerId(user.getId());
		} else {
			offers = offerRepository.findByBuyerId(user.getId());
		}
		return offers.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	public OfferResponse getOfferDetails(String offerId, String userEmail) {
		Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new RuntimeException("Offer not found"));
		User user = userRepository.findByEmail(userEmail).orElseThrow();
		if (!offer.getBuyer().getId().equals(user.getId()) && !offer.getSeller().getId().equals(user.getId())) {
			throw new RuntimeException("Unauthorized");
		}
		OfferResponse resp = mapToResponse(offer);
		
		boolean isMyTurn = false;
		if (resp.getStatus().equals("PENDING") || resp.getStatus().equals("COUNTERED")) {
			if (!resp.getHistory().isEmpty()) {
				OfferRoundResponse lastRound = resp.getHistory().get(resp.getHistory().size() - 1);
				if (!lastRound.getActorRole().equals(user.getRole().name())) {
					isMyTurn = true;
				}
			}
		}
		resp.setMyTurn(isMyTurn);
		
		return resp;
	}

	private void recordRound(Offer offer, User actor, java.math.BigDecimal price, OfferAction action, String message) {
		OfferRound round = new OfferRound();
		round.setOffer(offer);
		round.setRoundNo(offer.getRounds());
		round.setActor(actor);
		round.setActorRole(actor.getRole());
		round.setPrice(price);
		round.setAction(action);
		round.setMessage(message);
		offerRoundRepository.save(round);
	}

	private OfferResponse mapToResponse(Offer offer) {
		OfferResponse resp = new OfferResponse();
		resp.setId(offer.getId());
		resp.setProductId(offer.getProduct().getId());
		resp.setProductName(offer.getProduct().getName());
		resp.setBuyerName(offer.getBuyer().getName());
		resp.setSellerName(offer.getSeller().getName());
		resp.setInitialPrice(offer.getInitialPrice());
		resp.setCurrentPrice(offer.getCurrentPrice());
		resp.setFinalPrice(offer.getFinalPrice());
		resp.setStatus(offer.getStatus().name());
		resp.setRounds(offer.getRounds());
		resp.setExpiresAt(offer.getExpiresAt());
		resp.setAiEscalated(offer.getAiEscalated() != null && offer.getAiEscalated());

		List<OfferRoundResponse> history = offerRoundRepository.findByOfferIdOrderByRoundNoAsc(offer.getId()).stream()
				.map(r -> {
					OfferRoundResponse h = new OfferRoundResponse();
					h.setId(r.getId());
					h.setRoundNo(r.getRoundNo());
					h.setActorName(r.getActor().getName());
					h.setActorRole(r.getActorRole().name());
					h.setPrice(r.getPrice());
					h.setAction(r.getAction().name());
					h.setMessage(r.getMessage());
					h.setCreatedAt(r.getCreatedAt());
					return h;
				}).collect(Collectors.toList());
		resp.setHistory(history);

		return resp;
	}

	private void handleAiNegotiation(Offer offer) {
		if (offer.getStatus() == OfferStatus.REJECTED || offer.getStatus() == OfferStatus.ACCEPTED || offer.getStatus() == OfferStatus.EXPIRED) {
			return; // No need to negotiate
		}
		
		AiNegotiationService.AiDecision decision = aiNegotiationService.generateAiResponse(
			offer.getCurrentPrice(), 
			offer.getProduct().getAiTargetPrice(), 
			offer.getProduct().getBasePrice(), 
			offer.getProduct().getName(), 
			offer.getRounds()
		);
		
		String aiMessage = "[🤖 AI Agent] " + decision.getMessage();

		if (decision.isAccept()) {
			offer.setStatus(OfferStatus.ACCEPTED);
			offer.setFinalPrice(offer.getCurrentPrice()); // The buyer's price is accepted!
			offerRepository.save(offer);
			recordRound(offer, offer.getSeller(), offer.getCurrentPrice(), OfferAction.ACCEPT, aiMessage);
			eventPublisher.publishEvent(new com.negotiatex.event.OfferAcceptedEvent(this, offer.getId()));
		} else if (decision.isEscalate()) {
			offer.setAiEscalated(true);
			offer.setCurrentPrice(decision.getCounterPrice());
			offerRepository.save(offer);
			recordRound(offer, offer.getSeller(), decision.getCounterPrice(), OfferAction.COUNTER, aiMessage);
		} else {
			offer.setRounds(offer.getRounds() + 1);
			offer.setCurrentPrice(decision.getCounterPrice());
			offer.setStatus(OfferStatus.COUNTERED);
			offerRepository.save(offer);
			recordRound(offer, offer.getSeller(), decision.getCounterPrice(), OfferAction.COUNTER, aiMessage);
		}
	}
}
