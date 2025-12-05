package com.gestiondestock.repository;

import com.gestiondestock.entity.SortieStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SortieStockRepository extends JpaRepository<SortieStock,UUID > {

    //les sorties recentes
    @Query("SELECT s FROM SortieStock s ORDER BY s.date_sortie DESC")
    List<SortieStock> findSortiesRecentes();

    //les sorties par periode
    @Query("SELECT s FROM SortieStock s WHERE s.date_sortie BETWEEN :debut AND :fin")
    List<SortieStock> findSortiesParPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );
}
