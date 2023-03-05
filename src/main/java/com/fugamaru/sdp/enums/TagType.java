package com.fugamaru.sdp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * タグタイプ列挙
 */
@AllArgsConstructor
public enum TagType {
    PICTURE_CREATION_DATETIME(306),
    PICTURE_ORIGINAL_CREATION_DATETIME(36867),
    VIDEO_CREATION_DATETIME(256);

    @Getter
    private final int tagType;
}
