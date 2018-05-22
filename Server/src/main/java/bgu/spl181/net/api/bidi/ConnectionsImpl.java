package bgu.spl181.net.api.bidi;

import bgu.spl181.net.srv.bidi.ConnectionHandler;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this class implements {@link Connections, and allow the system to controll the sending massege
 *
 * this class has clients map the holds all the connectionHandlers for the clients
 *
 */
public class ConnectionsImpl<T> implements Connections<T> {

    protected ConcurrentHashMap<Integer,ConnectionHandler<T>> clients;

    public ConnectionsImpl() {
        this.clients = new ConcurrentHashMap<>();
    }

    /**
     * send to te connecion id the msg
     * @param connectionId the connection Id that sould sen him a massege
     * @param msg the message that send
     * @return
     */
    @Override
    public boolean send(int connectionId, T msg) {
        try {
            clients.get(connectionId).send(msg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /***
     * broadcast to all clients a messae
     * @param msg
     */
    @Override
    public void broadcast(T msg) {
        for (Map.Entry<Integer,ConnectionHandler<T>> entry : clients.entrySet()){
            send(entry.getKey(),msg);
        }

    }

    /**
     * disconnect the clients by removing him from the clients' map
     * @param connectionId that should disconnect
     */
    @Override
    public void disconnect(int connectionId) {
        clients.remove(connectionId);
    }


    public ConcurrentHashMap<Integer,ConnectionHandler<T>> getClients(){
        return clients;
        }
}