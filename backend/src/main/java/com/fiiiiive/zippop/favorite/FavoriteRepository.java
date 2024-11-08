package com.fiiiiive.zippop.favorite;

import com.fiiiiive.zippop.favorite.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f " +
            "WHERE f.customer.email = :customerEmail " +
            "AND f.store.storeIdx = :storeIdx")
    Optional<Favorite> findByCustomerEmailAndStoreIdx(String customerEmail, Long storeIdx);

    @Query("SELECT f FROM Favorite f " +
            "JOIN FETCH f.customer " +
            "JOIN FETCH f.store " +
            "WHERE f.customer.email = :customerEmail")
    Optional<List<Favorite>> findAllByCustomerEmail(String customerEmail);
}
