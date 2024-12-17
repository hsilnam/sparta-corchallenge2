package com.sparta.corporatechallenge2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productUserNotificationHistory")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductUserNotificationHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int restockRound;

}
