# decs

Diergo Easy CSV Streamable

This Java library supports parsing and generation of comma separated value
([csv](https://en.wikipedia.org/wiki/Comma-separated_values)) data as defined
in [RFC 4180](http://tools.ietf.org/html/rfc4180).

The format is a simple line based text format for table data. Many databases
or spreadsheet software like MS-Excel can use this data as an interchange
format. Each line is a row of the table, the column values are separated by a
comma (or an other character). If the separator character is part of a column
value, the value has to be quoted double.

The library offers read and write support using
[Java 8 Streams](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
and [functional interfaces](https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html).
It has no dependencies

The project is build with [gradle](https://gradle.org/).
