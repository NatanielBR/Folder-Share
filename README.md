# Folder Share

Folder sharing is a program made in Kotlin and Ktor to share the running folder via an http URL. The default url is 0.0.0.0:8080 and there's no way to change it (I'll implement it later).

# How to use

* Download the latest binaries

* Execute jar file in terminal using: `java -jar bin.jar`.

# Arguments
* `--folder or -f` the folder path to share, by default is a same folder the executable.
* `--port or -p` the web port, by default is 8080.
* `--bind or -b` the address to bind, by default is 127.0.0.1 .
