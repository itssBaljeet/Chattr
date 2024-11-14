package com.noahharris.chattr.model;

public class CallRequestMessage {

    private String callerUsername;
    private String receiverUsername;
    private Long callId;

    public CallRequestMessage(String callerUsername, String receiverUsername, Long callId) {
        this.callerUsername = callerUsername;
        this.receiverUsername = receiverUsername;
        this.callId = callId;
    }

    public String getCallerUsername() {
        return callerUsername;
    }

    public void setCallerUsername(String callerUsername) {
        this.callerUsername = callerUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }
}
