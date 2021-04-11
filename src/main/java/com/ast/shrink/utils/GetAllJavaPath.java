package com.ast.shrink.utils;

import com.github.javaparser.utils.Log;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.utils.Utils.assertNotNull;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class GetAllJavaPath {

    private GetAllJavaPath(){};

    public static List<Path> getAllJavaPath(Path root) throws IOException {
        assertNotNull(root);
        List<Path> javaPath = new ArrayList<>();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!attrs.isDirectory() && file.toString().endsWith(".java")) {
                    javaPath.add(file);
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return isSensibleDirectoryToEnter(dir) ? CONTINUE : SKIP_SUBTREE;
            }
        });
        return javaPath;
    }

    private static boolean isSensibleDirectoryToEnter(Path dir) throws IOException {
        return !Files.isHidden(dir);
    }
}
