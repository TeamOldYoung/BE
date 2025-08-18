package com.app.oldYoung.global.security.exception;

import com.app.oldYoung.global.common.apiResponse.exception.CustomException;
import com.app.oldYoung.global.common.apiResponse.exception.ErrorCode;

public class AuthHandler extends CustomException {

    public AuthHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}