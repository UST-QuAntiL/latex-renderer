package com.renderLatex.service;

import com.renderLatex.entities.LatexContent;
import com.renderLatex.utils.Utils;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.*;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
public class RenderService {
    private String latexDocClass = "\\documentclass[border=0.50001bp,convert={convertexe={imgconvert},outext=.png}]{standalone} \n";
    private final String docStart = "\\begin{document} \n";
    private final String docEnd = " \\end{document} \n";
    private final String tempDirectory = Utils.concatPaths("", "tmp");


    @Autowired
    public  RenderService() {
        // Recreate temp Folder to clean up all old files when restarting
        File file = new File(tempDirectory);
        try {
            FileUtils.deleteDirectory(file);
            if (file.mkdir()) {
                System.out.println("directory created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String render(LatexContent latexContent){
        final String currentImagePath = Utils.extendPathWithUUID(tempDirectory);
        final String content = latexContent.getContent();
        final String packages = String.join(" ", latexContent.getLatexPackages());

        String output = latexContent.getOutput();
        if (output.equals("fullPdf")) {
            this.latexDocClass = "\\documentclass{article} \n";
            output = "pdf";
        }

        //create and render TexDocument
        createTexDoc(content, packages, currentImagePath);
        renderTex("renderFile.tex",  currentImagePath);

        // render content as Image if requested
        if (output.equals("svg")){
            convertToSvg("renderFile.pdf", currentImagePath);
        } else if (output.equals("jpg") || output.equals("png")){
            convertToImage("renderFile.pdf", currentImagePath, output);
        }

        return Utils.concatPaths(currentImagePath, "renderFile." + output);
    }

    public String renderAsPdf(LatexContent latexContent){
        final String currentImagePath = Utils.extendPathWithUUID(tempDirectory);
        final String content = latexContent.getContent();
        final String packages = String.join(" ", latexContent.getLatexPackages());
        createTexDoc(content, packages, currentImagePath);
        renderTex("renderFile.tex",  currentImagePath);
        return Utils.concatPaths(currentImagePath, "renderFile.pdf");
    }

    public String renderAsSvg(LatexContent latexContent){
        final String currentImagePath = Utils.extendPathWithUUID(tempDirectory);
        final String content = latexContent.getContent();
        final String packages = String.join(" ", latexContent.getLatexPackages());
        createTexDoc(content, packages, currentImagePath);
        renderTex("renderFile.tex", currentImagePath);
        convertToSvg("renderFile.pdf", currentImagePath);
        return Utils.concatPaths(currentImagePath, "renderFile.svg");
    }

    public String renderAsPng(LatexContent latexContent){
        final String currentImagePath = Utils.extendPathWithUUID(tempDirectory);
        final String content = latexContent.getContent();
        final String packages = String.join(" ", latexContent.getLatexPackages());
        createTexDoc(content, packages, currentImagePath);
        renderTex("renderFile.tex", currentImagePath);
        convertToImage("renderFile.pdf", currentImagePath, "png");
        return Utils.concatPaths(currentImagePath, "renderFile.png");
    }

    public String renderAsJpg(LatexContent latexContent){
        final String currentImagePath = Utils.extendPathWithUUID(tempDirectory);
        final String content = latexContent.getContent();
        final String packages = String.join(" ", latexContent.getLatexPackages());
        createTexDoc(content, packages, currentImagePath);
        renderTex("renderFile.tex", currentImagePath);
        convertToImage("renderFile.pdf", currentImagePath, "jpg");
        return Utils.concatPaths(currentImagePath, "renderFile.jpg");
    }

    public String renderAsFullPdf(LatexContent latexContent){
        this.latexDocClass = "\\documentclass{article} \n";
        final String currentImagePath = Utils.extendPathWithUUID(tempDirectory);
        final String content = latexContent.getContent();
        final String packages = String.join(" ", latexContent.getLatexPackages());
        createTexDoc(content, packages, currentImagePath);
        renderTex("renderFile.tex", currentImagePath);
        return Utils.concatPaths(currentImagePath, "renderFile.pdf");
    }


    public void createTexDoc(String content, String packages, String currentImagePath){
        File file = new File(currentImagePath);
        if (!file.exists()){
            System.out.println(file.mkdir());
            System.out.println("directory created");
        }
        try ( FileWriter fileWriter = new FileWriter(currentImagePath + "/renderFile.tex")) {
            fileWriter.write(this.latexDocClass + packages + this.docStart + content + this.docEnd);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void renderTex(String filename, String currentImagePath ){
        try {
            ProcessBuilder processBuilderRenderTex = new ProcessBuilder();
            if(System.getProperty("os.name").startsWith("Windows")){
                processBuilderRenderTex.directory(new File(currentImagePath));
                processBuilderRenderTex.command("cmd.exe", "/c", "pdflatex -halt-on-error -shell-escape " +  filename);
            } else {
                processBuilderRenderTex.directory(new File(currentImagePath));
                processBuilderRenderTex.command("/bin/bash", "-c", "pdflatex -halt-on-error -shell-escape " +filename);
            }

            Process processTex = processBuilderRenderTex.start();

            StringBuilder output = new StringBuilder();

            BufferedReader readerTex = new BufferedReader(
                    new InputStreamReader(processTex.getInputStream()));

            String line;
            while ((line = readerTex.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitValTex = processTex.waitFor();
            if (exitValTex == 0) {
                System.out.println("Successfully rendered Tex");
                System.out.println(output);
            } else {
                System.out.println("Tex could not be rendered");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void convertToSvg(String filename, String currentImagePath ) {
        try (PDDocument document = Loader.loadPDF(new File(currentImagePath+ "/" + filename))) {
            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

            String svgNS = "http://www.w3.org/2000/svg";
            Document svgDocument = domImpl.createDocument(svgNS, "svg", null);
            SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(svgDocument);
            ctx.setEmbeddedFontsOn(true);


            for (int i = 0; i < document.getNumberOfPages(); i++) {
                if (i > 0){
                    // If rendering for more than 1 page is supported adjust filename and return
                    break;
                }

                String svgFName = currentImagePath + "/renderFile.svg";
                (new File(svgFName)).createNewFile();

                SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, false);

                PDFPageable pageable = new PDFPageable(document);
                Printable page = pageable.getPrintable(i);
                PageFormat format = pageable.getPageFormat(i);

                page.print(svgGenerator, format, i);
                svgGenerator.stream(svgFName);
            }
        } catch (IOException | PrinterException e) {
            e.printStackTrace();
        }
    }


    public void convertToImage(String filename, String currentImagePath, String type ){
        try (PDDocument document = Loader.loadPDF(new File(currentImagePath+ "/" + filename)))
            {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                for (int page = 0; page < document.getNumberOfPages(); ++page)
                {
                    if (page > 0){
                        // If rendering for more than 1 page is supported adjust filename and return
                        break;
                    }
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                    ImageIO.write (bim, type.toUpperCase(Locale.ROOT), new File (currentImagePath+"/renderFile." + type.toLowerCase(Locale.ROOT)));
                }
            } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
