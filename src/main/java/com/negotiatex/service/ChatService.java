package com.negotiatex.service;

import com.negotiatex.dto.request.ChatMessageRequest;
import com.negotiatex.dto.response.ChatMessageResponse;
import com.negotiatex.entity.ChatMessage;
import com.negotiatex.entity.ChatRoom;
import com.negotiatex.entity.Offer;
import com.negotiatex.entity.User;
import com.negotiatex.repository.ChatMessageRepository;
import com.negotiatex.repository.ChatRoomRepository;
import com.negotiatex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public void createChatRoomForOffer(Offer offer) {
        if(chatRoomRepository.findByOfferId(offer.getId()).isPresent()) {
            return; // Already exists
        }
        ChatRoom room = new ChatRoom();
        room.setOffer(offer);
        room.setBuyer(offer.getBuyer());
        room.setSeller(offer.getSeller());
        chatRoomRepository.save(room);
    }

    public List<ChatMessageResponse> getHistory(String offerId, String currentUserId) {
        ChatRoom room = chatRoomRepository.findByOfferId(offerId).orElseGet(() -> {
            Offer offer = new Offer();
            // In a robust system we'd fetch the offer here from OfferRepository
            // For now, if the room doesn't exist, we just return empty list.
            return null;
        });

        if (room == null) return List.of();

        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(room.getId()).stream().map(msg -> {
            ChatMessageResponse res = new ChatMessageResponse();
            res.setId(msg.getId());
            res.setSenderId(msg.getSender().getId());
            res.setSenderName(msg.getSender().getName());
            res.setContent(msg.getMessage());
            res.setTimestamp(msg.getCreatedAt());
            res.setMine(msg.getSender().getId().equals(currentUserId));
            return res;
        }).collect(Collectors.toList());
    }

    @Autowired private com.negotiatex.repository.OfferRepository offerRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
                
        ChatRoom room = chatRoomRepository.findByOfferId(request.getOfferId())
                .orElseGet(() -> {
                    Offer offer = offerRepository.findById(request.getOfferId()).orElseThrow();
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setOffer(offer);
                    newRoom.setBuyer(offer.getBuyer());
                    newRoom.setSeller(offer.getSeller());
                    return chatRoomRepository.save(newRoom);
                });

        ChatMessage msg = new ChatMessage();
        msg.setChatRoom(room);
        msg.setSender(sender);
        msg.setMessage(request.getContent());
        msg = chatMessageRepository.save(msg);

        ChatMessageResponse res = new ChatMessageResponse();
        res.setId(msg.getId());
        res.setSenderId(sender.getId());
        res.setSenderName(sender.getName());
        res.setContent(msg.getMessage());
        res.setTimestamp(msg.getCreatedAt());
        res.setMine(false); // Default false for socket broadcast evaluation later on client
        return res;
    }
}
