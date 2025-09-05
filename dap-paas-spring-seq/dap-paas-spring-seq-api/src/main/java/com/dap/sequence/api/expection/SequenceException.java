package com.dap.sequence.api.expection;


import com.dap.sequence.api.exception.ExceptionEnum;

public class SequenceException extends RuntimeException {

    private String errorCode;

    private String message;


    public SequenceException(String message) {
        super(message);
    }

    public SequenceException(String message, Throwable cause) {
        super(message, cause);
    }


    public SequenceException(String errorCode, String... errorMessageParams) {
        super(errorCode);

    }

    public SequenceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public SequenceException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getResultMsg());
        this.errorCode = exceptionEnum.getResultCode();
        this.message = exceptionEnum.getResultMsg();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}