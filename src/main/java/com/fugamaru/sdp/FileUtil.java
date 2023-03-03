package com.fugamaru.sdp;

import com.fugamaru.sdp.enums.FileType;
import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;

public class FileUtil {
    /**
     * ファイルタイプを取得する
     *
     * @param path ファイルパス
     * @return ファイルタイプ
     */
    public static FileType getFileType(Path path) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(path);

            if (mimeType.contains("image/")) return FileType.PICTURE;
            if (mimeType.contains("video/")) return FileType.VIDEO;

            return FileType.OTHER;
        } catch (IOException e) {
            System.out.println("An error occurred while working with the file");
            return FileType.OTHER;
        }
    }
}
