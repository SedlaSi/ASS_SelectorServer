package cache;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 4.5.16.
 */
public class FileItem {

    private byte[] file;
    private ArrayList<byte []> passwords;

    public FileItem(byte[] file, ArrayList<byte []> passwords){
        this.file = file;
        this.passwords = passwords;
    }

    public boolean isSecured(){
        return this.passwords != null;
    }

    public byte[] getFile() {
        return file;
    }

    public ArrayList<byte []> getPasswords() {
        return this.passwords;
    }
}
