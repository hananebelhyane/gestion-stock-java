package gestiondestock.util;

import gestiondestock.model.FournisseurModel;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CSVExporter {

    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_HEADER = "ID,Nom,Prénom,Email,Téléphone,Adresse";

    public static File exportToCSV(List<FournisseurModel> fournisseurs, Window owner, String fileName) throws IOException {
        if (fournisseurs == null || fournisseurs.isEmpty()) {
            throw new IllegalArgumentException("La liste des fournisseurs est vide");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les fournisseurs");
        fileChooser.setInitialFileName(fileName);
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showSaveDialog(owner);
        
        if (file != null) {
            writeCSV(fournisseurs, file);
            return file;
        }
        
        return null;
    }

    private static void writeCSV(List<FournisseurModel> fournisseurs, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            
            for (FournisseurModel fournisseur : fournisseurs) {
                writer.write(toCSVLine(fournisseur));
                writer.newLine();
            }
        }
    }

    private static String toCSVLine(FournisseurModel fournisseur) {
        return escapeCSV(fournisseur.getId() != null ? fournisseur.getId().toString() : "") + CSV_SEPARATOR +
               escapeCSV(fournisseur.getNom()) + CSV_SEPARATOR +
               escapeCSV(fournisseur.getPrenom()) + CSV_SEPARATOR +
               escapeCSV(fournisseur.getEmail()) + CSV_SEPARATOR +
               escapeCSV(fournisseur.getTelephone()) + CSV_SEPARATOR +
               escapeCSV(fournisseur.getAdresse());
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