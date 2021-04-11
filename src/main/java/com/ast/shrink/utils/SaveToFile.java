package com.ast.shrink.utils;

import com.ast.shrink.ast.CopyAST;
import com.ast.shrink.ast.MyNode;
import com.ast.shrink.ast.ShrinkAST;
import com.ast.shrink.printer.MyDotPrinter;
import com.ast.shrink.printer.MyYamlPrinter;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;

import javax.security.sasl.SaslClient;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves various representations of shrinked AST trees from java files from mainDir to directory saveDir.
 * Saves three types of files:
 * JavaFileName.txt - dump of shrinked AST tree
 * JavaFileName_YamlPrinter.txt - visualization of shrinked AST tree. It is constructed with MyYamlPrinter from src/main/java/com.ast.shrink/printer
 * JavaFileName.dot - intermediate representation of shrinked AST tree Is is constructed with MyDotPrinter from src/main/java/com.ast.shrink/printer
 * You can transform it to png with terminal command:
 * dot -Tpng JavaFileName.dot > ast.png
 *
 */
public class SaveToFile {
    public Path mainDir;
    public Path saveDir;
    public Integer numberOfShrinks;

    public SaveToFile(Integer numberOfShrinks){
        this(numberOfShrinks, Paths.get("./src/main/resources"), Paths.get("./src/main/Data"));
    }

    /**
     *
     * @param numberOfShrinks number of shrinks to perform on AST
     * @param mainDir directory with java files. By default it is ./src/main/resources
     * @param saveDir directory to which we save representations. By default it is ./src/main/Data
     */
    public SaveToFile(Integer numberOfShrinks, Path mainDir, Path saveDir){
        this.numberOfShrinks = numberOfShrinks;
        this.mainDir = mainDir;
        this.saveDir = saveDir;
    }

    /**
     * Saves representations of shrinkes ASTs.
     * @throws IOException
     */
    public void save() throws IOException {
        SourceRoot sourceRoot = new SourceRoot(mainDir);

        List<Path> javaPath = GetAllJavaPath.getAllJavaPath(mainDir);
        List<Path> saveDirs = new ArrayList<>();
        for (Path path : javaPath){
            saveDirs.add(Paths.get(saveDir.toString(), mainDir.relativize(path.getParent()).toString()));
        }
        for (int index = 0; index < javaPath.size(); index++){
            Path path = javaPath.get(index);
            Path dir = saveDirs.get(index);
            CompilationUnit cu = sourceRoot.parse("", mainDir.relativize(path).toString());
            MyNode root = CopyAST.copyAST(cu);
            ShrinkAST shrinker = new ShrinkAST();
            root = shrinker.shrink(root, numberOfShrinks);

            /*
            Save dot
             */
            MyDotPrinter printer = new MyDotPrinter();
            String tmp = path.getFileName().toString();
            tmp = tmp.substring(0, tmp.lastIndexOf('.')) + ".dot";
            String fileName =  Paths.get(dir.toString(), tmp).toString();
            Files.createDirectories(dir);
            try (FileWriter fileWriter = new FileWriter(fileName);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.print(printer.output(root));
            }

            /*
            Save ast as serializable object to txt file
             */
            tmp = path.getFileName().toString();
            tmp = tmp.substring(0, tmp.lastIndexOf('.')) + ".txt";
            fileName =  Paths.get(dir.toString(), tmp).toString();
            try(FileOutputStream fout = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fout);){
                oos.writeObject(root);
            }

            /*
            Save with YamlPrinter
             */
            tmp = path.getFileName().toString();
            tmp = tmp.substring(0, tmp.lastIndexOf('.')) + "_YamlPrinter" + ".txt";
            fileName =  Paths.get(dir.toString(), tmp).toString();

            MyYamlPrinter myYamlPrinter = new MyYamlPrinter();
            try (FileWriter fileWriter = new FileWriter(fileName);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.print(myYamlPrinter.output(root));
            }
        }
    }

}
