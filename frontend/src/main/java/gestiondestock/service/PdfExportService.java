package gestiondestock.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import gestiondestock.dto.ClientTopDTO;
import gestiondestock.dto.ProduitVenduDTO;
import gestiondestock.dto.StatistiquesGeneralesDTO;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfExportService {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(51, 65, 85); // #334155
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(248, 250, 252); // #f8fafc

    /**
     * Exporte les statistiques en PDF
     */
    public static File exportStatistiquesPdf(
            StatistiquesGeneralesDTO statsGenerales,
            List<ProduitVenduDTO> topProduits,
            List<ClientTopDTO> topClients,
            String periode) {

        try {

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "Statistiques_" + timestamp + ".pdf";
            String userHome = System.getProperty("user.home");
            String filePath = userHome + File.separator + "Downloads" + File.separator + fileName;

            System.out.println("📄 Création du PDF : " + filePath);


            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);


            addHeader(document, periode);


            addStatistiquesGenerales(document, statsGenerales);


            addTopProduits(document, topProduits);


            addTopClients(document, topClients);


            addFooter(document);


            document.close();

            System.out.println("✅ PDF créé avec succès !");

            return new File(filePath);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création du PDF : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private static void addHeader(Document document, String periode) {
        Paragraph title = new Paragraph("RAPPORT DE STATISTIQUES")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(title);

        Paragraph subtitle = new Paragraph("Période : " + periode)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);

        Paragraph date = new Paragraph("Généré le : " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30)
                .setFontColor(ColorConstants.GRAY);
        document.add(date);
    }


    private static void addStatistiquesGenerales(Document document, StatistiquesGeneralesDTO stats) {
        document.add(new Paragraph("STATISTIQUES GÉNÉRALES")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10));

        float[] columnWidths = {3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth()
                .setMarginBottom(20);


        table.addHeaderCell(createHeaderCell("Indicateur"));
        table.addHeaderCell(createHeaderCell("Valeur"));


        table.addCell(createCell("Total Produits"));
        table.addCell(createCell(String.valueOf(stats.getNombreTotalProduits())));

        table.addCell(createCell("Total Clients"));
        table.addCell(createCell(String.valueOf(stats.getNombreTotalClients())));

        table.addCell(createCell("Total Commandes"));
        table.addCell(createCell(String.valueOf(stats.getNombreTotalCommandes())));

        table.addCell(createCell("Chiffre d'Affaires Total"));
        table.addCell(createCell(decimalFormat.format(stats.getChiffreAffairesTotal()) + " DH"));

        table.addCell(createCell("Valeur Stock"));
        table.addCell(createCell(decimalFormat.format(stats.getValeurTotaleStock()) + " DH"));

        table.addCell(createCell("Produits en Rupture"));
        table.addCell(createCell(String.valueOf(stats.getNombreProduitsRupture())));

        document.add(table);
    }

    private static void addTopProduits(Document document, List<ProduitVenduDTO> topProduits) {
        document.add(new Paragraph("TOP 5 PRODUITS LES PLUS VENDUS")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10));

        if (topProduits == null || topProduits.isEmpty()) {
            document.add(new Paragraph("Aucune donnée disponible").setItalic().setMarginBottom(20));
            return;
        }

        float[] columnWidths = {1, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth()
                .setMarginBottom(20);


        table.addHeaderCell(createHeaderCell("Rang"));
        table.addHeaderCell(createHeaderCell("Produit"));
        table.addHeaderCell(createHeaderCell("Quantité Vendue"));

        // Données
        int rang = 1;
        for (ProduitVenduDTO produit : topProduits) {
            table.addCell(createCell(String.valueOf(rang++)));
            table.addCell(createCell(produit.getNomProduit()));
            table.addCell(createCell(String.valueOf(produit.getQuantiteVendue())));
        }

        document.add(table);
    }


    private static void addTopClients(Document document, List<ClientTopDTO> topClients) {
        document.add(new Paragraph("TOP 5 MEILLEURS CLIENTS")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10));

        if (topClients == null || topClients.isEmpty()) {
            document.add(new Paragraph("Aucune donnée disponible").setItalic().setMarginBottom(20));
            return;
        }

        float[] columnWidths = {1, 3, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth()
                .setMarginBottom(20);


        table.addHeaderCell(createHeaderCell("Rang"));
        table.addHeaderCell(createHeaderCell("Client"));
        table.addHeaderCell(createHeaderCell("Total Achats"));
        table.addHeaderCell(createHeaderCell("Nb Commandes"));


        int rang = 1;
        for (ClientTopDTO client : topClients) {
            table.addCell(createCell(String.valueOf(rang++)));
            table.addCell(createCell(client.getPrenom() + " " + client.getNom()));
            table.addCell(createCell(decimalFormat.format(client.getTotalAchats()) + " DH"));
            table.addCell(createCell(String.valueOf(client.getNombreCommandes())));
        }

        document.add(table);
    }


    private static void addFooter(Document document) {
        Paragraph footer = new Paragraph("───────────────────────────────────────────────")
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30)
                .setFontColor(ColorConstants.LIGHT_GRAY);
        document.add(footer);

        Paragraph footerText = new Paragraph("Système de Gestion de Stock - Rapport généré automatiquement")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(footerText);
    }


    private static Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }


    private static Cell createCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setPadding(6)
                .setTextAlignment(TextAlignment.LEFT);
    }
}