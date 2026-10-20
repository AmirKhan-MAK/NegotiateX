package com.negotiatex.repository;

import com.negotiatex.entity.UserBehaviorEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBehaviorEventRepository extends JpaRepository<UserBehaviorEvent, String> {
    List<UserBehaviorEvent> findByUserId(String userId);
    long countByUserIdAndEventType(String userId, String eventType);
}
