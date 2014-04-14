# RiotAPI

This project is designed to offer a simple binding to the official
RiotGames League of Legends API.


## Shards

Shards are the different regions of Riot's servers. The *Shard* enum defines several constants
for each of these regions:

```java
Shard.name;         // The name, as used by the api
Shard.jabberUrl;    // The url of the chat server for this region
Shard.jabberPort;   // The port for the chat server
Shard.prodUrl;      // The url of the production server for this region
```

The *Shard* enum is not complete yet, however support for the major regions is added.
Planned:
* Add missing regions
* A couple of constants are missing, namely the prod ports as well as all URLs for the KR region


## RiotUtil

A place for all those functions that didn't find a home anywhere else, such as *standardizeSummonerName()*,
which removes all spaced and converts the string to lower case.


## REST

Initialize the API by creating an *ApiHandler* object by passing a *Shard* (the target region) and an API key:

```java
ApiHandler handler = new ApiHandler(Shard.EUW, "my-api-key");
```

Access api methods by calling the corresponding method on the handler:

```java
Map<String, Summoner> summoners = handler.getSummoners("Snoopeh", "Froggen", "Wickd");
```

Convenience methods are also available:

```java
Summoner froggen = handler.getSummoner("Froggen");
```

API methods return POJOs - all fields are available via getters. You can get an overview over the fields on the
official API website: https://developer.riotgames.com/api/methods

All API request have the potential to fail. Failure is signaled by a *RequestException*, which is for convenience
left unchecked. You can access the exact error code via *RequestException.getErrorType()*