package cache;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Created by root on 4.5.16.
 */
public class FileItemCache {

    private final Function<String, FileItem> loader;
    private final HashMap<String, SoftReference<FileItem>> cachedData;

    public FileItemCache(Function<String, FileItem> loader) {
        this.loader = loader;
        this.cachedData = new HashMap<>();
        ((FunctionFileItem) loader).setCachedData(cachedData);
    }

    HashMap<String, SoftReference<FileItem>> getCachedData() {
        return cachedData;
    }

    public FileItem get(String file) throws Exception {
        SoftReference<FileItem> r = cachedData.get(file);
        FileItem fileItem;
        if (r == null) {
            try {
                fileItem = loader.apply(file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


            if (fileItem == null) {
                throw new Exception("NO SUCH FILE OR DIRECTORY EXCEPTION");
            }
            cachedData.put(file, new SoftReference<>(fileItem));
        } else if ((fileItem = r.get()) == null) {
            fileItem = loader.apply(file);
            cachedData.replace(file, new SoftReference<>(fileItem));
        }

        return fileItem;
    }

    public void remove(String file) throws Exception {
        boolean exists;
        File f = new File(file);
        if (f.isHidden()) {
            throw new Exception("FILE NOT EXISTS EXCEPTION");
        }
        Path path = FileSystems.getDefault().getPath(file);
        //try{
        cachedData.remove(file);
        exists = Files.deleteIfExists(path);
        updateFolderAbove(file);
        /*} catch (Exception e){
            exists = false;
        }*/
        if (!exists) {
            throw new Exception("FILE NOT EXISTS EXCEPTION");
        }
    }

    public void put(String file, byte[] body) throws Exception {
        File f = new File(file);
        if (f.isHidden()) {
            throw new Exception("FILE NOT EXISTS EXCEPTION");
        }
        FileUtils.writeByteArrayToFile(new File(file), body);
        updateFolderAbove(file);
    }

    private void updateFolderAbove(String url) {
        byte[] path = new byte[0];
        try {
            path = url.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        char c;
        int i;
        for (i = path.length - 1; i > 0; i--) {
            c = (char) path[i];
            if (c == '/') break;
        }
        if ((i + 1) <= url.length()) {
            cachedData.remove(url.substring(0, i + 1));
        }
    }

}
