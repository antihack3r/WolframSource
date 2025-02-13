/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.creativetab;

import javax.annotation.Nullable;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;

public abstract class CreativeTabs {
    public static final CreativeTabs[] CREATIVE_TAB_ARRAY = new CreativeTabs[12];
    public static final CreativeTabs BUILDING_BLOCKS = new CreativeTabs(0, "buildingBlocks"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(Blocks.BRICK_BLOCK));
        }
    };
    public static final CreativeTabs DECORATIONS = new CreativeTabs(1, "decorations"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(Blocks.DOUBLE_PLANT), 1, BlockDoublePlant.EnumPlantType.PAEONIA.getMeta());
        }
    };
    public static final CreativeTabs REDSTONE = new CreativeTabs(2, "redstone"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.REDSTONE);
        }
    };
    public static final CreativeTabs TRANSPORTATION = new CreativeTabs(3, "transportation"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(Blocks.GOLDEN_RAIL));
        }
    };
    public static final CreativeTabs MISC = new CreativeTabs(6, "misc"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.LAVA_BUCKET);
        }
    };
    public static final CreativeTabs SEARCH = new CreativeTabs(5, "search"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.COMPASS);
        }
    }.setBackgroundImageName("item_search.png");
    public static final CreativeTabs FOOD = new CreativeTabs(7, "food"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.APPLE);
        }
    };
    public static final CreativeTabs TOOLS = new CreativeTabs(8, "tools"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.IRON_AXE);
        }
    }.setRelevantEnchantmentTypes(EnumEnchantmentType.ALL, EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE);
    public static final CreativeTabs COMBAT = new CreativeTabs(9, "combat"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Items.GOLDEN_SWORD);
        }
    }.setRelevantEnchantmentTypes(EnumEnchantmentType.ALL, EnumEnchantmentType.ARMOR, EnumEnchantmentType.ARMOR_FEET, EnumEnchantmentType.ARMOR_HEAD, EnumEnchantmentType.ARMOR_LEGS, EnumEnchantmentType.ARMOR_CHEST, EnumEnchantmentType.BOW, EnumEnchantmentType.WEAPON, EnumEnchantmentType.WEARABLE, EnumEnchantmentType.BREAKABLE);
    public static final CreativeTabs BREWING = new CreativeTabs(10, "brewing"){

        @Override
        public ItemStack getTabIconItem() {
            return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
        }
    };
    public static final CreativeTabs MATERIALS = MISC;
    public static final CreativeTabs field_192395_m = new CreativeTabs(4, "hotbar"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Blocks.BOOKSHELF);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_) {
            throw new RuntimeException("Implement exception client-side.");
        }

        @Override
        public boolean func_192394_m() {
            return true;
        }
    };
    public static final CreativeTabs INVENTORY = new CreativeTabs(11, "inventory"){

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(Blocks.CHEST));
        }
    }.setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;
    private String theTexture = "items.png";
    private boolean hasScrollbar = true;
    private boolean drawTitle = true;
    private EnumEnchantmentType[] enchantmentTypes = new EnumEnchantmentType[0];
    private ItemStack iconItemStack;

    public CreativeTabs(int index, String label) {
        this.tabIndex = index;
        this.tabLabel = label;
        this.iconItemStack = ItemStack.EMPTY;
        CreativeTabs.CREATIVE_TAB_ARRAY[index] = this;
    }

    public int getTabIndex() {
        return this.tabIndex;
    }

    public String getTabLabel() {
        return this.tabLabel;
    }

    public String getTranslatedTabLabel() {
        return "itemGroup." + this.getTabLabel();
    }

    public ItemStack getIconItemStack() {
        if (this.iconItemStack.func_190926_b()) {
            this.iconItemStack = this.getTabIconItem();
        }
        return this.iconItemStack;
    }

    public abstract ItemStack getTabIconItem();

    public String getBackgroundImageName() {
        return this.theTexture;
    }

    public CreativeTabs setBackgroundImageName(String texture) {
        this.theTexture = texture;
        return this;
    }

    public boolean drawInForegroundOfTab() {
        return this.drawTitle;
    }

    public CreativeTabs setNoTitle() {
        this.drawTitle = false;
        return this;
    }

    public boolean shouldHidePlayerInventory() {
        return this.hasScrollbar;
    }

    public CreativeTabs setNoScrollbar() {
        this.hasScrollbar = false;
        return this;
    }

    public int getTabColumn() {
        return this.tabIndex % 6;
    }

    public boolean isTabInFirstRow() {
        return this.tabIndex < 6;
    }

    public boolean func_192394_m() {
        return this.getTabColumn() == 5;
    }

    public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
        return this.enchantmentTypes;
    }

    public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType ... types) {
        this.enchantmentTypes = types;
        return this;
    }

    public boolean hasRelevantEnchantmentType(@Nullable EnumEnchantmentType enchantmentType) {
        if (enchantmentType != null) {
            EnumEnchantmentType[] enumEnchantmentTypeArray = this.enchantmentTypes;
            int n = this.enchantmentTypes.length;
            int n2 = 0;
            while (n2 < n) {
                EnumEnchantmentType enumenchantmenttype = enumEnchantmentTypeArray[n2];
                if (enumenchantmenttype == enchantmentType) {
                    return true;
                }
                ++n2;
            }
        }
        return false;
    }

    public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_) {
        for (Item item : Item.REGISTRY) {
            item.getSubItems(this, p_78018_1_);
        }
    }
}

