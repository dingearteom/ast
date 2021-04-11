package com.ast.shrink;

import com.ast.shrink.utils.SaveToFile;
import java.io.*;

public class Main
{
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        SaveToFile saver = new SaveToFile(2);
        saver.save();
    }
}
