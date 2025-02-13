/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.util.ArrayList;

public class CompactArrayList {
    private ArrayList list = null;
    private int initialCapacity = 0;
    private float loadFactor = 1.0f;
    private int countValid = 0;

    public CompactArrayList() {
        this(10, 0.75f);
    }

    public CompactArrayList(int p_i24_1_) {
        this(p_i24_1_, 0.75f);
    }

    public CompactArrayList(int p_i25_1_, float p_i25_2_) {
        this.list = new ArrayList(p_i25_1_);
        this.initialCapacity = p_i25_1_;
        this.loadFactor = p_i25_2_;
    }

    public void add(int p_add_1_, Object p_add_2_) {
        if (p_add_2_ != null) {
            ++this.countValid;
        }
        this.list.add(p_add_1_, p_add_2_);
    }

    public boolean add(Object p_add_1_) {
        if (p_add_1_ != null) {
            ++this.countValid;
        }
        return this.list.add(p_add_1_);
    }

    public Object set(int p_set_1_, Object p_set_2_) {
        Object object = this.list.set(p_set_1_, p_set_2_);
        if (p_set_2_ != object) {
            if (object == null) {
                ++this.countValid;
            }
            if (p_set_2_ == null) {
                --this.countValid;
            }
        }
        return object;
    }

    public Object remove(int p_remove_1_) {
        Object object = this.list.remove(p_remove_1_);
        if (object != null) {
            --this.countValid;
        }
        return object;
    }

    public void clear() {
        this.list.clear();
        this.countValid = 0;
    }

    public void compact() {
        float f;
        if (this.countValid <= 0 && this.list.size() <= 0) {
            this.clear();
        } else if (this.list.size() > this.initialCapacity && (f = (float)this.countValid * 1.0f / (float)this.list.size()) <= this.loadFactor) {
            int i = 0;
            int j = 0;
            while (j < this.list.size()) {
                Object object = this.list.get(j);
                if (object != null) {
                    if (j != i) {
                        this.list.set(i, object);
                    }
                    ++i;
                }
                ++j;
            }
            int k = this.list.size() - 1;
            while (k >= i) {
                this.list.remove(k);
                --k;
            }
        }
    }

    public boolean contains(Object p_contains_1_) {
        return this.list.contains(p_contains_1_);
    }

    public Object get(int p_get_1_) {
        return this.list.get(p_get_1_);
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }

    public int getCountValid() {
        return this.countValid;
    }
}

