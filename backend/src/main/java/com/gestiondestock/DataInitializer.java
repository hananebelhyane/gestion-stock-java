package com.gestiondestock;

import com.gestiondestock.entity.Admin;
import com.gestiondestock.entity.Client;
import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.entity.CommandeFournisseur;
import com.gestiondestock.entity.Produit;
import com.gestiondestock.entity.Stock;
import com.gestiondestock.entity.Magasinier;
import com.gestiondestock.repository.AdminRepository;
import com.gestiondestock.repository.ClientRepository;
import com.gestiondestock.repository.MagasinierRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final ClientRepository clientRepository;
    private final MagasinierRepository magasinierRepository;
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager em;

    public DataInitializer(AdminRepository adminRepository, ClientRepository clientRepository, MagasinierRepository magasinierRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.magasinierRepository = magasinierRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "admin";
        if (adminRepository.findByUsername(adminUsername).isEmpty()) {
            Admin a = new Admin();

            try {
                a.setUsername(adminUsername);
                a.setNom("System");
                a.setPrenom("Administrator");
                a.setEmail("admin@example.com");
                a.setTelephone("0000000000");
                a.setMotDePasse(passwordEncoder.encode("admin123"));
            } catch (NoSuchMethodError | Exception ex) {

            }
            adminRepository.save(a);
            System.out.println("Seeded admin user: " + adminUsername + " / admin123");
        }

        String clientUsername = "client";
        if (clientRepository.findByUsername(clientUsername).isEmpty()) {
            Client c = new Client();
            c.setUsername(clientUsername);
            c.setNom("John");
            c.setPrenom("Client");
            c.setTelephone("1111111111");
            c.setMotDePasse(passwordEncoder.encode("client123"));
            clientRepository.save(c);
            System.out.println("Seeded client user: " + clientUsername + " / client123");
        }

        // Seed Magasinier
        String magasinierUsername = "magasinier";
        if (magasinierRepository.findByUsername(magasinierUsername).isEmpty()) {
            Magasinier m = new Magasinier();
            m.setUsername(magasinierUsername);
            m.setNom("Paul");
            m.setPrenom("Magasinier");
            m.setTelephone("5555555555");
            m.setMotDePasse(passwordEncoder.encode("magasinier123"));
            magasinierRepository.save(m);
            System.out.println("Seeded magasinier user: " + magasinierUsername + " / magasinier123");
        }

        // If there are already commandes, assume demo dataset exists
        long cmdCount = ((Number) em.createQuery("select count(c) from CommandeClient c").getSingleResult()).longValue();
        if (cmdCount > 0) {
            return;
        }

        // Seed Products
        Produit p1 = new Produit();
        p1.setNom("Paper A4");
        p1.setDescription("500 sheets pack");
        p1.setPrixUnitaire(3.5);
        em.persist(p1);
        Produit p2 = new Produit();
        p2.setNom("Ink Cartridge");
        p2.setDescription("Black");
        p2.setPrixUnitaire(19.0);
        em.persist(p2);
        Produit p3 = new Produit();
        p3.setNom("USB Cable");
        p3.setDescription("Type-C");
        p3.setPrixUnitaire(2.5);
        em.persist(p3);
        Produit p4 = new Produit();
        p4.setNom("Laptop Stand");
        p4.setDescription("Aluminum");
        p4.setPrixUnitaire(18.0);
        em.persist(p4);

        // Seed Stock (one row per product)
        Stock s1 = new Stock();
        s1.setProduit(p1);
        s1.setQuantiteDisponible(120);
        s1.setSeuilAlerte(20);
        em.persist(s1);
        Stock s2 = new Stock();
        s2.setProduit(p2);
        s2.setQuantiteDisponible(5);
        s2.setSeuilAlerte(10);
        em.persist(s2);
        Stock s3 = new Stock();
        s3.setProduit(p3);
        s3.setQuantiteDisponible(0);
        s3.setSeuilAlerte(5);
        em.persist(s3); // out-of-stock
        Stock s4 = new Stock();
        s4.setProduit(p4);
        s4.setQuantiteDisponible(40);
        s4.setSeuilAlerte(10);
        em.persist(s4);

        // Additional clients for KPIs
        Client cNew1 = ensureClient("alice", "Alice", "Martin", "2222222222", "alice123");
        Client cNew2 = ensureClient("bob", "Bob", "Smith", "3333333333", "bob123");
        Client cOld = ensureClient("charlie", "Charlie", "Brown", "4444444444", "charlie123");

        // Client orders: two new (first order within 7 days), and one old (had order before 7 days)
        em.persist(newCommandeClient(cNew1, LocalDateTime.now().minusDays(2))); // counts as new client
        em.persist(newCommandeClient(cNew1, LocalDateTime.now().minusDays(1))); // additional within 7d
        em.persist(newCommandeClient(cNew2, LocalDateTime.now().minusDays(6))); // counts as new client

        em.persist(newCommandeClient(cOld, LocalDateTime.now().minusDays(10))); // first older than 7d
        em.persist(newCommandeClient(cOld, LocalDateTime.now().minusDays(3)));  // recent order but not new client

        // Supplier orders: 2 pending, 1 delivered
        em.persist(newCommandeFournisseur(p2, CommandeFournisseur.StatutCommande.en_attente, LocalDateTime.now().minusDays(1)));
        em.persist(newCommandeFournisseur(p3, CommandeFournisseur.StatutCommande.en_attente, LocalDateTime.now().minusDays(2)));
        em.persist(newCommandeFournisseur(p1, CommandeFournisseur.StatutCommande.livree, LocalDateTime.now().minusDays(5)));

        System.out.println("Demo data seeded: products, stock, clients, orders, supplier orders");
    }

    private Client ensureClient(String username, String nom, String prenom, String telephone, String rawPassword) {
        return clientRepository.findByUsername(username).orElseGet(() -> {
            Client c = new Client();
            c.setUsername(username);
            c.setNom(nom);
            c.setPrenom(prenom);
            c.setTelephone(telephone);
            c.setMotDePasse(passwordEncoder.encode(rawPassword));
            return clientRepository.save(c);
        });
    }

    private CommandeClient newCommandeClient(Client client, LocalDateTime date) {
        CommandeClient cc = new CommandeClient();
        cc.setClient(client);
        cc.setDateCommande(date);
        cc.setStatut(CommandeClient.StatutCommande.en_attente);
        return cc;
    }

    private CommandeFournisseur newCommandeFournisseur(Produit p, CommandeFournisseur.StatutCommande statut, LocalDateTime date) {
        CommandeFournisseur cf = new CommandeFournisseur();
        cf.setProduit(p);
        cf.setStatut(statut);
        cf.setCommandeDate(date);
        return cf;
    }
}
