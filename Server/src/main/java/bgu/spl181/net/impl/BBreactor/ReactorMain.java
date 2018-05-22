package bgu.spl181.net.impl.BBreactor;


import bgu.spl181.net.api.MessageEncoderDecoderImpl;
import bgu.spl181.net.api.bidi.MovieRentalProtocol;
import bgu.spl181.net.api.bidi.MovieSharedData;
import bgu.spl181.net.api.bidi.bidiMessagingProtocolImpl;
import bgu.spl181.net.srv.Server;
import java.net.InetAddress;

import static bgu.spl181.net.impl.BBtpc.TPCMain.ReadFromJson;

public class ReactorMain {

    public static void main(String[] args){
       int numOfThreads = 7;
        try {
           //System.out.println( "main :"+ InetAddress.getLocalHost());
        }catch (Exception e){}
        MovieSharedData movieSharedData = ReadFromJson();
       Server reactorServer = Server.reactor(
                numOfThreads,
                Integer.decode(args[0]).intValue(),
                ()->  new MovieRentalProtocol(movieSharedData),
                ()-> new MessageEncoderDecoderImpl<>()
        );
       reactorServer.serve();
    }




}
