package com.fiiiiive.zippop.favorite.model.dto;

import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetFavoriteRes {
    private SearchStoreRes searchStoreRes;
}
