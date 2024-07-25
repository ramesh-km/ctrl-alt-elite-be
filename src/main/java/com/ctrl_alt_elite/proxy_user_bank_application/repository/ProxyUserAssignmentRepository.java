package com.ctrl_alt_elite.proxy_user_bank_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ctrl_alt_elite.proxy_user_bank_application.entity.ProxyUserAssignment;

import java.time.LocalDateTime;
import java.util.List;

public interface ProxyUserAssignmentRepository extends JpaRepository<ProxyUserAssignment, Long> {

    List<ProxyUserAssignment> findByUser(String user);
    
    List<ProxyUserAssignment> findByUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(String user, LocalDateTime currentDateTime, LocalDateTime currentDateTime2);
    
    List<ProxyUserAssignment> findByProxyUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(String proxyUser, LocalDateTime currentDateTime, LocalDateTime currentDateTime2);

    List<ProxyUserAssignment> findByUserAndProxyUser(String user, String proxyUser);

    List<ProxyUserAssignment> findByUserAndProxyUserAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            String user, String proxyUser, LocalDateTime currentDateTime, LocalDateTime currentDateTime2);
}
