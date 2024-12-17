package com.sparta.corporatechallenge2.repository;

import com.sparta.corporatechallenge2.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}