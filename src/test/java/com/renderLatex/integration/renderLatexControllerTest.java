package com.renderLatex.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renderLatex.entities.LatexContent;
import com.renderLatex.util.IntegrationTestHelper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class renderLatexControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IntegrationTestHelper integrationTestHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void renderLatexAsSvgSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("image/svg+xml")))
                .andReturn();

    }

    @Test
    public void renderLatexAsPdfSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        latexContent.setOutput("pdf");
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn();

    }

    @Test
    public void renderLatexAsFullPdfSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        latexContent.setOutput("fullPdf");
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn();

    }

    @Test
    public void renderLatexAsPngSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        latexContent.setOutput("png");
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn();

    }

    @Test
    public void renderLatexAsJpgSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        latexContent.setOutput("jpg");
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andReturn();
    }

    @Test
    public void renderViaGetSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        this.mockMvc.perform(get("/renderLatex")
                .param("output", latexContent.getOutput())
                .param("content", java.net.URLEncoder.encode(latexContent.getContent(), StandardCharsets.UTF_8.name()))
                .param("packages", java.net.URLEncoder.encode(latexContent.getLatexPackages().get(0), StandardCharsets.UTF_8.name()))
                .param("packages", java.net.URLEncoder.encode(latexContent.getLatexPackages().get(1), StandardCharsets.UTF_8.name())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("image/svg+xml")));
    }

    @Test
    public void renderViaGetOnlyRequiredParamsSucceeds() throws Exception {
        this.mockMvc.perform(get("/renderLatex")
                .param("content", java.net.URLEncoder.encode("$/pi$", StandardCharsets.UTF_8.name())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("image/svg+xml")));
    }
}




