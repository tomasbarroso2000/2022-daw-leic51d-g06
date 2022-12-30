# Exploding Battleships API

## Development time commands

* Execute the Gradle task extractUberJar so that the artifcats required for the server to run can be created and start docker image with development time services
```
gradlew extractUberJar
```
```
docker compose up --build --force-recreate 
```
OR
```
gradlew composeUp
```
    
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
