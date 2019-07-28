# Adrenalina - ing-sw-2019-39
First-person shooter table-game adapted for your computer: grab some ammo, grab a gun, and start shooting. Build up an arsenal for a killer turn. Combat resolution is quick and diceless. And if you get shot, you get faster!

Project work for "085923 - PROVA FINALE (INGEGNERIA DEL SOFTWARE)" - Politecnico di Milano.
# Introduction
The Javadoc can be found in docs/.

The slides used during the presentation can be found [here](https://github.com/bebora/sweng-pres).

The jars for the server, the client and the bot can be generated in target/assembly using 
```
mvn clean package
```

The latest jars that are discussed below can be found in the Release page, or in the jar/ directory.

*TESTED WITH JAVA >=11*
## Features
- Complete rules
- CLI 
- GUI 
- Socket 
- RMI 
- 2 Advanced Features:
  + Multiple matches
  + Domination Mode
# Server
The *server* package will need no external dependencies, and it can be run with:
```
java -jar server.jar
```
External configurations can be added in the same folder before running:
-  game.properties
```
time=30000
max_weapons=3
lobby_time=20000
skulls=8
max_marks=3
max_ammo=3
reward_points_normal=8,6,4,2,1
reward_points_frenzy=2,1,1,1
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
