package com.ast.shrink.printer;

import com.ast.shrink.ast.MyNode;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyYamlPrinter {
    private static final int NUM_SPACES_FOR_INDENT = 4;
    public Path dir;

    public MyYamlPrinter() {
        this(Paths.get("."));
    };

    public MyYamlPrinter(Path dir) {
        this.dir = dir;
    }

    private MyNode transofrmPathToMyNode(Path file) throws IOException, ClassNotFoundException{
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

    public String output(Path file) throws IOException, ClassNotFoundException{
        MyNode root = transofrmPathToMyNode(file);
        return output(root);
    }

    public String output(MyNode node) {
        StringBuilder output = new StringBuilder();
        output.append("---");
        output(node, 0, output);
        output.append(System.lineSeparator() + "...");
        return output.toString();
    }

    public void output(MyNode node, int level, StringBuilder builder) {
        builder.append(System.lineSeparator() + indent(level) + node.name);

        level++;
        for (MyNode child : node.children){
            output(child, level, builder);
        }
    }

    private static String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++)
            for (int j = 0; j < NUM_SPACES_FOR_INDENT; j++)
                sb.append(" ");
        return sb.toString();
    }

    public void print(Path file) throws IOException, ClassNotFoundException{
        MyNode root = transofrmPathToMyNode(file);
        print(root);
    }

    public void print(MyNode node) {
        System.out.println(output(node));
    }

    public void printToFile(Path file) throws IOException, ClassNotFoundException{
        MyNode root = transofrmPathToMyNode(file);
        String tmp = file.toString();
        tmp = tmp.substring(0, tmp.lastIndexOf('.')) + "_YamlPrinter" + ".txt";
        Path fileName =  Paths.get(tmp);
        printToFile(root, fileName);
    }

    public void printToFile(MyNode node, Path file) throws IOException{
        String fileName =  Paths.get(dir.toString(), file.toString()).toString();
        try (FileWriter fileWriter = new FileWriter(fileName);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(output(node));
        }
    }
}
