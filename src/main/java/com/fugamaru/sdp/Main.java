package com.fugamaru.sdp;

import com.fugamaru.sdp.enums.FileType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
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
                System.out.println("\n" + path);
                FileType fileType = FileUtil.getFileType(path);

                if (fileType == FileType.OTHER) {
                    System.out.println("Processing of this file will be skipped: " + path);
                    return;
                }

                if (fileType == FileType.PICTURE || fileType == FileType.VIDEO) {
                    Optional<Date> shootingDate = ExifUtil.getShootingDate(fileType, path);
                    if (shootingDate.isPresent()) {
                        String prefix = new SimpleDateFormat("yyyy_MM_dd").format(shootingDate.get());
                        Path renamedPath = path.resolveSibling(prefix + "_" + path.getFileName());

                        try {
                            Files.move(path, renamedPath);
                        } catch (IOException e) {
                            System.out.println("An error occurred while renaming the file: " + path);
                        }
                    }
                }
            });
        } catch (IOException e) {
            System.out.println("An error occurred while working with the file.");
        }
    }
}