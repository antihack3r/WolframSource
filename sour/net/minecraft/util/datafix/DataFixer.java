/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.datafix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFixer
implements IDataFixer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<IFixType, List<IDataWalker>> walkerMap = Maps.newHashMap();
    private final Map<IFixType, List<IFixableData>> fixMap = Maps.newHashMap();
    private final int version;

    public DataFixer(int versionIn) {
        this.version = versionIn;
    }

    public NBTTagCompound process(IFixType type, NBTTagCompound compound) {
        int i = compound.hasKey("DataVersion", 99) ? compound.getInteger("DataVersion") : -1;
        return i >= 1139 ? compound : this.process(type, compound, i);
    }

    @Override
    public NBTTagCompound process(IFixType type, NBTTagCompound compound, int versionIn) {
        if (versionIn < this.version) {
            compound = this.processFixes(type, compound, versionIn);
            compound = this.processWalkers(type, compound, versionIn);
        }
        return compound;
    }

    private NBTTagCompound processFixes(IFixType type, NBTTagCompound compound, int versionIn) {
        List<IFixableData> list = this.fixMap.get(type);
        if (list != null) {
            int i = 0;
            while (i < list.size()) {
                IFixableData ifixabledata = list.get(i);
                if (ifixabledata.getFixVersion() > versionIn) {
                    compound = ifixabledata.fixTagCompound(compound);
                }
                ++i;
            }
        }
        return compound;
    }

    private NBTTagCompound processWalkers(IFixType type, NBTTagCompound compound, int versionIn) {
        List<IDataWalker> list = this.walkerMap.get(type);
        if (list != null) {
            int i = 0;
            while (i < list.size()) {
                compound = list.get(i).process(this, compound, versionIn);
                ++i;
            }
        }
        return compound;
    }

    public void registerWalker(FixTypes type, IDataWalker walker) {
        this.registerWalkerAdd(type, walker);
    }

    public void registerWalkerAdd(IFixType type, IDataWalker walker) {
        this.getTypeList(this.walkerMap, type).add(walker);
    }

    public void registerFix(IFixType type, IFixableData fixable) {
        List list = this.getTypeList(this.fixMap, type);
        int i = fixable.getFixVersion();
        if (i > this.version) {
            LOGGER.warn("Ignored fix registered for version: {} as the DataVersion of the game is: {}", (Object)i, (Object)this.version);
        } else if (!list.isEmpty() && ((IFixableData)Util.getLastElement(list)).getFixVersion() > i) {
            int j = 0;
            while (j < list.size()) {
                if (((IFixableData)list.get(j)).getFixVersion() > i) {
                    list.add(j, fixable);
                    break;
                }
                ++j;
            }
        } else {
            list.add(fixable);
        }
    }

    private <V> List<V> getTypeList(Map<IFixType, List<V>> map, IFixType type) {
        ArrayList list = map.get(type);
        if (list == null) {
            list = Lists.newArrayList();
            map.put(type, list);
        }
        return list;
    }
}

