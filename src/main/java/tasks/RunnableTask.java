package tasks;

import cache.FileItemCache;
import provider.FileCacheProvider;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by root on 4.5.16.
 */
public abstract class RunnableTask implements Runnable {

    protected final SocketChannel client;
    final FileItemCache fileItemCache;
    String url;
    String password;
    byte[] message;
    String acceptContent;
    public static String ROOT_PATH;
    static final String WRONG_URL_MSG = "<html><body><h1>Wrong path, please try again.</h1></body></html>";
    public static final String WRONG_PASSWORD_EXCEPTION = "WRONG PASSWORD EXCEPTION";
    public static final String WRONG_PASSWORD_MSG = "<html><body><h1>Please insert correct username:password then try again.</h1></body></html>";
    public static final String INTERNAL_ERR_MSG = "<html><body><h1>Internal server error, please repeat your task.</h1></body></html>";
    public static final String REQUEST_FAILED_HEADER = "HTTP/1.1 401 FAILURE\n";
    public static final String REQUEST_FAILED_HEADER_NOT_FOUND = "HTTP/1.1 404 Page not found\n";
    public static final String REQUEST_FAILED_HEADER_AUTHORIZATION = "HTTP/1.1 401 Authorization failed\n";
    public static final String REQUEST_FAILED_HEADER_INTERNAL_ERROR = "HTTP/1.1 501 Internal Error\n";

    public static final String CONTENT_TYPE_HTML = "Content-Type: text/html\n";
    public static final String CONTENT_TYPE_JPEG = "Content-Type: image/jpeg\n";
    public static final String CONTENT_TYPE_PNG = "Content-Type: image/png\n";
    public static final String REQUEST_SUCCESS_HEADER = "HTTP/1.1 200 OK\n";
    public static final String PUT_SUCCESS_BEGIN_MSG = "<html><body><h1>Successfully created a new file: ";
    public static final String PUT_SUCCESS_END_MGS = "</h1></body></html>";
    public static final String DELETE_SUCCESS_BEGIN_MSG =  "<html><body><h1>File ";
    public static final String DELETE_SUCCESS_END_MSG =  " has been deleted.</h1></body></html>";
    public static final String DELETE_ERR_MSG = "<html><body><h1>No file or directory to be deleted.</h1></body></html>";
    static final String ACCEPT_TYPE_REQUEST = "Accept: ";
    static final String AUTHORIZATION_REQUEST = "Authorization: Basic ";

    OperationTask operationTask;

    RunnableTask(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider){
        this.fileItemCache = fileCacheProvider.getFileItemCache();
        this.client = client;
        operationTask = null;
        this.message = message;
    }

    @Override
    public void run() {

    }

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
        //System.out.println("URL: |"+url+"|");
        // mame URL a ted musime najit content-type a authorization
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
        //System.out.println("AcceptContent:|"+acceptContent+"|");

        beg = msgStr.indexOf(AUTHORIZATION_REQUEST);
        if(beg == -1){
            //System.out.println("no authorization");
            password = null;
        } else {
            beg += AUTHORIZATION_REQUEST.length();
            end = beg;
            while(end < msg.length && (char)msg[end] != '\n') end++;
            password = msgStr.substring(beg,end-1);
            System.out.println("BasedPassword = |"+password+"|");
        }
        //System.out.println("Password: "+password);
        //System.out.println("URL: " + url);

    }

    public OperationTask getOperationTask(){
        return operationTask;
    }

}
