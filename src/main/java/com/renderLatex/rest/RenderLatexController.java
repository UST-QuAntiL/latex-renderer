package com.renderLatex.rest;


import com.renderLatex.entities.LatexContent;
import com.renderLatex.service.RenderService;
import com.renderLatex.annotation.ApiVersion;
import com.renderLatex.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@ApiVersion("v1")
public class RenderLatexController {

    private RenderService renderService;

    @Autowired
    public RenderLatexController(RenderService renderService) {
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
        String filepath = this.renderService.renderAsPng(latexContent);
        Path pngPath = Paths.get(filepath);
        byte[] png = Files.readAllBytes(pngPath);
        Utils.removeDirectory(filepath);
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
        String filepath = this.renderService.renderAsSvg(latexContent);
        Path svgPath = Paths.get(filepath);
        byte[] svg = Files.readAllBytes(svgPath);
        Utils.removeDirectory(filepath);
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
        String filepath =this.renderService.renderAsPdf(latexContent);
        Path pdfPath = Paths.get(filepath);
        byte[] pdf = Files.readAllBytes(pdfPath);
        Utils.removeDirectory(filepath);
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
        String filepath = this.renderService.renderAsFullPdf(latexContent);
        Path pdfPath = Paths.get(filepath);
        byte[] pdf = Files.readAllBytes(pdfPath);
        Utils.removeDirectory(filepath);
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
        String filepath ="";
        HttpHeaders headers = new HttpHeaders();
        switch (latexContent.getOutput()){
            case "pdf":
                filepath = this.renderService.renderAsPdf(latexContent);
                Path pdfPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(pdfPath);
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "png":
                filepath = this.renderService.renderAsPng(latexContent);
                Path pngPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(pngPath);
                headers.setContentType(MediaType.IMAGE_PNG);
                break;
            case "jpg":
                filepath = this.renderService.renderAsJpg(latexContent);
                Path jpgPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(jpgPath);
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;
            case "fullPdf":
                filepath = this.renderService.renderAsFullPdf(latexContent);
                Path fullPdfPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(fullPdfPath);
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "svg":
                filepath = this.renderService.renderAsSvg(latexContent);
                Path svgPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(svgPath);
                headers.setContentType(MediaType.valueOf("image/svg+xml"));
                break;
        }
        Utils.removeDirectory(filepath);
        return new ResponseEntity<>(renderedFile, headers, HttpStatus.OK);
    }


    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as format given in url")
    @GetMapping(
            value = "/renderLatex"
    )
    public ResponseEntity<byte[]> renderLatexGet(@RequestParam("packages") List<String> packages, @RequestParam("content") String content, @RequestParam("output") String output) throws IOException {
        try {
            for(int i = 0; i < packages.size(); i++){
                packages.set(i, java.net.URLDecoder.decode(packages.get(i), StandardCharsets.UTF_8.name()));
            }
            content = java.net.URLDecoder.decode(content, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LatexContent latexContent= new LatexContent(content, packages, output);
        byte[] renderedFile = null;
        String filepath ="";
        HttpHeaders headers = new HttpHeaders();
        switch (latexContent.getOutput()){
            case "pdf":
                filepath = this.renderService.renderAsPdf(latexContent);
                Path pdfPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(pdfPath);
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "png":
                filepath = this.renderService.renderAsPng(latexContent);
                Path pngPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(pngPath);
                headers.setContentType(MediaType.IMAGE_PNG);
                break;
            case "jpg":
                filepath = this.renderService.renderAsJpg(latexContent);
                Path jpgPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(jpgPath);
                headers.setContentType(MediaType.IMAGE_JPEG);
                break;
            case "fullPdf":
                filepath = this.renderService.renderAsFullPdf(latexContent);
                Path fullPdfPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(fullPdfPath);
                headers.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "svg":
                filepath = this.renderService.renderAsSvg(latexContent);
                Path svgPath = Paths.get(filepath);
                renderedFile = Files.readAllBytes(svgPath);
                headers.setContentType(MediaType.valueOf("image/svg+xml"));
                break;
        }
        CacheControl cacheControl = CacheControl.maxAge(1000, TimeUnit.DAYS).cachePublic();
        headers.setCacheControl(cacheControl);
        Utils.removeDirectory(filepath);
        return new ResponseEntity<>(renderedFile, headers, HttpStatus.OK);
    }


    @RequestMapping("/")
    public String home() {
        return "Latex-Renderer is running";
    }
}
