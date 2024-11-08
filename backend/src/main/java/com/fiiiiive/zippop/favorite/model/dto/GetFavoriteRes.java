package com.fiiiiive.zippop.favorite.model.dto;

import com.fiiiiive.zippop.popup_store.model.dto.GetPopupStoreRes;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetFavoriteRes {
    private GetPopupStoreRes getPopupStoreRes;
}
