package com.gestiondestock.repository;

import com.gestiondestock.entity.EntreeStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EntreeStockRepository extends JpaRepository<EntreeStock, UUID> {

    //les entrées recentes
    @Query("SELECT e FROM EntreeStock e ORDER BY e.date_entree DESC")
    List<EntreeStock> findEntreesRecentes();

    //les entrées par periode
    @Query("SELECT e FROM EntreeStock e WHERE e.date_entree BETWEEN :debut AND :fin")
    List<EntreeStock> findEntreesParPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
            );






    java.util.List<EntreeStock> findByCommandefournisseurId(UUID commandeFournisseurId);
}
