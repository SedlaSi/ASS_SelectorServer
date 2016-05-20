package tasks;

import cache.FileItem;
import security.PasswordDecoder;
import provider.FileCacheProvider;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by root on 4.5.16.
 */
public class PUTRunnableTask extends RunnableTask {

    byte [] body;
    private final String EMPTY_BODY_ERR = "Body of the file cannot be empty.";

    public PUTRunnableTask(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider) {
        super(message, client, fileCacheProvider);
        operationTask = OperationTask.PUT;
    }

    @Override
    public void run(){
        //System.out.println("PUT TASK COMPLETED!!");
        try {
            parseMessage();
            if(url != null) readBody();
            if(url == null) {
                client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + WRONG_URL_MSG).getBytes()));
                client.close();
                return;
            }
            if(body == null){
                /*client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER + CONTENT_TYPE_HTML + "\n" + EMPTY_BODY_ERR).getBytes()));
                client.close();*/
                body = new byte [] {};
                //return;
            }
            //client.write(ByteBuffer.wrap(("TASK = PUT "+message).getBytes()));
            try{
                String subUrl;
                int a = url.length()-1;
                while(url.charAt(a) != '/') a--;
                subUrl = url.substring(0,a);
                //System.out.println("SUB URL: " + subUrl);
                FileItem fileItem = fileItemCache.get(ROOT_PATH + subUrl);
                if(fileItem.isSecured() && !PasswordDecoder.correctInformations(password,fileItem)){
                    throw new Exception(WRONG_PASSWORD_EXCEPTION);
                }
                fileItemCache.put(ROOT_PATH + url,body);
                client.write(ByteBuffer.wrap((REQUEST_SUCCESS_HEADER + CONTENT_TYPE_HTML + "\n" + PUT_SUCCESS_BEGIN_MSG+url+PUT_SUCCESS_END_MGS).getBytes()));
            } catch (Exception e){
                if(e.getMessage() != null && e.getMessage().equals(WRONG_PASSWORD_EXCEPTION)){
                    client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_AUTHORIZATION + CONTENT_TYPE_HTML + "\n" + WRONG_PASSWORD_MSG).getBytes()));
                } else {
                    client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_INTERNAL_ERROR + CONTENT_TYPE_HTML + "\n" + INTERNAL_ERR_MSG).getBytes()));
                }
            }
            //readURL();
            //readLogin();
        } catch (Exception e) {
            System.out.println("readURL or readLogin exception");
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("close client exception...such a drag");
            //e.printStackTrace();
        }
    }

    protected void readBody(){
        int end = new String(message).indexOf(url) + url.length();
        try {

            boolean doubleSpace = ((int)message[end] == 10 && (int)message[end+1] == 13 && (int)message[end+2] == 10);
            while(end < message.length-2 && !doubleSpace){
                //System.out.println("chars: "+(int)msg[end]+" "+(int)msg[end+1]);
                doubleSpace = ((int)message[end] == 10 && (int)message[end+1] == 13 && (int)message[end+2] == 10);
                end++;
            }
            //System.out.println("chars: "+(int)msg[end]+" "+(int)msg[end+1]);
            //System.out.println("chars: "+(int)msg[end+1]+" "+(int)msg[end+2]);
            //System.out.println("line break "+(int)'\n');
            if(end < message.length-2) {
                end+=2;
            }
            //System.out.println("end = "+end + ", message.length = "+message.length);
            body = Arrays.copyOfRange(message,end,message.length);
        } catch (Exception e){
            //e.printStackTrace();
            body = null;
        }
        //System.out.println("BODY:|"+new String(body));
    }

   /* @Override
    protected void parseMessage(){
        byte [] msg = message;
        //System.out.println("Message:|"+new String(msg)+"|");
        char c;
        int beg;
        for(beg = 0; beg < msg.length; beg++){
            c = (char)msg[beg];
            if(c != ' ' && c != '/' && c != '.'){ // INVALID URL SET
                url = null;
                return;
            } else if(c == '/'){
                break;
            }
        }
        int end;
        for(end = beg; end < msg.length; end++){ // ten posledni znak uz nemuze nic zkazit
            if((char)msg[end] == ' ' || (char)msg[end] == '\n' || (int)msg[end] == 13){
                //System.out.println("posledni char "+(int)msg[end] + " predposledni char "+(int)msg[end-1]);

                url = new String(Arrays.copyOfRange(message,beg,end));
                //System.out.println(url+"==/admin ??? ->"+url.equals("/admin"));
                break;
            }
            if(end+2 < msg.length && ((char)msg[end] == '%' && (char)msg[end+1] == '2' && (char)msg[end+2] == '0')){
                url = new String(Arrays.copyOfRange(message,beg,end));
                end += 3;
                break;
            }
        }
        if(url == null || url.isEmpty()) {
            //System.out.println(end + "==" + message.length());
            url = new String(Arrays.copyOfRange(message,beg,end));
        }

        String msgStr = new String(msg);
        beg = msgStr.indexOf(ACCEPT_TYPE_REQUEST);
        if(beg == -1){
            acceptContent = null;
        } else {
            beg += ACCEPT_TYPE_REQUEST.length();
            end = beg;
            while(end < msg.length && (char)msg[end] != '\n') end++;
            acceptContent = msgStr.substring(beg,end-1);
        }

        beg = msgStr.indexOf(AUTHORIZATION_REQUEST);
        if(beg == -1){
            //System.out.println("no authorization");
            password = null;
        } else {
            beg += AUTHORIZATION_REQUEST.length();
            end = beg;
            while(end < msg.length && (char)msg[end] != '\n') end++;
            password = msgStr.substring(beg,end-1);
            //System.out.println("BasedPassword = |"+password+"|");
        }

        try {

            boolean doubleSpace = ((int)message[end] == 10 && (int)message[end+1] == 13 && (int)message[end+2] == 10);
            while(end < message.length-2 && !doubleSpace){
                //System.out.println("chars: "+(int)msg[end]+" "+(int)msg[end+1]);
                doubleSpace = ((int)message[end] == 10 && (int)message[end+1] == 13 && (int)message[end+2] == 10);
                end++;
            }
            //System.out.println("chars: "+(int)msg[end]+" "+(int)msg[end+1]);
            //System.out.println("chars: "+(int)msg[end+1]+" "+(int)msg[end+2]);
            //System.out.println("line break "+(int)'\n');
            if(end < message.length-2) {
                end+=2;
            }
            //System.out.println("end = "+end + ", message.length = "+message.length);
            body = Arrays.copyOfRange(message,end,message.length);
        } catch (Exception e){
            //e.printStackTrace();
            body = null;
        }
        //System.out.println("BODY:|"+new String(body));

        //System.out.println("URL: "+url);
        //System.out.println("password: "+password);
        //System.out.println("BODY: "+body);
    }*/

}
