package com.renderLatex.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renderLatex.entities.LatexContent;
import com.renderLatex.service.RenderService;
import com.renderLatex.annotation.ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@CrossOrigin(allowedHeaders = "*", origins = "*")
@ApiVersion("v1")
public class RenderLatexController {

    private ObjectMapper objectMapper;
    private RenderService renderService;

    @Autowired
    public RenderLatexController(ObjectMapper objectMapper, RenderService renderService) {
        this.objectMapper = objectMapper;
        this.renderService = renderService;
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as png")
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

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as svg")
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

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as zipped svg")
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

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as pdf")
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

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as full pdf")
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

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as format given in the request body")
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
    }


    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render qasm source code as pdf")
    @PostMapping(
            value = "/renderQasmAsPdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public @ResponseBody
    byte[] renderQasmAsPdf(@RequestBody String qasmFile) throws IOException {
        this.renderService.renderQasmAsPdf(qasmFile);

        Path pdfPath = Paths.get("qasm.pdf");
        byte[] pdf = Files.readAllBytes(pdfPath);
        return pdf;
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render qasm source code as svg")
    @PostMapping(
            value = "/renderQasmAsSvg",
            produces = "image/svg+xml"
    )
    public @ResponseBody
    byte[] renderQasmAsSvg(@RequestBody String qasmFile) throws IOException {
        this.renderService.renderQasmAsSvg(qasmFile);

        Path svgPath = Paths.get("content1.svg");
        byte[] svg = Files.readAllBytes(svgPath);
        return svg;
    }

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }
}
