/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class PropertiesOrdered
extends Properties {
    private Set<Object> keysOrdered = new LinkedHashSet<Object>();

    @Override
    public synchronized Object put(Object p_put_1_, Object p_put_2_) {
        this.keysOrdered.add(p_put_1_);
        return super.put(p_put_1_, p_put_2_);
    }

    @Override
    public Set<Object> keySet() {
        Set<Object> set = super.keySet();
        this.keysOrdered.retainAll(set);
        return Collections.unmodifiableSet(this.keysOrdered);
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(this.keySet());
    }
}

