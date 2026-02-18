package com.fiiiiive.zippop.goods.service;

import com.fiiiiive.zippop.goods.model.Goods;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.GoodsDto;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.popup.model.Popup;
import com.fiiiiive.zippop.popup.policy.PopupPolicy;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final PopupRepository popupRepository;
    private final PopupPolicy popupPolicy;

    // 굿즈 등록
    @Transactional
    public void createGoods(CustomUserDetails user, List<String> urls, GoodsDto.CreateGoodsReq req) throws BaseException {

        // 팝업 조회(popupIdx)
        Popup popup = popupRepository.findByPopupIdx(req.getPopupIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.GOODS_REGISTER_FAIL_NOT_FOUND_STORE)
        );

        // 팝업 소유권 확인
        popupPolicy.validateOwner(popup, user);

        // 굿즈 생성
        Goods goods = Goods.create(
                req.getGoodsName(),
                req.getGoodsAmount(),
                req.getGoodsPrice(),
                req.getGoodsContent(),
                popup
        );

        // 굿즈 이미지 추가
        goods.addImages(urls);

        goodsRepository.save(goods);

    }

    // 굿즈 조회
    @Transactional(readOnly = true)
    public GoodsDto.GetGoodsRes getGoods(Long goodIdx) throws BaseException {

        // 굿즈 조회(goodsIdx)
        Goods goods = goodsRepository.findByGoodsIdx(goodIdx).orElseThrow(
                () -> new BaseException(BaseMessage.GOODS_SEARCH_FAIL_NOT_FOUND_STORE)
        );

        return goods.toDto();

    }

    // 굿즈 목록 조회
    @Transactional(readOnly = true)
    public Page<GoodsDto.GetGoodsRes> getGoodsList(Long popupIdx, String keyword, int page, int size) throws BaseException {

        // 굿즈 페이지(popupIdx, keyword, pageable) 조회
        // if: 굿즈 검색 조회
        // else: 굿즈 목록 조회
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idx"));
        Page<Goods> goodsPage;
        if(keyword != null) {
            goodsPage = goodsRepository.findAllByPopupIdxAndKeyword(popupIdx, keyword, pageable);
        }
        else {
            goodsPage = goodsRepository.findAllByPopupIdx(popupIdx, pageable);
        }

        return Goods.toDtoPage(goodsPage);

    }

    // 굿즈 수정
    @Transactional
    public void updateGoods(CustomUserDetails user, Long goodsIdx, List<String> urls, GoodsDto.UpdateGoodsReq req) throws BaseException {

        // 굿즈 조회(goodsIdx)
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx).orElseThrow(
                () -> new BaseException(BaseMessage.GOODS_UPDATE_FAIL_NOT_FOUND)
        );
        
        // 팝업 소유 확인
        popupPolicy.validateOwner(goods.getPopup(), user);

        // 굿즈 수정
        goods.update(req);

        // 굿즈 이미지가 재등록 되었다면 기존의 이미지는 삭제하고 입력받은 이미지를 저장
        if (urls != null) {
            goods.replaceImages(urls);
        }

    }

    // 굿즈 삭제
    @Transactional
    public void deleteGoods(CustomUserDetails user, Long goodsIdx) throws BaseException{

        // 굿즈 조회(goodsIdx)
        Goods goods = goodsRepository.findByGoodsIdx(goodsIdx).orElseThrow(
                () -> new BaseException(BaseMessage.STORE_DELETE_FAIL_NOT_FOUND)
        );

        // 팝업 소유 확인
        popupPolicy.validateOwner(goods.getPopup(), user);
        
        // 굿즈 삭제
        goodsRepository.delete(goods);

    }

}
