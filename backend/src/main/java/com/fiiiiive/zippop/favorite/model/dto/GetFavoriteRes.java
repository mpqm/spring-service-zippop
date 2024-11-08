package com.fiiiiive.zippop.favorite.model.dto;

import com.fiiiiive.zippop.store.model.dto.GetStoreRes;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetFavoriteRes {
    private GetStoreRes getStoreRes;
}
