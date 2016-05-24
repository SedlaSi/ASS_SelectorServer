package server;

import java.nio.channels.SelectionKey;

/**
 * Created by root on 3/29/16.
 */
public interface TCPProtocol {
    public void handleAccept(SelectionKey key);

    public void handleRead(SelectionKey key);

    public void handleWrite(SelectionKey key);
}
