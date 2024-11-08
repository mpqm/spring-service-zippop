package com.fiiiiive.zippop.favorite.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.favorite.model.entity.Favorite;
import com.fiiiiive.zippop.favorite.model.dto.GetFavoriteRes;
import com.fiiiiive.zippop.favorite.repository.FavoriteRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.store.model.dto.SearchStoreImageRes;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public void active(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {
        Customer customer = customerRepository.findById(customUserDetails.getIdx())
        .orElseThrow(() -> (new BaseException(BaseResponseMessage.FAVORITE_ACTIVE_FAIL_MEMBER_NOT_FOUND)));
        Store store = storeRepository.findById(storeIdx)
        .orElseThrow(() -> (new BaseException(BaseResponseMessage.FAVORITE_ACTIVE_FAIL_STORE_NOT_FOUND)));
        Optional<Favorite> result = favoriteRepository.findByCustomerIdxAndStoreIdx(customUserDetails.getIdx(), storeIdx);
        if(result.isPresent()){
            Favorite favorite = result.get();
            favoriteRepository.deleteById(favorite.getIdx());
        } else {
            Favorite favorite = Favorite.builder()
                    .store(store)
                    .customer(customer)
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    public List<GetFavoriteRes> searchAll(CustomUserDetails customUserDetails) throws BaseException {
        List<Favorite> favoriteList = favoriteRepository.findAllByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(()->new BaseException(BaseResponseMessage.FAVORITE_SEARCH_ALL_FAIL));
        List<GetFavoriteRes> getFavoriteResList = new ArrayList<>();
        for(Favorite favorite: favoriteList){
            Store store = favorite.getStore();
            List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
            for(StoreImage storeImage : store.getStoreImageList()){
                SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                        .storeImageIdx(storeImage.getIdx())
                        .storeImageUrl(storeImage.getUrl())
                        .createdAt(storeImage.getCreatedAt())
                        .updatedAt(storeImage.getUpdatedAt())
                        .build();
                searchStoreImageResList.add(searchStoreImageRes);
            }
            SearchStoreRes searchStoreRes = SearchStoreRes.builder()
                    .storeIdx(store.getIdx())
                    .companyEmail(store.getCompanyEmail())
                    .storeName(store.getName())
                    .storeContent(store.getContent())
                    .storeAddress(store.getAddress())
                    .category(store.getCategory())
                    .likeCount(store.getLikeCount())
                    .totalPeople(store.getTotalPeople())
                    .storeStartDate(store.getStartDate())
                    .storeEndDate(store.getEndDate())
                    .createdAt(store.getCreatedAt())
                    .updatedAt(store.getUpdatedAt())
                    .searchStoreImageResList(searchStoreImageResList)
                    .build();
            GetFavoriteRes getFavoriteRes = GetFavoriteRes.builder()
                    .searchStoreRes(searchStoreRes)
                    .build();
            getFavoriteResList.add(getFavoriteRes);
        }
        return getFavoriteResList;
    }
}
