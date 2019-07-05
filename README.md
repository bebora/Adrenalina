# ing-sw-2019-39
## Funzionalità sviluppate
- Regole Complete
- CLI 
- GUI 
- Socket 
- RMI 
- 2 Funzionalità avanzate:
  + Partite multiple
  + Modalità dominazione
# Introduction
The Javadoc can be found in docs/.

The jars for the server, the client and the bot can be generated in target/assembly using 
```
clean mvn package
```

The latest jars that are discussed below can be found in the Release page, or in the target/ directory.

*TESTED WITH JAVA 12*
# Server
The *server* package will need no external dependencies, and it can be run with:
```
java -jar server.jar
```
External configurations can be added in the same folder before running:
-  game.properties
```
time=100000 
max_weapons=3
lobby_time=5000
```
- connection.properties
```
url=localhost
RMIPort=1099
SocketPort=1337
```
# Client
The *client* package will need JavaFX as an external dependency. You can easily find it here: https://gluonhq.com/products/javafx .
After downloading and extracting, you can use this command to run the client:
```
java --module-path $PATH_TO_JAVAFX --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar client.jar
```
## Bot
A small reference to our bot is needed, crucial to our testing phase. You can run it and play (just in localhost! Do not spam public servers please!) with:
```
java -jar bot.jar
```
