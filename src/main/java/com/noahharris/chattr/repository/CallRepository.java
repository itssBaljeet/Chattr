package com.noahharris.chattr.repository;

import com.noahharris.chattr.model.Call;
import com.noahharris.chattr.model.CallStatus;
import com.noahharris.chattr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    List<Call> findByReceiverUsername(String username);
    List<Call> findByCallerUsername(String username);
    List<Call> findByStatus(CallStatus status);
    List<Call> findByCallerAndStatus(User caller, CallStatus status);
    List<Call> findByReceiverAndStatus(User receiver, CallStatus status);
}
