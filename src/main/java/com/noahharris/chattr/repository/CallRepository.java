package com.noahharris.chattr.repository;

import com.noahharris.chattr.model.Call;
import com.noahharris.chattr.model.CallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    List<Call> findByReceiver(String username);
    List<Call> findByCaller(String username);
    List<Call> findByStatus(String status);
    List<Call> findByCallerAndStatus(String caller, CallStatus status);
    List<Call> findByReceiverAndStatus(String receiver, CallStatus status);
}
