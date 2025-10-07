package com.fiiiiive.zippop.goods.service;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.dto.GoodsDto;
import com.fiiiiive.zippop.goods.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsImageRepository;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.store.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final StoreRepository storeRepository;
    private final GoodsImageRepository goodsImageRepository;

    // 스토어 소유권 확인
    public void checkOwnership(Store store, CustomUserDetails customUserDetails) throws BaseException {
        if (!Objects.equals(store.getCompanyEmail(), customUserDetails.getEmail())) throw new BaseException(BaseMessage.STORE_OWN_FAIL_INVALID_MEMBER);
    }

    // 굿즈 등록
    @Transactional
    public GoodsDto.CreateGoodsRes registerGoods(CustomUserDetails customUserDetails, Long storeIdx, List<String> urls, GoodsDto.CreateGoodsReq dto) throws BaseException {

        // 스토어 조회(storeIdx)
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.GOODS_REGISTER_FAIL_NOT_FOUND_STORE)
        );
        
        // 스토어 소유 확인
        checkOwnership(store, customUserDetails);

        // 굿즈 생성
        Goods goods = dto.toEntity(store);
        goodsRepository.save(goods);

        // 굿즈 이미지 생성
        for(String url: urls) goodsImageRepository.save(GoodsDto.CreateGoodsImageReq.toEntity(goods, url));

        // DTO 반환
        return GoodsDto.CreateGoodsRes.builder().goodsIdx(goods.getIdx()).build();

    }

    // 굿즈 조회
    @Transactional(readOnly = true)
    public GoodsDto.SearchGoodsRes searchGoods(Long goodIdx) throws BaseException {

        // 굿즈 조회(goodsIdx)
        Goods goods = goodsRepository.findByGoodsIdx(goodIdx).orElseThrow(
                () -> new BaseException(BaseMessage.GOODS_SEARCH_FAIL_NOT_FOUND_STORE)
        );

        // DTO 반환
        return goods.toDto();

    }

    // 굿즈 목록 조회
    public Page<GoodsDto.SearchGoodsRes> searchAllGoods(Long storeIdx, String keyword, int page, int size) throws BaseException {

        // 굿즈 페이지(storeIdx, keyword, pageable) 조회
        // if: 굿즈 검색 조회
        // else: 굿즈 목록 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Goods> goodsPage = null;
        if(keyword != null) goodsPage = goodsRepository.findAllByStoreIdxAndKeyword(storeIdx, keyword, pageable);
        else goodsPage = goodsRepository.findAllByStoreIdx(storeIdx, pageable);

        //  예외 : 조회 결과가 없을때
        if (goodsPage.isEmpty()) throw new BaseException(BaseMessage.GOODS_SEARCH_ALL_FAIL_STORE_NOT_NOT_FOUND);

        // DTO 반환
        return Goods.toDtoPage(goodsPage);

    }

    // 굿즈 수정
    @Transactional
    public GoodsDto.UpdateGoodsRes updateGoods(CustomUserDetails customUserDetails, Long goodsIdx, List<String> urls, GoodsDto.UpdateGoodsReq dto) throws BaseException {

        // 굿즈 조회(goodsIdx)
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx).orElseThrow(
                () -> new BaseException(BaseMessage.GOODS_UPDATE_FAIL_NOT_FOUND)
        );
        
        // 스토어 소유 확인
        checkOwnership(goods.getStore(), customUserDetails);
        
        // 굿즈 수정
        goodsRepository.save(goods.update(dto));

        // 굿즈 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지를 저장
        if(urls != null){
            goodsImageRepository.deleteAllByGoodsIdx(goodsIdx);
            for (String url : urls) goodsImageRepository.save(GoodsDto.CreateGoodsImageReq.toEntity(goods, url));
        }

        // DTO 반환
        return GoodsDto.UpdateGoodsRes.builder().goodsIdx(goods.getIdx()).build();

    }

    // 굿즈 삭제
    @Transactional
    public void deleteGoods(CustomUserDetails customUserDetails, Long goodsIdx) throws BaseException{

        // 굿즈 조회(goodsIdx)
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_DELETE_FAIL_NOT_FOUND)
        );
        
        // 스토어 소유 확인
        checkOwnership(goods.getStore(), customUserDetails);
        
        // 굿즈 삭제
        goodsRepository.deleteById(goodsIdx);

    }

}
