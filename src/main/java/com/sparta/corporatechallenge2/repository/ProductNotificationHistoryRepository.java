package com.sparta.corporatechallenge2.repository;

import com.sparta.corporatechallenge2.entity.ProductNotificationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistoryEntity, Long> {
    Optional<ProductNotificationHistoryEntity> findTopByProductIdOrderByCreatedAtDesc(Long productId);
}