/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ShapelessRecipes
implements IRecipe {
    private final ItemStack recipeOutput;
    private final NonNullList<Ingredient> recipeItems;
    private final String field_194138_c;

    public ShapelessRecipes(String p_i47500_1_, ItemStack p_i47500_2_, NonNullList<Ingredient> p_i47500_3_) {
        this.field_194138_c = p_i47500_1_;
        this.recipeOutput = p_i47500_2_;
        this.recipeItems = p_i47500_3_;
    }

    @Override
    public String func_193358_e() {
        return this.field_194138_c;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public NonNullList<Ingredient> func_192400_c() {
        return this.recipeItems;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.func_191197_a(inv.getSizeInventory(), ItemStack.EMPTY);
        int i = 0;
        while (i < nonnulllist.size()) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.getItem().hasContainerItem()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().getContainerItem()));
            }
            ++i;
        }
        return nonnulllist;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ArrayList list = Lists.newArrayList(this.recipeItems);
        int i = 0;
        while (i < inv.getHeight()) {
            int j = 0;
            while (j < inv.getWidth()) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);
                if (!itemstack.func_190926_b()) {
                    boolean flag = false;
                    for (Ingredient ingredient : list) {
                        if (!ingredient.apply(itemstack)) continue;
                        flag = true;
                        list.remove(ingredient);
                        break;
                    }
                    if (!flag) {
                        return false;
                    }
                }
                ++j;
            }
            ++i;
        }
        return list.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return this.recipeOutput.copy();
    }

    public static ShapelessRecipes func_193363_a(JsonObject p_193363_0_) {
        String s = JsonUtils.getString(p_193363_0_, "group", "");
        NonNullList<Ingredient> nonnulllist = ShapelessRecipes.func_193364_a(JsonUtils.getJsonArray(p_193363_0_, "ingredients"));
        if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        }
        if (nonnulllist.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
        }
        ItemStack itemstack = ShapedRecipes.func_192405_a(JsonUtils.getJsonObject(p_193363_0_, "result"), true);
        return new ShapelessRecipes(s, itemstack, nonnulllist);
    }

    private static NonNullList<Ingredient> func_193364_a(JsonArray p_193364_0_) {
        NonNullList<Ingredient> nonnulllist = NonNullList.func_191196_a();
        int i = 0;
        while (i < p_193364_0_.size()) {
            Ingredient ingredient = ShapedRecipes.func_193361_a(p_193364_0_.get(i));
            if (ingredient != Ingredient.field_193370_a) {
                nonnulllist.add(ingredient);
            }
            ++i;
        }
        return nonnulllist;
    }

    @Override
    public boolean func_194133_a(int p_194133_1_, int p_194133_2_) {
        return p_194133_1_ * p_194133_2_ >= this.recipeItems.size();
    }
}

