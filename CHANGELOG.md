# decs

## Change Log

All notable changes to [this project](README.md) will be documented in this
file. This project adheres to [Semantic Versioning](http://semver.org).
 

### 3.1.0
(2015-10-13)

Added

* [Rows](src/main/java/diergo/csv/Rows.java) has new method `toStringArray` ([#2](https://github.com/aburmeis/decs/issues/2))
* [Writers](src/main/java/diergo/csv/Writers.java) has new methods `toWriter` and `toWriterUnordered` with optional line separator ([#4](https://github.com/aburmeis/decs/issues/4))
* [Maps](src/main/java/diergo/csv/Maps.java) has many new methods to manipulate maps like `removingValue`, `renamingValue` and `renamingValue` ([#5](https://github.com/aburmeis/decs/issues/5))
* [Values](src/main/java/diergo/csv/Values.java) has a new method `convertedValue` to allow more flexible conversions than the former `parsedValue` only. 


### 3.0.1
(2015-10-07)

Fixed

* [#1](https://github.com/aburmeis/decs/issues/1): Hanging parser on illegal input with errors ignored


### 3.0.0
(2015-10-01)

The initial release. 

