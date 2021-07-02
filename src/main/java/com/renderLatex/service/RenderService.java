package com.renderLatex.service;

import com.renderLatex.entities.LatexContent;
import com.renderLatex.utils.Utils;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.Locale;


@Service
public class RenderService {
    // TODO varwidth should be optional parameter?
    private String latexDocClass = "\\documentclass[margin=7pt, varwidth]{standalone} \n";
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
        //create new Folder for renderingProcess and get its Path
        final String currentRenderPath = Utils.createPathWithUUID(tempDirectory);
        String content = latexContent.getContent();
        content = content.replaceAll("(\\t(?!([^\\w]| )))","\\\\t");
        String packages = String.join(" ", latexContent.getLatexPackages());

        String output = latexContent.getOutput();
        if (output.equals("fullPdf")) {
            this.latexDocClass = "\\documentclass{article} \n";
            output = "pdf";
        }

        //create and render TexDocument
        createTexDoc(content, packages, currentRenderPath);
        renderTex("renderFile.tex",  currentRenderPath);

        // render content as Image if requested
        if (output.equals("svg")){
            convertToSvg("renderFile.pdf", currentRenderPath);
        } else if (output.equals("jpg") || output.equals("png")){
            convertToImage("renderFile.pdf", currentRenderPath, output);
        }

        return Utils.concatPaths(currentRenderPath, "renderFile." + output);
    }

    public void createTexDoc(String content, String packages, String currentRenderPath){
        try ( FileWriter fileWriter = new FileWriter(Utils.concatPaths(currentRenderPath, "renderFile.tex"))) {
            fileWriter.write(this.latexDocClass + packages + this.docStart + content + this.docEnd);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void renderTex(String filename, String currentRenderPath ){
        try {
            ProcessBuilder processBuilderRenderTex = new ProcessBuilder();
            processBuilderRenderTex.directory(new File(currentRenderPath));
            if(System.getProperty("os.name").startsWith("Windows")){
                processBuilderRenderTex.command("cmd.exe", "/c", "pdflatex -halt-on-error -shell-escape " + filename);
            } else {
                processBuilderRenderTex.command("/bin/bash", "-c", "pdflatex -halt-on-error -shell-escape " + filename);
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

    public void convertToSvg(String filename, String currentRenderPath ) {
        try (PDDocument document = Loader.loadPDF(new File( Utils.concatPaths(currentRenderPath, filename)))) {
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

                String svgFName = Utils.concatPaths(currentRenderPath, "renderFile.svg");
                (new File(svgFName)).createNewFile();

                SVGGraphics2D svgGenerator = new SVGGraphics2D(ctx, false);

                PDFPageable pageable = new PDFPageable(document);
                Printable page = pageable.getPrintable(i);
                PageFormat format = pageable.getPageFormat(i);

                page.print(svgGenerator, format, i);
                svgGenerator.stream(svgFName);

                SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(
                        XMLResourceDescriptor.getXMLParserClassName());

                File file = new File(svgFName);
                InputStream is = new FileInputStream(file);

                Document document2 = factory.createDocument(
                        file.toURI().toURL().toString(), is);
                UserAgent agent = new UserAgentAdapter();
                DocumentLoader loader= new DocumentLoader(agent);
                BridgeContext context = new BridgeContext(agent, loader);
                context.setDynamic(true);
                GVTBuilder builder= new GVTBuilder();
                GraphicsNode root= builder.build(context, document2);

                Element svgRoot = document2.getDocumentElement();
                double width = root.getPrimitiveBounds().getWidth();
                double height = root.getPrimitiveBounds().getHeight();
                svgRoot.setAttributeNS(null, "width", String.valueOf(width*2.25));
                svgRoot.setAttributeNS(null, "height", String.valueOf(height*2.25));
                svgRoot.setAttributeNS(null, "viewBox", "0 0 " + String.valueOf(width) + " " + String.valueOf(height));
                saveSvgDocumentToFile((SVGDocument) document2, file);
            }
        } catch (IOException | PrinterException e) {
            e.printStackTrace();
        }
    }

    public void convertToImage(String filename, String currentRenderPath, String output ){
        try (PDDocument document = Loader.loadPDF(new File(Utils.concatPaths(currentRenderPath, filename))))
            {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                for (int page = 0; page < document.getNumberOfPages(); ++page)
                {
                    if (page > 0){
                        // If rendering for more than 1 page is supported adjust filename and return
                        break;
                    }
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                    ImageIO.write (bim, output.toUpperCase(Locale.ROOT), new File (Utils.concatPaths(currentRenderPath, "renderFile." + output)));
                }
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSvgDocumentToFile(SVGDocument document, File file)
            throws FileNotFoundException, IOException {
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        try (Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
            svgGenerator.stream(document.getDocumentElement(), out);
        }
    }
}
