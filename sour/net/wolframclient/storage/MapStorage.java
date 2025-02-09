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
import java.util.HashMap;
import net.wolframclient.storage.Storage;

public final class MapStorage
extends HashMap<String, String>
implements Storage {
    private final File file;
    @Deprecated
    public final String title;

    public MapStorage(File file, String title) {
        this.file = file;
        this.title = title;
    }

    @Override
    public void load() {
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonReader reader = new JsonReader((Reader)new FileReader(this.file));){
                reader.beginObject();
                while (reader.hasNext()) {
                    try {
                        this.set(reader.nextName(), reader.nextString());
                    }
                    catch (Exception e) {
                        reader.skipValue();
                    }
                }
                reader.endObject();
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

    @Override
    public void save() {
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonWriter writer = new JsonWriter((Writer)new FileWriter(this.file));){
                writer.setIndent("    ");
                writer.beginObject();
                for (String key : this.keySet()) {
                    writer.name(key);
                    writer.value((String)this.get(key));
                }
                writer.endObject();
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

    public void set(String key, Object value) {
        this.set(key, value, true);
    }

    public void set(String key, Object value, boolean overwrite) {
        if (overwrite || !this.containsKey(key)) {
            this.put(key, value.toString());
        }
    }

    public boolean getBoolean(String identifier) {
        return Boolean.parseBoolean((String)this.get(identifier));
    }

    public double getDouble(String identifier) {
        try {
            return Double.parseDouble((String)this.get(identifier));
        }
        catch (Exception e) {
            return 0.0;
        }
    }

    public int getInt(String identifier) {
        return (int)this.getFloat(identifier);
    }

    public float getFloat(String identifier) {
        try {
            return Float.parseFloat((String)this.get(identifier));
        }
        catch (Exception e) {
            return 0.0f;
        }
    }

    public long getLong(String identifier) {
        try {
            return Long.parseLong((String)this.get(identifier));
        }
        catch (Exception e) {
            return 0L;
        }
    }

    public String getTitle() {
        return this.title;
    }
}

