package com.sparta.corporatechallenge2.repository;

import com.sparta.corporatechallenge2.entity.ProductUserNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductUserNotificationRepository extends JpaRepository<ProductUserNotificationEntity, Long> {
    List<ProductUserNotificationEntity> findByProductIdAndActiveTrueOrderById(Long productId);
}