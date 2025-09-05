package com.dap.sequence.client.balancer;

public class Server {
    private String host;

    private int port;

    private int index;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Server(String host, int port, int index) {
        this.host = host;
        this.port = port;
        this.index = index;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
