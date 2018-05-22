package bgu.spl181.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * encode and decode the message got from the buffer
 * @param <T>
 */
public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder<T> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    @Override
    public T decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == '\n') {
            return popString();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    @Override
    public byte[] encode(T message) {
        return (message + "\n").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private T popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        T result = (T) new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
