/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.lang.reflect.Field;
import net.minecraft.src.FieldLocatorFixed;
import net.minecraft.src.FieldLocatorName;
import net.minecraft.src.FieldLocatorType;
import net.minecraft.src.IFieldLocator;
import net.minecraft.src.Reflector;
import net.minecraft.src.ReflectorClass;

public class ReflectorField {
    private IFieldLocator fieldLocator = null;
    private boolean checked = false;
    private Field targetField = null;

    public ReflectorField(ReflectorClass p_i82_1_, String p_i82_2_) {
        this(new FieldLocatorName(p_i82_1_, p_i82_2_));
    }

    public ReflectorField(ReflectorClass p_i83_1_, String p_i83_2_, boolean p_i83_3_) {
        this(new FieldLocatorName(p_i83_1_, p_i83_2_), p_i83_3_);
    }

    public ReflectorField(ReflectorClass p_i84_1_, Class p_i84_2_) {
        this(p_i84_1_, p_i84_2_, 0);
    }

    public ReflectorField(ReflectorClass p_i85_1_, Class p_i85_2_, int p_i85_3_) {
        this(new FieldLocatorType(p_i85_1_, p_i85_2_, p_i85_3_));
    }

    public ReflectorField(Field p_i86_1_) {
        this(new FieldLocatorFixed(p_i86_1_));
    }

    public ReflectorField(IFieldLocator p_i87_1_) {
        this(p_i87_1_, false);
    }

    public ReflectorField(IFieldLocator p_i88_1_, boolean p_i88_2_) {
        this.fieldLocator = p_i88_1_;
        if (!p_i88_2_) {
            this.getTargetField();
        }
    }

    public Field getTargetField() {
        if (this.checked) {
            return this.targetField;
        }
        this.checked = true;
        this.targetField = this.fieldLocator.getField();
        if (this.targetField != null) {
            this.targetField.setAccessible(true);
        }
        return this.targetField;
    }

    public Object getValue() {
        return Reflector.getFieldValue(null, this);
    }

    public void setValue(Object p_setValue_1_) {
        Reflector.setFieldValue(null, this, p_setValue_1_);
    }

    public void setValue(Object p_setValue_1_, Object p_setValue_2_) {
        Reflector.setFieldValue(p_setValue_1_, this, p_setValue_2_);
    }

    public boolean exists() {
        return this.getTargetField() != null;
    }
}

