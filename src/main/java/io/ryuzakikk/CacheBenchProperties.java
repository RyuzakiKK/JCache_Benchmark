package io.ryuzakikk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class CacheBenchProperties {
    final int limit;
    final String[] providers;

    CacheBenchProperties(String path) throws IOException {
        String limitDefault = "1000";
        String providersDefault = String.join(","
                , "com.hazelcast.cache.HazelcastCachingProvider"
                , "blazingcache.jcache.BlazingCacheProvider"
                , "org.infinispan.jcache.embedded.JCachingProvider");
        Properties prop = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream != null) {
            prop.load(inputStream);
            limit = Integer.parseInt(prop.getProperty("limit", limitDefault));
            providers = prop.getProperty("providers", providersDefault).split(",");
        } else {
            limit = Integer.parseInt(limitDefault);
            providers = providersDefault.split(",");
        }
    }
}
