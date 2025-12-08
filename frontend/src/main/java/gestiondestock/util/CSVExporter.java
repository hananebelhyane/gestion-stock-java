package gestiondestock.util;

import gestiondestock.model.ClientModel;
import gestiondestock.model.FournisseurModel;
import gestiondestock.model.MagasinierModel;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {

    private static final String CSV_SEPARATOR = ",";

    // Fournisseurs
    public static File exportToCSV(List<FournisseurModel> fournisseurs, Window owner, String fileName) throws IOException {
        if (fournisseurs == null || fournisseurs.isEmpty()) {
            throw new IllegalArgumentException("La liste des fournisseurs est vide");
        }
        File file = chooseFile(owner, fileName, "Exporter les fournisseurs");
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID,Nom,Prénom,Email,Téléphone,Adresse");
                writer.newLine();
                for (FournisseurModel fournisseur : fournisseurs) {
                    writer.write(escapeCSV(fournisseur.getId() != null ? fournisseur.getId().toString() : "") + CSV_SEPARATOR +
                                 escapeCSV(fournisseur.getNom()) + CSV_SEPARATOR +
                                 escapeCSV(fournisseur.getPrenom()) + CSV_SEPARATOR +
                                 escapeCSV(fournisseur.getEmail()) + CSV_SEPARATOR +
                                 escapeCSV(fournisseur.getTelephone()) + CSV_SEPARATOR +
                                 escapeCSV(fournisseur.getAdresse()));
                    writer.newLine();
                }
            }
        }
        return file;
    }

    // Clients (ObservableList + Stage signature expected by controllers)
    public static File exportClientsToCSV(ObservableList<ClientModel> clients, Stage owner, String fileName) throws IOException {
        if (clients == null || clients.isEmpty()) {
            throw new IllegalArgumentException("La liste des clients est vide");
        }
        File file = chooseFile(owner, fileName, "Exporter les clients");
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID,Nom,Prénom,Username,Téléphone,Adresse");
                writer.newLine();
                for (ClientModel client : clients) {
                    writer.write(escapeCSV(client.getId() != null ? client.getId().toString() : "") + CSV_SEPARATOR +
                                 escapeCSV(client.getNom()) + CSV_SEPARATOR +
                                 escapeCSV(client.getPrenom()) + CSV_SEPARATOR +
                                 escapeCSV(client.getUsername()) + CSV_SEPARATOR +
                                 escapeCSV(client.getTelephone()) + CSV_SEPARATOR +
                                 escapeCSV(client.getAdresse()));
                    writer.newLine();
                }
            }
        }
        return file;
    }

    // Magasiniers (ObservableList + Stage signature expected by controllers)
    public static File exportMagasiniersToCSV(ObservableList<MagasinierModel> magasiniers, Stage owner, String fileName) throws IOException {
        if (magasiniers == null || magasiniers.isEmpty()) {
            throw new IllegalArgumentException("La liste des magasiniers est vide");
        }
        File file = chooseFile(owner, fileName, "Exporter les magasiniers");
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID,Nom,Prénom,Username,Téléphone");
                writer.newLine();
                for (MagasinierModel m : magasiniers) {
                    writer.write(escapeCSV(m.getId() != null ? m.getId().toString() : "") + CSV_SEPARATOR +
                                 escapeCSV(m.getNom()) + CSV_SEPARATOR +
                                 escapeCSV(m.getPrenom()) + CSV_SEPARATOR +
                                 escapeCSV(m.getUsername()) + CSV_SEPARATOR +
                                 escapeCSV(m.getTelephone()));
                    writer.newLine();
                }
            }
        }
        return file;
    }

    private static File chooseFile(Window owner, String fileName, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName(fileName);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showSaveDialog(owner);
    }

    private static String escapeCSV(String value) {
        if (value == null) { return ""; }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}