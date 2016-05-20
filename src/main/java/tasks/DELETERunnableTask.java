package tasks;

import cache.FileItem;
import security.PasswordDecoder;
import provider.FileCacheProvider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Created by root on 4.5.16.
 */
public class DELETERunnableTask extends RunnableTask {

    private static final Logger logger = Logger.getLogger("DELETERunnableTask");

    public DELETERunnableTask(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider) {
        super(message, client, fileCacheProvider);
        operationTask = OperationTask.DELETE;
    }

    @Override
    public void run(){
        //System.out.println("DELETE TASK COMPLETED!!");


            try{
                parseMessage();
                if(url == null){
                    client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + WRONG_URL_MSG).getBytes("UTF-8")));
                    client.close();
                    return;
                }
                FileItem fileItem = fileItemCache.get(ROOT_PATH + url);
                if(fileItem.isSecured() && !PasswordDecoder.correctInformations(password,fileItem)){
                    throw new Exception(WRONG_PASSWORD_EXCEPTION);
                }
                fileItemCache.remove(ROOT_PATH + url);
                client.write(ByteBuffer.wrap((REQUEST_SUCCESS_HEADER + CONTENT_TYPE_HTML + "\n" + DELETE_SUCCESS_BEGIN_MSG+url+DELETE_SUCCESS_END_MSG).getBytes("UTF-8")));
                logger.fine("File "+url+" has been deleted by client "+client.getLocalAddress());
            } catch (Exception e){
                try {
                    if(e.getMessage() != null && e.getMessage().equals(WRONG_PASSWORD_EXCEPTION)){
                        client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_AUTHORIZATION + CONTENT_TYPE_HTML + "\n" + WRONG_PASSWORD_MSG).getBytes("UTF-8")));
                    } else {

                            client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + DELETE_ERR_MSG).getBytes("UTF-8")));
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            //client.write(ByteBuffer.wrap(("TASK = DELETE "+message).getBytes()));
            //readURL();
            //readLogin();

        try {
            client.close();
        } catch (IOException e) {
            System.out.println("close client exception...such a drag");
            //e.printStackTrace();
        }
    }

}
