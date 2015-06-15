# Changelog
---------

### 2.0.3
##### Domain
* Fixed a bug where multiple prod urls where not being parsed correctly

##### Rtmp
* LcdsGameInvitationStatus.getLobbyStatus has been renamed to checkLobbyStatus

### 2.0.1
##### Domain
* Fixed misspelled field name in com.riotgames.platform.gameinvite.contract.Member (hasDelegateInvitePower -> hasDelegatedInvitePower)



### 2.0.0
##### Rtmp
* Fixed bug where the heartbeat executor wasn't properly restarted on connection failure.

##### Loginqueue
* Switched to new auth mechanism (this breaks the login queue and rtmp backwards compatibility)

##### Rest
* Fixed crash if the response had no Content-Encoding header


### 1.6.0
##### Rest
* Added support for featured-games v1.0 and current-game v.10
* Both of those apis are structured slightly different from the usual rest apis. Use 'handler.featuredGamesHandler.getFeaturedGames()'


##### Rtmp
* Fixed NPE in close()


### 1.5.7
##### Domain
* Added missing game mutators to practice game config
* Added LobbyMetaData
* Updated Season

##### Rtmp
* Fixed typo in gameService.startChampSelect
* Fixed a bug where the handshake failed on slow connections
* Fixed an incorrect parameter to PlayerPreferenceService.loadPreferenceByKey

##### Rest
* Fixed an incorrect return type for the match endpoint
* Added missing fields to match
* Added maps endpoint to static data
* Added locales and localized messages endpoint to static dat<

##### Xmpp
* Added helper method for sending messages to summoner ids.
* Added method for joining a chat room without name hashing


### 1.5.6
##### Domain
* Queue ids now static
* Added invitee state
* Added lobby member class
* Fixed xmpp url loading for shards

##### Rtmp
* Fixed bug where bot game lobby creation called the normal team lobby creation
* Rtmp subscription channels now throw error if the channel is retrieved before its name is known
* Fixed a bug where accepting an invitation would decline the inviation
* Made all service names public
* Added javadoc to all services
* Deprecated ambiguous varargs sendRpc() methods, and replaced them with explicit versions
* Invoke callback throws IllegalStateException if wait times out but no value has been set
* Fixed bug where LCDS Heartbeats disconnected the client
* Fixed bug where wrong player class was used in SummonerTeamService.createPlayer
* Adjusted heartbeat interval
* Added automatic call to summonerTeamService.createPlayer, which if not called prevented some calls from returning

### 1.5.5
##### Xmpp
* Added toString() to RiotStatus that encodes the status in a XMPP-compatible presence format

##### Domain
* Added bot difficulty constants
* Added queue id constants
* Added missing classes for game invitations
* Added missing game modes
* Added queue type and queue id for the new custom game mode, nemesis pick
* Added "Master" league tier
* Added new map (SR Beta)
* Added new season

##### Rtmp
* Fixed bug where bot game invites would not pass the queue id
* Added Javadoc to LcdsGameInvitationService
* Made RtmpClient autocloseable
* Added automatic heartbeats to RtmpClient

### 1.5.4
##### General
* Unified sibling dependencies via ${project.version}

##### Xmpp
* Renamed field user and its getters to username since that was conflicting with getUser() from the superclass.

##### Spectator
* Added compression method to SpectatedGame and encryption method to GameEncryptionData to mirror decompression and decryption

### 1.5.3
##### General
* Fixed several versioning problems in the pom

##### Domain
* Updated several spectator URLs
* Updated the region tags for LAN and LAS, which were renamed to LA1 and LA2
* Added altNames to shards. This list is being maintained manually for now
* Added generic Shard.get that matches on shard region name, shard spectator platform and any of the alt names
* Shard getters now throw ShardNotFoundException if no shard with that name was found
* Added field "previousSeasonHighestTeamReward" to Summoner
* Added multiple fields to the dynamic client configuration sent at RTMP login
* Added missing constants to QueueType, GameType, GameMode

##### RTMP
* Improved exception handling
* Fixed UTF-8 encoding error

##### Login Queue
* Now always runs at least once, even if a timeout occurs before


### 1.5.2
##### Domain
* Added missing fields to GameDto

##### Rest
* Updated team to v2.4. Apologies, this should've been in v1.3.0

### 1.5.1
##### Spectator
* Fixed a bug where failing to pull a keyframe or chunk would crash GameUpdateTask

### 1.5.0
##### Domain
* Added getByName to Shard

##### Rtmp
* Added support for ignoring SSL certificate chain errors.
* Added missing fields: LoginDataPacket.restrictedGamesRemainingForRanked, LeagueList.nextApexUpdate, LeagueList.maxLeagueSize

##### Rest
* Added support for Status v1.0
* Added summoner id to ParticipantPlayer
* Added runes and masteries to Participant
* Added match game and mode to Match
* Added dominion victory score to PostMatchTeamOverview
* Added missing fields and constants for the Ascension Event

##### Spectator
* GameUpdateTask now supports callbacks for failed chunks and keyframes
* GameUpdateTask now supports multiple callbacks per event
* Deprecated: setOnError, setOnFinished, setOnChunkPulled, setOnKeyframePulled (use add* instead)
* InProgressGame now adds the encryption key to the metadata if it isn't transmitted by the server

### 1.4.0
##### Spectator
* GamePool.submit() now returns GameUpdateTask
* Added callbacks to GameUpdateTask for received chunks, received keyframes and when the game is finished

## 1.3.0
##### Domain
* Bugfix: Fixed incorrect chat urls for the Vietnam and Indonesian regions 
* Added fallback loading of older versions.

##### Rtmp
* Bugfix: Closing RtmpClient now also closes the underlying writer thread instead of just interrupting it

##### Rest
* Updated league to v2.5
* Added support for gzip-encoding
* Added new filter options for matchhistory calls
* Added new event types for match timelines
* Bugfix: Removed double API call in getMatch()

##### Spectator
* Deprecated singleton constructor of GamePool due to its inability to return running tasks.
* Added callbacks to interface with GameUpdateTask: onChunkPulled, onKeyframePulled, onFinished

## 1.2.0
##### Domain
* Dynamic loading for shard constants
* Removed: Shard.baseUrl
* Bugfix: Added missing Participant subclasses
* Bugfix: Fixed bug where extra spaces at the end of Shard constants resulted in illegal URIs

## 1.1.0
##### Domain
* Bugfix: Fixed wrong REST Api URLs

##### Rest
* Added support for match-v2.2
* Added support for matchhistory-v2.2
* Bugfixes: Various wrong endpoints and bad field types

------

## 1.0.0
* Initial release
