package tasks;

import org.junit.Test;
import provider.FileCacheProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.junit.Assert.*;
/**
 * Created by root on 19.5.16.
 */
public class TestPUTRunnableTask {

    @Test
    public void testParseMessage(){
        //byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider
        String url = "/my_url";
        String author = "Authorization: Basic ";
        String password = "USER:PASS";
        String acc = "Accept: ";
        String acceptContent = "html/text";

        byte [] message = (url + " \n" + author +  password + " \n" + acc + acceptContent + " \n").getBytes();
        SocketChannel client = null;
        FileCacheProvider fileCacheProvider = new FileCacheProvider();
        RunnableTask runnableTask = new PUTRunnableTask(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(url,runnableTask.url);
        assertEquals(password, runnableTask.password);


        message = (url + " \n").getBytes();
        runnableTask = new PUTRunnableTask(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        runnableTask.getOperationTask();
        assertNull(runnableTask.password);

        message = (" asdasd").getBytes();
        runnableTask = new PUTRunnableTask(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertNull(runnableTask.url);

        message = ("/adress%20").getBytes();
        runnableTask = new PUTRunnableTask(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(runnableTask.url, "/adress");

        message = ("/adress").getBytes();
        runnableTask = new PUTRunnableTask(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(runnableTask.url, "/adress");
        assertEquals(((PUTRunnableTask)runnableTask).body,null);

        byte [] msg = new byte [] {'/','a','\n',(byte)10,(byte)13,(byte)10,'a'};
        runnableTask = new PUTRunnableTask(msg,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(runnableTask.url, "/a");
        assertEquals(((PUTRunnableTask)runnableTask).body[0],'a');
    }

   /* @Test
    public void testRun(){
        Selector selector;
        ServerSocketChannel serverSocket;
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress("localhost",5012));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        byte [] msg = new byte [] {'/','a','\n',(byte)10,(byte)13,(byte)10,'a'};
        SocketChannel client = null;
        try {
            client = SocketChannel.open();
            client.connect(new InetSocketAddress("localhost",5012));
            client.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(7);
        FileCacheProvider fileCacheProvider = new FileCacheProvider();
        RunnableTask runnableTask = new PUTRunnableTask(msg,client,fileCacheProvider);
        runnableTask.run();
        try {
            client.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        System.out.println(buffer.toString());

    }*/
}
