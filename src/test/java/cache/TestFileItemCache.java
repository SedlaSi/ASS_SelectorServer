package cache;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import provider.FileCacheProvider;
import tasks.RunnableTask;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by root on 20.5.16.
 */
public class TestFileItemCache {

    @Test
    public void testGet(){
        RunnableTask.ROOT_PATH = "/tmp";
        FileItemCache fileItemCache = new FileCacheProvider().getFileItemCache();
        String path = "/tmp/file";

        FileItem fileItem;

        try {
            fileItem = fileItemCache.get("/tmp/qeqweqfss");
            assertTrue(false);
        } catch (Exception e){
            assertTrue(true);
        }

        File f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        try {
            fileItem = fileItemCache.get(path);
            assertNotNull(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        fileItemCache.getCachedData().get(path).clear();
        try {
            fileItem = fileItemCache.get(path);
            assertNotNull(fileItem);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        f.delete();
    }

    @Test
    public void testRemove(){
        RunnableTask.ROOT_PATH = "/tmp";
        FileItemCache fileItemCache = new FileCacheProvider().getFileItemCache();
        String path = "/tmp/file";

        FileItem fileItem;
        File f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            fileItemCache.remove(path);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            fileItemCache.remove("/tmp/asdadqeqweqweqeq");
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
        f.delete();

        path = "/tmp/.hidden";
        f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            fileItemCache.remove(path);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch (Exception e) {
            assertTrue(true);
        }

        f.delete();

    }

    @Test
    public void testPut(){
        RunnableTask.ROOT_PATH = "/tmp";
        FileItemCache fileItemCache = new FileCacheProvider().getFileItemCache();
        String path = "/tmp/.hidden";
        File f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            fileItemCache.put(path,null);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch (Exception e) {
            assertTrue(true);
        }
        f.delete();

        path = "/tmp/normal_file";
        f = new File(path);
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            fileItemCache.put(path,null);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(true);
        }
        try {
            fileItemCache.put(path,"body".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        f.delete();

    }
}
