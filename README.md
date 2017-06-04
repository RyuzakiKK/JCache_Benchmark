# JCache_Benchmark

This simple JCache benchmark will compare the time of PUT, GET and REMOVE statements between Hazelcast, BlazingCache and Infinispan.

The file `src/main/resources/configuration.properties` contains the configurations for the benchmark.
`limit` is the number of repetitions for every statements.
`providers` are the JCache providers to test. If you want to add other new providers remember to add their dependencies in the `pom.xml` file.
