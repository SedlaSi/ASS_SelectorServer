package provider;

import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Created by root on 20.5.16.
 */
public class TestPoolProvider {

    @Test
    public void testGetPool(){
        PoolProvider poolProvider = new PoolProvider(5);
        assertNotNull(poolProvider.getPool());

    }
}

