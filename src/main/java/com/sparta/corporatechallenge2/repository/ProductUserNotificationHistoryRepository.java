package com.sparta.corporatechallenge2.repository;

import com.sparta.corporatechallenge2.entity.ProductUserNotificationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductUserNotificationHistoryRepository extends JpaRepository<ProductUserNotificationHistoryEntity, Long> {
}