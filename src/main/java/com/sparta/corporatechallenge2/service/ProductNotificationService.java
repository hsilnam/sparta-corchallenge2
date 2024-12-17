package com.sparta.corporatechallenge2.service;

public interface ProductNotificationService {
    public void sendRestockNotifications(Long productId);
    public void resendRestockNotifications(Long productId);
}
