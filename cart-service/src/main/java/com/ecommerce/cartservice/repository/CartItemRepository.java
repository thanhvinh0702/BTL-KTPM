package com.ecommerce.cartservice.repository;

import com.ecommerce.cartservice.model.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart_CartId(Long cartId);

    // Xóa 1 sản phẩm khỏi giỏ hàng
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.productId = :productId")
    void removeProductFromCart(@Param("cartId") Long cartId, @Param("productId") Long productId);

    // Xóa toàn bộ sản phẩm trong giỏ hàng
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    void removeAllProductsFromCart(@Param("cartId") Long cartId);

    // Đếm số sản phẩm còn lại trong giỏ hàng
    long countByCart_CartId(Long cartId);
}
