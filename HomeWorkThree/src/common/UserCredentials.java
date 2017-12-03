package common;

import java.io.Serializable;

public class UserCredentials implements Serializable {
    private  String username;
    private  String password;
    private boolean loggedIn = false;
    public int id;


    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;


    }
    public UserCredentials(){
        this.loggedIn = false;
        this.id = -1;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean getLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean status) {
        this.loggedIn = status;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
