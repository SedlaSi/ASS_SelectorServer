package provider;


import cache.FileItemCache;
import cache.FunctionFileItem;

/**
 * Created by root on 4.5.16.
 */
public class FileCacheProvider {
    private final FileItemCache fileItemCache;
    private final FunctionFileItem functionFileItem;


    public FileCacheProvider() {
        functionFileItem = new FunctionFileItem();
        fileItemCache = new FileItemCache(functionFileItem);
    }

    public FileItemCache getFileItemCache() {
        return fileItemCache;
    }
}
