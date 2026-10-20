package com.negotiatex.controller;

import com.negotiatex.dto.request.ChatMessageRequest;
import com.negotiatex.dto.response.ChatMessageResponse;
import com.negotiatex.entity.User;
import com.negotiatex.repository.UserRepository;
import com.negotiatex.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/v1/chat/{offerId}/history")
    public List<ChatMessageResponse> getHistory(@PathVariable String offerId, Principal principal) {
        if(principal == null) throw new RuntimeException("Not authenticated");
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        return chatService.getHistory(offerId, user.getId());
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessageRequest, Principal principal) {
        if(principal == null) throw new RuntimeException("Not authenticated");

        // Save the message in DB
        ChatMessageResponse msg = chatService.saveMessage(chatMessageRequest, principal.getName());
        
        // Broadcast it to the specific room topic
        messagingTemplate.convertAndSend("/topic/offers/" + chatMessageRequest.getOfferId(), msg);
    }
}
