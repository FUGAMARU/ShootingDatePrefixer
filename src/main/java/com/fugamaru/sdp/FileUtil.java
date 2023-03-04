package com.fugamaru.sdp;

import com.fugamaru.sdp.enums.FileType;
import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.fusesource.jansi.Ansi.ansi;

public class FileUtil {
    private final Path path;
    private final Tika tika = new Tika();

    public FileUtil(Path path) {
        this.path = path;
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
            System.out.print(ansi().a("Renamed -> ").fgBrightGreen().a(to).reset());
        } catch (IOException e) {
            System.out.println(ansi().fgBrightRed().a("An error occurred while renaming").reset());
        }
    }
}
