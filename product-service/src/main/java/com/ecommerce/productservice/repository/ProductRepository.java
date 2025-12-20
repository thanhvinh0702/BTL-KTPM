package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
    @Query(value = "SELECT * FROM product WHERE name LIKE CONCAT('%', :productName, '%')", nativeQuery = true)
    List<Product> findByName(@Param("productName") String productName);
    List<Product> findByCategoryName(String categoryName);
    @Query(value = "SELECT * FROM product WHERE category_name = :categoryName AND name LIKE CONCAT('%', :productName, '%')", nativeQuery = true)
    List<Product> findByCategoryNameAndName(@Param("categoryName") String categoryName, @Param("productName") String productName);
}
