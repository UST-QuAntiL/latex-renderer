package com.renderLatex.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renderLatex.entities.LatexContent;
import com.renderLatex.service.RenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class RenderLatexController {

    private ObjectMapper objectMapper;
    private RenderService renderService;

    @Autowired
    public RenderLatexController(ObjectMapper objectMapper, RenderService renderService) {
        this.objectMapper = objectMapper;
        this.renderService = renderService;
    }

    @PostMapping(
            value = "/renderLatexAsPng",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public @ResponseBody
    byte[] renderLatexAsPng(@RequestBody LatexContent latexContent) throws IOException {
        this.renderService.renderAsPng(latexContent);

        Path pngPath = Paths.get("renderFile.png");
        byte[] png = Files.readAllBytes(pngPath);
        return png;
    }

    @PostMapping(
            value = "/renderLatexAsSvg",
            produces = "image/svg+xml"
    )
    public @ResponseBody
    byte[] renderLatexAsSvg(@RequestBody LatexContent latexContent) throws IOException {
        this.renderService.renderAsSvg(latexContent, false);

        Path svgPath = Paths.get("content1.svg");
        byte[] svg = Files.readAllBytes(svgPath);
        return svg;
    }

    @PostMapping(
            value = "/renderLatexAsSvgz",
            produces = "image/svg+xml"
    )
    public @ResponseBody
    byte[] renderLatexAsSvgz(@RequestBody LatexContent latexContent) throws IOException {
        this.renderService.renderAsSvg(latexContent, true);

        Path svgPath = Paths.get("content1.svgz");
        byte[] svg = Files.readAllBytes(svgPath);
        return svg;
    }

    @PostMapping(
            value = "/renderLatexAsPdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public @ResponseBody
    byte[] renderLatexAsPdf(@RequestBody LatexContent latexContent) throws IOException {
        this.renderService.renderAsPdf(latexContent);

        Path pdfPath = Paths.get("renderFile.pdf");
        byte[] pdf = Files.readAllBytes(pdfPath);
        return pdf;
    }

    @PostMapping(
            value = "/renderLatexAsFullPdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public @ResponseBody
    byte[] renderLatexAsFullPdf(@RequestBody LatexContent latexContent) throws IOException {
        this.renderService.renderAsFullPdf(latexContent);

        Path pdfPath = Paths.get("renderFile.pdf");
        byte[] pdf = Files.readAllBytes(pdfPath);
        return pdf;
    }

    @PostMapping(
            value = "/renderLatex"

    )
    public ResponseEntity<byte[]> renderLatex(@RequestBody LatexContent latexContent) throws IOException {
        byte[] renderedFile = null;
        HttpHeaders headers = new HttpHeaders();
        switch (latexContent.getOutput()){
            case "pdf":
                this.renderService.renderAsPdf(latexContent);
                Path pdfPath = Paths.get("renderFile.pdf");
                renderedFile = Files.readAllBytes(pdfPath);
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "png":
                this.renderService.renderAsPng(latexContent);
                Path pngPath = Paths.get("renderFile.png");
                renderedFile = Files.readAllBytes(pngPath);
                headers.setContentType(MediaType.IMAGE_PNG);
                break;
            case "fullPdf":
                this.renderService.renderAsFullPdf(latexContent);
                Path fullPdfPath = Paths.get("renderFile.pdf");
                renderedFile = Files.readAllBytes(fullPdfPath);
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "svg":
                this.renderService.renderAsSvg(latexContent, false);
                Path svgPath = Paths.get("content1.svg");
                renderedFile = Files.readAllBytes(svgPath);
                headers.setContentType(MediaType.valueOf("image/svg+xml"));
                break;
            case "svgz":
                this.renderService.renderAsSvg(latexContent, true);
                Path svgzPath = Paths.get("content1.svgz");
                renderedFile = Files.readAllBytes(svgzPath);
                headers.setContentType(MediaType.valueOf("image/svg+xml"));
                break;
        }

        return new ResponseEntity<>(renderedFile, headers, HttpStatus.OK);
//        return pdf;
    }
}