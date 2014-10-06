# Changelog
---------
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
