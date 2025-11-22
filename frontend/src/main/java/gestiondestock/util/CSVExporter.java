package gestiondestock.util;

import gestiondestock.model.ClientModel;
import gestiondestock.model.MagasinierModel;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {

    private static final String CSV_SEPARATOR = ",";

    // Export pour les clients
    public static File exportClientsToCSV(List<ClientModel> clients, Window owner, String fileName) throws IOException {
        if (clients == null || clients.isEmpty()) {
            throw new IllegalArgumentException("La liste des clients est vide");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les clients");
        fileChooser.setInitialFileName(fileName);
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showSaveDialog(owner);
        
        if (file != null) {
            writeClientsCSV(clients, file);
            return file;
        }
        
        return null;
    }

    // Export pour les magasiniers
    public static File exportMagasiniersToCSV(List<MagasinierModel> magasiniers, Window owner, String fileName) throws IOException {
        if (magasiniers == null || magasiniers.isEmpty()) {
            throw new IllegalArgumentException("La liste des magasiniers est vide");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les magasiniers");
        fileChooser.setInitialFileName(fileName);
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showSaveDialog(owner);
        
        if (file != null) {
            writeMagasiniersCSV(magasiniers, file);
            return file;
        }
        
        return null;
    }

    private static void writeClientsCSV(List<ClientModel> clients, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // En-tête
            writer.write("ID,Nom,Prénom,Username,Téléphone,Adresse");
            writer.newLine();
            
            // Données
            for (ClientModel client : clients) {
                writer.write(toCSVLineClient(client));
                writer.newLine();
            }
        }
    }

    private static void writeMagasiniersCSV(List<MagasinierModel> magasiniers, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // En-tête
            writer.write("ID,Nom,Prénom,Username,Téléphone");
            writer.newLine();
            
            // Données
            for (MagasinierModel magasinier : magasiniers) {
                writer.write(toCSVLineMagasinier(magasinier));
                writer.newLine();
            }
        }
    }

    private static String toCSVLineClient(ClientModel client) {
        return escapeCSV(client.getId() != null ? client.getId().toString() : "") + CSV_SEPARATOR +
               escapeCSV(client.getNom()) + CSV_SEPARATOR +
               escapeCSV(client.getPrenom()) + CSV_SEPARATOR +
               escapeCSV(client.getUsername()) + CSV_SEPARATOR +
               escapeCSV(client.getTelephone()) + CSV_SEPARATOR +
               escapeCSV(client.getAdresse());
    }

    private static String toCSVLineMagasinier(MagasinierModel magasinier) {
        return escapeCSV(magasinier.getId() != null ? magasinier.getId().toString() : "") + CSV_SEPARATOR +
               escapeCSV(magasinier.getNom()) + CSV_SEPARATOR +
               escapeCSV(magasinier.getPrenom()) + CSV_SEPARATOR +
               escapeCSV(magasinier.getUsername()) + CSV_SEPARATOR +
               escapeCSV(magasinier.getTelephone());
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        
        return value;
    }
}