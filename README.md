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