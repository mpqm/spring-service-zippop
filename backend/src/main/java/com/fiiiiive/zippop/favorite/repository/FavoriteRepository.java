package com.fiiiiive.zippop.favorite.repository;

import com.fiiiiive.zippop.favorite.model.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f " +
            "JOIN FETCH f.customer " +
            "JOIN FETCH f.store " +
            "WHERE f.customer.idx = :customerIdx " +
            "AND f.store.idx = :storeIdx")
    Optional<Favorite> findByCustomerIdxAndStoreIdx(Long customerIdx, Long storeIdx);

    @Query("SELECT f FROM Favorite f " +
            "JOIN FETCH f.customer " +
            "JOIN FETCH f.store " +
            "WHERE f.customer.idx = :customerIdx")
    Optional<List<Favorite>> findAllByCustomerIdx(Long customerIdx);
}
