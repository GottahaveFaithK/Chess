# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

Sequence Diagram
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9TeAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBii3h8UITyYplGpVKSjDpagAxJCcGCMygcmA6SwwFmzMQ6KAoYAAa1FAxgmyQYHiMoGrJgwAQiuAHGlKAAHtkwBoOZTuXcEcSVPUxVAOUSRCoHZUbs8YAojUrTe0VegAKLmlTYAgFD23Ur3YrmeqBJzBYbjObqYC0hbLMNQbx1XVTOUGwMm6XyZXoKFmTiYW1c9TeqqelD1NA+BAIOMk+5NqmqWogY1gFCu5kDDns7R2lv3Yy1BQcDhq8XaPtegdz5vD0dK8cKHzalHAE-xGeN3dD3lLldr4-a92I7f3GFPYvoq1YtTdrAfnCra+sWrx6nKSz1PsILntq7QQDWaCQcslxxpQrZJhg9ThE4TgZhM4GfDAUHAsssHxPBiHIfsVzoBwpheL4ATQOwtIxAKcBhtIcAKDAAAyEBZIUmHMI61B+s0bRdL0BjqPkaAZrKcxrL8-wcFcIG8oBfojMsSnzIs3yqTsexLNCjxAWJVBIiWUAogggnCjieJgISr6GDuFJ7jSdKTvps5ebei78jAQoiq6EpSrZrKYAqSqqgAkmgVCGkga6ujFg72gmPpOh2MBdj2W4eVZfpJSlyAcBlcoRlGMaFJpOXwMgyYwKmACMGbLFmqg5gZ+aFtAyLTBe0BIAAXiguwwLRDZZQuVk2RFm7uby7n1DUSAAGbSsZAKxcaiXJala5NPofw7NegXZYtzoFd2varU1IH1OVJ1nVsOy1Sg0byWh8KJi1WFtU4nWjN13J9XmYwFkWw3kWNk3TbN9EwGjbbWduZI3ty9QHnIKBPvEZ4Xle6MyDjC6VEu-qrv6pObuTT3vhZfoCUJGSqP+mDk9pAO5eJoEEaWREkd85GUbWxEoXWTPVOhTUidhuEwGBIvzNL0FkRektIZrqF0Qx3h+P4XgoOgMRxIkZsW+zvhYCJa3y8WDTSGGfFhu0YbdD0smqPJwwSwh6D-VprPFkHiGYHzTvtsiTIOfY9vOWornFZ5nJDj5YBEyTcHB2gAWZ9l1MhQA4nS9P51H808s9eX1JHIdPQLmP5SA8QoCAqpN4UcUqjAb2VQaOsF8VwHOy6I3aojU3fb9saNYDJRgCmoPDKrEPZrmUGw0NMrT-Es-I-W9EtxT13UkYKDcEeF555e2hF-Odel-U0g33ShhEy+eWtnz9QuZ7mjuHCegsXgaWdhhIGq8YA4TwmgVWKMjZMX8IqNc-hsDClVHxK0MAy5yg0I7UqLsy6ex9vYOUgdR5R0apUABI9q4hxjrdfKlC5j2StAQrMbk-4ZxftnXOvdn57jvOXSuRNdZXWLgtVuNle7j1YXjTu3dGEUTHv3I6FU0pqKkczVufp6SH2PvPeqodFYwLXmDTeYwepQ13oNYsPhjFQAmlNMyp9zHY0vsOGAtIc49m4WoaRL8xH1ArswIJNpKZ1yUfxPBEAtr4MIYolmsI-TsLULgnIID0n8wxjpZYmTVB5gaOMTJCVpB5nauEYIgQQSbHiFqCchEDJjG+MkUAypqqfEMiCTJAA5OUhkLgwE6JAwW0CV7KwQTY4ppTylykqdU2p9TliNOaT0tpHSEBdK2aZfpcohlzBGWM5BnhjYBA4AAdjcE4FATgYhhmCHATiAA2eAY5DBBJgEUGBscaj1Ekh0ChVDD66wzIMuUEz4z0PDo3Gh6BIVHOhbkz8AKbKZJRPjccQTeHtgxXdbRWhxx+JRXMA68VB7HWHlCil+iCnFgAEImhTjkUxf0l6VCViDR5mZIY7xho4+oeg1yKlxKnOshtz610EffYRITRHBXCRIxFhRa5gLbgiphDU+FyLuh3LuPc1WUoHkPHRCiGUvVFC4txHAOWLygRY6ZINrG6VsQK-qQq4YHwRq4pGHjpV6ovjI3xOKUBBJRHStkiqgpvxgOG5Jcx3QavrnHMlcxKmpLhXkl4YwKlVJpEs6Q5k8map0vm4teYC0woVsvVq8CN6LMzYWjNKBKnnMYibSwN8HKbEtkgBIYAe09ggP2gAUhAYUSbDD+E6d3P5K8AUSSaPSaSPRMnUJ1RmbAuye1QDgBAByUA1g1q8Q8XNuiC47r3ZQQ9x7T3FtLeiuJAArKdaBI3vuneKly+K26EvysSgmCaj3QBgAAakHtIU1WiTowF3cAfd97wPgYLTzdGVrJ4wBZRwNlYAHWFDlpM51rVUx8o9dvL1e9iyipgL+yVyCZUxOzpGgtIi418hVZEuUKaYmasxbxzcmiZ0FQgMwLavgGzEYA2mwFOHWUoAleyyMP0zEybrdyyxvLVaUd6jvAaPq6MMZyFKhs591oJpUaqbaCHb0HrA7AbIMBqwhxE-hqaoHj3lmNKaFzoYiOYb4SQ+oAZfOWBDIhQjGG0Zcuai61MwRdN2IM96-ehpwv+aokx4NsqZTYBJRGuUUahPyA4yXLjooCsgcyb8hlDCgFDjRZZELSDzH1uBvAtrhsLmoK8EhgdQ7+vSkQEqWAwBsC7sIHkAovziEGJdm7D2XsfbGHPQwkA3A8AokJCw-V7cttQAUJN5AIAZtoB24o7xoa8aHYDAgS7qb41Lc9mGcsCAYBTkoKoR9WY1jkUgIhVQhIRMcj0AYGAlAiyxpuvt+oUPoDp1axaOqnKnUddgQAVlwk2re+nqPCs+xwNQo5iD1RgIkyHjj8SBvM8FnNn5AE+GASw1rIxa35J5Y2xB7PPFAA
