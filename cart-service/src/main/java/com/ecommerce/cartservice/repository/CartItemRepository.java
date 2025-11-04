package com.ecommerce.cartservice.repository;

import com.ecommerce.cartservice.model.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Lấy danh sách item trong 1 giỏ hàng
     */
    List<CartItem> findByCart_CartId(Long cartId);

    /**
     * xóa 1 sản pham khoi gio hang
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.productId = :productId")
    void deleteByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    // Xóa toàn bộ sản phẩm trong giỏ hàng
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);

    // Đếm số sản phẩm trong giỏ hàng
    long countByCart_CartId(Long cartId);
}
