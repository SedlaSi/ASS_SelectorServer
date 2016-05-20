package provider;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by root on 20.5.16.
 */
public class TestFileCacheProvider {

    @Test
    public void testGetFileItemCache(){
        FileCacheProvider fileCacheProvider = new FileCacheProvider();
        assertNotNull(fileCacheProvider.getFileItemCache());
    }
}
