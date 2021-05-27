package com.renderLatex.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
public class LatexContent {
    private String content = "";
    private List<String> latexPackages = new ArrayList<>();
    private String output = "svg";
    private MediaType contentType;
    private byte[] returnFile;
    private String filepath;

    public LatexContent(String content, List<String> latexPackages,String output){
        this.content = content;
        this.latexPackages = latexPackages;
        this.output = output;
    }
}
