package bgu.spl181.net.api.bidi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MovieRentalProtocol extends bidiMessagingProtocolImpl {


    public MovieRentalProtocol(MovieSharedData movieSharedData) {
        super(movieSharedData);
    }

    /**
     * parsering the args into a execution on the {@link MovieSharedData
     * according to the args received
     * @param args a string that recevied from the client exculuded "REQUEST"
     */

    @Override
    public void parseringRequest(String args) {
        String result;
        String[] resultWithBroad;
        String msgToReturn;
        String msgToBroadcast;
        MovieSharedData movieSharedData = (MovieSharedData)sharedData;
        String[] msg= args.split(" ");
        if (msg.length==0){
            connections.send(connectionId,"ERROR request failed");
            return;
        } else if(!movieSharedData.isLoggedIn(connectionId)) {
            connections.send(connectionId, "ERROR request " + msg[0] + " failed");
        }
        else {
            String argument=args.substring(args.indexOf(" ")+1);
            switch (msg[0]) {
                case "balance":
                    if(msg[1].equals("info")){
                        result= movieSharedData.commandRequestBalanceInfo(connectionId);
                        connections.send(connectionId,result);

                    }
                    else if (msg[1].equals("add")){
                        result=movieSharedData.commandRequestBalanceAdd(connectionId,msg[2]);
                        connections.send(connectionId,result);

                    }
                    else {
                        connections.send(connectionId,"ERROR request " + msg[0] + " failed");
                    }
                    break;
                case "info":
                    if (msg.length==1){//TODO what if we get an empty string handle """"
                        result = movieSharedData.commandRequestMovieInfo(null);
                        connections.send(connectionId,result);
                    }
                    else{
                        argument= argument.substring(argument.indexOf("\"")+1, argument.lastIndexOf("\""));
                        result = movieSharedData.commandRequestMovieInfo(argument);
                        connections.send(connectionId,result);
                    }
                    break;
                case  "rent":
                    argument= argument.substring(argument.indexOf("\"")+1, argument.lastIndexOf("\""));
                    resultWithBroad=movieSharedData.commandRequestMovieRent(connectionId,argument);
                    msgToReturn = resultWithBroad[0];
                    msgToBroadcast = resultWithBroad[1];
                    connections.send(connectionId,msgToReturn);
                    if (msgToReturn.substring(0,3).equals("ACK")){
                        broadcast(msgToBroadcast);
                    }
                    break;
                case "return":
                    argument= argument.substring(argument.indexOf("\"")+1, argument.lastIndexOf("\""));
                    resultWithBroad= movieSharedData.commandRequestReturnMovie(connectionId,argument);
                    msgToReturn = resultWithBroad[0];
                    msgToBroadcast = resultWithBroad[1];
                    connections.send(connectionId,msgToReturn);
                    if (msgToReturn.substring(0,3).equals("ACK")){
                        broadcast(msgToBroadcast);
                    }
                    break;
                case "addmovie":
                    args= args.substring(args.indexOf(" ")+2);
                    String movieName=  args.substring(0,args.indexOf("\""));
                    args= args.substring(args.indexOf("\"")+2);
                    int amount = Integer.decode(args.substring(0,args.indexOf(" ")));
                    args= args.substring(args.indexOf(" ")+1);
                    int price;
                    if (args.contains(" ")) {
                         price = Integer.decode(args.substring(0, args.indexOf(" ")));
                    }else { price = Integer.decode(args);

                    }
                    if(args.indexOf(" ")==-1){
                        resultWithBroad=movieSharedData.commandRequestAdminAddMovie(connectionId,movieName,amount,price,new LinkedList<>());
                        msgToReturn = resultWithBroad[0];
                        msgToBroadcast = resultWithBroad[1];
                        connections.send(connectionId,msgToReturn);
                        if (msgToReturn.substring(0,3).equals("ACK")){
                            broadcast(msgToBroadcast);
                        }
                        break;
                    }
                    else{
                        args= args.substring(args.indexOf(" ")+1);
                        List<String> banned = new LinkedList<>();
                        while(args.indexOf("\"")!= -1 ){
                            args = args.substring(1);
                            String country = args.substring(0,args.indexOf("\""));
                            banned.add(country);
                            args = args.substring(args.indexOf("\""));
                            if(args.length()>1){
                                args = args.substring(2);
                            }else {
                                args = "";
                            }
                        }
                        resultWithBroad=movieSharedData.commandRequestAdminAddMovie(connectionId,movieName,amount,price,banned);
                        msgToReturn = resultWithBroad[0];
                        msgToBroadcast = resultWithBroad[1];
                        connections.send(connectionId,msgToReturn);
                        if (msgToReturn.substring(0,3).equals("ACK")){
                            broadcast(msgToBroadcast);
                        }
                        break;
                    }

                case "remmovie":
                    argument= argument.substring(argument.indexOf("\"")+1, argument.lastIndexOf("\""));
                    Movie movieToBeRemoved = movieSharedData.getMovieFromListByMovieName(argument);
                    resultWithBroad = movieSharedData.commandRequestAdminRemmovie(connectionId,argument);
                    msgToReturn = resultWithBroad[0];
                    msgToBroadcast = resultWithBroad[1];
                    connections.send(connectionId,msgToReturn);
                    if (msgToReturn.substring(0,3).equals("ACK")){
                        broadcast(msgToBroadcast);
                    }
                    break;
                case "changeprice":
                    int split = argument.lastIndexOf(" ");
                    Integer pricetobe= Integer.decode(argument.substring(split+1));
                    argument = argument.substring(0,split);
                    String movieNameToSearch = argument.substring(argument.indexOf("\"")+1, argument.lastIndexOf("\""));
                    Movie movie = movieSharedData.getMovieFromListByMovieName(movieNameToSearch);
                    resultWithBroad = movieSharedData.commandRequestAdminChangePrice(connectionId,movieNameToSearch,pricetobe);
                    msgToReturn = resultWithBroad[0];
                    msgToBroadcast = resultWithBroad[1];
                    connections.send(connectionId,msgToReturn);
                    if (msgToReturn.substring(0,3).equals("ACK")){
                        broadcast(msgToBroadcast);
                    }
                    break;
                default:
                    connections.send(connectionId,"ERROR request " + msg[0] + " failed");
                    break;
            }
        }


    }


    /**
     * broadcast the maessage got to all loggedIn clients
     * @param msg the message should broadcast
     */
    public void broadcast(String msg){
            ConcurrentHashMap<Integer, User> map = sharedData.getMapOfLoggedInUsersByConnectedIds();
            for (ConcurrentHashMap.Entry<Integer, User> entry : map.entrySet()) {
                Integer connectionId = entry.getKey();
                connections.send(connectionId, msg);
            }

    }
}