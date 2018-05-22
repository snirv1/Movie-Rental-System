package bgu.spl181.net.api.bidi;

import java.io.Serializable;

public  class User implements Serializable {


    /**
     * object that holds the abstract user
     */
    protected String username;
    protected String password;
    protected String type;
    protected transient boolean isLoggedIn;
    protected transient int connectionId;



    public User(String username, String password, String type ,int connectionId) {
        this.username = username;
        this.password = password;
        this.type = type;
        isLoggedIn = false;
        this.connectionId = connectionId;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }


}
