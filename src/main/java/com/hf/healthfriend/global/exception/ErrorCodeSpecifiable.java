package com.hf.healthfriend.global.exception;

public interface ErrorCodeSpecifiable {

    int status();

    String code();

    String name();

    String message();

    int statusCodeSeries();
}
