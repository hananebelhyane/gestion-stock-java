package com.gestiondestock.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.gestiondestock.dto.OrdersOverTimePoint;
import com.gestiondestock.dto.RecentActivity;
import com.gestiondestock.dto.StockAlertItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<OrdersOverTimePoint> ordersOverTime(int days) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days - 1);
        LocalDateTime since = start.atStartOfDay();

        List<LocalDateTime> timestamps = em.createQuery(
                "select cc.dateCommande from CommandeClient cc where cc.dateCommande >= :since",
                LocalDateTime.class)
            .setParameter("since", since)
            .getResultList();

        Map<LocalDate, Integer> counts = new HashMap<>();
        for (LocalDateTime ts : timestamps) {
            LocalDate d = ts.toLocalDate();
            counts.put(d, counts.getOrDefault(d, 0) + 1);
        }

        List<OrdersOverTimePoint> points = new ArrayList<>();
        LocalDate d = start;
        while (!d.isAfter(today)) {
            points.add(new OrdersOverTimePoint(d, counts.getOrDefault(d, 0)));
            d = d.plusDays(1);
        }
        return points;
    }

    public List<RecentActivity> recentActivities(int limit) {
        // Entries
        List<Object[]> entries = em.createQuery(
            "select e.produit.nom, e.quantite, e.date_entree from EntreeStock e order by e.date_entree desc")
            .setMaxResults(limit)
            .getResultList();
        // Removals
        List<Object[]> removals = em.createQuery(
            "select s.produit.nom, s.quantite, s.date_sortie from SortieStock s order by s.date_sortie desc")
            .setMaxResults(limit)
            .getResultList();
        List<RecentActivity> list = new ArrayList<>();
        for (Object[] row : entries) {
            list.add(new RecentActivity((String) row[0], ((Number) row[1]).intValue(), (java.time.LocalDateTime) row[2], "ENTRY"));
        }
        for (Object[] row : removals) {
            list.add(new RecentActivity((String) row[0], -((Number) row[1]).intValue(), (java.time.LocalDateTime) row[2], "REMOVAL"));
        }
        list.sort((a, b) -> b.date.compareTo(a.date));
        // Seed sample data when empty to help initial UI preview
        if (list.isEmpty()) {
            list.add(new RecentActivity("Produit A", 12, LocalDateTime.now().minusDays(1), "ENTRY"));
            list.add(new RecentActivity("Produit B", -5, LocalDateTime.now().minusDays(2), "REMOVAL"));
            list.add(new RecentActivity("Produit C", 7, LocalDateTime.now().minusDays(3), "ENTRY"));
        }
        if (list.size() > limit) return list.subList(0, limit);
        return list;
    }

    public List<StockAlertItem> recentAlerts(int limit) {
        List<Object[]> alerts = em.createQuery(
            "select a.produit.nom, a.dateAlerte, a.message from AlerteStock a order by a.dateAlerte desc")
            .setMaxResults(limit)
            .getResultList();
        List<StockAlertItem> list = new ArrayList<>();
        for (Object[] row : alerts) {
            list.add(new StockAlertItem((String) row[0], (java.time.LocalDateTime) row[1], (String) row[2]));
        }
        // Seed sample alerts when empty to help initial UI preview
        if (list.isEmpty()) {
            list.add(new StockAlertItem("Produit B", LocalDateTime.now().minusDays(1), "Stock faible (< 5)"));
            list.add(new StockAlertItem("Produit D", LocalDateTime.now().minusDays(2), "Rupture de stock"));
        }
        return list;
    }
}
