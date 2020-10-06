package com.renderLatex.service;

import com.renderLatex.entities.LatexContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;

@Service
public class RenderService {
    private String latexDocClass = "\\documentclass[border=0.50001bp,convert={convertexe={imgconvert},outext=.png}]{standalone} \n";
    private final String docStart = "\\begin{document} \n";
    private final String docEnd = " \\end{document} \n";
    private String packages = "";
    private String content = "";

    @Autowired
    public RenderService(){

    }

    public void renderAsPdf(LatexContent latexContent){
        this.content = latexContent.getContent();
        this.packages = "";
        latexContent.getLatexPackages().forEach(item -> this.packages += (item + " \n "));
        createTexDoc();
        renderTex("renderFile.tex");
    }

    public void renderAsSvg(LatexContent latexContent, boolean zipped){
        this.content = latexContent.getContent();
        this.packages = "";
        latexContent.getLatexPackages().forEach(item -> this.packages += (item + " \n "));
        createTexDoc();
        renderTex("renderFile.tex");
        convertToSvg(zipped,"renderFile.pdf");
    }

    public void renderAsPng(LatexContent latexContent){
        this.content = latexContent.getContent();
        this.packages = "";
        latexContent.getLatexPackages().forEach(item -> this.packages += (item + " \n "));
        createTexDoc();
        renderTex("renderFile.tex");
        convertToPng("renderFile.pdf");
    }

    public void createTexDoc(){
        try ( FileWriter fileWriter = new FileWriter("renderFile.tex")) {
            fileWriter.write(this.latexDocClass + this.packages + this.docStart + this.content + this.docEnd);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void renderAsFullPdf(LatexContent latexContent){
        this.latexDocClass = "\\documentclass{article} \n";
        this.packages = "";
        this.content = latexContent.getContent();
        latexContent.getLatexPackages().forEach(item -> this.packages += (item + " \n "));
        createTexDoc();
        renderTex("renderFile.tex");
    }

    public void renderQasmAsPdf(String qasmContent){
        createQasmFile(qasmContent);
        convertToQasmTex();
        renderTex("qasm.tex");
    }

    public void renderQasmAsSvg(String qasmContent){
        createQasmFile(qasmContent);
        convertToQasmTex();
        renderTex("qasm.tex");
        convertToSvg(false, "qasm.pdf");
    }

    public void renderTex(String filename){
        try {
            ProcessBuilder processBuilderRenderTex = new ProcessBuilder();
            if(System.getProperty("os.name").startsWith("Windows")){
                processBuilderRenderTex.command("cmd.exe", "/c", "pdflatex -shell-escape " + filename);
            } else {
                processBuilderRenderTex.command("/bin/bash", "-c", "pdflatex -shell-escape " + filename);
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

    public void convertToSvg(boolean zipped, String filename){
        try {
            String userDirectory = Paths.get("").toAbsolutePath().toString();

            ProcessBuilder processBuilderRenderSvg = new ProcessBuilder();
//            processBuilderRenderSvg.command("cmd.exe", "/c", "inkscape --without-gui --file=renderFile.pdf --export-plain-svg=renderFile.svg"); works but svg not pretty

            if(System.getProperty("os.name").startsWith("Windows")) {
                if (zipped) {
                    processBuilderRenderSvg.command("cmd.exe", "/c", "pdf2svg2.bat \"" + userDirectory + "\\" + filename + "\" \"" + userDirectory + "\" -z");
                } else {
                    processBuilderRenderSvg.command("cmd.exe", "/c", "pdf2svg2.bat \"" + userDirectory + "\\" + filename + "\" \"" + userDirectory + "\"");
                }
            } else {
                if (zipped) {
                    processBuilderRenderSvg.command("/bin/bash", "-c",  "pdf2svg "+ filename + " " + "content1.svg -z" );
                } else {
                    processBuilderRenderSvg.command("/bin/bash", "-c",  "pdf2svg "+ filename + " " + "content1.svg");
                }
            }
            Process processSvg = processBuilderRenderSvg.start();

            BufferedReader readerSvg = new BufferedReader(
                    new InputStreamReader(processSvg.getInputStream()));

            StringBuilder outputSvg = new StringBuilder();
            String lineSvg;
            while ((lineSvg = readerSvg.readLine()) != null) {
                outputSvg.append(lineSvg + "\n");
            }
            int exitValSvg = processSvg.waitFor();
            if (exitValSvg == 0) {
                System.out.println("Successfully rendered Svg");
                System.out.println(outputSvg);
            } else {
                System.out.println("Svg could not be rendered");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void createQasmFile(String qasmContent){
        try ( FileWriter fileWriter = new FileWriter("test.qasm")) {
            fileWriter.write(qasmContent);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void convertToQasmTex(){
        try {
            ProcessBuilder processBuilderRender = new ProcessBuilder();
            if(System.getProperty("os.name").startsWith("Windows")) {
                processBuilderRender.command("cmd.exe", "/c", "python qasm2tex.py test.qasm");
            } else {
                //TODO: python dependencies in Docker
                processBuilderRender.command("/bin/bash", "-c", "python qasm2tex.py test.qasm");
            }
            Process process = processBuilderRender.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Successfully rendered Tex");
                try ( FileWriter fileWriter = new FileWriter("qasm.tex")) {
                    fileWriter.write(String.valueOf(output));
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println(e.toString());
                    e.printStackTrace();
                }
                System.out.println(output);
            } else {
                System.out.println("Qasm could not be rendered");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void convertToPng(String filename){
        try {
            ProcessBuilder processBuilderRender = new ProcessBuilder();

            if(System.getProperty("os.name").startsWith("Windows")) {
                //should be done via imagemagick integration
            } else {
                processBuilderRender.command("/bin/bash", "-c",  "pdftoppm -png "+ filename + " > renderFile.png");
            }
            Process process = processBuilderRender.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Successfully rendered PNG");
                System.out.println(output);
            } else {
                System.out.println("PNG could not be rendered");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
