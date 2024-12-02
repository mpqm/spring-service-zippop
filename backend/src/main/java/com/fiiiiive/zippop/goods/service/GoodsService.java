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
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        // 1. 예외: 팝업 스토어가 존재하지 않을때, 팝업 스토어에 등록된 기업회원이메일이 다를때
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.GOODS_REGISTER_FAIL_NOT_FOUND_STORE)
        );
        if(!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.STORE_UPDATE_FAIL_INVALID_MEMBER);
        }
        // Goods 엔티티 생성 및 저장
        Goods goods = Goods.builder()
                .name(dto.getGoodsName())
                .amount(dto.getGoodsAmount())
                .price(dto.getGoodsPrice())
                .content(dto.getGoodsContent())
                .store(store)
                .status("GOODS_RESERVE")
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
        // 1. 예외 : 팝업 굿즈가 존재하지 않을 때
        Goods goods = goodsRepository.findByGoodsIdx(goodIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.GOODS_SEARCH_FAIL_NOT_FOUND_STORE)
        );

        // 2. Goods DTO page 반환
        List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();
        for(GoodsImage goodsImage : goods.getGoodsImageList()){
            SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                    .goodsImageIdx(goodsImage.getIdx())
                    .goodsImageUrl(goodsImage.getUrl())
                    .createdAt(goodsImage.getCreatedAt())
                    .updatedAt(goodsImage.getUpdatedAt())
                    .build();
            searchGoodsImageResList.add(searchGoodsImageRes);
        }
        return SearchGoodsRes.builder()
                .storeName(goods.getStore().getName())
                .goodsIdx(goods.getIdx())
                .goodsName(goods.getName())
                .goodsPrice(goods.getPrice())
                .goodsStatus(goods.getStatus())
                .goodsContent(goods.getContent())
                .goodsAmount(goods.getAmount())
                .searchGoodsImageResList(searchGoodsImageResList)
                .build();
    }

    // 팝업 굿즈 목록 조회(storeIdx, 검색어, 일반)
    public Page<SearchGoodsRes> searchAll(Long storeIdx, String keyword, int page, int size) throws BaseException {
        /*
        1. 굿즈 페이징 조회
        if : 팝업 스토어 상세 페이지 들어갔을때 전체 검색
        else : 팝업 스토어 상세 페이지 들어갔을때 전체 목록 조회
        예외 : 조회 결과가 없을때
         */
        Page<Goods> goodsPage = null;
        if(keyword != null){
            goodsPage = goodsRepository.findAllByStoreIdxAndKeyword(storeIdx, keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        } else {
            goodsPage = goodsRepository.findAllByStoreIdx(storeIdx, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        }
        if (goodsPage.isEmpty()) {
            throw new BaseException(BaseResponseMessage.GOODS_SEARCH_ALL_FAIL_STORE_NOT_NOT_FOUND);
        }

        // 2. Goods DTO page 반환
        Page<SearchGoodsRes> searchGoodsResPage = goodsPage.map(goods -> {
            List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();
            for(GoodsImage goodsImage : goods.getGoodsImageList()){
                SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                        .goodsImageIdx(goodsImage.getIdx())
                        .goodsImageUrl(goodsImage.getUrl())
                        .createdAt(goodsImage.getCreatedAt())
                        .updatedAt(goodsImage.getUpdatedAt())
                        .build();
                searchGoodsImageResList.add(searchGoodsImageRes);
            }
            return SearchGoodsRes.builder()
                    .storeName(goods.getStore().getName())
                    .goodsIdx(goods.getIdx())
                    .goodsName(goods.getName())
                    .goodsPrice(goods.getPrice())
                    .goodsContent(goods.getContent())
                    .goodsStatus(goods.getStatus())
                    .goodsAmount(goods.getAmount())
                    .searchGoodsImageResList(searchGoodsImageResList)
                    .build();
        });
        return searchGoodsResPage;
    }

    // 팝업 굿즈 수정
    @Transactional
    public UpdateGoodsRes update(CustomUserDetails customUserDetails, Long goodsIdx, List<String> fileNames, UpdateGoodsReq dto) throws BaseException{
        // 1. 예외: 굿즈 조회 결과가 없을 때, 등록된 굿즈의 스토어의 이메일이 인증된 사용자의 이메일과 다를때
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.GOODS_UPDATE_FAIL_NOT_FOUND)
        );
        if(!Objects.equals(goods.getStore().getCompanyEmail(), customUserDetails.getEmail())) {
            throw new BaseException(BaseResponseMessage.GOODS_UPDATE_FAIL_NOT_FOUND_INVALID_MEMBER);
        }
        // 2. 팝업 굿즈 업데이트 / 굿즈 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지를 저장
        goods.update(
                dto.getGoodsName(),
                dto.getGoodsContent(),
                dto.getGoodsPrice(),
                dto.getGoodsAmount()
        );
        goodsRepository.save(goods);
        if(fileNames != null){
            goodsImageRepository.deleteAllByGoodsIdx(goodsIdx);
            for (String fileName : fileNames) {
                GoodsImage goodsImage = GoodsImage.builder()
                        .url(fileName)
                        .goods(goods)
                        .build();
                goodsImageRepository.save(goodsImage);
            }
        }
        return UpdateGoodsRes.builder().goodsIdx(goods.getIdx()).build();
    }

    // 팝업 굿즈 삭제
    @Transactional
    public void delete(CustomUserDetails customUserDetails, Long goodsIdx) throws BaseException{
        // 1. 예외: 팝업 굿즈가 존재하지 않을때, 등록한 팝업굿즈와 현재 인증 사용자의 이메일이 다를때
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.STORE_DELETE_FAIL_NOT_FOUND)
        );
        if(!Objects.equals(goods.getStore().getCompanyEmail(), customUserDetails.getEmail())){
            throw new BaseException(BaseResponseMessage.STORE_DELETE_FAIL_INVALID_MEMBER);
        }
        goodsRepository.deleteById(goodsIdx);
    }
}
