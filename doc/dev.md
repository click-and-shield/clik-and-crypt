# Dev notes

## Start the development environment

```Batchfile
powershell.exe -NoExit -ExecutionPolicy Bypass -File .\start.ps1 -env env1
```

or

```Batchfile
powershell.exe -NoExit -ExecutionPolicy Bypass -File .\start.ps1 -env env2
```

## Build

```Batchfile
mvn clean compile
```

## Build and test locally

If you just want to quickly test the application:

```powershell
mvn clean javafx:run -Dargs="encrypt TOTO"

$input_file="$env:ROOT_DIR\test-data\input.txt"
Write-Output "${input_file}"
mvn clean javafx:run -Dargs="encrypt ${input_file}"

$input_file="$env:ROOT_DIR\test-data\input.txt"
Write-Output "${input_file}"
mvn -X clean javafx:run -Dargs="encrypt ${input_file}"

$input_file="$env:ROOT_DIR\test-data\input.txt"
Write-Output "${input_file}"
mvn clean javafx:run -Dargs="decrypt ${input_file}"
```

However, the previous test uses JavaFX modules. It does not use JavaFX JMODs.
It is essential to test the application when it is compiled using the JavaFX JMODs.
Indeed, the JMODs impose some constraints on the classes you can use in the application.

Using the JVM to execute the application:

```Powershell
java --module-path "C:\Users\denis\.m2\repository\org\openjfx\javafx-base\23\javafx-base-23-win.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-base\23\javafx-base-23.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-controls\23\javafx-controls-23-win.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-controls\23\javafx-controls-23.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-graphics\23\javafx-graphics-23-win.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-graphics\23\javafx-graphics-23.jar" --add-modules javafx.base,javafx.controls,javafx.graphics -classpath "C:\Users\denis\Documents\github\click-and-crypt\target\classes;C:\Users\denis\.m2\repository\commons-cli\commons-cli\1.9.0\commons-cli-1.9.0.jar;C:\Users\denis\.m2\repository\org\jetbrains\annotations\26.0.2\annotations-26.0.2.jar" org.shadow.click_and_crypt.Main encrypt C:\Users\denis\Documents\github\click-and-crypt\test-data\input.txt
```

> Please note that the command above is derived from the output of the following command (see next section):
>
> ```Batchfile
> $input_file="$env:ROOT_DIR\test-data\input.txt"
> mvn -X clean javafx:run -Dargs="encrypt ${input_file}"
> ```
>
> * Ensure that the arguments for `--module-path` and `-classpath` are enclosed in double quotes.
> * Be sure to remove any occurrences of the character "`,`".

## Get the command line for launching the application

You can instruct Maven to display the command line it executes. Ti do so, just set the option "`-X`":

```Batchfile
$input_file="$env:ROOT_DIR\test-data\input.txt"
mvn -X clean javafx:run -Dargs="encrypt ${input_file}"
```

Example of output:

```
[DEBUG] Executing command line: [C:\Users\denis\Documents\java\jdk-23\bin\java.exe, --module-path, C:\Users\denis\.m2\repository\org\openjfx\javafx-base\23\javafx-base-23-win.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-
base\23\javafx-base-23.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-controls\23\javafx-controls-23-win.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-controls\23\javafx-controls-23.jar;C:\Users\denis\.m2\repository
\org\openjfx\javafx-graphics\23\javafx-graphics-23-win.jar;C:\Users\denis\.m2\repository\org\openjfx\javafx-graphics\23\javafx-graphics-23.jar, --add-modules, javafx.base,javafx.controls,javafx.graphics, -classpath, C:\Users\den
is\Documents\github\click-and-crypt\target\classes;C:\Users\denis\.m2\repository\commons-cli\commons-cli\1.9.0\commons-cli-1.9.0.jar;C:\Users\denis\.m2\repository\org\jetbrains\annotations\26.0.2\annotations-26.0.2.jar, org.shadow.click_and_crypt.Main, encrypt, C:\Users\denis\Documents\github\click-and-crypt\test-data\input.txt]
```

## Useful Maven commands

Maven:

```powershell
mvn dependency:resolve-plugins
mvn dependency:tree
mvn dependency:list
mvn dependency:collect
mvn dependency:build-classpath
mvn dependency:build-classpath | ?{$_ -notmatch '^\[INFO\]'}
```

### Interesting links

* Icons: https://iconduck.com/icons/21681/lightning-bolt
* Colors: https://venngage.com/blog/pastel-color-palettes/
* Characters: https://www.compart.com/en/unicode/U+1F512
