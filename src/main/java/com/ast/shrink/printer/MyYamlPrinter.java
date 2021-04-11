package com.ast.shrink.printer;

import com.ast.shrink.ast.MyNode;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for production of representations JavaFileName_YamlPrinter.txt.
 */
public class MyYamlPrinter {
    private static final int NUM_SPACES_FOR_INDENT = 4;
    public Path dir;

    public MyYamlPrinter() {
        this(Paths.get("."));
    };
    /*
    You can specify directory.
     */
    public MyYamlPrinter(Path dir) {
        this.dir = dir;
    }

    /*
    Internal supporting method.
     */
    private MyNode transformPathToMyNode(Path file) throws IOException, ClassNotFoundException{
        Path path = Paths.get(dir.toString(), file.toString());
        String fileName = path.toString();
        MyNode root = null;
        try (FileInputStream fin = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fin);
        ){
            root = (MyNode)ois.readObject();
        }
        return root;
    }

    /**
     * Constructs text representation for dumped shrinked AST.
     *
     * @param file path to dumped shrinked AST, one of those that are stored in JavaFileName.txt.
     * @return Text representation of shrinked AST, one those that are stored in  JavaFileName_YamlPrinter.txt files.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public String output(Path file) throws IOException, ClassNotFoundException{
        MyNode root = transformPathToMyNode(file);
        return output(root);
    }

    /**
     * Constructs text representation for shrinked AST.
     * @param node - root of AST.
     * @return Text representation of shrinked AST, one those that are stored in  JavaFileName_YamlPrinter.txt files.
     */

    public String output(MyNode node) {
        StringBuilder output = new StringBuilder();
        output.append("---");
        output(node, 0, output);
        output.append(System.lineSeparator() + "...");
        return output.toString();
    }

    /*
    Internal supporting method.
     */
    private void output(MyNode node, int level, StringBuilder builder) {
        builder.append(System.lineSeparator() + indent(level) + node.name);

        level++;
        for (MyNode child : node.children){
            output(child, level, builder);
        }
    }

    /*
    Internal supporting method
     */
    private static String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            for (int j = 0; j < NUM_SPACES_FOR_INDENT; j++)
                sb.append(" ");
        return sb.toString();
    }

    /**
     * Prints text representation of dumped shrinked AST to console
     * @param file path to dumped shrinked AST, one of those that are stored in JavaFileName.txt.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void print(Path file) throws IOException, ClassNotFoundException{
        MyNode root = transformPathToMyNode(file);
        print(root);
    }

    /**
     * Prints text representation of shrinked AST to console.
     * @param node - root of AST
     */

    public void print(MyNode node) {
        System.out.println(output(node));
    }

    /**
     * Prints text representation of dumped shrinked AST to file. Destination file will appear in the same directory, but
     * with suffix _YamlPrinter.
     * @param file path to dumped shrinked AST, one of those that are stored in JavaFileName.txt.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void printToFile(Path file) throws IOException, ClassNotFoundException{
        MyNode root = transformPathToMyNode(file);
        String tmp = file.toString();
        tmp = tmp.substring(0, tmp.lastIndexOf('.')) + "_YamlPrinter" + ".txt";
        Path fileName =  Paths.get(tmp);
        printToFile(root, fileName);
    }

    /**
     * Prints text representation of shrinked AST to file.
     *
     * @param node - root of shrinked AST.
     * @param file path to destination file.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void printToFile(MyNode node, Path file) throws IOException{
        String fileName =  Paths.get(dir.toString(), file.toString()).toString();
        try (FileWriter fileWriter = new FileWriter(fileName);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(output(node));
        }
    }
}
