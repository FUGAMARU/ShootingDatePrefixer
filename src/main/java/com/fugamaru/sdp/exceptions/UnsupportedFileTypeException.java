package com.fugamaru.sdp.exceptions;

/**
 * 画像・動画以外のファイルのメタデーターを取得しようとした場合の例外
 */
public class UnsupportedFileTypeException extends Exception {
    public UnsupportedFileTypeException(String msg) {
        super(msg);
    }
}
