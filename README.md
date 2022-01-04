# Networking game
![java](https://img.shields.io/badge/made%20in%20-Java-%23ff723b?logo=Java&logoColor=abcdef) [![CircleCI](https://circleci.com/gh/zaze06/Networking_game/tree/master.svg?style=shield)](https://circleci.com/gh/zaze06/Networking_game/tree/master)
[![text](https://img.shields.io/badge/using-org.json-%23ff723b?logo=json&logoColor=black)](https://www.json.org/json-en.html) ![version](https://img.shields.io/badge/Version-0.2-%23ff723b)

## Way?
Well I wanted to make a game me  and my siblings can play (Doesn't mean you can't). So that's way I made this game.
## Special thanks to
[catch23](https://stackoverflow.com/users/1498427/catch23) who made the maze algorithm

## How to play?
1. download the latest jar or build yor own from this repository
2. open a command prompt 
    1. Windows
       1. press win key (the key whit a window logo) or cmd
       2. type `cmd`
       3. press enter
    2. Mac Os
       1. press cmd(win key) + space
       2. type `terminal`
       3. press enter
    3. Linux
       1. press alt + ctrl + T
3. use the `$cd` to go to the directory of the downloaded jar file
4. type `$ java -jar game.jar server` to start the server. Note if you already have a server skip this step
5. type `$ java -jar game.jar <ip to server>` to connect to the server you may need to open a new command prompt from step 2
6. Have fun!
## How to compile your own
1. make sure the repository is buildable [![CircleCI](https://circleci.com/gh/zaze06/Networking_game/tree/master.svg?style=shield)](https://circleci.com/gh/zaze06/Networking_game/tree/master)
2. download the repo ether via the download button on GitHub or
    1. using git `$git clone https://github.com/zaze06/Networking_game.git`
    2. or by using your favorite IDE
3. either build it using gradle or compile it in other means
    1. if you're using gradle go into the directory of the repository
        1. open a command prompt in sad directory by following the second step in [How to play](https://github.com/zaze06/Networking_game#how-to-play)
        2. run `./gradlew build` or `gradlew build`. Note you might need to run `chmod +x ./gradlew` to make it executable
4. now you will have a jar called `game-<version>.jar` in `build/libs/` folder. note <version> is the curent version of the game
##TODO
- [X] Make client and server talk and send valid data
- [X] Send the map to the client
- [X] Send movment data to server
- [X] Update map on movment from any client
- [X] Shorten the map data packet
- [ ] Make the chat work
- [ ] Generate new maze when all players ar done(or generate a new one for the player that compleated the curent maze)
- [ ] Setup a account system(might not hapen at all)