/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 */
package net.wolframclient.storage;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import net.wolframclient.storage.Storage;

public class ArrayStorage
extends ArrayList<String>
implements Storage {
    private final File file;

    public ArrayStorage(File file) {
        this.file = file;
    }

    @Override
    public void load() {
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonReader reader = new JsonReader((Reader)new FileReader(this.file));){
                reader.beginArray();
                while (reader.hasNext()) {
                    try {
                        this.add(reader.nextString());
                    }
                    catch (Exception e) {
                        reader.skipValue();
                    }
                }
                reader.endArray();
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.loadDefaults();
        }
    }

    protected void loadDefaults() {
    }

    @Override
    public void save() {
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonWriter writer = new JsonWriter((Writer)new FileWriter(this.file));){
                writer.beginArray();
                for (String item : this) {
                    writer.value(item);
                }
                writer.endArray();
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

