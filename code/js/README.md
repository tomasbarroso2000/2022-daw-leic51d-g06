# Exploding Battleships Web Application

## Development time commands

* Execute the Gradle task extractUberJar so that the artifcats required for the server to run can be created
```
cd ../jvm
```
```
gradlew extractUberJar
```

* Start docker image with development time services and web client
```
docker compose up --build --force-recreate 
```

Note: The docker-compose.yaml file contains instructions on how to get the application to be visible to every device in the private network.

* Start shell on postgres container

```
docker exec -ti db-tests bash
```

* Start `psql` inside postgres container
```
psql -U dbuser -d db
```
* `psql` commands
  * `\h` - show help. 
  * `\d <table>` - show table.
  * `select ... ;` - execute query.
  * `\q` - quit `psql`.