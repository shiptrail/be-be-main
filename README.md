# Backend

start development with

```
 $ sbt run
```

To reformat all Scala files use `sbt scalafmt`. The build will may fail if some files are not formatted correctly.

To run all tests and see all compiler warnings use:

```
 $ sbt clean test
```

After `sbt stage` start the server in production mode with:

```
 $ ./target/universal/stage/bin/backend-server
```

## Creating Debian packages

The Debian packages come with systemd configurations which will automatically
start the server. 
 
systemd service name: backend-server
listen port: 9000
restart after unreachable for: 10 seconds

Execute:
```
 $ sbt clean debian:package-bin
```

The deb package can then be found in:
```
 target/backend-server_1.0-SNAPSHOT_all.deb
```

Note that the package depends on openjdk8-jre which should be available
in every recent Debian distribution. 

For Debian jessie you will need to activate the 
jessie-backports repo by adding:

```
deb http://ftp.debian.org/debian jessie-backports main
```

to ```/etc/apt/source.list```. Afterwards run 
```
 $ apt-get update; apt-get install openjdk8-jre
```

You may also need to configure the standard java version used by Debian
to openjdk8-jre by executing:
 ```
 $ update-alternatives --config java 
 ```

You may (re)install the package by executing (it is assumed that 
backend-server_1.0-SNAPSHOT_all.deb is in the current):
```
 $ dpkg -P backend-server; dpkg -i backend-server_1.0-SNAPSHOT_all.deb 
```

### Known issues
The logging is not configured correctly yet. We probably need to create a 
logger configuration for production environments in play.
