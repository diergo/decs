# DeCS: Diergo Easy CSV Streamable

This Java library supports parsing and generation of comma separated value
([csv](https://en.wikipedia.org/wiki/Comma-separated_values)) data as defined
in [RFC 4180](http://tools.ietf.org/html/rfc4180).

The format is a simple line based text format for table data. Many databases
or spreadsheet software like MS-Excel can use this data as an interchange
format. Each line is a row of the table, the column values are separated by a
comma (or an other character). If the separator or quote character is part of
a column value, the value has to be quoted.


## Usage [![Download](https://api.bintray.com/packages/aburmeis/maven/decs/images/download.svg)](https://bintray.com/aburmeis/maven/decs/_latestVersion)

The [package](src/main/java/diergo/csv) contains a tool box to read and write
CSV data using [Java 8 Streams](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
and [functional interfaces](https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html).
You can easily connect the functionaliy by using
[`map()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#map-java.util.function.Function-),
[`filter()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#filter-java.util.function.Predicate-)
and [`collect()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#collect-java.util.stream.Collector-)
of a [`Stream`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html).
As there are no direct dependencies of the tools you can simply extend it for
your needs by creating new lambdas or functional interfaces and inject them to
mappings and filters.

For an example usage have a look at the [package documentation](src/main/java/diergo/csv/package-info.java)
or the [integration test](src/test/java/diergo/csv/CsvIntegrationTest.java).
For the release notes, have a look at the [change log](CHANGELOG.md).


## Build ![CI status](https://travis-ci.org/aburmeis/decs.svg)

The project is build with [gradle](https://gradle.org/) continuously by
[Travis CI](https://travis-ci.org/aburmeis/decs/) and is published via
[Bintray](https://bintray.com/aburmeis/maven/decs/) to
[jcenter](http://jcenter.bintray.com/diergo/decs/) under [Apache License Version 2.0](LICENSE).
 
To integrate the library in your project, use the following dependency:

**gradle:**

```gradle
  compile 'diergo:decs:3.1.1'
```

**maven:**
```xml
  <dependency>
    <groupId>diergo</groupId>
    <artifactId>decs</artifactId>
    <version>3.1.1</version>
  </dependency>
```

It has no external dependencies except an optional
[SLF4J 1.7](http://www.slf4j.org) dependency for an error handler.
