Contributing to DeCS
====================

Build
-----

The project is build with [gradle](https://gradle.org/) using the wrapper:
```
$ ./gradlew clean check
```

For the _check_ task beside tests also a coverage check (using [jacoco](https://www.jacoco.org/jacoco/))
and static code analysis (using [spotbugs](https://spotbugs.github.io)) is done.


Branches ![CI status](https://travis-ci.com/diergo/decs.svg)
--------------------------------------------------------------

The branch `master` is the integration branch for the upcoming release.
To create a release just create a tag using the version.

Every branch is built continuously by
[Travis CI](https://travis-ci.com/github/diergo/decs) by executing the task `check`.
Any contribution can be done on a feature branch to be merged to the integration branch.
To do so, use a pull request.


Release
-------

By tagging on `master` a new release will be published and made accessible using [JitPack](https://jitpack.io/#de.diergo/decs).
