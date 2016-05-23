package tasks;

import org.junit.Test;
import static org.junit.Assert.*;
import provider.FileCacheProvider;

import java.nio.channels.SocketChannel;

/**
 * Created by root on 19.5.16.
 */
public class TestRunnableTask {

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
        RunnableTask runnableTask = new RunnableTaskImpl(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(url,runnableTask.url);
        assertEquals(password, runnableTask.password);


        message = (url + " \n").getBytes();
        runnableTask = new RunnableTaskImpl(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        runnableTask.run();
        runnableTask.getOperationTask();
        assertNull(runnableTask.password);

        message = (" asdasd").getBytes();
        runnableTask = new RunnableTaskImpl(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertNull(runnableTask.url);

        message = ("/adress%20 ").getBytes();
        runnableTask = new RunnableTaskImpl(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(runnableTask.url, "/adress");

        message = ("/adress").getBytes();
        runnableTask = new RunnableTaskImpl(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(runnableTask.url, "/adress");

        message = ("/adres\n").getBytes();
        runnableTask = new RunnableTaskImpl(message,client,fileCacheProvider);
        runnableTask.parseMessage();
        assertEquals(runnableTask.url, "/adres");

    }

    private class RunnableTaskImpl extends RunnableTask {

        RunnableTaskImpl(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider) {
            super(message, client, fileCacheProvider);
        }

    }
}
