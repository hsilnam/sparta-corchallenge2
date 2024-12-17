package com.sparta.corporatechallenge2.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name ="Product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int restockRound;

    @Column(nullable = false)
    private int stock;

}
