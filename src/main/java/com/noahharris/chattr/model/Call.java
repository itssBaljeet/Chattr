package com.noahharris.chattr.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Class representing CALL table in database
// Keeps record of start/stop time and caller/receiver
@Entity
@Table(name = "call")
public class Call {
    // Fields for database entity (table)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_id", nullable = false)
    private long id;

    @Column(name = "caller")
    private String caller;

    @Column(name = "receiver")
    private String receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CallStatus status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "stop_time")
    private LocalDateTime stopTime;

    // No-arg default Constructor
    public Call() {}

    // Constructor used when making Call records in database
    public Call(String caller, String receiver, CallStatus status, LocalDateTime startTime, LocalDateTime stopTime) {
        this.caller = caller;
        this.receiver = receiver;
        this.status = status;
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

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

}
