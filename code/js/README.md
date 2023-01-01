# Exploding Battleships Web Application

## Development time commands

* Execute the Gradle task extractUberJar so that the artifcats required for the server to run can be created
```
cd ../jvm
```
```
gradlew extractUberJar
```

* Compile source code to be run in a docker container
```
npm run build
```

* Start docker image with the client application ready to be used
```
docker compose up --build --force-recreate 
```

Note: The webpack.config.js file contains instructions on how to get the application to be visible to every device in the private network.
