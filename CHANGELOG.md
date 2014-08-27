# Changelog
---------
## *pending* 1.3.0
##### Domain
* Bugfix: Fixed incorrect chat urls for the Vietnam and Indonesian regions 
* Added fallback loading of older versions.

##### Rtmp
* Bugfix: Closing RtmpClient now also closes the underlying writer thread instead of just interrupting it

##### Rest
* Added support for gzip-encoding
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
