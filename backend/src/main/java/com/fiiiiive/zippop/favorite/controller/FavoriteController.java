package com.fiiiiive.zippop.favorite.controller;

import com.fiiiiive.zippop.global.common.responses.BaseResponse;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.favorite.service.FavoriteService;
import com.fiiiiive.zippop.favorite.model.dto.GetFavoriteRes;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "favorite-api", description = "Favorite")
@Slf4j
@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/active")
    public ResponseEntity<BaseResponse> active(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam Long storeIdx) throws Exception {
        favoriteService.active(customUserDetails, storeIdx);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.FAVORITE_INACTIVE_SUCCESS));
    }

    @GetMapping("/search-all")
    public ResponseEntity<BaseResponse> list(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {
        List<GetFavoriteRes> response = favoriteService.searchAll(customUserDetails);
        return ResponseEntity.ok(new BaseResponse(BaseResponseMessage.FAVORITE_SEARCH_ALL_SUCCESS, response));
    }
}
