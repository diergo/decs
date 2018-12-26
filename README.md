# DeCS: Diergo Easy CSV Streamable

This Java library supports parsing and generation of comma separated value
([csv](https://en.wikipedia.org/wiki/Comma-separated_values)) data as defined
in [RFC 4180](http://tools.ietf.org/html/rfc4180).

The format is a simple line based text format for table data. Many databases
or spreadsheet software like MS-Excel can use this data as an interchange
format. Each line is a row of the table, the column values are separated by a
comma (or an other character). If the separator or quote character is part of
a column value, the value has to be quoted.


Usage
-----

The [diergo.csv](src/main/java/diergo/csv) package contains a tool box to read and write
CSV data using [Java 8 Streams](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
and [functional interfaces](https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html).
You can easily connect the functionaliy by using
[`map()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#map-java.util.function.Function-),
[`filter()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#filter-java.util.function.Predicate-)
and [`collect()`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#collect-java.util.stream.Collector-)
of a [`Stream`](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html).
As there are no direct dependencies of the tools you can simply extend it for
your needs by creating new lambdas or functional interfaces and inject them to
mappings and filters:

```
  List<Map<String, String>> lines = Readers.asLines(new FileReader("input.csv", StandardCharsets.UTF_8))
      // CSV parser turns each line into a row
      .map(CsvParserBuilder.csvParser().separatedBy(',').build()).flatMap(Collection::stream)
      // turn each line into a map, the first line is treated as header with column names
      .map(Maps.toMaps()).flatMap(Collection::stream);

  lines.stream()
      // create a stream of rows with an initial header row containing the column names
      .map(Maps.toRowsWithHeader()).flatMap(Collection::stream)
      // CSV printer turns each row into one line
      .map(CsvPrinterBuilder.csvPrinter().separatedBy(',').build())
      .collect(Appendables.toAppendable(new FileWriter("output.csv", StandardCharsets.UTF_8), '\n'));
```

There are more ready to use helper functions to filter and map at [Rows](src/main/java/diergo/csv/Rows.java), [Maps](src/main/java/diergo/csv/Maps.java) and [Values](src/main/java/diergo/csv/Values.java).
Handling of comments, headers and separators is configured using the builders for CSV [parser](src/main/java/diergo/csv/CsvParserBuilder.java) and [printer](src/main/java/diergo/csv/CsvPrinterBuilder.java).
For the release notes, have a look at the [change log](CHANGELOG.md).


Dependency [![Download](https://api.bintray.com/packages/aburmeis/maven/decs/images/download.svg)](https://bintray.com/aburmeis/maven/decs/_latestVersion)
----------------------------------------------------------------------------------------------------------------------------------------------------------

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

The library has no external dependencies except two optional ones:
* [SLF4J 1.7](http://www.slf4j.org) for an [error handler](src/main/java/diergo/csv/ErrorHandlers.java)
* [javax.activation](https://stackoverflow.com/questions/46493613/what-is-the-replacement-for-javax-activation-package-in-java-9) for [MimeTypes](src/main/java/diergo/csv/MimeTypes.java) helper running on Java 11 or later.


License
-------

This library is published under [Apache License Version 2.0](LICENSE).