/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import net.minecraft.src.Config;
import net.minecraft.src.RangeInt;

public class RangeListInt {
    private RangeInt[] ranges = new RangeInt[0];

    public void addRange(RangeInt p_addRange_1_) {
        this.ranges = (RangeInt[])Config.addObjectToArray(this.ranges, p_addRange_1_);
    }

    public boolean isInRange(int p_isInRange_1_) {
        int i = 0;
        while (i < this.ranges.length) {
            RangeInt rangeint = this.ranges[i];
            if (rangeint.isInRange(p_isInRange_1_)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public int getCountRanges() {
        return this.ranges.length;
    }

    public RangeInt getRange(int p_getRange_1_) {
        return this.ranges[p_getRange_1_];
    }

    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("[");
        int i = 0;
        while (i < this.ranges.length) {
            RangeInt rangeint = this.ranges[i];
            if (i > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(rangeint.toString());
            ++i;
        }
        stringbuffer.append("]");
        return stringbuffer.toString();
    }
}

