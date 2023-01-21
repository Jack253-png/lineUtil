package com.mcreater.lineutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);
        if (arguments.size() <= 1) {
            System.out.println("Usage: java -jar lineUtil.jar D:\\projects\\lineUtil;D:\\projects\\fxui3 .*.java;.*.cpp;.*.h");
        }

        Vector<File> files = new Vector<>();
        Vector<Pattern> patterns = new Vector<>();

        Arrays.stream(arguments.get(1).split(";")).forEach(s -> patterns.add(Pattern.compile(s)));

        Arrays.stream(arguments.get(0).split(";")).forEach(s -> {
            try {
                Files.walkFileTree(Paths.get(s), new FileVisitor<Path>() {
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        return FileVisitResult.CONTINUE;
                    }
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        patterns.forEach(pattern -> {
                            if (pattern.matcher(file.toFile().getName()).find()) {
                                files.add(file.toFile());
                            }
                        });
                        return FileVisitResult.CONTINUE;
                    }
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                System.err.println("An error occurred");
                e.printStackTrace();
            }
        });
        AtomicLong lines = new AtomicLong();
        files.forEach(file -> {
            try {
                System.out.printf("Finded source file %s\n", file.getName());
                BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())));
                lines.addAndGet(reader.lines().count());
                reader.close();
            } catch (Exception e) {
                System.err.println("An error occurred");
                e.printStackTrace();
            }
        });
        System.out.println(lines.get());
    }
}
