package com.gestiondestock.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class DashboardService {
    @PersistenceContext
    private EntityManager em;

    public int newClients7d() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Number n = (Number) em.createQuery(
                "select count(distinct c) " +
                "from CommandeClient cc join cc.client c " +
                "where cc.dateCommande >= :since and " +
                "      not exists (select 1 from CommandeClient cc2 where cc2.client = c and cc2.dateCommande < :since)")
            .setParameter("since", since)
            .getSingleResult();
        return n == null ? 0 : n.intValue();
    }

    public int clientOrders7d() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        Number n = (Number) em.createQuery(
                "select count(cc) from CommandeClient cc where cc.dateCommande >= :since")
            .setParameter("since", since)
            .getSingleResult();
        return n == null ? 0 : n.intValue();
    }

    public int pendingSupplierOrders() {
        Number n = (Number) em.createQuery(
                "select count(cf) from CommandeFournisseur cf where cf.statut = com.gestiondestock.entity.CommandeFournisseur$StatutCommande.EN_ATTENTE")
            .getSingleResult();
        return n == null ? 0 : n.intValue();
    }

    public int outOfStock() {
        Number n = (Number) em.createQuery(
                "select count(p) from Produit p " +
                "where (select coalesce(sum(s.quantiteDisponible),0) from Stock s where s.produit = p) = 0")
            .getSingleResult();
        return n == null ? 0 : n.intValue();
    }
}
