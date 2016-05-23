package tasks;

import cache.FileItem;
import security.PasswordDecoder;
import provider.FileCacheProvider;
import server.TCPServerSelector;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * HERE WILL BE CLASS FOR RUNNING ONE CERTAIN GET TASK, WITH THE INPUT URL
 * THIS CLASS WILL ALSO HAVE TO MANAGE LOGGING IN AS WELL AS PUT AND DELETE CLASSES
 */
public class GETRunnableTask extends RunnableTask {


    public GETRunnableTask(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider) {
        super(message, client, fileCacheProvider);
        operationTask = OperationTask.GET;
    }

    @Override
    public void run() {
        byte [] fileOutput;
        try {
            parseMessage();
            //System.out.println("URL "+url);
            //System.out.println("My url "+RunnableTask.ROOT_PATH);
            if(url == null){
                System.out.println("url == null");
                client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + WRONG_URL_MSG).getBytes("UTF-8")));
                // TESTING PURPOSE  ------ DELETE AFTER
                client.close();
                // TESTING PURPOSE  ------ DELETE AFTER
                return;
            }
            try{
                fileOutput = fileOutput();
            } catch (Exception e){
               if(e.getMessage() != null && e.getMessage().equals(WRONG_PASSWORD_EXCEPTION)){
                   System.out.println(WRONG_PASSWORD_EXCEPTION);
                   try {
                       client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_AUTHORIZATION + CONTENT_TYPE_HTML + "\n" + WRONG_PASSWORD_MSG).getBytes("UTF-8")));
                   } catch (IOException e1) {
                       System.out.println("Writing client exception....really?.....that is such a drag...");
                   }
               } else {
                   try {

                       System.out.println(WRONG_URL_MSG);
                       client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + WRONG_URL_MSG).getBytes("UTF-8")));
                   } catch (IOException e1) {
                       System.out.println("Writing client exception....really?.....that is such a drag...");
                   }
               }
                // TESTING PURPOSE  ------ DELETE AFTER
                try {
                    client.close();
                } catch (IOException easd) {
                    System.out.println("Exception while closing client.");
                    e.printStackTrace();
                }
                // TESTING PURPOSE  ------ DELETE AFTER
                return;
            }
            client.write(ByteBuffer.wrap((REQUEST_SUCCESS_HEADER + new String(fileOutput,"UTF-8")).getBytes("UTF-8")));
        } catch (Exception e) {
            try {
                System.out.println(INTERNAL_ERR_MSG);
                client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_INTERNAL_ERROR + CONTENT_TYPE_HTML + "\n" + INTERNAL_ERR_MSG).getBytes("UTF-8")));
            } catch (IOException e1) {
                System.out.println("Writing client exception....really?.....that is such a drag...");
            }
            System.out.println("readURL or readLogin exception");
            e.printStackTrace();
        }
        // TESTING PURPOSE  ------ DELETE AFTER
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("close client exception...such a drag");
            e.printStackTrace();
        }
        // TESTING PURPOSE  ------ DELETE AFTER
    }

    private byte[] fileOutput() throws Exception{
        FileItem  fileItem = fileItemCache.get(TCPServerSelector.ROOT_PATH + url);
        if(fileItem.isSecured() && !PasswordDecoder.correctInformations(password,fileItem)){
            throw new Exception(WRONG_PASSWORD_EXCEPTION);
        }
        //System.out.println("Accept: |"+acceptContent+"|");
        //if(acceptContent.indexOf(""))
        return fileItem.getFile();
    }



}
