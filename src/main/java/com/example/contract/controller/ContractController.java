package com.example.contract.controller;

import com.example.contract.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class ContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping("/")
    public String form() {
        return "form";
    }

    @PostMapping("/generate-contract")
    public void generateContract(@RequestParam Map<String, String> formData, HttpServletResponse response) throws IOException {
        contractService.enrichFormData(formData);
        byte[] document = contractService.generateDocFromTemplate("template.docx", formData);
        sendDocResponse(response, "employment_contract.docx", document);
    }

    @PostMapping("/generate-prikaz")
    public void generatePrikaz(@RequestParam Map<String, String> formData, HttpServletResponse response) throws IOException {
        contractService.enrichFormData(formData);
        byte[] document = contractService.generateDocFromTemplate("Приказ template.docx", formData);
        sendDocResponse(response, "prikaz.docx", document);
    }

    @PostMapping("/generate-nda")
    public void generateNDA(@RequestParam Map<String, String> formData, HttpServletResponse response) throws IOException {
        contractService.enrichFormData(formData);
        byte[] document = contractService.generateDocFromTemplate("NDA template.docx", formData);
        sendDocResponse(response, "nda.docx", document);
    }

    private void sendDocResponse(HttpServletResponse response, String filename, byte[] document) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        try (OutputStream out = response.getOutputStream()) {
            out.write(document);
        }
    }
}
