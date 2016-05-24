package cache;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 4.5.16.
 */
public class FileItem {

    private final byte[] file;
    private final ArrayList<byte []> passwords;

    public FileItem(byte[] file, ArrayList<byte []> passwords){
        this.file = Arrays.copyOf(file,file.length);
        this.passwords = passwords;
    }

    public boolean isSecured(){
        return this.passwords != null;
    }

    public byte[] getFile() {
        return Arrays.copyOf(file,file.length);
    }

    public ArrayList<byte []> getPasswords() {
        return this.passwords;
    }

}
