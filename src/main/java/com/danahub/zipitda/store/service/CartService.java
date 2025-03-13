package com.danahub.zipitda.store.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.store.domain.Cart;
import com.danahub.zipitda.store.domain.Product;
import com.danahub.zipitda.store.dto.CartRequestDto;
import com.danahub.zipitda.store.dto.CartResponseDto;
import com.danahub.zipitda.store.repository.CartRepository;
import com.danahub.zipitda.store.repository.ProductRepository;
import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 장바구니 추가
    @Transactional
    public Long addToCart(CartRequestDto requestDto, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        Product product = productRepository.findById(requestDto.productId())
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        Cart cart = Cart.builder()
                .user(user)
                .product(product)
                .quantity(requestDto.quantity())
                .build();

        return cartRepository.save(cart).getId();
    }

    // 장바구니 조회
    public List<CartResponseDto> getCartItems(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        return cartRepository.findByUserId(user.getId()).stream()
                .map(cart -> new CartResponseDto(
                        cart.getId(),
                        cart.getProduct().getId(),
                        cart.getProduct().getName(),
                        cart.getProduct().getPrice(),
                        cart.getQuantity()
                )).toList();
    }

    // 장바구니 수량 수정
    @Transactional
    public void updateCartItem(Long cartId, int quantity, Authentication authentication) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        if (!cart.getUser().getEmail().equals(authentication.getName())) {
            throw new ZipitdaException(ErrorType.UNAUTHORIZED);
        }

        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    // 장바구니 상품 삭제
    @Transactional
    public void removeCartItem(Long cartId, Authentication authentication) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND));

        if (!cart.getUser().getEmail().equals(authentication.getName())) {
            throw new ZipitdaException(ErrorType.UNAUTHORIZED);
        }

        cartRepository.delete(cart);
    }

    // 장바구니 전체 비우기
    @Transactional
    public void clearCart(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ZipitdaException(ErrorType.USER_NOT_FOUND));

        cartRepository.deleteByUserId(user.getId());
    }
}