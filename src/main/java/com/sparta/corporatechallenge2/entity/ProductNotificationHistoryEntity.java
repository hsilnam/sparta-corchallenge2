package com.sparta.corporatechallenge2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductNotificationHistory")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductNotificationHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int restockRound;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Long lastNotifiedUserId;
}
