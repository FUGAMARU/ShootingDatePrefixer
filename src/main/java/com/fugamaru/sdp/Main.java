package com.fugamaru.sdp;

import com.fugamaru.sdp.enums.FileType;
import org.fusesource.jansi.AnsiConsole;

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
                FileUtil file = new FileUtil(path);

                FileType fileType = file.getFileType();

                if (fileType == FileType.OTHER) {
                    System.out.print(ansi().fgBrightCyan().a("Skipped").reset());
                    return;
                }

                if (fileType == FileType.PICTURE || fileType == FileType.VIDEO) {
                    Optional<Date> shootingDate = ExifUtil.getShootingDate(fileType, path);
                    
                    if (shootingDate.isPresent()) {
                        String prefix = new SimpleDateFormat("yyyy_MM_dd").format(shootingDate.get());
                        Path renamedPath = path.resolveSibling(prefix + "_" + path.getFileName());

                        file.renameFile(renamedPath);
                    }
                }
            });
        } catch (IOException e) {
            System.out.println(ansi().fgBrightRed().a("Error occurred while working with the file").reset());
        }
    }
}