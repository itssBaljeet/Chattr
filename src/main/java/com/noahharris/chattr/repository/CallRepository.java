package com.noahharris.chattr.repository;

import com.noahharris.chattr.model.Call;
import com.noahharris.chattr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    List<Call> findByReceiverUsername(String username);
    List<Call> findByCallerUsername(String username);
    List<Call> findByStatus(Call.CallStatus status);
    List<Call> findByCallerAndStatus(User caller, Call.CallStatus status);
    List<Call> findByReceiverAndStatus(User receiver, Call.CallStatus status);
}
