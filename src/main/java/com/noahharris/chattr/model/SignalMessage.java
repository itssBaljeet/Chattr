package com.noahharris.chattr.model;

public class SignalMessage {
    private String type;        // Offer, answer, candidate
    private String sdp;
    private Object candidate;   // ICE candidate string
    private String sdpMid;      // Media stream identification tag for the candidate
    private Integer sdpMLineIndex; // Index of the media description for the candidate
    private String roomCode;    // Room code to differentiate rooms
    private String sender;      // Sender's username to identify the message source

    // Constructor
    public SignalMessage(String type, String sdp, Object candidate, String sdpMid, Integer sdpMLineIndex, String roomCode, String sender) {
        this.type = type;
        this.sdp = sdp;
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.roomCode = roomCode;
        this.sender = sender;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public Object getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public Integer getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(Integer sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
