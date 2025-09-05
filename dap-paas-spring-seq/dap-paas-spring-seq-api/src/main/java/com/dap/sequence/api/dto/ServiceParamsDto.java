package com.dap.sequence.api.dto;

/**
 * @author: liu
 * @date: 2022/3/1 14:00
 * @description:
 */
public class ServiceParamsDto {
    private String seqCode;
   private String clientPort;

    public String getSeqCode() {
        return seqCode;
    }

    public void setSeqCode(String seqCode) {
        this.seqCode = seqCode;
    }

    public String getClientPort() {
        return clientPort;
    }

    public void setClientPort(String clientPort) {
        this.clientPort = clientPort;
    }
}
