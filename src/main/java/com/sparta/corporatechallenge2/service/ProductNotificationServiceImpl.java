package com.sparta.corporatechallenge2.service;

import com.sparta.corporatechallenge2.entity.*;
import com.sparta.corporatechallenge2.exception.ErrorCode;
import com.sparta.corporatechallenge2.exception.ProductException;
import com.sparta.corporatechallenge2.repository.ProductNotificationHistoryRepository;
import com.sparta.corporatechallenge2.repository.ProductRepository;
import com.sparta.corporatechallenge2.repository.ProductUserNotificationHistoryRepository;
import com.sparta.corporatechallenge2.repository.ProductUserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductNotificationServiceImpl implements ProductNotificationService {
    private final ProductRepository productRepository;
    private final ProductNotificationHistoryRepository notificationHistoryRepository;
    private final ProductUserNotificationRepository userNotificationRepository;
    private final ProductUserNotificationHistoryRepository userNotificationHistoryRepository;

    @Override
    public void sendRestockNotifications(Long productId) {
        ProductEntity product = fetchProduct(productId);
        product.setRestockRound(product.getRestockRound() + 1);

        ProductNotificationHistoryEntity notificationHistory = createNotificationHistory(productId, product.getRestockRound(), NotificationStatus.IN_PROGRESS);

        List<ProductUserNotificationEntity> users = userNotificationRepository.findByProductIdAndActiveTrueOrderById(productId);

        for (ProductUserNotificationEntity user : users) {
            if (processUserNotification(product, notificationHistory, user)) {
                return;
            }
        }

        updateNotificationStatus(notificationHistory, NotificationStatus.COMPLETED);
    }

    @Override
    public void resendRestockNotifications(Long productId) {
        ProductEntity product = fetchProduct(productId);

        ProductNotificationHistoryEntity notificationHistory = notificationHistoryRepository
                .findTopByProductIdOrderByCreatedAtDesc(productId)
                .orElseThrow(() -> {
                    return null;
                });

        if (notificationHistory == null) {
            return;
        }

        Long lastNotifiedUserId = notificationHistory.getLastNotifiedUserId();

        updateNotificationStatus(notificationHistory, NotificationStatus.IN_PROGRESS);

        List<ProductUserNotificationEntity> users = userNotificationRepository.findByProductIdAndActiveTrueOrderById(productId);

        boolean resumeFromLastUser = lastNotifiedUserId != null;
        for (ProductUserNotificationEntity user : users) {
            if (resumeFromLastUser && user.getUserId() <= lastNotifiedUserId) {
                continue;
            }
            if (processUserNotification(product, notificationHistory, user)) {
                return;
            }
        }

        updateNotificationStatus(notificationHistory, NotificationStatus.COMPLETED);
    }


    private ProductEntity fetchProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND_ERROR));
    }

    private ProductNotificationHistoryEntity createNotificationHistory(Long productId, int restockRound, NotificationStatus status) {
        return notificationHistoryRepository.save(
                ProductNotificationHistoryEntity.builder()
                        .productId(productId)
                        .restockRound(restockRound)
                        .status(status)
                        .build()
        );
    }

    private void updateNotificationStatus(ProductNotificationHistoryEntity notificationHistory, NotificationStatus status) {
        notificationHistory.setStatus(status);
        notificationHistoryRepository.save(notificationHistory);
    }

    private boolean processUserNotification(ProductEntity product, ProductNotificationHistoryEntity notificationHistory, ProductUserNotificationEntity user) {
        if (product.getStock() <= 0) {
            updateNotificationStatus(notificationHistory, NotificationStatus.CANCELED_BY_SOLD_OUT);
            return true; // 재고가 소진되었으므로 종료
        }

        try {
            notifyUser(product, notificationHistory, user);
        } catch (Exception e) {
            updateNotificationStatus(notificationHistory, NotificationStatus.CANCELED_BY_ERROR);
            throw e;
        }
        return false;
    }

    private void notifyUser(ProductEntity product, ProductNotificationHistoryEntity notificationHistory, ProductUserNotificationEntity user) {
        userNotificationHistoryRepository.save(
                ProductUserNotificationHistoryEntity.builder()
                        .productId(product.getId())
                        .userId(user.getUserId())
                        .restockRound(product.getRestockRound())
                        .build()
        );
        notificationHistory.setLastNotifiedUserId(user.getUserId());
        product.setStock(product.getStock() - 1);
    }
}
