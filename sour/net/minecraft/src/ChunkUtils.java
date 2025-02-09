/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.src.ReflectorClass;
import net.minecraft.src.ReflectorField;
import net.minecraft.world.chunk.Chunk;

public class ChunkUtils {
    private static ReflectorField fieldHasEntities = null;

    public static boolean hasEntities(Chunk p_hasEntities_0_) {
        if (fieldHasEntities == null) {
            fieldHasEntities = ChunkUtils.findFieldHasEntities(p_hasEntities_0_);
        }
        if (!fieldHasEntities.exists()) {
            return true;
        }
        Boolean obool = (Boolean)Reflector.getFieldValue(p_hasEntities_0_, fieldHasEntities);
        return obool == null ? true : obool;
    }

    private static ReflectorField findFieldHasEntities(Chunk p_findFieldHasEntities_0_) {
        try {
            ArrayList<Field> list = new ArrayList<Field>();
            ArrayList<Object> list1 = new ArrayList<Object>();
            Field[] afield = Chunk.class.getDeclaredFields();
            int i = 0;
            while (i < afield.length) {
                Field field = afield[i];
                if (field.getType() == Boolean.TYPE) {
                    field.setAccessible(true);
                    list.add(field);
                    list1.add(field.get(p_findFieldHasEntities_0_));
                }
                ++i;
            }
            p_findFieldHasEntities_0_.setHasEntities(false);
            ArrayList<Object> list2 = new ArrayList<Object>();
            for (Field field1 : list) {
                list2.add(field1.get(p_findFieldHasEntities_0_));
            }
            p_findFieldHasEntities_0_.setHasEntities(true);
            ArrayList<Object> list3 = new ArrayList<Object>();
            for (Field field2 : list) {
                list3.add(field2.get(p_findFieldHasEntities_0_));
            }
            ArrayList<Field> list4 = new ArrayList<Field>();
            int j = 0;
            while (j < list.size()) {
                Field field3 = (Field)list.get(j);
                Boolean obool = (Boolean)list2.get(j);
                Boolean obool1 = (Boolean)list3.get(j);
                if (!obool.booleanValue() && obool1.booleanValue()) {
                    list4.add(field3);
                    Boolean obool2 = (Boolean)list1.get(j);
                    field3.set(p_findFieldHasEntities_0_, obool2);
                }
                ++j;
            }
            if (list4.size() == 1) {
                Field field4 = (Field)list4.get(0);
                return new ReflectorField(field4);
            }
        }
        catch (Exception exception) {
            Config.warn(String.valueOf(exception.getClass().getName()) + " " + exception.getMessage());
        }
        Config.warn("Error finding Chunk.hasEntities");
        return new ReflectorField(new ReflectorClass(Chunk.class), "hasEntities");
    }
}

