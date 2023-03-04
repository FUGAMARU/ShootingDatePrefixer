package com.fugamaru.sdp;

import com.fugamaru.sdp.enums.FileType;
import com.fugamaru.sdp.exceptions.DatetimeReadException;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.lang.System.exit;
import static org.fusesource.jansi.Ansi.*;

public class Main {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        System.out.print(ansi().fgCyan().a("Type the path of the target directory > ").reset());

        Scanner scanner = new Scanner(System.in);
        Path targetDir = Paths.get(scanner.nextLine());
        scanner.close();

        if (!Files.isDirectory(targetDir) || Files.notExists(targetDir)) {
            System.out.println(ansi().fgBrightRed().a("The specified path is not a directory or does not exist.").reset());
            exit(1);
        }

        try (Stream<Path> paths = Files.walk(targetDir)) {
            paths.filter(path -> !Files.isDirectory((path))).forEach(path -> {
                System.out.println("\n" + path);
                TargetFile targetFile = new TargetFile(path);

                FileType fileType = targetFile.getFileType();

                switch (fileType) {
                    case PICTURE, VIDEO -> {
                        try {
                            LocalDate shootingDate = ExifUtil.getShootingDate(targetFile);

                            assert shootingDate != null;
                            String prefix = shootingDate.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
                            Path renamedPath = path.resolveSibling(prefix + "_" + path.getFileName());

                            targetFile.renameFile(renamedPath);
                        } catch (DatetimeReadException e) {
                            System.out.println(ansi().fgBrightRed().a(e.getMessage()).reset());
                        }
                    }

                    case OTHER -> System.out.println(ansi().fgBrightCyan().a("Skipped").reset());
                }
            });
        } catch (IOException e) {
            System.out.println(ansi().fgBrightRed().a("Error occurred while working with the file"));
            System.out.println(ansi().a(e.getMessage()).reset());
        }
    }
}