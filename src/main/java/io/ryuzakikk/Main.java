package io.ryuzakikk;

import io.ryuzakikk.model.Lucky;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    public static void main(String[] args) {

        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.OFF);

        String configFile = "config.properties";
        String[] providers = {};
        int limit = 0;
        try {
            CacheBenchProperties properties = new CacheBenchProperties(configFile);
            limit = properties.limit;
            providers = properties.providers;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (limit > 0) {
            for (String provider : providers) {
                Cache<String, byte[]> cache = initializeCache("benchmark", provider, String.class, byte[].class);
                System.out.println("\n-- " + provider + " --");
                startLuckyBenchmark(cache, limit);
                closeCache(provider, cache);
            }
        } else {
            System.out.println("Limit value must be greater than zero.");
        }
    }

    private static void startLuckyBenchmark(Cache<String, byte[]> cache, int limit) {
        // Static seed for more consistent benchmarks
        long seed = 21;
        Random random = new Random(seed);
        byte[] arrayId = new byte[16]; // length bounded to 16

        // The first put is often way slower, de-commenting the following block the first put will be
        // excluded from the benchmark
        /*new Random().nextBytes(arrayId);
        try {
            cache.put("55", convertToBytes(new Lucky(new String(arrayId), random.nextInt(), random.nextInt())));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Lucky l;
        byte[] luckySerial = null;
        long startTime;
        long stopTime;
        long[] time = new long[limit];
        long sum = 0;
        String[] luckyId = new String[limit];
        // Benchmark for put statement
        for(int i = 0; i < limit; i++) {
            new Random().nextBytes(arrayId);
            l = new Lucky(new String(arrayId), random.nextInt(), random.nextInt());
            luckyId[i] = l.getLuckyId();
            try {
                luckySerial = convertToBytes(l);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startTime = System.nanoTime();
            cache.put(l.getLuckyId(), luckySerial);
            stopTime = System.nanoTime();
            time[i] = stopTime - startTime;
            sum += time[i];
        }
        System.out.println("PUT Benchmark (nanoseconds)");
        printStats(time, limit, sum);

        // Benchmark for get statement
        sum = 0;
        for(int i = 0; i < limit; i++) {
            startTime = System.nanoTime();
            cache.get(luckyId[i]);
            stopTime = System.nanoTime();
            time[i] = stopTime - startTime;
            sum += time[i];
        }
        System.out.println("\n\nGET Benchmark (nanoseconds)");
        printStats(time, limit, sum);

        // Benchmark for remove statement
        sum = 0;
        for(int i = 0; i < limit; i++) {
            startTime = System.nanoTime();
            cache.remove(luckyId[i]);
            stopTime = System.nanoTime();
            time[i] = stopTime - startTime;
            sum += time[i];
        }
        System.out.println("\n\nREMOVE Benchmark (nanoseconds)");
        printStats(time, limit, sum);
        System.out.println();
    }

    private static void printStats(long[] time, int limit, long sum) {
        Arrays.sort(time);
        int avg = (int) (sum / limit);
        System.out.format("%-12s", "min");
        System.out.format("%-12s", "max");
        System.out.format("%-12s", "avg");
        System.out.format("%-12s", "5 %ile");
        System.out.format("%-12s", "95 %ile");
        System.out.format("%-12s", "99 %ile");
        System.out.println();
        System.out.format("%-12d", time[0]);
        System.out.format("%-12d", time[limit - 1]);
        System.out.format("%-12d", avg);
        System.out.format("%-12d", time[(int) ((limit - 1) * 0.05)]);
        System.out.format("%-12d", time[(int) ((limit - 1) * 0.95)]);
        System.out.format("%-12d", time[(int) ((limit - 1) * 0.99)]);
    }

    private static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    private static <K, V> Cache<K, V> initializeCache(String name, String provider, Class<K> type1, Class<V> type2) {
        CachingProvider cp = Caching.getCachingProvider(provider);
        CacheManager cacheManager = cp.getCacheManager();
        // Create a typesafe configuration for the cache
        CompleteConfiguration<K, V> config = new MutableConfiguration<K, V>().setTypes(type1, type2);
        return cacheManager.createCache(name, config);
    }

    private static void closeCache(String provider, Cache<?, ?> cache) {
        cache.close();
        CachingProvider cp = Caching.getCachingProvider(provider);
        cp.close();
    }
}
