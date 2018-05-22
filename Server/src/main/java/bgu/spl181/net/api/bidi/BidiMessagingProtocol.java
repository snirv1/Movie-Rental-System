/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.api.bidi;

/**
 *
 * @author bennyl
 * This interface replaces the MessagingProtocol interface.
 * It exists to support peer to peer messaging via the Connections interface
 */
public interface BidiMessagingProtocol<T> {

    void start(int connectionId, Connections<T> connections);

    void process(T message);

    /**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
