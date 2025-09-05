package com.dap.sequence.client.exception;

/**
 * PaasSequenceException
 *
 * @author scalor
 * @date 2023/3/28
 **/
public class PaasSequenceException extends Exception {
    public PaasSequenceException(String message){
        super(message);
    }

    public PaasSequenceException(String message, Throwable cause){
        super(message, cause);
    }

    public PaasSequenceException(Throwable cause){
        super(cause);
    }

}
