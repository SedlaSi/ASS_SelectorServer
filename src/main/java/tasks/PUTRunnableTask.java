package tasks;

import cache.FileItem;
import security.PasswordDecoder;
import provider.FileCacheProvider;
import server.TCPServerSelector;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by root on 4.5.16.
 */
public class PUTRunnableTask extends RunnableTask {

    byte[] body;
    private static final Logger logger = Logger.getLogger("PUTRunnableTask");

    public PUTRunnableTask(byte[] message, SocketChannel client, FileCacheProvider fileCacheProvider) {
        super(message, client, fileCacheProvider);
        operationTask = OperationTask.PUT;
    }

    @Override
    public void run() {
        try {
            parseMessage();
            if (url != null) {
                readBody();
            } else {
                client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_NOT_FOUND + CONTENT_TYPE_HTML + "\n" + WRONG_URL_MSG).getBytes("UTF-8")));
                client.close();
                return;
            }
            if (body == null) {
                body = new byte[]{};
            }
            try {
                String subUrl;
                int a = url.length() - 1;
                while (url.charAt(a) != '/') a--;
                subUrl = url.substring(0, a);
                FileItem fileItem = fileItemCache.get(TCPServerSelector.ROOT_PATH + subUrl);
                if (fileItem.isSecured() && !PasswordDecoder.correctInformations(password, fileItem)) {
                    throw new Exception(WRONG_PASSWORD_EXCEPTION);
                }
                fileItemCache.put(TCPServerSelector.ROOT_PATH + url, body);
                client.write(ByteBuffer.wrap((REQUEST_SUCCESS_HEADER + CONTENT_TYPE_HTML + "\n" + PUT_SUCCESS_BEGIN_MSG + url + PUT_SUCCESS_END_MGS).getBytes("UTF-8")));
                logger.fine("New file " + url + " created by user " + client.getLocalAddress());
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().equals(WRONG_PASSWORD_EXCEPTION)) {
                    client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_AUTHORIZATION + REQUIRED_AUTHENTICATION + CONTENT_TYPE_HTML + "\n" + WRONG_PASSWORD_MSG).getBytes("UTF-8")));
                } else {
                    client.write(ByteBuffer.wrap((REQUEST_FAILED_HEADER_INTERNAL_ERROR + CONTENT_TYPE_HTML + "\n" + INTERNAL_ERR_MSG).getBytes("UTF-8")));
                }
            }
        } catch (Exception e) {
            System.out.println("readURL or readLogin exception");
            e.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("close client exception...such a drag");
        }
    }

    private void readBody() {
        int end = 0;
        try {
            end = new String(message, "UTF-8").indexOf(url) + url.length();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {

            boolean doubleSpace = ((int) message[end] == 10 && (int) message[end + 1] == 13 && (int) message[end + 2] == 10);
            while (end < message.length - 2 && !doubleSpace) {
                doubleSpace = ((int) message[end] == 10 && (int) message[end + 1] == 13 && (int) message[end + 2] == 10);
                end++;
            }
            if (end < message.length - 2) {
                end += 2;
            }

            body = Arrays.copyOfRange(message, end, message.length);
        } catch (Exception e) {
            body = null;
        }
    }
}
