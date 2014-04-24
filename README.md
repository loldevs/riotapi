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

## RiotUtil

A place for all those functions that didn't find a home anywhere else, such as *standardizeSummonerName()*,
which removes all spaced and converts the string to lower case.


## REST

Initialize the API by creating an *ApiHandler* object by passing a *Shard* (the target region) and an API key:

```java
ApiHandler handler = new ApiHandler(Shard.EUW, "my-api-key");
```

Alternatively, you can use a *ThrottledApiHandler*, which will automatically space request to prevent hitting
the rate limit. Limits are specified as a timeframe, consisting of a *timeDelta* and a *TimeUnit* as well as a maximum
number of requests that may be sent in this timeframe. For example, for development, one such limit would have a timeDelta
of 10, a TimeUnit.SECONDS and 10 requests maximum (the "10 requests in 10 seconds" limit).
In fact, a *ThrottledApiHandler* using the development key limits can be created via *ThrottledApiHandler.developmentDefault*,
otherwise, you need to create it yourself:

```java
ThrottledApiHandler handler = new ThrottledApiHandler(Shard.EUW, "my-api-key", new Limit(10, TimeUnit.SECONDS, 10), new Limit(10, TimeUnit.MINUTES, 50);
```

Requests via a throttled handler will execute immediately if accessing the static-data part of the api. Otherwise,
a Future<T> is returned, where T is the type that the normal ApiHandler would return for this method. In this case, the
request will execute some time in the future (get() on the future object will block until the request has executed)

Access api methods by calling the corresponding method on the handler:

```java
// ApiHandler
Map<String, Summoner> summoners = handler.getSummoners("Snoopeh", "Froggen", "Wickd");
// ThrottledApiHandler
Future<Map<String, Summoner>> futureSummoners = handler.getSummoners("Snoopeh", "Froggen", "Wickd");
Map<String, Summoner> summoners = futureSummoners.get();
```

Convenience methods are also available:

```java
Summoner froggen = handler.getSummoner("Froggen");
```

API methods return POJOs - all fields are available via getters. You can get an overview over the fields on the
official API website: https://developer.riotgames.com/api/methods

All API request have the potential to fail. Failure is signaled by a *RequestException*, which is for convenience
left unchecked. You can access the exact error code via *RequestException.getErrorType()*
