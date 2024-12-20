/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.lang.reflect.Field;
import net.minecraft.src.IFieldLocator;

public class FieldLocatorFixed
implements IFieldLocator {
    private Field field;

    public FieldLocatorFixed(Field p_i34_1_) {
        this.field = p_i34_1_;
    }

    @Override
    public Field getField() {
        return this.field;
    }
}

