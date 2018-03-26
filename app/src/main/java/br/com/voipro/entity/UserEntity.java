package br.com.voipro.entity;

import java.io.Serializable;

public class UserEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    private String user;
    private String password;
    private String host;

    public UserEntity() {
    }

    public UserEntity(String user, String password, String host) {
        this.user = user;
        this.password = password;
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
