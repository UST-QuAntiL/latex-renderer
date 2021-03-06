package com.renderLatex.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renderLatex.entities.LatexContent;
import com.renderLatex.util.IntegrationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void renderLatexSucceeds() throws Exception {
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
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatexAsPdf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn();

    }

    @Test
    public void renderLatexAsPngSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatexAsPng")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn();

    }

    @Test
    public void renderLatexAsSvgSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatexAsSvg")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("image/svg+xml")))
                .andReturn();
    }

    @Test
    public void renderLatexAsSvgzSucceeds() throws Exception {
        LatexContent latexContent = this.integrationTestHelper.getDefaultLatexContent();
        System.out.print(this.objectMapper.writeValueAsString(latexContent));
        System.out.println(this.objectMapper);
        MvcResult postResult = this.mockMvc.perform(post("/renderLatexAsSvgz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(latexContent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("image/svg+xml")))
                .andReturn();
    }

}




