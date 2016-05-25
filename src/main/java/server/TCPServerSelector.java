package server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

public class TCPServerSelector {

    private static final Logger logger = Logger.getLogger("server.TCPServerSelector");
    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 5012;
    public static final String ROOT_PATH = "/home/sedlasi1/Desktop/Skola/ASS/Semestralka/server/root"; //Home folder of server
    // DO NOT put '/' at the end of the path
    private static final int POOL_SIZE = 20; // Number of threads in pool
    private static final int TIMEOUT = 2; // Wait timeout (milliseconds)

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(SERVER_IP, SERVER_PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        TCPProtocol protocol = new EchoSelectorProtocol(serverSocket, selector, POOL_SIZE);
        logger.finest("Starting server with IP: " + SERVER_IP + ", PORT: " + SERVER_PORT);
        while (true) {
            if (selector.select(TIMEOUT) == 0) {
                continue;
            }
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next();
                if (key.isAcceptable()) {
                    protocol.handleAccept(key);
                } else if (key.isReadable()) {
                    protocol.handleRead(key);
                } else {
                    protocol.handleWrite(key);
                }

                keyIter.remove();
            }
        }
    }
}
