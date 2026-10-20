package com.negotiatex.dto.response;

import java.time.LocalDateTime;

public class ChatMessageResponse {
	private String id;
	private String senderId;
	private String senderName;
	private String content;
	private LocalDateTime timestamp;
	private boolean isMine; // Evaluated at runtime for UI styling

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean mine) {
		isMine = mine;
	}
}
