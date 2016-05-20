import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by root on 19.5.16.
 */
public class TestTCPServerSelector {

    private static String getNoLogReq = "GET /\n\n\n";
    private static String getNoLogRes = "HTTP/1.1 200 OK\n" +
            "Content-Type: text/html\n" +
            "\n" +
            "<html><body><h2>file</h2><br><h1>admin</h1><br></body></html>\n";
    private static String getLogWrongReq = "GET /admin S:T";
    private static String getLogWrongRes = "HTTP/1.1 401 Authorization failed\n" +
            "Content-Type: text/html\n" +
            "\n" +
            "<html><body><h1>Please insert correct username:password then try again.</h1></body></html>\n";
    private static String wrongPathGet = "HTTP/1.1 404 Page not found\n" +
            "Content-Type: text/html\n" +
            "\n" +
            "<html><body><h1>Wrong path, please try again.</h1></body></html>\n";
    private static String wrongPathDelete = "HTTP/1.1 404 Page not found\n" +
            "Content-Type: text/html\n" +
            "\n" +
            "<html><body><h1>No file or directory to be deleted.</h1></body></html>\n";

    @Test
    public void testMainGET(){
        String path = "/tmp/server";
        File serverMainFolder = new File(path);
        String pth = "/tmp/server/file";
        File file = new File(pth);
        String adminPath = "/tmp/server/admin";
        File adminFolder = new File(adminPath);
        String htaccessPath = "/tmp/server/admin/.htaccess";
        File htaccessFile = new File(htaccessPath);
        try {
            serverMainFolder.getParentFile().mkdirs();
            serverMainFolder.mkdir();
            file.getParentFile().mkdirs();
            file.createNewFile();
            adminFolder.mkdir();
            htaccessFile.createNewFile();
            FileUtils.writeByteArrayToFile(htaccessFile, "USER:PASS".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch (Exception e) {
            assertTrue(true);
        }


        Thread serverThead = new Thread(() -> {
            try {
                TCPServerSelector.SERVER_HOME_FOLDER = "/tmp/server";
                TCPServerSelector.main(new String [] {""});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThead.start();
        Socket client;
        PrintWriter out;
        BufferedReader in;
        try {
            synchronized (this){
                this.wait(500);
            }
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        StringBuilder strb = new StringBuilder();
        String line;
        try {
            out.write(getNoLogReq);
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(),getNoLogRes);
        } catch (Exception e){
            e.printStackTrace();
        }


        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write(getLogWrongReq);
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(),getLogWrongRes);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("GET a");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(), wrongPathGet);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("GET /blabla/jo");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(), wrongPathGet);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            out.write("GET /");
            out.flush();
            client.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        htaccessFile.delete();
        adminFolder.delete();
        file.delete();
        serverMainFolder.delete();


    }

    @Test
    public void testMainDELETE(){
        String pass = "PASS";
        String username = "USER:";
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
            return;
        }
        byte [] codedPassMD5 = md5.digest(pass.getBytes());
        String base64Code = Base64.encode((username + pass).getBytes());

        String path = "/tmp/server";
        File serverMainFolder = new File(path);
        String pth = "/tmp/server/file";
        File file = new File(pth);
        String adminPath = "/tmp/server/admin";
        File adminFolder = new File(adminPath);
        String htaccessPath = "/tmp/server/admin/.htaccess";
        File htaccessFile = new File(htaccessPath);
        String securedFilePath = "/tmp/server/admin/secured";
        File securedFile = new File(securedFilePath);
        try {
            serverMainFolder.getParentFile().mkdirs();
            serverMainFolder.mkdir();
            file.getParentFile().mkdirs();
            file.createNewFile();
            adminFolder.mkdir();
            htaccessFile.createNewFile();

            byte [] combined = new byte [username.length() + codedPassMD5.length];

            System.arraycopy(username.getBytes(),0,combined,0         ,username.length());
            System.arraycopy(codedPassMD5,0,combined,username.length(),codedPassMD5.length);

            FileUtils.writeByteArrayToFile(htaccessFile, combined);
            securedFile.createNewFile();
            FileUtils.writeByteArrayToFile(securedFile, "BODY".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }


        Thread serverThead = new Thread(() -> {
            try {
                TCPServerSelector.SERVER_HOME_FOLDER = "/tmp/server";
                TCPServerSelector.main(new String [] {""});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThead.start();
        Socket client;
        PrintWriter out;
        BufferedReader in;
        try {
            synchronized (this){
                this.wait(500);
            }
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        StringBuilder strb = new StringBuilder();
        String line;
        try {
            out.write("DELETE /gtgtgtgtgtgt");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(), wrongPathDelete);
        } catch (Exception e){
            e.printStackTrace();
        }


        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("DELETE /file");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(),"HTTP/1.1 200 OK\nContent-Type: text/html\n\n<html><body><h1>File /file has been deleted.</h1></body></html>\n");
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("DELETE asd ");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            System.out.println(strb);
            assertEquals(strb.toString(),wrongPathGet);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("DELETE /admin/secured  ");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(),getLogWrongRes);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.write("DELETE /admin/secured  \nAuthorization: Basic "+base64Code+" \n");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(),"HTTP/1.1 200 OK\n" +
                    "Content-Type: text/html\n" +
                    "\n" +
                    "<html><body><h1>File /admin/secured has been deleted.</h1></body></html>\n");
        } catch (Exception e){
            e.printStackTrace();
        }


        htaccessFile.delete();
        adminFolder.delete();
        file.delete();
        serverMainFolder.delete();

    }

    @Test
    public void testMainPUT(){
        String pass = "PASS";
        String username = "USER:";
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            assertTrue(false);
            return;
        }
        byte [] codedPassMD5 = md5.digest(pass.getBytes());
        String base64Code = Base64.encode((username + pass).getBytes());

        String path = "/tmp/server";
        File serverMainFolder = new File(path);
        String pth = "/tmp/server/file";
        File file = new File(pth);
        String adminPath = "/tmp/server/admin";
        File adminFolder = new File(adminPath);
        String htaccessPath = "/tmp/server/admin/.htaccess";
        File htaccessFile = new File(htaccessPath);
        String securedFilePath = "/tmp/server/admin/secured";
        File securedFile = new File(securedFilePath);
        try {
            serverMainFolder.getParentFile().mkdirs();
            serverMainFolder.mkdir();
            file.getParentFile().mkdirs();
            file.createNewFile();
            adminFolder.mkdir();
            htaccessFile.createNewFile();

            byte [] combined = new byte [username.length() + codedPassMD5.length];

            System.arraycopy(username.getBytes(),0,combined,0         ,username.length());
            System.arraycopy(codedPassMD5,0,combined,username.length(),codedPassMD5.length);

            FileUtils.writeByteArrayToFile(htaccessFile, combined);
            securedFile.createNewFile();
            FileUtils.writeByteArrayToFile(securedFile, "BODY".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }


        Thread serverThead = new Thread(() -> {
            try {
                TCPServerSelector.SERVER_HOME_FOLDER = "/tmp/server";
                TCPServerSelector.main(new String [] {""});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThead.start();
        Socket client;
        PrintWriter out;
        BufferedReader in;
        try {
            synchronized (this){
                this.wait(500);
            }
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        StringBuilder strb = new StringBuilder();
        String line;
        try {
            out.write("PUT /fiqqle");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            //System.out.println(strb);
            assertEquals(strb.toString(), "HTTP/1.1 200 OK\n" +
                    "Content-Type: text/html\n" +
                    "\n" +
                    "<html><body><h1>Successfully created a new file: /fiqqle</h1></body></html>\n");
        } catch (Exception e){
            e.printStackTrace();
        }
        File fiqqle = new File("/tmp/server/fiqqle");
        fiqqle.delete();

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("PUT asdas");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            System.out.println(strb);
            assertEquals(strb.toString(),wrongPathGet);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("PUT /admin/KK");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            System.out.println(strb);
            assertEquals(strb.toString(),getLogWrongRes);
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            strb = new StringBuilder();
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write("PUT /KK "+(char)10+(char)13+(char)10+"BODY");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            System.out.println(strb);
            assertEquals(strb.toString(),"HTTP/1.1 200 OK\n" +
                    "Content-Type: text/html\n" +
                    "\n" +
                    "<html><body><h1>Successfully created a new file: /KK</h1></body></html>\n");
        } catch (Exception e){
            e.printStackTrace();
            assertTrue(false);
        }

        File KK = new File("/tmp/server/KK");
        KK.delete();
        htaccessFile.delete();
        securedFile.delete();
        adminFolder.delete();
        file.delete();
        serverMainFolder.delete();
    }

    @Test
    public void testMainWrongInput(){
        Thread serverThead = new Thread(() -> {
            try {
                TCPServerSelector.SERVER_HOME_FOLDER = "/tmp/server";
                TCPServerSelector.main(new String [] {""});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThead.start();
        Socket client;
        PrintWriter out;
        BufferedReader in;
        try {
            synchronized (this){
                this.wait(500);
            }
            client = new Socket("localhost", 5012);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        StringBuilder strb = new StringBuilder();
        String line;
        try {
            out.write("wrong input");
            out.flush();
            while((line = in.readLine()) != null){
                strb.append(line).append('\n');
            }
            System.out.println(strb);
            assertEquals(strb.toString(), "HTTP/1.1 404 Page not found\n" +
                    "Content-Type: text/html\n" +
                    "\n" +
                    "<html><body><h1>Unknown command, please try again.</h1></body></html>\n");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
