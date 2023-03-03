package com.fugamaru.sdp.enums;

/**
 * タグタイプ列挙
 */
public enum TagType {
    PICTURE_CREATION_DATETIME(306),
    VIDEO_CREATION_DATETIME(256);

    private final int tagType;

    TagType(int tagType) {
        this.tagType = tagType;
    }

    /**
     * ファイルタイプからタグタイプを取得する
     *
     * @param fileType ファイルタイプ
     * @return タグタイプ (画像・動画以外は0)
     */
    public static int getTagTypeFromFileType(FileType fileType) {
        switch (fileType) {
            case PICTURE -> {
                return PICTURE_CREATION_DATETIME.tagType;
            }
            case VIDEO -> {
                return VIDEO_CREATION_DATETIME.tagType;
            }
            default -> {
                return 0;
            }
        }
    }
}
