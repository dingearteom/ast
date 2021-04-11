# AST

This Java project contains a library for shrinking abstract syntax trees. For initial construction of ASTs it uses [![javaparser]()](https://github.com/javaparser/javaparser#javaparser).

## How To Compile Sources
The Project uses Maven build system. 
If you checked out the project from GitHub you can build the project with maven using:
```
mvn clean install
```
Then compile source files:
```
mvn javacc:javacc
```

## How to use

Open Class Main. A main function of it looks as follows:
```
public static void main( String[] args ) throws IOException, ClassNotFoundException {
        SaveToFile saver = new SaveToFile(2);
        saver.save();
}
```
2 here stands for number of shrinks that you can vary.
This code does the following:
* finds all the java files in folder src/main/resources. So, put all your java files you want to process here.
* constructs abstract syntax trees and shrinks them number of times you specified in SaveToFile constructor.
* saves all the data in folder src/main/Data using structure of folders in src/main/resources.

It saves three type of files:
* JavaFileName.txt where JavaFileName.java was in src/main/resources. It is just a dump of shrinked ast tree. I have class MyNode that I use for constructing 
abstract syntax trees and it is serializable, so it I dump. Such files can be used later for reconstructing ast tree and writing some its visual representation.
* JavaFileName_YamlPrinter.txt - file with visual representation of shrinked ast of file JavaFileName.java. It is constructed with src/main/java/com.ast.shrink/printer/MyYamlPrinter.
* JavaFileName.dot - it is intermediate representation of shrinked ast. It is used for building png files. Just use terminal command:
```
dot -Tpng JavaFileName.dot > ast.png
```
Though, it is worth doing only with really small code.


