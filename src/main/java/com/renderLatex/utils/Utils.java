package com.renderLatex.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Utils {
    private Utils() {}
    /*
    Remove the filename from the filepath (always 15 characters in Latex-Renderer)
    Then delete the directory and all included files
     */
    public static void removeDirectory(String filepath) throws IOException {
        filepath = filepath.substring(0, filepath.length() - 15);
        System.out.println("Deleting imagesource directory" + filepath);
        FileUtils.deleteDirectory(new File(filepath));
    }
}
