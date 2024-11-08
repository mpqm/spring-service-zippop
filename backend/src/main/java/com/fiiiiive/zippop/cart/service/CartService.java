package com.fiiiiive.zippop.cart.service;

import com.fiiiiive.zippop.cart.model.entity.Cart;
import com.fiiiiive.zippop.cart.model.dto.CreateCartReq;
import com.fiiiiive.zippop.cart.model.dto.CountCartRes;
import com.fiiiiive.zippop.cart.model.dto.CreateCartRes;
import com.fiiiiive.zippop.cart.model.dto.GetCartRes;
import com.fiiiiive.zippop.cart.repository.CartRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsRes;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsImageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final CustomerRepository customerRepository;

    public CreateCartRes register(CustomUserDetails customUserDetails, CreateCartReq dto) throws BaseException {
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
                .orElseThrow(() -> (new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_MEMBER_NOT_FOUND)));
        Goods goods = goodsRepository.findById(dto.getProductIdx())
                .orElseThrow(() -> (new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_GOODS_NOT_FOUND)));
        Optional<Cart> result = cartRepository.findByCustomerEmailAndProductIdx(customUserDetails.getEmail(), dto.getProductIdx());
        if(result.isPresent()){ throw new BaseException(BaseResponseMessage.CART_REGISTER_FAIL_EXIST); }
        Cart cart = Cart.builder()
                .customer(customer)
                .goods(goods)
                .count(dto.getCount())
                .price(goods.getPrice())
                .build();
        cartRepository.save(cart);
        return CreateCartRes.builder()
                .cartIdx(cart.getCartIdx())
                .customerIdx(customer.getCustomerIdx())
                .productIdx(goods.getProductIdx())
                .count(cart.getItemCount())
                .itemPrice(cart.getItemPrice())
                .build();
    }


    public List<GetCartRes> searchAll(CustomUserDetails customUserDetails) throws BaseException {
        List<Cart> cartList = cartRepository.findAllByCustomerEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new BaseException(BaseResponseMessage.CART_SEARCH_FAIL));
        List<GetCartRes> getCartResList = new ArrayList<>();
        for(Cart cart: cartList){
            Goods goods = cart.getGoods();
            List<GetGoodsImageRes> imageResList = goods.getGoodsImageList().stream()
                    .map(image -> GetGoodsImageRes.builder()
                            .productImageIdx(image.getGoodsImageIdx())
                            .imageUrl(image.getImageUrl())
                            .createdAt(image.getCreatedAt())
                            .updatedAt(image.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList());

            GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                    .productIdx(goods.getProductIdx())
                    .productName(goods.getProductName())
                    .productPrice(goods.getProductPrice())
                    .productContent(goods.getProductContent())
                    .productAmount(goods.getProductAmount())
                    .createdAt(goods.getCreatedAt())
                    .updatedAt(goods.getUpdatedAt())
                    .getGoodsImageResList(imageResList)
                    .build();

            GetCartRes getCartRes = GetCartRes.builder()
                    .cartIdx(cart.getCartIdx())
                    .getGoodsRes(getGoodsRes)
                    .count(cart.getItemCount())
                    .itemPrice(cart.getItemPrice())

                    .build();
            getCartResList.add(getCartRes);
        }
        return getCartResList;
    }

    public CountCartRes count(CustomUserDetails customUserDetails, Long cartIdx, Long operation) throws BaseException {
        Cart cart = cartRepository.findByIdAndCustomerEmail(cartIdx, customUserDetails.getEmail())
                .orElseThrow(() -> (new BaseException(BaseResponseMessage.CART_COUNT_FAIL_NOT_FOUND)));
        if (operation == 0){
            cart.setItemCount(cart.getItemCount() + 1);
            cartRepository.save(cart);
            return CountCartRes.builder().cart(cart).build();
        } else if (operation == 1) {
            if(cart.getItemCount() == 0){
                throw new BaseException(BaseResponseMessage.CART_COUNT_FAIL_IS_0);
            }
            cart.setItemCount(cart.getItemCount() - 1);
            cartRepository.save(cart);
            return CountCartRes.builder().cart(cart).build();
        } else{
            throw new BaseException(BaseResponseMessage.CART_COUNT_FAIL_INVALID_OPERATION);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(CustomUserDetails customUserDetails, Long cartIdx) throws BaseException {
        cartRepository.deleteByIdAndCustomerEmail(cartIdx, customUserDetails.getEmail());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(CustomUserDetails customUserDetails) throws BaseException {
        cartRepository.deleteAllByCustomerEmail(customUserDetails.getEmail());
    }
}


