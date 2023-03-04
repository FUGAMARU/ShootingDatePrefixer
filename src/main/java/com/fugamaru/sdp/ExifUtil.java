package com.fugamaru.sdp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.fugamaru.sdp.enums.FileType;
import com.fugamaru.sdp.enums.TagType;
import com.fugamaru.sdp.exceptions.DatetimeReadException;
import com.fugamaru.sdp.exceptions.UnsupportedFileTypeException;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ExifUtil {
    /**
     * 画像・動画ファイルの撮影日時を取得する
     *
     * @param targetFile 対象ファイル
     * @return 撮影日時
     * @throws DatetimeReadException DatetimeReadException
     */
    public static LocalDate getShootingDate(TargetFile targetFile) throws DatetimeReadException, UnsupportedFileTypeException {
        Path filePath = targetFile.getPath();
        FileType fileType = targetFile.getFileType();

        String exceptionMessage = "No valid metadata existed for the shooting date: " + filePath; //撮影日時に関するメタデーターが存在しない場合に独自例外に投げるメッセージ

        try {
            if (fileType == FileType.OTHER) {
                throw new UnsupportedFileTypeException("Not an picture or video file: " + filePath);
            }

            Metadata metadata = ImageMetadataReader.readMetadata(filePath.toFile());

            List<Directory> dirs = new ArrayList<>();
            metadata.getDirectories().iterator().forEachRemaining(dirs::add);

            switch (fileType) {
                case PICTURE -> {
                    Optional<Directory> validDir = dirs.stream().filter(dir -> dir.containsTag(TagType.PICTURE_CREATION_DATETIME.getTagType()) || dir.containsTag(TagType.PICTURE_ORIGINAL_CREATION_DATETIME.getTagType())).findFirst();

                    if (validDir.isEmpty()) {
                        throw new DatetimeReadException(exceptionMessage);
                    }

                    Optional<Date> shootingDate1 = Optional.ofNullable(validDir.get().getDate(TagType.PICTURE_CREATION_DATETIME.getTagType(), TimeZone.getDefault()));
                    Optional<Date> shootingDate2 = Optional.ofNullable(validDir.get().getDate(TagType.PICTURE_ORIGINAL_CREATION_DATETIME.getTagType(), TimeZone.getDefault()));

                    Date shootingDate = shootingDate1.orElseGet(() -> shootingDate2.orElseThrow(RuntimeException::new));

                    if (isInitialDatetime(shootingDate)) {
                        throw new DatetimeReadException(exceptionMessage);
                    }

                    return LocalDate.ofInstant(shootingDate.toInstant(), ZoneId.systemDefault());
                }

                case VIDEO -> {
                    Optional<Directory> validDir = dirs.stream().filter(dir -> dir.containsTag(TagType.VIDEO_CREATION_DATETIME.getTagType())).findFirst();

                    Directory usableDir = validDir.orElseThrow(() -> new DatetimeReadException((exceptionMessage)));

                    Date shootingDate = usableDir.getDate(TagType.VIDEO_CREATION_DATETIME.getTagType(), TimeZone.getDefault());

                    if (isInitialDatetime(shootingDate)) {
                        throw new DatetimeReadException(exceptionMessage);
                    }

                    return LocalDate.ofInstant(shootingDate.toInstant(), ZoneId.systemDefault());
                }
            }
        } catch (ImageProcessingException | IOException e) {
            throw new DatetimeReadException(e.getMessage());
        }

        return null;
    }

    /**
     * Dateの中身が初期値かどうか
     *
     * @param date Date
     * @return Dateの中身が初期値かどうか
     */
    private static boolean isInitialDatetime(Date date) {
        return (date.getTime() / 1000L) == 0;
    }
}
