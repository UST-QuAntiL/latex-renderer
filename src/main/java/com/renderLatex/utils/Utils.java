package com.renderLatex.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class Utils {
    private Utils() {}
    /*
    Delete the directory and all included files
     */
    public static void removeDirectory(String filepath) throws IOException {
        filepath = Paths.get(filepath).getParent().toString();
        System.out.println("Deleting imagesource directory" + filepath);
        FileUtils.deleteDirectory(new File(filepath));
    }

    public static String concatPaths(String first, String second){
        return Paths.get(first).resolve(second).toAbsolutePath().toString();
    }

    public static String createPathWithUUID(String path){
        final String uuid = UUID.randomUUID().toString();
        final String uuidPath = Paths.get(path).resolve(uuid).toAbsolutePath().toString();
        File file = new File(uuidPath);
        if (!file.exists()){
            System.out.println(file.mkdir());
            System.out.println("directory created");
        }
        return uuidPath;
    }
}
