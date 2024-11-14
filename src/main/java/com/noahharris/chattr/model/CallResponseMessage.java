package com.noahharris.chattr.model;

public class CallResponseMessage {

    private Long callId;
    private ResponseType response;

    public CallResponseMessage(ResponseType response) {
        this.response = response;
    }

    public ResponseType getResponse() {
        return response;
    }

    public void setResponse(ResponseType response) {
        this.response = response;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public Long getCallId() {
        return callId;
    }

    public enum ResponseType {
        ACCEPT,
        DECLINE
    }
}
