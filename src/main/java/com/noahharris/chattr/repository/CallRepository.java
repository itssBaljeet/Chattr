package com.noahharris.chattr.repository;

import com.noahharris.chattr.model.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    Call getCallByReceiver(String username);
    Call getCallByCaller(String username);
}
