## Build and run scenarios

```$bash
./gradlew clean build
java -jar build/libs/dynamo-ford-java-test-1.0-SNAPSHOT.jar -i soup=3 -i bread=2 -d 2020-03-12T12:00:00
java -jar build/libs/dynamo-ford-java-test-1.0-SNAPSHOT.jar -i apple=6 -i milk=1 -d 2020-03-12T12:00:00
java -jar build/libs/dynamo-ford-java-test-1.0-SNAPSHOT.jar -i apple=6 -i milk=1 -d 2020-03-17T12:00:00
java -jar build/libs/dynamo-ford-java-test-1.0-SNAPSHOT.jar -i apple=3 -i soup=2 -i bread=1 -d 2020-03-17T12:00:00
```