/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final LootTable EMPTY_LOOT_TABLE = new LootTable(new LootPool[0]);
    private final LootPool[] pools;

    public LootTable(LootPool[] poolsIn) {
        this.pools = poolsIn;
    }

    public List<ItemStack> generateLootForPools(Random rand, LootContext context) {
        ArrayList list = Lists.newArrayList();
        if (context.addLootTable(this)) {
            LootPool[] lootPoolArray = this.pools;
            int n = this.pools.length;
            int n2 = 0;
            while (n2 < n) {
                LootPool lootpool = lootPoolArray[n2];
                lootpool.generateLoot(list, rand, context);
                ++n2;
            }
            context.removeLootTable(this);
        } else {
            LOGGER.warn("Detected infinite loop in loot tables");
        }
        return list;
    }

    public void fillInventory(IInventory inventory, Random rand, LootContext context) {
        List<ItemStack> list = this.generateLootForPools(rand, context);
        List<Integer> list1 = this.getEmptySlotsRandomized(inventory, rand);
        this.shuffleItems(list, list1.size(), rand);
        for (ItemStack itemstack : list) {
            if (list1.isEmpty()) {
                LOGGER.warn("Tried to over-fill a container");
                return;
            }
            if (itemstack.func_190926_b()) {
                inventory.setInventorySlotContents(list1.remove(list1.size() - 1), ItemStack.EMPTY);
                continue;
            }
            inventory.setInventorySlotContents(list1.remove(list1.size() - 1), itemstack);
        }
    }

    private void shuffleItems(List<ItemStack> stacks, int p_186463_2_, Random rand) {
        ArrayList list = Lists.newArrayList();
        Iterator<ItemStack> iterator = stacks.iterator();
        while (iterator.hasNext()) {
            ItemStack itemstack = iterator.next();
            if (itemstack.func_190926_b()) {
                iterator.remove();
                continue;
            }
            if (itemstack.func_190916_E() <= 1) continue;
            list.add(itemstack);
            iterator.remove();
        }
        p_186463_2_ -= stacks.size();
        while (p_186463_2_ > 0 && !list.isEmpty()) {
            ItemStack itemstack2 = (ItemStack)list.remove(MathHelper.getInt(rand, 0, list.size() - 1));
            int i = MathHelper.getInt(rand, 1, itemstack2.func_190916_E() / 2);
            ItemStack itemstack1 = itemstack2.splitStack(i);
            if (itemstack2.func_190916_E() > 1 && rand.nextBoolean()) {
                list.add(itemstack2);
            } else {
                stacks.add(itemstack2);
            }
            if (itemstack1.func_190916_E() > 1 && rand.nextBoolean()) {
                list.add(itemstack1);
                continue;
            }
            stacks.add(itemstack1);
        }
        stacks.addAll(list);
        Collections.shuffle(stacks, rand);
    }

    private List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand) {
        ArrayList list = Lists.newArrayList();
        int i = 0;
        while (i < inventory.getSizeInventory()) {
            if (inventory.getStackInSlot(i).func_190926_b()) {
                list.add(i);
            }
            ++i;
        }
        Collections.shuffle(list, rand);
        return list;
    }

    public static class Serializer
    implements JsonDeserializer<LootTable>,
    JsonSerializer<LootTable> {
        public LootTable deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "loot table");
            LootPool[] alootpool = JsonUtils.deserializeClass(jsonobject, "pools", new LootPool[0], p_deserialize_3_, LootPool[].class);
            return new LootTable(alootpool);
        }

        public JsonElement serialize(LootTable p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add("pools", p_serialize_3_.serialize((Object)p_serialize_1_.pools));
            return jsonobject;
        }
    }
}

