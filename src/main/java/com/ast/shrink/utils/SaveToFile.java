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

public class SaveToFile {
    public Path mainDir;
    public Path saveDir;
    public Integer numberOfShrinks;

    public SaveToFile(Integer numberOfShrinks){
        this(numberOfShrinks, Paths.get("./src/main/resources"), Paths.get("./src/main/Data"));
    }

    public SaveToFile(Integer numberOfShrinks, Path mainDir, Path saveDir){
        this.numberOfShrinks = numberOfShrinks;
        this.mainDir = mainDir;
        this.saveDir = saveDir;
    }


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
