package security;

import cache.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by root on 6.5.16.
 */
public final class PasswordDecoder {

    public static final String SECURITY_FILE = ".htaccess";

    private static String decodeBase64ToHT(String code){
        byte [] dec = DatatypeConverter.parseBase64Binary(code);
        return new String(dec);
    }

    public static boolean correctInformations(String password, FileItem fileItem){
        if(password == null && fileItem.getPasswords() != null && !fileItem.getPasswords().isEmpty()){
            System.out.println("weqe");
            return false;
        }
        //System.out.println("Pass:"+password);
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        String username;
        String decodedPassword = decodeBase64ToHT(password);
        //System.out.println("Decoded password:|"+decodedPassword+"|");
        int i = decodedPassword.indexOf(':');
        if(i == -1) {
            return false;
        }

        username = decodedPassword.substring(0,i+1);
        decodedPassword = decodedPassword.substring(i+1,decodedPassword.length());
        byte [] md5Pass = md5.digest(decodedPassword.getBytes());
        byte [] userPass = new byte [username.length() + md5Pass.length];

        System.arraycopy(username.getBytes(),0,userPass,0         ,username.length());
        System.arraycopy(md5Pass,0,userPass,username.length(),md5Pass.length);
        // WRITE INCOMMING PASSWORD AND USERNAME TO FILE
        /*try {
            FileUtils.writeByteArrayToFile(new File("/home/sedlasi1/Desktop/Skola/ASS/Semestralka/server/root/admin/.htaccess"), userPass);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //System.out.println("PASS: |"+new String(userPass));
        //System.out.println("SASS: |"+new String(fileItem.getPasswords().get(0)));
        for(i = 0; i < fileItem.getPasswords().size(); i++){
            if(Arrays.equals(fileItem.getPasswords().get(i),userPass)){
                return true;
            }
        }
        return false;
    }


    public static ArrayList<byte []> passwordsFrom(String fileUrl) {
        ArrayList<byte []> passwords = new ArrayList<>();
        byte [] file;
        InputStream in;
        try {
            in = new FileInputStream(new File(fileUrl));
            file = IOUtils.toByteArray(in);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        //System.out.println("FILE:|"+new String(file)+"|");
        int idx = 0;
        int end = 0;
        while(end < file.length && (char)file[end] != '\n') end++;
        passwords.add(Arrays.copyOfRange(file,idx,end));
        idx = end;
        end ++;
        while(end < file.length){
            while(end < file.length && (char)file[end] != '\n') end++;
            passwords.add(Arrays.copyOfRange(file,idx+1,end));
            idx = end;
            end ++;
        }
        if(passwords.size() == 1 && new String(passwords.get(0)).equals("")){
            passwords.remove(0);
        }

        if(passwords.isEmpty()){
            return null;
        }
        return passwords;

    }
}
