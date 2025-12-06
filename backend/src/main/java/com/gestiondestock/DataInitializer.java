package com.gestiondestock;

import com.gestiondestock.entity.Admin;
import com.gestiondestock.entity.Client;
import com.gestiondestock.entity.CommandeClient;
import com.gestiondestock.entity.CommandeFournisseur;
import com.gestiondestock.entity.Produit;
import com.gestiondestock.entity.Stock;
import com.gestiondestock.entity.Magasinier;
import com.gestiondestock.entity.Categorie;
import com.gestiondestock.entity.Fournisseur;
import com.gestiondestock.repository.AdminRepository;
import com.gestiondestock.repository.ClientRepository;
import com.gestiondestock.repository.MagasinierRepository;
import com.gestiondestock.repository.CategorieRepository;
import com.gestiondestock.repository.FournisseurRepository;

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
    private final CategorieRepository categorieRepository;
    private final FournisseurRepository fournisseurRepository;
    @PersistenceContext
    private EntityManager em;

    public DataInitializer(AdminRepository adminRepository,
                           ClientRepository clientRepository,
                           MagasinierRepository magasinierRepository,
                           PasswordEncoder passwordEncoder,
                           CategorieRepository categorieRepository,
                           FournisseurRepository fournisseurRepository) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.magasinierRepository = magasinierRepository;
        this.passwordEncoder = passwordEncoder;
        this.categorieRepository = categorieRepository;
        this.fournisseurRepository = fournisseurRepository;
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

        // Seed Categories used by frontend combos
        ensureCategorie("Électronique");
        ensureCategorie("Informatique");
        ensureCategorie("Mobilier");
        ensureCategorie("Bureautique");
        ensureCategorie("Alimentaire");
        ensureCategorie("Vêtements");
        ensureCategorie("Autre");

        // Seed Fournisseurs used by frontend combos
        ensureFournisseur("TechCorp", "techcorp@example.com", "+212 600-000000", "Casablanca");
        ensureFournisseur("PhoneDistri", "phonedistri@example.com", "+212 601-000000", "Rabat");
        ensureFournisseur("OfficePlus", "officeplus@example.com", "+212 602-000000", "Marrakech");
        ensureFournisseur("SoundTech", "soundtech@example.com", "+212 603-000000", "Fes");
        ensureFournisseur("FurnitureCo", "furnitureco@example.com", "+212 604-000000", "Tangier");
        ensureFournisseur("GeneralSupplies", "generalsupplies@example.com", "+212 605-000000", "Agadir");
        ensureFournisseur("Autre", "autre@example.com", "+212 606-000000", "Oujda");

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
        Produit p5 = new Produit();
        p5.setNom("Wireless Mouse");
        p5.setDescription("Ergonomic design");
        p5.setPrixUnitaire(15.0);
        em.persist(p5);
        Produit p6 = new Produit();
        p6.setNom("Keyboard");
        p6.setDescription("Mechanical RGB");
        p6.setPrixUnitaire(45.0);
        em.persist(p6);

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
        Stock s5 = new Stock();
        s5.setProduit(p5);
        s5.setQuantiteDisponible(75);
        s5.setSeuilAlerte(15);
        em.persist(s5);
        Stock s6 = new Stock();
        s6.setProduit(p6);
        s6.setQuantiteDisponible(30);
        s6.setSeuilAlerte(10);
        em.persist(s6);

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

    private void ensureCategorie(String nom) {
        categorieRepository.findFirstByNomIgnoreCase(nom).orElseGet(() -> {
            Categorie c = new Categorie();
            c.setNom(nom);
            c.setDescription(nom);
            return categorieRepository.save(c);
        });
    }

    private void ensureFournisseur(String nom, String email, String telephone, String adresse) {
        fournisseurRepository.findFirstByNomIgnoreCase(nom).orElseGet(() -> {
            Fournisseur f = new Fournisseur();
            f.setNom(nom);
            f.setPrenom("");
            f.setEmail(email);
            f.setTelephone(telephone);
            f.setAdresse(adresse);
            return fournisseurRepository.save(f);
        });
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
