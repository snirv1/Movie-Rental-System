package bgu.spl181.net.api.bidi;

import java.io.IOException;

/**
 * This interface should map a unique ID for each active client
 connected to the server. The implementation of Connections is part of the server
 pattern and not part of the protocol.

 * @param <T>
 */

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);
}
