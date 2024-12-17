package com.sparta.corporatechallenge2;

import com.sparta.corporatechallenge2.entity.*;
import com.sparta.corporatechallenge2.repository.ProductNotificationHistoryRepository;
import com.sparta.corporatechallenge2.repository.ProductRepository;
import com.sparta.corporatechallenge2.repository.ProductUserNotificationHistoryRepository;
import com.sparta.corporatechallenge2.repository.ProductUserNotificationRepository;
import com.sparta.corporatechallenge2.service.ProductNotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
Mock: 가짜 객체, 외부 의존성 제거
 */


public class ProductNotificationServiceTest {

    @InjectMocks // 실제 테스트하려는 객체
    private ProductNotificationServiceImpl service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductNotificationHistoryRepository notificationHistoryRepository;

    @Mock
    private ProductUserNotificationRepository userNotificationRepository;

    @Mock
    private ProductUserNotificationHistoryRepository userNotificationHistoryRepository;

    private ProductEntity product;

    private ProductNotificationHistoryEntity notificationHistory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 생성
        product = ProductEntity.builder()
                .id(1L)
                .restockRound(1)
                .stock(5)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product)); // Mock객체가 반환할 값

        notificationHistory = ProductNotificationHistoryEntity.builder()
                .id(1L)
                .productId(1L)
                .restockRound(2)
                .status(NotificationStatus.IN_PROGRESS)
                .build();


        when(notificationHistoryRepository.save(any(ProductNotificationHistoryEntity.class)))
                .thenReturn(notificationHistory);

    }

    @Test
    void testSendRestockNotifications_CompletesSuccessfully() {
        ProductUserNotificationEntity user1 = ProductUserNotificationEntity.builder()
                .id(1L)
                .productId(1L)
                .userId(1L)
                .active(true)
                .build();

        ProductUserNotificationEntity user2 = ProductUserNotificationEntity.builder()
                .id(2L)
                .productId(1L)
                .userId(2L)
                .active(true)
                .build();

        when(userNotificationRepository.findByProductIdAndActiveTrueOrderById(1L))
                .thenReturn(Arrays.asList(user1, user2));

        // Act
        service.sendRestockNotifications(1L);

        // Assert: 상태 IN_PROGRESS와 COMPLETED가 각각 저장되는지 확인
        verify(notificationHistoryRepository, times(2)).save(argThat(history ->
                history.getStatus() == NotificationStatus.IN_PROGRESS
                        || history.getStatus() == NotificationStatus.COMPLETED
        ));

        // 각 유저에 대한 히스토리가 저장되었는지 확인
        verify(userNotificationHistoryRepository, times(2)).save(any(ProductUserNotificationHistoryEntity.class));

        // 재고 감소 검증
        assertEquals(3, product.getStock());
    }


    @Test
    void testSendRestockNotifications_StopsWhenOutOfStock() {
        product.setStock(1);

        ProductUserNotificationEntity user1 = ProductUserNotificationEntity.builder()
                .id(1L)
                .productId(1L)
                .userId(1L)
                .active(true)
                .build();

        ProductUserNotificationEntity user2 = ProductUserNotificationEntity.builder()
                .id(2L)
                .productId(1L)
                .userId(2L)
                .active(true)
                .build();

        when(userNotificationRepository.findByProductIdAndActiveTrueOrderById(1L))
                .thenReturn(Arrays.asList(user1, user2));

        service.sendRestockNotifications(1L);

        // 첫 번째 호출: 상태 IN_PROGRESS
        verify(notificationHistoryRepository).save(argThat(history ->
                history.getStatus() == NotificationStatus.IN_PROGRESS
        ));

        // 두 번째 호출: 상태 CANCELED_BY_SOLD_OUT
        verify(notificationHistoryRepository).save(argThat(history ->
                history.getStatus() == NotificationStatus.CANCELED_BY_SOLD_OUT
        ));

        // 알림 전송이 한 번만 성공하고 재고가 0이 되었는지 확인
        verify(userNotificationHistoryRepository, times(1)).save(any(ProductUserNotificationHistoryEntity.class));
        assertEquals(0, product.getStock());
    }


    @Test
    void testSendRestockNotifications_StatusUpdatesToCompleted() {
        ProductUserNotificationEntity user = ProductUserNotificationEntity.builder()
                .id(1L)
                .productId(1L)
                .userId(1L)
                .active(true)
                .build();

        when(userNotificationRepository.findByProductIdAndActiveTrueOrderById(1L))
                .thenReturn(List.of(user));

        service.sendRestockNotifications(1L);

        // 상태가 IN_PROGRESS와 COMPLETED로 저장되었는지 확인
        verify(notificationHistoryRepository, times(2)).save(argThat(history ->
                history.getStatus() == NotificationStatus.IN_PROGRESS || history.getStatus() == NotificationStatus.COMPLETED
        ));
    }


    @Test
    void testSendRestockNotifications_StatusUpdatesToCanceledBySoldOut() {
        product.setStock(1);

        ProductUserNotificationEntity user1 = ProductUserNotificationEntity.builder()
                .id(1L)
                .productId(1L)
                .userId(1L)
                .active(true)
                .build();

        ProductUserNotificationEntity user2 = ProductUserNotificationEntity.builder()
                .id(2L)
                .productId(1L)
                .userId(2L)
                .active(true)
                .build();


        when(userNotificationRepository.findByProductIdAndActiveTrueOrderById(1L))
                .thenReturn(Arrays.asList(user1, user2));

        service.sendRestockNotifications(1L);

        // 상태가 CANCELED_BY_SOLD_OUT로 저장되었는지 확인
        verify(notificationHistoryRepository).save(argThat(history ->
                history.getStatus() == NotificationStatus.CANCELED_BY_SOLD_OUT
        ));
    }

    @Test
    void testSendRestockNotifications_StopsDueToError() {
        ProductUserNotificationEntity user1 = ProductUserNotificationEntity.builder()
                .id(1L)
                .productId(1L)
                .userId(1L)
                .active(true)
                .build();

        when(userNotificationRepository.findByProductIdAndActiveTrueOrderById(1L))
                .thenReturn(Arrays.asList(user1));

        doThrow(new RuntimeException("Simulated Error"))
                .when(userNotificationHistoryRepository).save(any(ProductUserNotificationHistoryEntity.class));

        assertThrows(RuntimeException.class, () -> service.sendRestockNotifications(1L));

        verify(notificationHistoryRepository, times(1)).save(
                argThat(history -> history.getStatus() == NotificationStatus.CANCELED_BY_ERROR)
        );
    }

    @Test
    void testResendRestockNotifications_RestartsAfterFailure() {
        product.setStock(3);

        ProductNotificationHistoryEntity previousHistory = ProductNotificationHistoryEntity.builder()
                .id(1L)
                .productId(1L)
                .restockRound(1)
                .lastNotifiedUserId(1L)
                .status(NotificationStatus.CANCELED_BY_ERROR)
                .build();

        when(notificationHistoryRepository.findTopByProductIdOrderByCreatedAtDesc(1L))
                .thenReturn(Optional.of(previousHistory));

        ProductUserNotificationEntity user2 = ProductUserNotificationEntity.builder()
                .id(2L).productId(1L).userId(2L).active(true).build();
        ProductUserNotificationEntity user3 = ProductUserNotificationEntity.builder()
                .id(3L).productId(1L).userId(3L).active(true).build();

        when(userNotificationRepository.findByProductIdAndActiveTrueOrderById(1L))
                .thenReturn(Arrays.asList(user2, user3));

        service.resendRestockNotifications(1L);

        verify(userNotificationHistoryRepository, times(2)).save(any(ProductUserNotificationHistoryEntity.class));
        verify(notificationHistoryRepository, atLeast(1)).save(any(ProductNotificationHistoryEntity.class));
        assertEquals(1, product.getStock());
    }
}
