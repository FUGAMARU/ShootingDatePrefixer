package com.fugamaru.sdp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.fugamaru.sdp.enums.FileType;
import com.fugamaru.sdp.enums.TagType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ExifUtil {
    /**
     * 撮影日時を取得する
     *
     * @param fileType ファイルタイプ
     * @param path     ファイルパス
     * @return 撮影日時 (撮影日時のメタデーターが存在しなかったりエラーが発生した場合はnull)
     */
    public static Optional<Date> getShootingDate(FileType fileType, Path path) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(path.toFile());
            int tagType = TagType.getTagTypeFromFileType(fileType);

            List<Directory> dirs = new ArrayList<>();
            metadata.getDirectories().iterator().forEachRemaining(dirs::add);

            Optional<Directory> validDir = dirs.stream().filter(dir -> dir.containsTag(tagType)).findFirst();

            return validDir.map(directory -> directory.getDate(TagType.getTagTypeFromFileType(fileType), TimeZone.getDefault()));
        } catch (ImageProcessingException | IOException e) {
            System.out.println("An error occurred while retrieving the shooting date and time.");
            return Optional.empty();
        }
    }
}
