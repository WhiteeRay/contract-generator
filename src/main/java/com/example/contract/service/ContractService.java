package com.example.contract.service;

import com.example.contract.numtostring.KazakhNumberToWords;
import com.example.contract.numtostring.RussianNumberToWords;
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
        formData.put("monthkz-1", today.minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, new Locale("kk")));
        formData.put("monthru-1", today.minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, new Locale("ru")));
        formData.put("YYYY", String.valueOf(today.getYear()));
        formData.put("DD", String.format("%02d", today.getDayOfMonth()));
        formData.put("monthkz", today.getMonth().getDisplayName(TextStyle.FULL, new Locale("kk")));
        formData.put("monthru", today.getMonth().getDisplayName(TextStyle.FULL, new Locale("ru")));
        String salaryNum = formData.getOrDefault("salaryNum", "0");
        long salary = Long.parseLong(salaryNum);
        String salaryWordkz = KazakhNumberToWords.convert(salary);
        String salaryWordru = RussianNumberToWords.convert(salary);

        formData.put("salaryNum", salaryNum);
        formData.put("salaryWordkz", salaryWordkz);
        formData.put("salaryWordru", salaryWordru);



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
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs == null || runs.isEmpty()) continue;


            StringBuilder fullTextBuilder = new StringBuilder();
            for (XWPFRun run : runs) {
                String text = run.getText(0);
                if (text != null) {
                    fullTextBuilder.append(text);
                }
            }

            String replacedText = fullTextBuilder.toString();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                replacedText = replacedText.replace(entry.getKey(), entry.getValue());
            }


            for (int i = runs.size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }


            XWPFRun newRun = paragraph.createRun();
            newRun.setText(replacedText);
        }
    }

}
