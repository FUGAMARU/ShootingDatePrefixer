package com.fugamaru.sdp.exceptions;

/**
 * EXIFから撮影日時を取得する時に撮影日時のタグが存在しなかったりライブラリーが例外を投げた場合の例外
 */
public class DatetimeReadException extends Exception {
    public DatetimeReadException(String msg) {
        super(msg);
    }
}
