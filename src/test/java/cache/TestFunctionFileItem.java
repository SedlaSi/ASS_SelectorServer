package cache;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import tasks.RunnableTask;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by root on 20.5.16.
 */
public class TestFunctionFileItem {

    @Test
    public void testApply(){
        String path = "/tmp/.hidden";
        RunnableTask.ROOT_PATH = "/tmp";
        byte [] body = "This is body".getBytes();
        String incommingBody = "Content-Type: text/html\n" +
                "\n" +
                "<html><body><h2>This is body\n" +
                "</h2></body></html>";
        String incommingFolder = "Content-Type: text/html\n" +
                "\n" +
                "<html><body><h2>file</h2><br><h1>folder</h1><br></body></html>";
        byte [] incommingImageSmile = new byte [] {67,111,110,116,101,110,116,45,84,121,112,101,58,32,105,109,97,103,101,47,106,112,101,103,10,10,-1,-40,-1,-32,0,16,74,70,73,70,0,1,2,0,0,1,0,1,0,0,-1,-37,0,67,0,8,6,6,7,6,5,8,7,7,7,9,9,8,10,12,20,13,12,11,11,12,25,18,19,15,20,29,26,31,30,29,26,28,28,32,36,46,39,32,34,44,35,28,28,40,55,41,44,48,49,52,52,52,31,39,57,61,56,50,60,46,51,52,50,-1,-37,0,67,1,9,9,9,12,11,12,24,13,13,24,50,33,28,33,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,-1,-64,0,17,8,0,9,0,9,3,1,34,0,2,17,1,3,17,1,-1,-60,0,31,0,0,1,5,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,2,3,4,5,6,7,8,9,10,11,-1,-60,0,-75,16,0,2,1,3,3,2,4,3,5,5,4,4,0,0,1,125,1,2,3,0,4,17,5,18,33,49,65,6,19,81,97,7,34,113,20,50,-127,-111,-95,8,35,66,-79,-63,21,82,-47,-16,36,51,98,114,-126,9,10,22,23,24,25,26,37,38,39,40,41,42,52,53,54,55,56,57,58,67,68,69,70,71,72,73,74,83,84,85,86,87,88,89,90,99,100,101,102,103,104,105,106,115,116,117,118,119,120,121,122,-125,-124,-123,-122,-121,-120,-119,-118,-110,-109,-108,-107,-106,-105,-104,-103,-102,-94,-93,-92,-91,-90,-89,-88,-87,-86,-78,-77,-76,-75,-74,-73,-72,-71,-70,-62,-61,-60,-59,-58,-57,-56,-55,-54,-46,-45,-44,-43,-42,-41,-40,-39,-38,-31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-1,-60,0,31,1,0,3,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,2,3,4,5,6,7,8,9,10,11,-1,-60,0,-75,17,0,2,1,2,4,4,3,4,7,5,4,4,0,1,2,119,0,1,2,3,17,4,5,33,49,6,18,65,81,7,97,113,19,34,50,-127,8,20,66,-111,-95,-79,-63,9,35,51,82,-16,21,98,114,-47,10,22,36,52,-31,37,-15,23,24,25,26,38,39,40,41,42,53,54,55,56,57,58,67,68,69,70,71,72,73,74,83,84,85,86,87,88,89,90,99,100,101,102,103,104,105,106,115,116,117,118,119,120,121,122,-126,-125,-124,-123,-122,-121,-120,-119,-118,-110,-109,-108,-107,-106,-105,-104,-103,-102,-94,-93,-92,-91,-90,-89,-88,-87,-86,-78,-77,-76,-75,-74,-73,-72,-71,-70,-62,-61,-60,-59,-58,-57,-56,-55,-54,-46,-45,-44,-43,-42,-41,-40,-39,-38,-30,-29,-28,-27,-26,-25,-24,-23,-22,-14,-13,-12,-11,-10,-9,-8,-7,-6,-1,-38,0,12,3,1,0,2,17,3,17,0,63,0,-38,-77,-74,-79,-70,-5,14,-91,-86,-37,-69,94,52,49,73,123,113,38,-98,-69,-124,-118,-114,39,115,119,-57,-112,81,-40,124,-91,-111,-93,-5,54,-43,80,10,-125,-58,127,-62,91,-15,-89,-2,125,-75,-65,-4,18,47,-1,0,26,-90,-8,-69,-2,78,90,-33,-2,-62,-102,119,-2,-125,13,125,45,92,54,-27,-43,-21,113,-97,-1,-39};
        byte [] imageSmile = new byte [] {-1,-40,-1,-32,0,16,74,70,73,70,0,1,2,0,0,1,0,1,0,0,-1,-37,0,67,0,8,6,6,7,6,5,8,7,7,7,9,9,8,10,12,20,13,12,11,11,12,25,18,19,15,20,29,26,31,30,29,26,28,28,32,36,46,39,32,34,44,35,28,28,40,55,41,44,48,49,52,52,52,31,39,57,61,56,50,60,46,51,52,50,-1,-37,0,67,1,9,9,9,12,11,12,24,13,13,24,50,33,28,33,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,50,-1,-64,0,17,8,0,9,0,9,3,1,34,0,2,17,1,3,17,1,-1,-60,0,31,0,0,1,5,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,2,3,4,5,6,7,8,9,10,11,-1,-60,0,-75,16,0,2,1,3,3,2,4,3,5,5,4,4,0,0,1,125,1,2,3,0,4,17,5,18,33,49,65,6,19,81,97,7,34,113,20,50,-127,-111,-95,8,35,66,-79,-63,21,82,-47,-16,36,51,98,114,-126,9,10,22,23,24,25,26,37,38,39,40,41,42,52,53,54,55,56,57,58,67,68,69,70,71,72,73,74,83,84,85,86,87,88,89,90,99,100,101,102,103,104,105,106,115,116,117,118,119,120,121,122,-125,-124,-123,-122,-121,-120,-119,-118,-110,-109,-108,-107,-106,-105,-104,-103,-102,-94,-93,-92,-91,-90,-89,-88,-87,-86,-78,-77,-76,-75,-74,-73,-72,-71,-70,-62,-61,-60,-59,-58,-57,-56,-55,-54,-46,-45,-44,-43,-42,-41,-40,-39,-38,-31,-30,-29,-28,-27,-26,-25,-24,-23,-22,-15,-14,-13,-12,-11,-10,-9,-8,-7,-6,-1,-60,0,31,1,0,3,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,2,3,4,5,6,7,8,9,10,11,-1,-60,0,-75,17,0,2,1,2,4,4,3,4,7,5,4,4,0,1,2,119,0,1,2,3,17,4,5,33,49,6,18,65,81,7,97,113,19,34,50,-127,8,20,66,-111,-95,-79,-63,9,35,51,82,-16,21,98,114,-47,10,22,36,52,-31,37,-15,23,24,25,26,38,39,40,41,42,53,54,55,56,57,58,67,68,69,70,71,72,73,74,83,84,85,86,87,88,89,90,99,100,101,102,103,104,105,106,115,116,117,118,119,120,121,122,-126,-125,-124,-123,-122,-121,-120,-119,-118,-110,-109,-108,-107,-106,-105,-104,-103,-102,-94,-93,-92,-91,-90,-89,-88,-87,-86,-78,-77,-76,-75,-74,-73,-72,-71,-70,-62,-61,-60,-59,-58,-57,-56,-55,-54,-46,-45,-44,-43,-42,-41,-40,-39,-38,-30,-29,-28,-27,-26,-25,-24,-23,-22,-14,-13,-12,-11,-10,-9,-8,-7,-6,-1,-38,0,12,3,1,0,2,17,3,17,0,63,0,-39,-77,-74,-79,-69,-5,14,-89,-85,91,-69,94,52,49,73,123,113,38,-98,-69,-124,-118,-114,39,115,119,-57,-112,81,-40,124,-91,-111,-93,-5,54,-43,80,10,-125,-59,-1,0,-62,93,-15,-81,-2,125,-75,-65,-4,18,47,-1,0,26,-90,-8,-65,-2,78,98,-33,-2,-62,-102,119,-2,-125,13,125,49,94,124,97,-56,-18,-35,-18,86,-25,-1,-39};
        byte [] incommingImageXbox = new byte[] {67,111,110,116,101,110,116,45,84,121,112,101,58,32,105,109,97,103,101,47,112,110,103,10,10,-119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,0,0,0,9,0,0,0,9,8,6,0,0,0,-32,-111,6,16,0,0,0,-96,73,68,65,84,120,-38,99,72,75,75,99,-128,98,85,32,-34,9,-60,-33,-128,120,55,16,107,-64,-28,64,4,55,16,111,2,-30,34,-88,-62,108,32,86,7,-30,124,32,-34,12,-60,60,32,69,-75,64,-4,31,-118,-115,-96,-70,-11,-112,-60,-102,64,2,-41,-95,-100,62,32,102,5,-30,120,32,102,1,-30,46,-88,-8,77,-112,-94,-17,64,124,17,-120,25,-127,120,1,84,98,30,-44,-60,115,64,-4,3,-60,120,0,-60,126,32,99,-111,-84,0,-31,14,32,-10,6,-30,-121,32,69,-111,64,92,-126,-90,0,-122,-101,-127,56,26,-26,-3,-43,56,20,109,-122,5,1,8,-13,3,113,35,16,95,1,-30,-105,64,124,21,-120,91,-128,88,0,36,15,0,-57,48,-114,9,125,118,40,-32,0,0,0,0,73,69,78,68,-82,66,96,-126};
        byte [] imageXbox = new byte [] {-119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,0,0,0,9,0,0,0,9,8,6,0,0,0,-32,-111,6,16,0,0,0,-96,73,68,65,84,120,-38,99,72,75,75,99,-128,98,85,32,-34,9,-60,-33,-128,120,55,16,107,-64,-28,64,4,55,16,111,2,-30,34,-88,-62,108,32,86,7,-30,124,32,-34,12,-60,60,32,69,-75,64,-4,31,-118,-115,-96,-70,-11,-112,-60,-102,64,2,-41,-95,-100,62,32,102,5,-30,120,32,102,1,-30,46,-88,-8,77,-112,-94,-17,64,124,17,-120,25,-127,120,1,84,98,30,-44,-60,115,64,-4,3,-60,120,0,-60,126,32,99,-111,-84,0,-31,14,32,-10,6,-30,-121,32,69,-111,64,92,-126,-90,0,-122,-101,-127,56,26,-26,-3,-43,56,20,109,-122,5,1,8,-13,3,113,35,16,95,1,-30,-105,64,124,21,-120,91,-128,88,0,36,15,0,-57,48,-114,9,125,118,40,-32,0,0,0,0,73,69,78,68,-82,66,96,-126};
        File f = new File(path);
        FileItem fileItem;
        FunctionFileItem func = new FunctionFileItem();
        HashMap<String, SoftReference<FileItem>> cachedData = new HashMap<>();

        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        func.setCachedData(cachedData);
        fileItem = func.apply(path);
        assertNull(fileItem);
        f.delete();


        path = "/tmp/file";
        f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileUtils.writeByteArrayToFile(f, body);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        fileItem = func.apply(path);
        assertNotNull(fileItem);
        assertNotNull(fileItem.getFile());
        assertEquals(new String(fileItem.getFile()),incommingBody);
        f.delete();

        fileItem = func.apply(path);
        assertNull(fileItem);

        path = "/tmp/smile.jpg";
        f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileUtils.writeByteArrayToFile(f, imageSmile);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        fileItem = func.apply(path);
        assertNotNull(fileItem);
        assertNotNull(fileItem.getFile());
        /*System.out.println("result:");
        for(byte b : fileItem.getFile()) System.out.print((int)b+",");
        System.out.println();*/
        assertEquals(new String(fileItem.getFile()),new String(incommingImageSmile));
        f.delete();

        path = "/tmp/xbox.png";
        f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileUtils.writeByteArrayToFile(f, imageXbox);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        fileItem = func.apply(path);
        assertNotNull(fileItem);
        assertNotNull(fileItem.getFile());
        /*System.out.println("result:");
        for(byte b : fileItem.getFile()) System.out.print((int)b+",");
        System.out.println();*/
        assertEquals(new String(fileItem.getFile()),new String(incommingImageXbox));
        f.delete();

        path = "/tmp/wrong.png";
        f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        fileItem = func.apply(path);
        assertNull(fileItem);
        f.delete();

        path = "/tmp/wrong.jpg";
        f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        fileItem = func.apply(path);
        assertNull(fileItem);
        f.delete();

        path = "/tmp/my_dir";
        f = new File(path);
        if(!f.mkdir()){
            f.delete();
            assertTrue(false);
            return;
        }

        String filePath = "/tmp/my_dir/file";
        File filePathFile = new File(filePath);
        String folderPath = "/tmp/my_dir/folder";
        File folderPathFile = new File(folderPath);
        try {
            if(!folderPathFile.mkdir()){
                filePathFile.delete();
                try {
                    FileUtils.deleteDirectory(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                folderPathFile.delete();
                assertTrue(false);
                return;
            }
            filePathFile.getParentFile().mkdirs();
            filePathFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            filePathFile.delete();
            try {
                FileUtils.deleteDirectory(f);
            } catch (IOException ae) {
                e.printStackTrace();
            }
            folderPathFile.delete();
            assertTrue(false);
            return;
        }

        fileItem = func.apply(path);
        assertNotNull(fileItem);
        assertEquals(new String(fileItem.getFile()),incommingFolder);
        assertNull(fileItem.getPasswords());

        String securedFilePath = "/tmp/my_dir/.htaccess";
        byte [] passwords = "USER:PASS\nADMIN:PASS".getBytes();
        File securedFile = new File(securedFilePath);
        try {
            securedFile.getParentFile().mkdirs();
            securedFile.createNewFile();
            FileUtils.writeByteArrayToFile(securedFile, passwords);
        } catch (IOException e) {
            e.printStackTrace();
            securedFile.delete();
            filePathFile.delete();
            try {
                FileUtils.deleteDirectory(f);
            } catch (IOException asde) {
                e.printStackTrace();
            }
            folderPathFile.delete();
            assertTrue(false);
            return;
        }
        cachedData.clear();
        func.setCachedData(cachedData);
        fileItem = func.apply(path);
        assertNotNull(fileItem);
        assertNotNull(fileItem.getFile());
        assertNotNull(fileItem.getPasswords());

        SoftReference<FileItem> sf = new SoftReference<FileItem>(fileItem);

        fileItem = func.apply(filePath);
        assertNotNull(fileItem);
        assertNotNull(fileItem.getFile());
        assertNotNull(fileItem.getPasswords());

        cachedData.put(path,sf);
        fileItem = func.apply(filePath);
        assertNotNull(fileItem);
        assertNotNull(fileItem.getFile());
        assertNotNull(fileItem.getPasswords());
        assertTrue(fileItem.isSecured());


        securedFile.delete();
        filePathFile.delete();
        try {
            FileUtils.deleteDirectory(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        folderPathFile.delete();



    }

}
