package com.fugamaru.sdp;

import com.fugamaru.sdp.beans.ExecutionArgumentsBean;
import com.fugamaru.sdp.exceptions.DatetimeReadException;
import com.fugamaru.sdp.exceptions.UnsupportedFileTypeException;
import org.fusesource.jansi.AnsiConsole;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static java.lang.System.exit;
import static org.fusesource.jansi.Ansi.*;

public class Main {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        Path targetDir = null;
        try {
            targetDir = getValidPath(args);
        } catch (IllegalArgumentException e) {
            System.err.println(ansi().fgBrightRed().a(e.getMessage()).reset());
            exit(1);
        }

        try (Stream<Path> paths = Files.walk(targetDir)) {
            paths.filter(path -> !Files.isDirectory((path))).forEach(path -> {
                System.out.println("\n" + path);
                TargetFile targetFile = new TargetFile(path);

                try {
                    LocalDate shootingDate = ExifUtil.getShootingDate(targetFile);
                    assert shootingDate != null;

                    String prefix = shootingDate.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
                    Path renamedPath = path.resolveSibling(prefix + "_" + path.getFileName());

                    targetFile.renameFile(renamedPath);
                } catch (DatetimeReadException | UnsupportedFileTypeException e) {
                    System.err.println(ansi().fgBrightRed().a(e.getMessage()).reset());
                }
            });
        } catch (IOException e) {
            System.err.println(ansi().fgBrightRed().a("Error occurred while working with the file"));
            System.err.println(ansi().a(e.getMessage()).reset());
        }
    }

    /**
     * 有効な処理対象フォルダーのパスを取得する
     *
     * @return 処理対象フォルダーのパス
     * @throws IllegalArgumentException IllegalArgumentException
     */
    private static Path getValidPath(String[] args) throws IllegalArgumentException {
        ExecutionArgumentsBean arguments = new ExecutionArgumentsBean();
        CmdLineParser parser = new CmdLineParser(arguments);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (arguments.getArguments().size() != 1) {
            throw new IllegalArgumentException("Incorrect number of execution arguments.");
        }

        Path specifiedPath = Paths.get(arguments.getArguments().get(0));

        if (!Files.isDirectory(specifiedPath) || Files.notExists(specifiedPath)) {
            throw new IllegalArgumentException("The specified path is not a directory or does not exist.");
        }

        return specifiedPath;
    }
}