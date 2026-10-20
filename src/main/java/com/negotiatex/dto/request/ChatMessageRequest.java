package com.negotiatex.dto.request;

public class ChatMessageRequest {
    private String offerId;
    private String content;

    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
