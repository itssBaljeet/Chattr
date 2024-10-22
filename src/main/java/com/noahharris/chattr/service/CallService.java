package com.noahharris.chattr.service;

import com.noahharris.chattr.model.Call;
import com.noahharris.chattr.model.CallStatus;
import com.noahharris.chattr.model.User;
import com.noahharris.chattr.repository.CallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CallService {

    public final CallRepository callRepository;

    @Autowired
    public CallService(CallRepository callRepository) {
        this.callRepository = callRepository;
    }

    public Call initiateCall(User caller, User receiver) {

        // Gets two lists of all active calls on two accounts
        List<Call> activeCallerCalls = callRepository.findByCallerAndStatus(caller, CallStatus.ACTIVE);
        List<Call> activeReceiverCalls = callRepository.findByReceiverAndStatus(receiver, CallStatus.ACTIVE);

        // If there are any active calls, throw error
        if (!activeCallerCalls.isEmpty() || !activeReceiverCalls.isEmpty()) {
            throw new IllegalStateException("Caller or receiver is already in active call");
        }

        Call newCall = new Call();
        newCall.setCaller(caller);
        newCall.setReceiver(receiver);
        newCall.setStartTime(LocalDateTime.now());
        newCall.setStatus(CallStatus.ACTIVE);

        callRepository.save(newCall);

        return newCall;
    }

    public void endCall(Call call) {

        // Update call status and set end time
        call.setStopTime(LocalDateTime.now());
        call.setStatus(CallStatus.ENDED);
        callRepository.save(call);

    }

    public Call findById(Long id) {
        return callRepository.findById(id).orElse(null);
    }
}
