/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import net.wolframclient.Wolfram;

public class FileUtils {
    public static synchronized List<String> readFile(File file) {
        ArrayList<String> tempList;
        block15: {
            FileReader fileReader;
            tempList = new ArrayList<String>();
            try {
                fileReader = new FileReader(file);
            }
            catch (FileNotFoundException e) {
                Wolfram.getWolfram().logger.error(e.getMessage());
                return tempList;
            }
            BufferedReader reader = null;
            try {
                try {
                    String line;
                    reader = new BufferedReader(fileReader);
                    while ((line = reader.readLine()) != null) {
                        tempList.add(line);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (reader == null) break block15;
                    try {
                        reader.close();
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return tempList;
    }

    public static synchronized void writeToFile(File file, List<String> text) {
        FileUtils.writeToFile(file, text.toArray(new String[text.size()]));
    }

    public static synchronized void writeToFile(File file, String[] text) {
        try (PrintWriter writer = null;){
            try {
                writer = new PrintWriter(new FileWriter(file));
                String[] stringArray = text;
                int n = text.length;
                int n2 = 0;
                while (n2 < n) {
                    String line = stringArray[n2];
                    writer.println(line);
                    writer.flush();
                    ++n2;
                }
            }
            catch (Exception localException) {
                Wolfram.getWolfram().logger.error(localException.getMessage());
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }
}

