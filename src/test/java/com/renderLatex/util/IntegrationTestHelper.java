package com.renderLatex.util;

import com.renderLatex.entities.LatexContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntegrationTestHelper {

    @Autowired
    private MockMvc mockMvc;

    public LatexContent getDefaultLatexContent() {
        String content = "\\begin{quantikz} " +
                "\\lstick{$\\ket{0}$} & \\gate{H} & \\ctrl{1} & \\gate{U} & \\ctrl{1} & \\swap{2} & \\ctrl{1} & \\qw \\\\ " +
                "\\lstick{$\\ket{0}$} & \\gate{H} & \\targ{} & \\octrl{-1} & \\control{} & \\qw & \\octrl{1} & \\qw \\\\ " +
                "&&&&&\\targX{} & \\gate{U} & \\qw " +
                "\\end{quantikz}";
        List<String> settings = new ArrayList<>();
        settings.add("\\usepackage{tikz} \n");
        settings.add("\\usetikzlibrary{quantikz} \n");

        String output = "svg";
        return new LatexContent(content, settings, output);
    }
}
