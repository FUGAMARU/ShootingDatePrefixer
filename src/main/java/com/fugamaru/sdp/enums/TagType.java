package com.fugamaru.sdp.enums;

/**
 * タグタイプ列挙
 */
public enum TagType {
    PICTURE_CREATION_DATETIME(306),
    PICTURE_ORIGINAL_CREATION_DATETIME(36867),
    VIDEO_CREATION_DATETIME(256);

    private final int tagType;

    TagType(int tagType) {
        this.tagType = tagType;
    }

    /**
     * タグタイプを取得する
     *
     * @return タグタイプ
     */
    public int getTagType() {
        return tagType;
    }
}
