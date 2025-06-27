Base Java Project
=================

This is a [Gradle][]-based Java project structure. Provided you have the [OpenJDK][] installed, the `gradlew` script will take care of all other dependencies.

[Java]: https://docs.oracle.com/javase/tutorial/
[Gradle]: https://gradle.org/
[OpenJDK]: https://adoptium.net/temurin/releases/

Put your source code in `src/main/java` (or in further subdirectories inside that, according to package). To change the name or package of the main class, make sure to update the `mainClass` line in the configuration file `build.gradle`.


## Running

To run your code (for debugging purposes), invoke the `gradlew` script with a `run` argument:

```
./gradlew run
```

If you need to provide command-line arguments:

```
./gradlew run --args="arg1 arg2 arg3"
```

If you run into permission problems:

```
bash gradlew run
```


## Linting and Testing

This project has been configured to use [PMD][] to check code quality, and (though you don't need it for OOSE) [JUnit 5][] to perform unit testing. To perform these steps:

[PMD]: https://docs.pmd-code.org/latest/
[JUnit 5]: https://junit.org/junit5/

```
./gradlew check
```

(Alternatively, run "`./gradlew build`".)

Look up and fix any of the [PMD warnings](), or [suppress][] them if/where appropriate.

[PMD warnings]: https://docs.pmd-code.org/latest/pmd_rules_java.html
[suppress]: https://docs.pmd-code.org/latest/pmd_userdocs_suppressing_warnings.html


## Logging

This project configures logging ([java.util.logging][]) so that you don't need to. Simply obtain logger objects with `Logger.getLogger()`, and insert logging statements where appropriate.

[java.util.logging]: https://docs.oracle.com/en/java/javase/21/core/java-logging-overview.html
