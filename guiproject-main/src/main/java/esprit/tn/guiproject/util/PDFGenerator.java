package esprit.tn.guiproject.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import esprit.tn.guiproject.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFGenerator {
    
    public static void generateUserReport(List<User> users, String filePath) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add title
        Paragraph title = new Paragraph("Rapport des Utilisateurs")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold();
        document.add(title);

        // Add date
        Paragraph date = new Paragraph("Généré le: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10);
        document.add(date);

        // Add space
        document.add(new Paragraph("\n"));

        // Create table with adjusted column widths (removed telephone column)
        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 20, 20, 30, 20}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Add headers
        table.addHeaderCell("ID");
        table.addHeaderCell("Nom");
        table.addHeaderCell("Prénom");
        table.addHeaderCell("Email");
        table.addHeaderCell("Rôle");
        table.addHeaderCell("Statut");

        // Add data
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getNom());
            table.addCell(user.getPrenom());
            table.addCell(user.getEmail());
            table.addCell(user.getRole());
            table.addCell(user.getStatut());
        }

        document.add(table);
        document.close();
    }
} 