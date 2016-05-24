package tasks;

import provider.FileCacheProvider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by root on 20.5.16.
 */
public class ExceptionTask extends RunnableTask {
    public ExceptionTask(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider) {
        super(message, client, fileCacheProvider);
    }

    @Override
    public void run() {
        try {
            client.write(ByteBuffer.wrap(message));
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception writing to client or closing connection.");
        }
    }
}
