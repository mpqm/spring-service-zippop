package com.fiiiiive.zippop.goods.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.*;
import com.fiiiiive.zippop.goods.repository.GoodsImageRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.PopupGoods;
import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import com.fiiiiive.zippop.goods.model.dto.GetGoodsImageRes;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoodsService {
    private final PopupGoodsRepository popupGoodsRepository;
    private final StoreRepository storeRepository;
    private final GoodsImageRepository goodsImageRepository;

    public CreateGoodsRes register(CustomUserDetails customUserDetails, Long storeIdx, List<String> fileNames, CreateGoodsReq dto) throws BaseException {
        Store store = storeRepository.findById(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_REGISTER_FAIL_STORE_NOT_FOUND));
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        PopupGoods popupGoods = PopupGoods.builder()
                .productName(dto.getProductName())
                .productAmount(dto.getProductAmount())
                .productPrice(dto.getProductPrice())
                .productContent(dto.getProductContent())
                .store(store)
                .build();
        popupGoodsRepository.save(popupGoods);
        List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
        for(String fileName: fileNames){
            GoodsImage goodsImage = GoodsImage.builder()
                    .imageUrl(fileName)
                    .popupGoods(popupGoods)
                    .build();
            goodsImageRepository.save(goodsImage);
            GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                    .productImageIdx(goodsImage.getPopupGoodsImageIdx())
                    .imageUrl(goodsImage.getImageUrl())
                    .createdAt(goodsImage.getCreatedAt())
                    .updatedAt(goodsImage.getUpdatedAt())
                    .build();
            getGoodsImageResList.add(getGoodsImageRes);
        }
        return CreateGoodsRes.builder()
                .productIdx(popupGoods.getProductIdx())
                .productName(popupGoods.getProductName())
                .productPrice(popupGoods.getProductPrice())
                .productContent(popupGoods.getProductContent())
                .productAmount(popupGoods.getProductAmount())
                .popupGoodsImageList(getGoodsImageResList)
                .createdAt(popupGoods.getCreatedAt())
                .updatedAt(popupGoods.getUpdatedAt())
                .build();
    }

    public Page<GetGoodsRes> search(String productName, int page, int size) throws BaseException {
        Page<PopupGoods> result = popupGoodsRepository.findByProductName(productName, PageRequest.of(page, size))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_SEARCH_FAIL_STORE_NOT_NOT_FOUND));
        Page<GetGoodsRes> getPopupGoodsResPage = result.map(popupGoods ->{
            List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
            List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
            for(GoodsImage popupGoodsImage : goodsImageList){
                GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                        .productImageIdx(popupGoodsImage.getPopupGoodsImageIdx())
                        .imageUrl(popupGoodsImage.getImageUrl())
                        .createdAt(popupGoodsImage.getCreatedAt())
                        .updatedAt(popupGoodsImage.getUpdatedAt())
                        .build();
                getGoodsImageResList.add(getGoodsImageRes);
            }
            GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                    .productIdx(popupGoods.getProductIdx())
                    .productName(popupGoods.getProductName())
                    .productPrice(popupGoods.getProductPrice())
                    .productContent(popupGoods.getProductContent())
                    .productAmount(popupGoods.getProductAmount())
                    .getGoodsImageResList(getGoodsImageResList)
                    .build();
            return getGoodsRes;
        });
        return getPopupGoodsResPage;
    }

    public Page<GetGoodsRes> searchStore(Long storeIdx, int page, int size) throws BaseException {
        Page<PopupGoods> result = popupGoodsRepository.findByStoreIdx(storeIdx, PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_SEARCH_FAIL_STORE_NOT_NOT_FOUND));
        Page<GetGoodsRes> getPopupGoodsResPage = result.map(popupGoods ->{
            List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
            List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
            for(GoodsImage popupGoodsImage : goodsImageList){
                GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                        .productImageIdx(popupGoodsImage.getPopupGoodsImageIdx())
                        .imageUrl(popupGoodsImage.getImageUrl())
                        .createdAt(popupGoodsImage.getCreatedAt())
                        .updatedAt(popupGoodsImage.getUpdatedAt())
                        .build();
                getGoodsImageResList.add(getGoodsImageRes);
            }
            GetGoodsRes getGoodsRes = GetGoodsRes.builder()
                    .productIdx(popupGoods.getProductIdx())
                    .productName(popupGoods.getProductName())
                    .productPrice(popupGoods.getProductPrice())
                    .productContent(popupGoods.getProductContent())
                    .productAmount(popupGoods.getProductAmount())
                    .getGoodsImageResList(getGoodsImageResList)
                    .build();
            return getGoodsRes;
        });
        return getPopupGoodsResPage;
    }

    public UpdateGoodsRes update(CustomUserDetails customUserDetails, Long productIdx, List<String> fileNames, UpdatePopupGoodsReq dto) throws BaseException{
        PopupGoods popupGoods = popupGoodsRepository.findByProductIdx(productIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_REGISTER_FAIL_STORE_NOT_FOUND));
        if(!Objects.equals(popupGoods.getStore().getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        popupGoods.setProductName(dto.getProductName());
        popupGoods.setProductAmount(dto.getProductAmount());
        popupGoods.setProductPrice(dto.getProductPrice());
        popupGoods.setProductContent(dto.getProductContent());
        popupGoodsRepository.save(popupGoods);
        List<GetGoodsImageRes> getGoodsImageResList = new ArrayList<>();
        List<GoodsImage> goodsImageList = popupGoods.getPopupGoodsImageList();
        for(GoodsImage goodsImage : goodsImageList){
            goodsImageRepository.deleteById(goodsImage.getPopupGoodsImageIdx());
        }
        for(String fileName: fileNames){
            GoodsImage goodsImage = GoodsImage.builder()
                    .imageUrl(fileName)
                    .popupGoods(popupGoods)
                    .build();
            GetGoodsImageRes getGoodsImageRes = GetGoodsImageRes.builder()
                    .productImageIdx(goodsImage.getPopupGoodsImageIdx())
                    .imageUrl(goodsImage.getImageUrl())
                    .createdAt(goodsImage.getCreatedAt())
                    .updatedAt(goodsImage.getUpdatedAt())
                    .build();
            getGoodsImageResList.add(getGoodsImageRes);
        }
        return UpdateGoodsRes.builder()
                .productIdx(popupGoods.getProductIdx())
                .productName(popupGoods.getProductName())
                .productPrice(popupGoods.getProductPrice())
                .productContent(popupGoods.getProductContent())
                .productAmount(popupGoods.getProductAmount())
                .createdAt(popupGoods.getCreatedAt())
                .updatedAt(popupGoods.getUpdatedAt())
                .popupGoodsImageList(getGoodsImageResList)
                .build();
    }

    public void delete(CustomUserDetails customUserDetails, Long productIdx) throws BaseException{
        PopupGoods popupGoods = popupGoodsRepository.findByProductIdx(productIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_NOT_FOUND));
        if(!Objects.equals(popupGoods.getStore().getCompanyEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_INVALID_MEMBER);
        }
        popupGoodsRepository.deleteById(productIdx);
    }

}
