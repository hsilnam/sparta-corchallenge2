package com.sparta.corporatechallenge2.contorller;

import com.sparta.corporatechallenge2.service.ProductNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductNotificationController {
    private final ProductNotificationService notificationService;

    @PostMapping("/{productId}/notifications/re-stock")
    public ResponseEntity<Void> sendRestockNotifications(@PathVariable Long productId) {
        notificationService.sendRestockNotifications(productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/{productId}/notifications/re-stock")
    public ResponseEntity<Void> resendRestockNotifications(@PathVariable Long productId) {
        notificationService.resendRestockNotifications(productId);
        return ResponseEntity.ok().build();
    }
}
