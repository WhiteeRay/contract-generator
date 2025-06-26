package com.example.contract.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ContractService {

    public void enrichFormData(Map<String, String> formData) {
        LocalDate today = LocalDate.now();

        String firstName = formData.getOrDefault("firstName", "").trim();
        String lastName = formData.getOrDefault("lastName", "").trim();
        String middle = formData.getOrDefault("middle", "").trim();

        String firstInitial = firstName.isEmpty() ? "" : firstName.charAt(0) + ".";
        String middleInitial = middle.isEmpty() ? "" : middle.charAt(0) + ".";

        String fullName = String.format("%s %s %s", firstName, lastName, middle).trim();
        String formattedName = String.format("%s %s %s", lastName, firstInitial, middleInitial).trim();

        formData.put("FULLNAME", fullName);
        formData.put("Fullname", fullName);
        formData.put("Surname N.M.", formattedName);

        formData.put("YYYY+1", String.valueOf(today.plusYears(1).getYear()));
        formData.put("DD-1", String.format("%02d", today.minusDays(1).getDayOfMonth()));
        formData.put("month-1", today.minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, new Locale("kk")));
        formData.put("YYYY", String.valueOf(today.getYear()));
        formData.put("DD", String.format("%02d", today.getDayOfMonth()));
        formData.put("month", today.getMonth().getDisplayName(TextStyle.FULL, new Locale("kk")));
    }

    public byte[] generateDocFromTemplate(String templateName, Map<String, String> formData) throws IOException {
        InputStream fis = getClass().getClassLoader().getResourceAsStream(templateName);
        if (fis == null) {
            throw new RuntimeException(templateName + " not found in resources folder!");
        }

        try (XWPFDocument doc = new XWPFDocument(fis);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            replaceInParagraphs(doc.getParagraphs(), formData);

            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        replaceInParagraphs(cell.getParagraphs(), formData);
                    }
                }
            }

            for (XWPFHeader header : doc.getHeaderList()) {
                replaceInParagraphs(header.getParagraphs(), formData);
            }

            for (XWPFFooter footer : doc.getFooterList()) {
                replaceInParagraphs(footer.getParagraphs(), formData);
            }

            doc.write(out);
            return out.toByteArray();
        }
    }

    private void replaceInParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> values) {
        for (XWPFParagraph paragraph : paragraphs) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : values.entrySet()) {
                        text = text.replace(entry.getKey(), entry.getValue());
                    }
                    run.setText(text, 0);
                }
            }
        }
    }
}
