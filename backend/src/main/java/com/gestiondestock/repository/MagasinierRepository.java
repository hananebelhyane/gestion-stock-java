package com.gestiondestock.repository;

import com.gestiondestock.entity.Magasinier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MagasinierRepository extends JpaRepository<Magasinier, UUID> {
    Optional<Magasinier> findByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Magasinier m WHERE m.username = :username AND m.deleted_at IS NULL")
    boolean existsByUsernameAndNotDeleted(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Magasinier m WHERE m.telephone = :telephone AND m.deleted_at IS NULL")
    boolean existsByTelephoneAndNotDeleted(@Param("telephone") String telephone);

    @Query("SELECT m FROM Magasinier m WHERE m.deleted_at IS NULL")
    List<Magasinier> findAllActive();

    @Query("SELECT m FROM Magasinier m WHERE m.deleted_at IS NOT NULL")
    List<Magasinier> findAllDeleted();

    @Query("SELECT m FROM Magasinier m WHERE (LOWER(m.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.username) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND m.deleted_at IS NULL")
    List<Magasinier> searchByNomOrPrenomOrUsername(@Param("keyword") String keyword);
}
