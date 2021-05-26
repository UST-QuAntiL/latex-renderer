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
import java.util.ArrayList;
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
    }, description = "Render latex source code as format given in the request body")
    @PostMapping(
            value = "/renderLatex"
    )
    public ResponseEntity<byte[]> renderLatex(@RequestBody LatexContent latexContent) throws IOException {
        try{
            latexContent = handleContentRendering(latexContent);
            HttpHeaders headers = new HttpHeaders();
            CacheControl cacheControl = CacheControl.maxAge(1000, TimeUnit.DAYS).cachePublic();
            headers.setCacheControl(cacheControl);
            headers.setContentType(latexContent.getContentType());
            Utils.removeDirectory(latexContent.getFilepath());
            return new ResponseEntity<>(latexContent.getReturnFile(), headers, HttpStatus.OK);
        }catch (IOException e) {
            // Rendering failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "500"),
    }, description = "Render latex source code as format given in url")
    @GetMapping(
            value = "/renderLatex"
    )
    public ResponseEntity<byte[]> renderLatexGet(@RequestParam(value = "packages", required = false) List<String> packages, @RequestParam("content") String content, @RequestParam(value = "output", required = false) String output) {
        try {
            if (output == null){
                output = "svg";
            }
            if (packages == null){
                packages = new ArrayList<String>();
            }
            for(int i = 0; i < packages.size(); i++){
                packages.set(i, java.net.URLDecoder.decode(packages.get(i), StandardCharsets.UTF_8.name()));
            }
            content = java.net.URLDecoder.decode(content, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Could not Decode URL
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        LatexContent latexContent= new LatexContent(content, packages, output);
        try{
            latexContent = handleContentRendering(latexContent);
            HttpHeaders headers = new HttpHeaders();
            // Enable long-term caching
            CacheControl cacheControl = CacheControl.maxAge(1000, TimeUnit.DAYS).cachePublic();
            headers.setCacheControl(cacheControl);
            headers.setContentType(latexContent.getContentType());
            Utils.removeDirectory(latexContent.getFilepath());
            return new ResponseEntity<>(latexContent.getReturnFile(), headers, HttpStatus.OK);
        }catch (IOException e) {
            // Rendering failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @RequestMapping("/")
    public String home() {
        return "Latex-Renderer is running";
    }

    private LatexContent handleContentRendering(LatexContent latexContent) throws IOException {
        String filepath ="";
        filepath = this.renderService.render(latexContent);
        Path path = Paths.get(filepath);
        latexContent.setReturnFile(Files.readAllBytes(path));
        switch (latexContent.getOutput()){
            case "pdf":
                latexContent.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "png":
                latexContent.setContentType(MediaType.IMAGE_PNG);
                break;
            case "jpg":
                latexContent.setContentType(MediaType.IMAGE_JPEG);
                break;
            case "fullPdf":
                latexContent.setContentType(MediaType.APPLICATION_PDF);
                break;
            case "svg":
                latexContent.setContentType(MediaType.valueOf("image/svg+xml"));
                break;
        }
        latexContent.setFilepath(filepath);
        return latexContent;
    }
}
