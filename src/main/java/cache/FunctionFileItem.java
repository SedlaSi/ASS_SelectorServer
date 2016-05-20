package cache;

import security.PasswordDecoder;
import tasks.RunnableTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by root on 4.5.16.
 */
public class FunctionFileItem implements Function<String,FileItem> {

    private static final String BEGIN_MSG = "<html><body>";
    private static final String END_MSG = "</body></html>";
    private static final String BEGIN_FOLDER = "<h1>";
    private static final String END_FOLDER = "</h1>";
    private static final String BEGIN_FILE = "<h2>";
    private static final String END_FILE = "</h2>";
    private static final String BREAK = "<br>";
    private static final String JPEG = ".jpeg";
    private static final String JPG = ".jpg";
    private static final String PNG = ".png";
    private HashMap<String, SoftReference<FileItem>> cachedData;

    /**
     * METHOD READS FILE AND RETURN RESULT AS <class>FileItem</class>.
     * CAN BE IMAGE OR FILE TEXT OR DUMP OF FOLDER!!!!
     * */
    @Override
    public FileItem apply(String file){
        File f = new File(file);
        if(f.isHidden()){
            return null;
        }
        FileItem fileItem;
        StringBuilder strb = new StringBuilder("");
        ArrayList<byte []> passwords = getPasswords(file);
        if(f.isDirectory()){
            // IF FILE PATH IS A FOLDER
            strb.append(RunnableTask.CONTENT_TYPE_HTML + "\n" + BEGIN_MSG);
            File[] listOfFiles = f.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && !listOfFiles[i].isHidden()) {
                    strb.append(BEGIN_FILE).append(listOfFiles[i].getName()).append(END_FILE).append(BREAK);
                } else if (listOfFiles[i].isDirectory() && !listOfFiles[i].isHidden()) {
                    strb.append(BEGIN_FOLDER).append(listOfFiles[i].getName()).append(END_FOLDER).append(BREAK);
                }
            }
            strb.append(END_MSG);
            fileItem = new FileItem(strb.toString().getBytes(),passwords);
        } else { //if(f.isFile())
            // IF FILE PATH IS A FILE
            String postFix = "";
            if(file.length() > 4){
                postFix = file.substring(file.length()-4,file.length());
            }
            if(postFix.equals(JPEG) || postFix.equals(JPG) || postFix.equals(PNG)){ // IF FILE PATH IS AN IMAGE
                String img;
                byte[] imageInByte = null;
                if(postFix.equals(JPEG) || postFix.equals(JPG)){
                    img = RunnableTask.CONTENT_TYPE_JPEG + "\n";

                    try{
                        BufferedImage originalImage = ImageIO.read(f);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write( originalImage, "jpg", baos );
                        baos.flush();
                        imageInByte = baos.toByteArray();
                        /*System.out.println("image:");
                        for(byte b: imageInByte) System.out.print((int)b+",");
                        System.out.println();*/
                    } catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    img = RunnableTask.CONTENT_TYPE_PNG + "\n";
                    try{
                        BufferedImage originalImage = ImageIO.read(f);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write( originalImage, "png", baos );
                        baos.flush();
                        imageInByte = baos.toByteArray();
                        /*System.out.println("image:");
                        for(byte b: imageInByte) System.out.print((int)b+",");
                        System.out.println();*/
                    } catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
                byte[] combined = null;
                if(imageInByte != null){
                    combined = new byte[img.length() + imageInByte.length];
                    System.arraycopy(img.getBytes(),0,combined,0         ,img.length());
                    System.arraycopy(imageInByte,0,combined,img.length(),imageInByte.length);
                }


                fileItem = new FileItem(combined,passwords);
            } else {  // IF FILE PATH IS A FILE
                try {
                    strb.append(RunnableTask.CONTENT_TYPE_HTML + "\n" + BEGIN_MSG + BEGIN_FILE);
                    String s;
                    BufferedReader bf = new BufferedReader(new FileReader(file));
                    while ((s = bf.readLine()) != null){
                        strb.append(s).append("\n");
                    }
                    strb.append(END_FILE + END_MSG);
                    bf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                fileItem = new FileItem(strb.toString().getBytes(),passwords);
            }
        }
        return fileItem;
    }

    private ArrayList<byte []> getPasswords(String file) {
        ArrayList<byte []> passwords = null;
        File f = new File(file);
        if(f.isDirectory()){ // v tom pripade musime projet i obsah teto slozky, jinak jdeme vyse pokud cesta neni /root
            File[] listOfFiles = f.listFiles();
            if(listOfFiles != null) {
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isHidden() && listOfFile.getName().equals(PasswordDecoder.SECURITY_FILE)) { //nasli jsme soubor s hesly
                        passwords = PasswordDecoder.passwordsFrom(file + "/" + PasswordDecoder.SECURITY_FILE);
                        return passwords;
                    }
                }
            }
        }

        //System.out.println("ROOT_PATH: "+RunnableTask.ROOT_PATH);
        if(f.getAbsolutePath().equals(RunnableTask.ROOT_PATH)){
            return passwords;
        }
        while(!f.getParent().equals(RunnableTask.ROOT_PATH) && passwords == null){
            //System.out.println("f address before: "+f.getAbsolutePath());
            f = f.getParentFile();
            //System.out.println("f address after: "+f.getAbsolutePath());
            SoftReference<FileItem> sf = cachedData.get(f.getAbsolutePath());
            FileItem fileItem;
            if(sf != null && (fileItem = sf.get()) != null){ // zkusim najit v cache jinak parsuju ze souboru
                passwords = fileItem.getPasswords();
            } else {
                passwords = PasswordDecoder.passwordsFrom(f.getAbsolutePath()+"/"+PasswordDecoder.SECURITY_FILE);
            }

        }

        return passwords;
    }

    void setCachedData(HashMap<String, SoftReference<FileItem>> cachedData){
        this.cachedData = cachedData;
    }
}
