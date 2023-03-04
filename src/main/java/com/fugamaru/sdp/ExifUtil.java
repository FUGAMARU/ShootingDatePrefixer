package com.fugamaru.sdp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.fugamaru.sdp.enums.TagType;
import com.fugamaru.sdp.exceptions.DatetimeReadException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class ExifUtil {
    /**
     * 画像・動画ファイルの撮影日時を取得する
     *
     * @param file 対象ファイル
     * @return 撮影日時
     * @throws DatetimeReadException DatetimeReadException
     */
    public static LocalDate getShootingDate(FileUtil file) throws DatetimeReadException {
        String exceptionMessage = "No valid metadata existed for the shooting date: " + file.getPath(); //撮影日時に関するメタデーターが存在しない場合に独自例外に投げるメッセージ

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

                default -> {
                    return null;
                }
            }
        } catch (ImageProcessingException | IOException e) {
            throw new DatetimeReadException(e.getMessage());
        }
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
