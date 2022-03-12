### Peer-to-peer file exchange (Torrent server)
*Simple peer-to-peer system for file exchange that uses a server for discovering users and files.*


The implementation consists of two components:
- Server - stores metadata about available files
- Client - shares files with other clients and gets information from the server about files availability

## Technologies
- Java 17
- Mockito, JUnit 5 (for unit tests)
## Usage
### Launch server
```
/usr/share/jdk/jdk-17/bin/java -jar out/artifacts/p2p_file_exchange_server_jar/p2p_file_exchange.jar <SERVER_PORT> <SERVER_LOG_FILES_DIR>
```
### Launch client
```
/usr/share/jdk/jdk-17/bin/java -jar out/artifacts/p2p_file_exchange_client_jar/p2p_file_exchange.jar <SERVER_ADDR> <SERVER_PORT> <PEERS_FILE> <CLIENT_LOG_FILES_DIR>
```
### Register files available from a client
```
TorrentClient=# register <USER> <FILE1, FILE2, ..., FILEN>
```
### Unregister files from a client
```
TorrentClient=# unregister <USER> <FILE1, FILE2, ..., FILEN>
```
### Get a list of all available files from the server
```
TorrentClient=# list-files
```
Returns all registered files on the server in the following format:
```<PEER> : <FILE_PATH>```
Entries are separated with a newline char ('\n').
### Download file from a peer
```
TorrentClient=# download <PEER> <SOURCE> <DESTINATION>
```
