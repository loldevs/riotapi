# RiotAPI

This is a Java 8 project designed to offer a simple binding to most League of Legends services. Right now this includes:

| module | description |
|--------|-------------|
|[domain](./domain) | Holding common information for all other modules. Mostly copies of the client's classes transmitted via RTMP in [com.riotgames](../tree/master/domain/src/main/java/net/boreeas/riotapi/com/riotgames), but also the [Shard](../blob/master/domain/src/main/java/net/boreeas/riotapi/Shard.java) enum, holding constants for all regions |
|[loginqueue](./loginqueue) | Login to a server. |
|[rest](./rest) | The official rest API provided by Riot. Includes an unthrottled handler as well as a throttled, asynchronous handler with configurable rate limits |
|[rtmp](./rtmp) | The part that makes the client run. |
|[spectator](./spectator) | Spectate games. Supports loading spectator files (.rofl) and streaming via the rest API. For non-featured games, you will need to provide a decryption key, which can be retrieved via RTMP |

Take a look at the [wiki](https://github.com/loldevs/riotapi/wiki) for a complete documentation.
