/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.lang.reflect.Field;
import net.minecraft.src.Config;
import net.minecraft.src.IFieldLocator;
import net.minecraft.src.ReflectorClass;

public class FieldLocatorType
implements IFieldLocator {
    private ReflectorClass reflectorClass = null;
    private Class targetFieldType = null;
    private int targetFieldIndex;

    public FieldLocatorType(ReflectorClass p_i36_1_, Class p_i36_2_) {
        this(p_i36_1_, p_i36_2_, 0);
    }

    public FieldLocatorType(ReflectorClass p_i37_1_, Class p_i37_2_, int p_i37_3_) {
        this.reflectorClass = p_i37_1_;
        this.targetFieldType = p_i37_2_;
        this.targetFieldIndex = p_i37_3_;
    }

    @Override
    public Field getField() {
        Class oclass = this.reflectorClass.getTargetClass();
        if (oclass == null) {
            return null;
        }
        try {
            Field[] afield = oclass.getDeclaredFields();
            int i = 0;
            int j = 0;
            while (j < afield.length) {
                Field field = afield[j];
                if (field.getType() == this.targetFieldType) {
                    if (i == this.targetFieldIndex) {
                        field.setAccessible(true);
                        return field;
                    }
                    ++i;
                }
                ++j;
            }
            Config.log("(Reflector) Field not present: " + oclass.getName() + ".(type: " + this.targetFieldType + ", index: " + this.targetFieldIndex + ")");
            return null;
        }
        catch (SecurityException securityexception) {
            securityexception.printStackTrace();
            return null;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}

