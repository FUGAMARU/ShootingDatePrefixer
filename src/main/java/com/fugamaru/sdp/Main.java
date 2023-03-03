package com.fugamaru.sdp;

import com.fugamaru.sdp.enums.FileType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        System.out.print("Type the path of the target directory > ");

        Scanner scanner = new Scanner(System.in);
        Path targetDir = Paths.get(scanner.nextLine());
        scanner.close();

        if (!Files.isDirectory(targetDir) || Files.notExists(targetDir)) {
            System.out.println("The specified path is not a directory or does not exist.");
            exit(1);
        }

        try (Stream<Path> paths = Files.walk(targetDir)) {
            paths.filter(path -> !Files.isDirectory((path))).forEach(path -> {
                System.out.println(path);
                FileType fileType = FileUtil.getFileType(path);
                System.out.println(fileType.name());
            });
        } catch (IOException e) {
            System.out.println("An error occurred while working with the file");
        }
    }
}