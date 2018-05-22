package bgu.spl181.net.api.bidi;


import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class SharedData {
    /**
     * abstract class the represent the the data of the system
     */
    protected  ConcurrentHashMap<String ,User> mapOfRegisteredUsersByUsername;// map userName to User
    protected  ConcurrentHashMap<Integer,User> mapOfLoggedInUsersByConnectedIds; // map connectionId to username
    private Object LogInLock;
    private Object registerLock;

    /**
     * default construct the params into null
     */
    public SharedData() {
        this.mapOfLoggedInUsersByConnectedIds = null;
        this.mapOfRegisteredUsersByUsername = null;
        this.registerLock = new Object();
        this.LogInLock = new Object();
    }

    /**
     * constructor
     * @param userMap map of user
     */
    public SharedData(ConcurrentHashMap<String, User> userMap) {
        this.mapOfLoggedInUsersByConnectedIds = new ConcurrentHashMap<>();
        this.mapOfRegisteredUsersByUsername = userMap;
        this.registerLock = new Object();
        this.LogInLock = new Object();

    }

    /**
     * execute register command
     * @param username the new username
     * @param password the new password
     * @param dataBlock the datablock to be add
     * @param connectionId represent the client
     * @return the message to be send to the client
     */
    protected  String commandRegister(String username , String password ,String dataBlock , Integer connectionId){
        synchronized (registerLock) {
        if (mapOfLoggedInUsersByConnectedIds.containsKey(connectionId) ||
                mapOfRegisteredUsersByUsername.containsKey(username) ||
                !isValidDataBlock(dataBlock)  ){
            return "ERROR registration failed";
        }
            addUser(username, password, connectionId, dataBlock);
            updateUserJson();
            updateServiceJson();
            return "ACK registration succeeded";
        }

    }


    /**
     * execute th log in command
     * @param username the username to be logged in
     * @param password the user name password
     * @param connectionId the client connection id
     * @return  the message to be send to the client
     */
    protected  String commandLogIn(String username , String password  ,Integer connectionId){
        synchronized (LogInLock) {
            if (!mapOfRegisteredUsersByUsername.containsKey(username) ||
                    mapOfLoggedInUsersByConnectedIds.containsKey(connectionId) ||
                    mapOfRegisteredUsersByUsername.get(username).isLoggedIn) {
                return "ERROR login failed";
            }
            User user = mapOfRegisteredUsersByUsername.get(username);
            if (!user.getPassword().equals(password)) {
                return "ERROR login failed";
            }
            user.setLoggedIn(true);
            mapOfLoggedInUsersByConnectedIds.put(connectionId, user);
            return "ACK login succeeded";
        }
    }
    protected  String commandSignOut(Integer connctionId){
        if(!mapOfLoggedInUsersByConnectedIds.containsKey(connctionId)){
            return "ERROR signout failed";
        }
        User user = mapOfLoggedInUsersByConnectedIds.remove(connctionId);
        user.setLoggedIn(false);
        return "ACK signout succeeded";

    }


    protected abstract boolean isValidDataBlock(String dataBlock);

    protected abstract void addUser(String username , String password, int connectionId, String dataBlock);

    public ConcurrentHashMap<String, User> getUserMap() {
        return mapOfRegisteredUsersByUsername;
    }

    public void setUserMap(ConcurrentHashMap<String, User> userMap) {
        this.mapOfRegisteredUsersByUsername = userMap;
    }

    public ConcurrentHashMap<Integer,User> getMapOfLoggedInUsersByConnectedIds() {
        return mapOfLoggedInUsersByConnectedIds;
    }
    protected abstract void updateUserJson();
    protected abstract void updateServiceJson();
}