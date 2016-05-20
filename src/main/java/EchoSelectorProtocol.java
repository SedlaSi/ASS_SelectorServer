import org.apache.commons.lang.ArrayUtils;
import provider.FileCacheProvider;
import provider.PoolProvider;
import tasks.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by root on 3/29/16.
 */
public class EchoSelectorProtocol implements TCPProtocol {
    private ByteBuffer tag;
    private ByteBuffer etOrutBuff;
    private ByteBuffer eleteBuf;
    private ServerSocketChannel server;
    private Selector selector;
    private static final String WELCOME_MSG = "";
    private static final String UNKNOWN_COMMAND_ERR = RunnableTask.REQUEST_FAILED_HEADER_NOT_FOUND + RunnableTask.CONTENT_TYPE_HTML + "\n<html><body><h1>Unknown command, please try again.</h1></body></html>";
    private PoolProvider poolProvider;
    private FileCacheProvider fileCacheProvider;

    public EchoSelectorProtocol(ServerSocketChannel serverSocketChannel, Selector selector, int poolSize){
        this.server = serverSocketChannel;
        this.selector = selector;
        poolProvider = new PoolProvider(poolSize);
        fileCacheProvider = new FileCacheProvider();
        tag = ByteBuffer.allocate(1);
        etOrutBuff = ByteBuffer.allocate(3);
        eleteBuf = ByteBuffer.allocate(6);
    }

    public void handleRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            if((socketChannel.read(tag)) == -1){
                throw new IOException("Read exception");
            }
            // *************************** GET ***************************
            if(((char)tag.array()[0]) == 'G'){
                if((socketChannel.read(etOrutBuff)) == -1){
                    throw new IOException("Read exception");
                }
                if(new String(etOrutBuff.array(),"UTF-8").equals("ET ")){ // RIGHT TAG ACQUIRED
                    //System.out.println("GET  acquired");
                    byte[] msg = readRest(socketChannel);
                    RunnableTask runnable = new GETRunnableTask(msg,socketChannel,fileCacheProvider);
                    SelectionKey key2 = socketChannel.register(selector, SelectionKey.OP_WRITE);
                    key2.attach(runnable);

                } else throw new Exception("WRONG INPUT");
                //**************************** PUT ***************************
            } else if(((char)tag.array()[0]) == 'P'){
                if((socketChannel.read(etOrutBuff)) == -1){
                    throw new IOException("Read exception");
                }
                if(new String(etOrutBuff.array(),"UTF-8").equals("UT ")){ // RIGHT TAG ACQUIRED
                    System.out.println("PUT  acquired");
                    byte[] msg = readRest(socketChannel);
                    RunnableTask runnable = new PUTRunnableTask(msg,socketChannel,fileCacheProvider);
                    SelectionKey key2 = socketChannel.register(selector, SelectionKey.OP_WRITE);
                    key2.attach(runnable);
                } else throw new Exception("WRONG INPUT");
                //**************************** DELETE ***************************
            } else if(((char)tag.array()[0]) == 'D'){
                if((socketChannel.read(eleteBuf)) == -1){
                    throw new IOException("Read exception");
                }
                if(new String(eleteBuf.array(),"UTF-8").equals("ELETE ")){ // RIGHT TAG ACQUIRED
                    //System.out.println("DELETE  acquired");
                    byte[] msg = readRest(socketChannel);
                    RunnableTask runnable = new DELETERunnableTask(msg,socketChannel,fileCacheProvider);
                    SelectionKey key2 = socketChannel.register(selector, SelectionKey.OP_WRITE);
                    key2.attach(runnable);
                } else {
                    throw new Exception("WRONG INPUT");
                }
            } else {
               throw new Exception("WRONG INPUT");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("IOException catched in EchoSelectorProtocol");
        } catch (Exception ex){
            //ex.printStackTrace();
            System.out.println("WRONG INPUT EXCEPTION");
            try{
                readRest(socketChannel);
                byte [] msg = UNKNOWN_COMMAND_ERR.getBytes("UTF-8");
                RunnableTask runnable = new ExceptionTask(msg,socketChannel,fileCacheProvider);
                SelectionKey key2 = socketChannel.register(selector, SelectionKey.OP_WRITE);
                key2.attach(runnable);
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Writing client exception");
            }
        } finally {
            tag.clear();
            eleteBuf.clear();
            etOrutBuff.clear();
        }
    }

    public void handleWrite(SelectionKey key) {
        try {
            SocketChannel client = (SocketChannel) key.channel();
            RunnableTask run = (RunnableTask) key.attachment();
            poolProvider.getPool().addTask(run);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void handleAccept(SelectionKey key) {
        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            SelectionKey key2 = client.register(selector, SelectionKey.OP_READ);
            SocketChannel output = (SocketChannel) key2.channel();
            output.write(ByteBuffer.wrap(WELCOME_MSG.getBytes("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readRest(SocketChannel socketChannel){
        ArrayList<Byte> strb = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(1);
        try {
            if((socketChannel.read(buffer)) == -1){
                throw new Exception("NO ADRESS EXCEPTION");
            }
            byte c = buffer.array()[0];
            int m;
            buffer.clear();
            while(true){
                strb.add(c);
                if((m = socketChannel.read(buffer)) == -1){
                    throw new Exception("CONNECTION CLOSED PREMATURELY");
                }
                if(m == 0){
                    break;
                }
                c = buffer.array()[0];
                buffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ArrayUtils.toPrimitive(strb.toArray(new Byte[strb.size()]));
    }



}
