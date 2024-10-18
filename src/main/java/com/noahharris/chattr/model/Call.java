package com.noahharris.chattr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CALL")
public class Call {
    // Fields for database entity (table)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_id")
    private Long id;
    @Column(name = "caller")
    private String caller;
    @Column(name = "receiver")
    private String receiver;
    @Column(name = "start_time")
    private int startTime;
    @Column(name = "stop_time")
    private int stopTime;

    // No-arg default Constructor
    protected Call() {}

    // Constructor used when making Call records in database
    public Call(Long id, String caller, String receiver, int startTime, int stopTime) {
        this.id = id;
        this.caller = caller;
        this.receiver = receiver;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getStopTime() {
        return stopTime;
    }

    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }

}
