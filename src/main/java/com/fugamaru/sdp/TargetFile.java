package com.fugamaru.sdp;

import com.fugamaru.sdp.enums.FileType;
import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.fusesource.jansi.Ansi.ansi;

public class TargetFile {
    private final Path path;
    private final Tika tika = new Tika();

    public TargetFile(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    /**
     * ファイルタイプを取得する
     *
     * @return ファイルタイプ
     */
    public FileType getFileType() {
        try {
            String mimeType = tika.detect(this.path);

            if (mimeType.contains("image/")) {
                return FileType.PICTURE;
            }

            if (mimeType.contains("video/")) {
                return FileType.VIDEO;
            }

            return FileType.OTHER;
        } catch (IOException e) {
            System.out.println(ansi().fgBrightRed().a("Failed to retrieve file type").reset());
            System.out.println(ansi().a(e.getMessage()).reset());
            return FileType.OTHER;
        }
    }

    /**
     * ファイルをリネームする
     *
     * @param to リネーム後ファイルパス
     */
    public void renameFile(Path to) {
        try {
            Files.move(this.path, to);
            System.out.println(ansi().a("Renamed -> ").fgBrightGreen().a(to).reset());
        } catch (IOException e) {
            System.out.println(ansi().fgBrightRed().a("An error occurred while renaming"));
            System.out.println(ansi().a(e.getMessage()).reset());
        }
    }
}
