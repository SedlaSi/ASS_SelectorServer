package security;

import cache.FileItem;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static org.junit.Assert.*;
/**
 * Created by root on 19.5.16.
 */
public class TestPasswordDecoder {

    @Test
    public void testCorrectInformations(){
        ArrayList<byte []> passwords = new ArrayList<>();
        String username = "USER:";
        String pass = "PASS";
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        byte [] md5Pass = md5.digest(pass.getBytes());
        byte [] userPass = new byte [username.length() + md5Pass.length];

        System.arraycopy(username.getBytes(),0,userPass,0         ,username.length());
        System.arraycopy(md5Pass,0,userPass,username.length(),md5Pass.length);
        passwords.add(userPass);
        FileItem fileItem = new FileItem("This is body of file".getBytes(),passwords);
        byte [] rightPassBase64 = Base64.encodeBase64((username + pass).getBytes());
        byte [] wrongPassBase64 = Base64.encodeBase64("USER:WRONGPASS".getBytes());
        assertTrue(PasswordDecoder.correctInformations(new String(rightPassBase64),fileItem));
        assertFalse(PasswordDecoder.correctInformations(new String(wrongPassBase64),fileItem));

        assertFalse(PasswordDecoder.correctInformations(null,fileItem));
        wrongPassBase64 = Base64.encodeBase64("USER_PASS".getBytes());
        assertFalse(PasswordDecoder.correctInformations(new String(wrongPassBase64),fileItem));

    }

    @Test
    public void testPasswordsFrom(){
        String path = "/tmp/file";
        File f = new File(path);
        String firstPass = "USER:PASS";
        String secondPass = "USER2:PASS2";
        String thirdPass = "U:P";
        byte [] passByte = (firstPass + "\n" + secondPass + "\n" + thirdPass).getBytes();
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileUtils.writeByteArrayToFile(f, passByte);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        ArrayList<byte []> passwords = PasswordDecoder.passwordsFrom(path);

        assertNotNull(passwords);
        assertFalse(passwords.isEmpty());
        assertEquals(passwords.size(),3);

        assertEquals(new String(passwords.get(0)),firstPass);
        assertEquals(new String(passwords.get(1)),secondPass);
        assertEquals(new String(passwords.get(2)),thirdPass);

        assertNull(PasswordDecoder.passwordsFrom("/tmp/asdasd12553asdqew"));
        f.delete();
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        passwords = PasswordDecoder.passwordsFrom(path);
        assertNull(passwords);

        f.delete();
    }

}
