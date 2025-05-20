package com.example.economicssimulatorserver.util;

import lombok.Getter;

@Getter
public class LocalizedException extends RuntimeException {
    private final String code;
    private final Object[] args;

    public LocalizedException(String code, Object... args) {
        super(code);
        this.code = code;
        this.args = args;
    }

}
