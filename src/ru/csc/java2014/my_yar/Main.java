package ru.csc.java2014.my_yar;

import javax.swing.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
/*        if (args.length != 3)
            printUsageAndExit();

        String cmd = args[0];
        File dir = new File(args[1]);
        File archive = new File(args[2]);

        switch (cmd) {
            case "pack":
                pack(dir, archive);
                break;
            case "unpack":
                unpack(dir, archive);
                break;
            default:
                printUsageAndExit();
                break;
        }*/

        ArchiverGUI gui = new ArchiverGUI();
        gui.setSize(500, 200);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }

    private static void printUsageAndExit() {
        System.out.println("Usage: <pack|unpack> <dir> <archive>");
        System.exit(1);
    }

    public static void pack(File dir, File archive) throws IOException {
        MyFileArchiver archiver = new MyFileArchiver(archive);
        archiver.pack(dir);
    }

    public static void unpack(File dir, File archive) throws IOException {
        MyFileArchiver archiver = new MyFileArchiver(archive);
        archiver.unpack(dir);
    }
}