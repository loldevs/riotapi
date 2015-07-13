# RiotAPI

This is a Java 8 project designed to offer a simple binding to most League of Legends services. Right now this includes:

| module | description |
|--------|-------------|
|[domain](https://github.com/loldevs/riotapi/wiki/domain) | Holding common information for all other modules. Mostly copies of the client's classes transmitted via RTMP in [com.riotgames](../tree/master/domain/src/main/java/net/boreeas/riotapi/com/riotgames), but also the [Shard](../blob/master/domain/src/main/java/net/boreeas/riotapi/Shard.java) enum, holding constants for all regions |
|[loginqueue](https://github.com/loldevs/riotapi/wiki/loginqueue) | Login to a server. |
|[rest](https://github.com/loldevs/riotapi/wiki/rest) | The official rest API provided by Riot. Includes an unthrottled handler as well as a throttled, asynchronous handler with configurable rate limits |
|[rtmp](https://github.com/loldevs/riotapi/wiki/rtmp) | The part that makes the client run. |
|[spectator](https://github.com/loldevs/riotapi/wiki/spectator) | Spectate games. Supports loading spectator files (.rofl) and streaming via the rest API. For non-featured games, you will need to provide a decryption key, which can be retrieved via RTMP or REST. Note that decoding frames isn't completely supported yet |
|[xmpp](https://github.com/loldevs/riotapi/wiki/xmpp) | Connect to the chat server. A simple wrapper that takes care of menial tasks such as channel name encoding |

Take a look at the [wiki](https://github.com/loldevs/riotapi/wiki) for a complete documentation.

## Releases
See our [release listing](https://github.com/loldevs/riotapi/releases)

## Get It
We are on maven! Include the following in the <dependencies> section of your pom.xml:
```xml
<dependency>
  <groupId>net.boreeas</groupId>
  <artifactId>riotapi.$REQUIRED_MODULE</artifactId>
  <version>2.0.4</version>
</dependency>
```
