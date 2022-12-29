# decs

## Change Log

All notable changes to [this project](README.md) will be documented in this
file. This project adheres to [Semantic Versioning](http://semver.org).

### 3.2.1
* fix automatic module name to `diergo.csv`

### 3.2.0
* removed SLF4J Dependency, error logging now uses a more flexible BiFunction
* removed `MimeTypes`support
* moved to `diergo` account, changed group to full domain `de.diergo`

### 3.1.1
* minimal Java 9 support ([#7](https://github.com/diergo/decs/issues/7))
* built with Java 8 and 11
* [Appendables](src/main/java/diergo/csv/Appendables.java) are now supported instead of Writers ([#6](https://github.com/diergo/decs/issues/6))
* migrated tests to [JUnit 5](https://junit.org/junit5/)
* [Writers](src/main/java/diergo/csv/Writers.java) is deprecated and will be removed in a future release
* dropped RELEASE-suffix of version

### 3.1.0

Added
* [Rows](src/main/java/diergo/csv/Rows.java) has new method `toStringArray` ([#2](https://github.com/diergo/decs/issues/2))
* [Writers](src/main/java/diergo/csv/Writers.java) has new methods `toWriter` and `toWriterUnordered` with optional line separator ([#4](https://github.com/diergo/decs/issues/4))
* [Maps](src/main/java/diergo/csv/Maps.java) has many new methods to manipulate maps like `removingValue`, `renamingValue` and `renamingValue` ([#5](https://github.com/diergo/decs/issues/5))
* [Values](src/main/java/diergo/csv/Values.java) has a new method `convertedValue` to allow more flexible conversions than the former `parsedValue` only. 


### 3.0.1

Fixed
* [#1](https://github.com/diergo/decs/issues/1): Hanging parser on illegal input with errors ignored


### 3.0.0

The initial release. 

