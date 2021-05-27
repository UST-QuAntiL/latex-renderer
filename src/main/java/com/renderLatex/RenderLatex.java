package com.renderLatex;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
@EnableWebMvc
@Slf4j
@OpenAPIDefinition(info = @Info(
        title = "latex-renderer",
        version = "1.1.0",
        description = "Platform for Sharing Quantum Software",
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
        contact = @Contact(url = "https://github.com/UST-QuAntiL/latex-renderer", name = "GitHub Repository")))
public class RenderLatex implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(RenderLatex.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("RenderLatex is up");
    }

}
