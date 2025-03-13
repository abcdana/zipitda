package com.danahub.zipitda.store.controller;

import com.danahub.zipitda.common.dto.CommonResponse;
import com.danahub.zipitda.store.dto.CartRequestDto;
import com.danahub.zipitda.store.dto.CartResponseDto;
import com.danahub.zipitda.store.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store/carts")
@RequiredArgsConstructor
@Tag(name = "cart", description = "장바구니 API")
public class CartController {

    private final CartService cartService;

    @PostMapping
    @Operation(summary = "장바구니 추가 API", description = "상품을 장바구니에 추가합니다.")
    public CommonResponse<Long> addToCart(Authentication authentication, @RequestBody CartRequestDto requestDto) {
        return CommonResponse.success(cartService.addToCart(requestDto, authentication));
    }

    @GetMapping
    @Operation(summary = "장바구니 조회 API", description = "현재 로그인한 사용자의 장바구니 목록을 조회합니다.")
    public CommonResponse<List<CartResponseDto>> getCartItems(Authentication authentication) {
        return CommonResponse.success(cartService.getCartItems(authentication));
    }

    @PutMapping("/{cartId}")
    @Operation(summary = "장바구니 수량 수정 API", description = "장바구니에 담긴 상품의 수량을 변경합니다.")
    public CommonResponse<Void> updateCartItem(@PathVariable Long cartId, @RequestParam int quantity, Authentication authentication) {
        cartService.updateCartItem(cartId, quantity, authentication);
        return CommonResponse.success();
    }

    @DeleteMapping("/{cartId}")
    @Operation(summary = "장바구니 상품 삭제 API", description = "장바구니에서 특정 상품을 삭제합니다.")
    public CommonResponse<Void> removeCartItem(@PathVariable Long cartId, Authentication authentication) {
        cartService.removeCartItem(cartId, authentication);
        return CommonResponse.success();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "장바구니 전체 비우기 API", description = "장바구니를 비웁니다.")
    public CommonResponse<Void> clearCart(Authentication authentication) {
        cartService.clearCart(authentication);
        return CommonResponse.success();
    }

}
