package bgu.spl181.net.api.bidi;



public abstract class bidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {
    /**
     * this class is implimetation of the user text protocol interface
     * its an abstarct class to keep the design pattern for any service that will implement the system.
     * this class handels masseges from the client and parsering them to the {@link SharedData  and the data will
     * return it and answer so this protocol will send it back to the client
     *
     * this is abstrct class in order to keep design pattern for serviceing. that mean that the implement service should
     * extends it and override the  abstract function
     */


    protected int connectionId;
    protected Connections<T> connections;
    protected SharedData sharedData;
    protected boolean shouldTerminated;


    /**
     * construct the protocol with share data given and set {@param shouldTerminated to false
     *
     * @param sharedData
     *
     */
    public bidiMessagingProtocolImpl(SharedData sharedData) {
        this.sharedData = sharedData;
        shouldTerminated=false;
    }

    /**
     * override for start method- set {@param connectionId to be the specific client connectionId
     *
     * @param connectionId - specific client connectionId - this client is the client who sent the message to the server
     * @param connections  represent the connctions class in order to send back to the clinet or broadcast for all the clients
     */
    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    /**
     * process the message given from the client
     * @param message the message got fro the client
     */
    @Override
    public void process(T message) {
        if (message instanceof String) {
            String[] msg = ((String) message).split(" ");
                switch (msg[0]) {
                    case "REGISTER":
                        register(msg);
                        break;
                    case "LOGIN":
                       login(msg);
                        break;
                    case "SIGNOUT":
                        signout();
                        break;
                    case "REQUEST":
                        String requestArgs = ((String) message).substring(((String) message).indexOf(" ")+1);
                        parseringRequest(requestArgs);
                        break;
                    default:
                        connections.send(connectionId,(T)"ERROR unknown command");
                        break;

                }
            }

        }





        /**
         * @return true if the connection should be terminated
         */
        @Override
        public boolean shouldTerminate () {
            return shouldTerminated;
        }

    /**
     * abstract function that will override by the specific service in order to procces REQUEST message
     * @param args
     */
        public abstract void parseringRequest(String args);

    /**
     * process register command. process the message and deviler for executing by the data
     * @param msg String Array that represent the original message that splitted into array by " "
     */


    public void register (String[] msg){
            String result;
            if (msg.length == 3) {
            String userName = msg[1];
            String password = msg[2];

                result = sharedData.commandRegister(userName,password, null, connectionId);
                connections.send(connectionId, (T) result);
            } else if (msg.length > 3) {
                String userName = msg[1];
                String password = msg[2];
                String datablock="";
                for (int i=3; i<msg.length;i++){
                    datablock= datablock + msg[i]+" ";
                }
                datablock=datablock.substring(0,datablock.length()-1);
                result = sharedData.commandRegister(userName, password, datablock, connectionId);
                connections.send(connectionId, (T) result);
            }else {
                connections.send(connectionId, (T) "ERROR registration failed");
            }
        }

    /**
     * parsering the command LOGIN and deliver to data for executing
     * @param msg String Array that represent the original message that splitted into array by " "
     */

    public void  login (String[] msg){
            String result;
            if (msg.length == 3) {
                result = sharedData.commandLogIn(msg[1], msg[2],connectionId);
                connections.send(connectionId, (T) result);
            } else {
                connections.send(connectionId, (T) "ERROR login failed");
            }
        }

    /**
     * once SIGNOUT command received the protocol deliver for the data to executing. if succeed, disconnect the client from the server
     */
        public void signout() {
        String result = sharedData.commandSignOut(connectionId);
        if(result.equals("ACK signout succeeded")){
            connections.send(connectionId,(T)result);
            shouldTerminated=true;
            connections.disconnect(connectionId);
        }
        else {
            connections.send(connectionId,(T)result);
        }

    }

    }


