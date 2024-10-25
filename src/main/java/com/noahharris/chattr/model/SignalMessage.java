package com.noahharris.chattr.model;

public class SignalMessage {
    private String type;  // Offer, answer, candidate
    private String sdp;
    private String candidate;
    private String roomCode;  // Add the room code to differentiate rooms

    // Constructor
    public SignalMessage(String type, String sdp, String candidate, String roomCode) {
        this.type = type;
        this.sdp = sdp;
        this.candidate = candidate;
        this.roomCode = roomCode;
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

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}

