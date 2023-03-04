package com.fugamaru.sdp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.fugamaru.sdp.enums.FileType;
import com.fugamaru.sdp.enums.TagType;
import com.fugamaru.sdp.exceptions.DatetimeReadException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static com.fugamaru.sdp.enums.FileType.PICTURE;
import static com.fugamaru.sdp.enums.FileType.VIDEO;

public class ExifUtil {
    /**
     * 画像・動画ファイルの撮影日時を取得する
     *
     * @param file 対象ファイル
     * @return 撮影日時
     * @throws DatetimeReadException DatetimeReadException
     */
    public static Date getShootingDate(FileUtil file) throws DatetimeReadException {
        String exceptionMessage = "No metadata existed for the shooting date: " + file.getPath(); //撮影日時に関するメタデーターが存在しない場合に独自例外に投げるメッセージ

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getPath().toFile());

            List<Directory> dirs = new ArrayList<>();
            metadata.getDirectories().iterator().forEachRemaining(dirs::add);

            switch (file.getFileType()) {
                case PICTURE -> {
                    Optional<Directory> validDir = dirs.stream().filter(dir -> dir.containsTag(TagType.PICTURE_CREATION_DATETIME.getTagType()) || dir.containsTag(TagType.PICTURE_ORIGINAL_CREATION_DATETIME.getTagType())).findFirst();

                    if (validDir.isEmpty()) {
                        throw new DatetimeReadException(exceptionMessage);
                    }

                    Optional<Date> shootingDate1 = Optional.ofNullable(validDir.get().getDate(TagType.PICTURE_CREATION_DATETIME.getTagType(), TimeZone.getDefault()));
                    Optional<Date> shootingDate2 = Optional.ofNullable(validDir.get().getDate(TagType.PICTURE_ORIGINAL_CREATION_DATETIME.getTagType(), TimeZone.getDefault()));
                    
                    return shootingDate1.orElseGet(() -> {
                        try {
                            return shootingDate2.orElseThrow(() -> new DatetimeReadException(exceptionMessage));
                        } catch (DatetimeReadException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                case VIDEO -> {
                    Optional<Directory> validDir = dirs.stream().filter(dir -> dir.containsTag(TagType.VIDEO_CREATION_DATETIME.getTagType())).findFirst();

                    Directory usableDir = validDir.orElseThrow(() -> new DatetimeReadException((exceptionMessage)));

                    return usableDir.getDate(TagType.VIDEO_CREATION_DATETIME.getTagType(), TimeZone.getDefault());
                }

                default -> {
                    return null;
                }
            }
        } catch (ImageProcessingException | IOException e) {
            throw new DatetimeReadException(e.getMessage());
        }
    }
}
