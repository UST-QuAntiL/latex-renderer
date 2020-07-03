package com.renderLatex;

import com.renderLatex.rest.RenderLatexController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
@EnableWebMvc
@Slf4j
public class RenderLatex implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(RenderLatex.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("RenderLatex is up");
    }

}