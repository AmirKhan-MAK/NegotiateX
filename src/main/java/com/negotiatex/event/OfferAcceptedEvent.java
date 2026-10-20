package com.negotiatex.event;

import org.springframework.context.ApplicationEvent;

public class OfferAcceptedEvent extends ApplicationEvent {
    private final String offerId;

    public OfferAcceptedEvent(Object source, String offerId) {
        super(source);
        this.offerId = offerId;
    }

    public String getOfferId() {
        return offerId;
    }
}
