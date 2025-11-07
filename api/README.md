API is generated from the JSON via

```

java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar generate \
-i ~/work/astra-ide-plugin/src/main/resources/apis/devops_v2_openapi.json \
-o ~/work/astra-ide-plugin/gen/devops_v2 \
-g kotlin \
--library jvm-retrofit2 \
--additional-properties=useCoroutines=true,serializationLibrary=gson,enumPropertyNaming=UPPERCASE,packageName=com.datastax.astra.devops_v2,artifactId=devops_v2-client


java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar generate \
-i ~/work/astra-ide-plugin/src/main/resources/apis/stargate_v2_openapi.json \
-o ~/work/astra-ide-plugin/gen/stargate_v2 \
-g kotlin \
--library jvm-retrofit2 \
--additional-properties=useCoroutines=true,serializationLibrary=gson,enumPropertyNaming=UPPERCASE,packageName=com.datastax.astra.stargate_v2,artifactId=stargate_v2-client
```


Then `src` and `doc` are replaced in the respective directories from that in `gen`

Using gson for JSON conversion because the enums in the spec don't match what comes across the wire.
