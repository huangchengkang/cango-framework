package com.cangoframework.api.apps.email;

/**
 * Created by cango on 2018/1/10.
 */
public class AuthConfig {
    private String mailServerHost;
    private String mailServerPort = "25";
    private String userName;
    private String password;

    public AuthConfig() {
    }

    public AuthConfig(String mailServerHost, String mailServerPort, String userName, String password) {
        this.mailServerHost = mailServerHost;
        this.mailServerPort = mailServerPort;
        this.userName = userName;
        this.password = password;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
