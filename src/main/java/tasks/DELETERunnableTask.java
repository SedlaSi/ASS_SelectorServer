package tasks;

import cache.FileItem;
import security.PasswordDecoder;
import provider.FileCacheProvider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by root on 4.5.16.
 */
public class DELETERunnableTask extends RunnableTask {

    public DELETERunnableTask(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider) {
        super(message, client, fileCacheProvider);
        operationTask = OperationTask.DELETE;
    }

    @Override
    public void run(){
        //System.out.println("DELETE TASK COMPLETED!!");
        try {
            parseMessage();
            if(url == null){
                client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + WRONG_URL_MSG).getBytes()));
                client.close();
                return;
            }
            try{
                FileItem fileItem = fileItemCache.get(ROOT_PATH + url);
                if(fileItem.isSecured() && !PasswordDecoder.correctInformations(password,fileItem)){
                    throw new Exception(WRONG_PASSWORD_EXCEPTION);
                }
                fileItemCache.remove(ROOT_PATH + url);
                client.write(ByteBuffer.wrap((REQUEST_SUCCESS_HEADER + CONTENT_TYPE_HTML + "\n" + DELETE_SUCCESS_BEGIN_MSG+url+DELETE_SUCCESS_END_MSG).getBytes()));
            } catch (Exception e){
                if(e.getMessage() != null && e.getMessage().equals(WRONG_PASSWORD_EXCEPTION)){
                    client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_AUTHORIZATION + CONTENT_TYPE_HTML + "\n" + WRONG_PASSWORD_MSG).getBytes()));
                } else {
                    client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + DELETE_ERR_MSG).getBytes()));
                }
            }
            //client.write(ByteBuffer.wrap(("TASK = DELETE "+message).getBytes()));
            //readURL();
            //readLogin();
        } catch (Exception e) {
            System.out.println("readURL or readLogin exception");
            //e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("close client exception...such a drag");
            //e.printStackTrace();
        }
    }

}
