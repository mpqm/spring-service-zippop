package com.fiiiiive.zippop.goods.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.*;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsImageRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsImageRes;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final StoreRepository storeRepository;
    private final GoodsImageRepository goodsImageRepository;

    // 팝업 굿즈 등록
    @Transactional
    public CreateGoodsRes register(CustomUserDetails customUserDetails, Long storeIdx, List<String> fileNames, CreateGoodsReq dto) throws BaseException {
        // 예외: 팝업 스토어가 존재하지 않을때, 팝업 스토어에 등록된 기업회원이메일이 다를때
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_REGISTER_FAIL_STORE_NOT_FOUND));
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        // Goods 엔티티 생성 및 저장
        Goods goods = Goods.builder()
                .name(dto.getGoodsName())
                .amount(dto.getGoodsAmount())
                .price(dto.getGoodsPrice())
                .content(dto.getGoodsContent())
                .store(store)
                .status("SELLING")
                .build();
        goodsRepository.save(goods);
        // GoodsImage 엔티티 리스트 생성 및 저장
        for(String fileName: fileNames){
            GoodsImage goodsImage = GoodsImage.builder()
                    .url(fileName)
                    .goods(goods)
                    .build();
            goodsImageRepository.save(goodsImage);
        }
        return CreateGoodsRes.builder().goodsIdx(goods.getIdx()).build();
    }

    // 팝업 굿즈 단일 조회
    @Transactional(readOnly = true)
    public SearchGoodsRes search(Long goodIdx) throws BaseException {
        Goods goods = goodsRepository.findByGoodsIdx(goodIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_SEARCH_FAIL_STORE_NOT_NOT_FOUND));
        List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();
        for(GoodsImage goodsImage : goods.getGoodsImageList()){
            SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                    .goodsImageIdx(goodsImage.getIdx())
                    .imageUrl(goodsImage.getUrl())
                    .createdAt(goodsImage.getCreatedAt())
                    .updatedAt(goodsImage.getUpdatedAt())
                    .build();
            searchGoodsImageResList.add(searchGoodsImageRes);
        }
        return SearchGoodsRes.builder()
                .goodsIdx(goods.getIdx())
                .goodsName(goods.getName())
                .goodsPrice(goods.getPrice())
                .goodsContent(goods.getContent())
                .goodsAmount(goods.getAmount())
                .searchGoodsImageResList(searchGoodsImageResList)
                .build();
    }

    // 팝업 굿즈 목록 조회(storeIdx)
    public Page<SearchGoodsRes> searchAll(Long storeIdx, int page, int size) throws BaseException {
        Page<Goods> result = goodsRepository.findByStoreIdx(storeIdx, PageRequest.of(page, size))
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_SEARCH_FAIL_STORE_NOT_NOT_FOUND));
        Page<SearchGoodsRes> searchGoodsResPage = result.map(goods -> {
            List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();
            for(GoodsImage goodsImage : goods.getGoodsImageList()){
                SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                        .goodsImageIdx(goodsImage.getIdx())
                        .imageUrl(goodsImage.getUrl())
                        .createdAt(goodsImage.getCreatedAt())
                        .updatedAt(goodsImage.getUpdatedAt())
                        .build();
                searchGoodsImageResList.add(searchGoodsImageRes);
            }
            return SearchGoodsRes.builder()
                    .goodsIdx(goods.getIdx())
                    .goodsName(goods.getName())
                    .goodsPrice(goods.getPrice())
                    .goodsContent(goods.getContent())
                    .goodsAmount(goods.getAmount())
                    .searchGoodsImageResList(searchGoodsImageResList)
                    .build();
        });
        return searchGoodsResPage;
    }

    // 팝업 재고 굿즈 목록 조회(검색어, 일반)
    public Page<SearchGoodsRes> searchAllAsGuest(String keyword, int page, int size) throws BaseException {
        Page<Goods> result = null;
        if(keyword != null) {
            result = goodsRepository.findByKeyword(keyword, PageRequest.of(page, size));
        } else {
            result = goodsRepository.findAll(PageRequest.of(page, size));
        }
        if (result.isEmpty()) {
            throw new BaseException(BaseResponseMessage.POPUP_GOODS_SEARCH_FAIL_STORE_NOT_NOT_FOUND);
        }
        Page<SearchGoodsRes> searchGoodsResPage = result.map(goods -> {
            List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();
            for(GoodsImage goodsImage : goods.getGoodsImageList()){
                SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                        .goodsImageIdx(goodsImage.getIdx())
                        .imageUrl(goodsImage.getUrl())
                        .createdAt(goodsImage.getCreatedAt())
                        .updatedAt(goodsImage.getUpdatedAt())
                        .build();
                searchGoodsImageResList.add(searchGoodsImageRes);
            }
            return SearchGoodsRes.builder()
                    .goodsIdx(goods.getIdx())
                    .goodsName(goods.getName())
                    .goodsPrice(goods.getPrice())
                    .goodsContent(goods.getContent())
                    .goodsAmount(goods.getAmount())
                    .searchGoodsImageResList(searchGoodsImageResList)
                    .build();
        });
        return searchGoodsResPage;
    }

    // 팝업 굿즈 수정
    @Transactional
    public UpdateGoodsRes update(CustomUserDetails customUserDetails, Long goodsIdx, List<String> fileNames, UpdateGoodsReq dto) throws BaseException{
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_REGISTER_FAIL_STORE_NOT_FOUND));
        if(!Objects.equals(goods.getStore().getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        goods.setName(dto.getGoodsName());
        goods.setAmount(dto.getGoodsAmount());
        goods.setPrice(dto.getGoodsPrice());
        goods.setContent(dto.getGoodsContent());
        goodsRepository.save(goods);
        List<String> existingUrls = new ArrayList<>();
        // Goods Image 업데이트
        // 기존 이미지 URL 목록 불러오기
        for(GoodsImage goodsImage : goods.getGoodsImageList()){
            existingUrls.add(goodsImage.getUrl());
        }
        // 삭제할 이미지 URL (기존에 있었으나 새 목록에 없는 것들)
        for (GoodsImage goodsImage : goods.getGoodsImageList()) {
            if (!fileNames.contains(goodsImage.getUrl())) {
                goodsImageRepository.deleteById(goodsImage.getIdx());
            }
        }

        // 추가할 이미지 URL (새 목록에 있는데 기존 목록에는 없는 것들)
        for (String fileName : fileNames) {
            if (!existingUrls.contains(fileName)) {
                GoodsImage goodsImage = GoodsImage.builder()
                        .url(fileName)
                        .goods(goods)
                        .build();
                goodsImageRepository.save(goodsImage);
            }
        }
        // UpdateGoodsRes 반환
        return UpdateGoodsRes.builder().goodsIdx(goods.getIdx()).build();
    }

    // 팝업 굿즈 삭제
    @Transactional
    public void delete(CustomUserDetails customUserDetails, Long goodsIdx) throws BaseException{
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_NOT_FOUND));
        if(!Objects.equals(goods.getStore().getCompanyEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_DELETE_FAIL_INVALID_MEMBER);
        }
        goodsRepository.deleteById(goodsIdx);
    }

}
