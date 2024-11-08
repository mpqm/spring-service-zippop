package com.fiiiiive.zippop.favorite.service;

import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.favorite.model.entity.Favorite;
import com.fiiiiive.zippop.favorite.model.dto.GetFavoriteRes;
import com.fiiiiive.zippop.favorite.repository.FavoriteRepository;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.store.model.dto.SearchStoreImageRes;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.model.entity.StoreImage;
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

    public void active(CustomUserDetails customUserDetails, Long storeIdx) throws BaseException {
        Customer customer = customerRepository.findById(customUserDetails.getIdx())
        .orElseThrow(() -> (new BaseException(BaseResponseMessage.FAVORITE_ACTIVE_FAIL_MEMBER_NOT_FOUND)));
        Store store = storeRepository.findById(storeIdx)
        .orElseThrow(() -> (new BaseException(BaseResponseMessage.FAVORITE_ACTIVE_FAIL_STORE_NOT_FOUND)));
        Optional<Favorite> result = favoriteRepository.findByCustomerEmailAndStoreIdx(customUserDetails.getEmail(), storeIdx);
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

    public List<GetFavoriteRes> list(CustomUserDetails customUserDetails) throws BaseException {
        List<Favorite> favoriteList = favoriteRepository.findAllByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(()->new BaseException(BaseResponseMessage.FAVORITE_SEARCH_ALL_FAIL));
        List<GetFavoriteRes> getFavoriteResList = new ArrayList<>();
        for(Favorite favorite: favoriteList){

            Store store = favorite.getStore();
            List<StoreImage> storeImageList = store.getPopupstoreImageList();
            List<SearchStoreImageRes> searchStoreImageResList = new ArrayList<>();
            for(StoreImage storeImage : storeImageList){
                SearchStoreImageRes searchStoreImageRes = SearchStoreImageRes.builder()
                        .storeImageIdx(storeImage.getStoreImageIdx())
                        .imageUrl(storeImage.getImageUrl())
                        .createdAt(storeImage.getCreatedAt())
                        .updatedAt(storeImage.getUpdatedAt())
                        .build();
                searchStoreImageResList.add(searchStoreImageRes);
            }
            SearchStoreRes searchStoreRes = SearchStoreRes.builder()
                    .storeIdx(store.getStoreIdx())
                    .companyEmail(store.getCompanyEmail())
                    .storeName(store.getStoreName())
                    .storeContent(store.getStoreContent())
                    .storeAddress(store.getStoreAddress())
                    .category(store.getCategory())
                    .likeCount(store.getLikeCount())
                    .totalPeople(store.getTotalPeople())
                    .storeStartDate(store.getStoreStartDate())
                    .storeEndDate(store.getStoreEndDate())
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
