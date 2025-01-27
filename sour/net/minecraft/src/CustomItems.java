/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.src.Blender;
import net.minecraft.src.Config;
import net.minecraft.src.CustomItemProperties;
import net.minecraft.src.CustomItemsComparator;
import net.minecraft.src.NbtTagValue;
import net.minecraft.src.ResUtils;
import net.minecraft.src.StrUtils;
import net.minecraft.util.ResourceLocation;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public class CustomItems {
    private static CustomItemProperties[][] itemProperties = null;
    private static CustomItemProperties[][] enchantmentProperties = null;
    private static Map mapPotionIds = null;
    private static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
    private static boolean useGlint = true;
    private static boolean renderOffHand = false;
    public static final int MASK_POTION_SPLASH = 16384;
    public static final int MASK_POTION_NAME = 63;
    public static final int MASK_POTION_EXTENDED = 64;
    public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
    public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
    public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
    public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
    public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
    public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
    private static final int[][] EMPTY_INT2_ARRAY = new int[0][];
    private static final Map<String, Integer> mapPotionDamages = CustomItems.makeMapPotionDamages();
    private static final String TYPE_POTION_NORMAL = "normal";
    private static final String TYPE_POTION_SPLASH = "splash";
    private static final String TYPE_POTION_LINGER = "linger";

    public static void update() {
        itemProperties = null;
        enchantmentProperties = null;
        useGlint = true;
        if (Config.isCustomItems()) {
            CustomItems.readCitProperties("mcpatcher/cit.properties");
            IResourcePack[] airesourcepack = Config.getResourcePacks();
            int i = airesourcepack.length - 1;
            while (i >= 0) {
                IResourcePack iresourcepack = airesourcepack[i];
                CustomItems.update(iresourcepack);
                --i;
            }
            CustomItems.update(Config.getDefaultResourcePack());
            if (itemProperties.length <= 0) {
                itemProperties = null;
            }
            if (enchantmentProperties.length <= 0) {
                enchantmentProperties = null;
            }
        }
    }

    private static void readCitProperties(String p_readCitProperties_0_) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(p_readCitProperties_0_);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            Config.dbg("CustomItems: Loading " + p_readCitProperties_0_);
            Properties properties = new Properties();
            properties.load(inputstream);
            inputstream.close();
            useGlint = Config.parseBoolean(properties.getProperty("useGlint"), true);
        }
        catch (FileNotFoundException var4) {
            return;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void update(IResourcePack p_update_0_) {
        Object[] astring = ResUtils.collectFiles(p_update_0_, "mcpatcher/cit/", ".properties", null);
        Map map = CustomItems.makeAutoImageProperties(p_update_0_);
        if (map.size() > 0) {
            Set set = map.keySet();
            Object[] astring1 = set.toArray(new String[set.size()]);
            astring = (String[])Config.addObjectsToArray(astring, astring1);
        }
        Arrays.sort(astring);
        List list = CustomItems.makePropertyList(itemProperties);
        List list1 = CustomItems.makePropertyList(enchantmentProperties);
        int i = 0;
        while (i < astring.length) {
            block13: {
                Object s = astring[i];
                Config.dbg("CustomItems: " + (String)s);
                try {
                    CustomItemProperties customitemproperties = null;
                    if (map.containsKey(s)) {
                        customitemproperties = (CustomItemProperties)map.get(s);
                    }
                    if (customitemproperties == null) {
                        ResourceLocation resourcelocation = new ResourceLocation((String)s);
                        InputStream inputstream = p_update_0_.getInputStream(resourcelocation);
                        if (inputstream == null) {
                            Config.warn("CustomItems file not found: " + (String)s);
                            break block13;
                        }
                        Properties properties = new Properties();
                        properties.load(inputstream);
                        customitemproperties = new CustomItemProperties(properties, (String)s);
                    }
                    if (customitemproperties.isValid((String)s)) {
                        CustomItems.addToItemList(customitemproperties, list);
                        CustomItems.addToEnchantmentList(customitemproperties, list1);
                    }
                }
                catch (FileNotFoundException var11) {
                    Config.warn("CustomItems file not found: " + (String)s);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            ++i;
        }
        itemProperties = CustomItems.propertyListToArray(list);
        enchantmentProperties = CustomItems.propertyListToArray(list1);
        Comparator comparator = CustomItems.getPropertiesComparator();
        int j = 0;
        while (j < itemProperties.length) {
            CustomItemProperties[] acustomitemproperties = itemProperties[j];
            if (acustomitemproperties != null) {
                Arrays.sort(acustomitemproperties, comparator);
            }
            ++j;
        }
        int k = 0;
        while (k < enchantmentProperties.length) {
            CustomItemProperties[] acustomitemproperties1 = enchantmentProperties[k];
            if (acustomitemproperties1 != null) {
                Arrays.sort(acustomitemproperties1, comparator);
            }
            ++k;
        }
    }

    private static Comparator getPropertiesComparator() {
        Comparator comparator = new Comparator(){

            public int compare(Object p_compare_1_, Object p_compare_2_) {
                CustomItemProperties customitemproperties = (CustomItemProperties)p_compare_1_;
                CustomItemProperties customitemproperties1 = (CustomItemProperties)p_compare_2_;
                if (customitemproperties.layer != customitemproperties1.layer) {
                    return customitemproperties.layer - customitemproperties1.layer;
                }
                if (customitemproperties.weight != customitemproperties1.weight) {
                    return customitemproperties1.weight - customitemproperties.weight;
                }
                return !customitemproperties.basePath.equals(customitemproperties1.basePath) ? customitemproperties.basePath.compareTo(customitemproperties1.basePath) : customitemproperties.name.compareTo(customitemproperties1.name);
            }
        };
        return comparator;
    }

    public static void updateIcons(TextureMap p_updateIcons_0_) {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            customitemproperties.updateIcons(p_updateIcons_0_);
        }
    }

    public static void loadModels(ModelBakery p_loadModels_0_) {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            customitemproperties.loadModels(p_loadModels_0_);
        }
    }

    public static void updateModels() {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            if (customitemproperties.type != 1) continue;
            TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
            customitemproperties.updateModelTexture(texturemap, itemModelGenerator);
            customitemproperties.updateModelsFull();
        }
    }

    private static List<CustomItemProperties> getAllProperties() {
        ArrayList<CustomItemProperties> list = new ArrayList<CustomItemProperties>();
        CustomItems.addAll(itemProperties, list);
        CustomItems.addAll(enchantmentProperties, list);
        return list;
    }

    private static void addAll(CustomItemProperties[][] p_addAll_0_, List<CustomItemProperties> p_addAll_1_) {
        if (p_addAll_0_ != null) {
            int i = 0;
            while (i < p_addAll_0_.length) {
                CustomItemProperties[] acustomitemproperties = p_addAll_0_[i];
                if (acustomitemproperties != null) {
                    int j = 0;
                    while (j < acustomitemproperties.length) {
                        CustomItemProperties customitemproperties = acustomitemproperties[j];
                        if (customitemproperties != null) {
                            p_addAll_1_.add(customitemproperties);
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
    }

    private static Map makeAutoImageProperties(IResourcePack p_makeAutoImageProperties_0_) {
        HashMap map = new HashMap();
        map.putAll(CustomItems.makePotionImageProperties(p_makeAutoImageProperties_0_, TYPE_POTION_NORMAL, Item.getIdFromItem(Items.POTIONITEM)));
        map.putAll(CustomItems.makePotionImageProperties(p_makeAutoImageProperties_0_, TYPE_POTION_SPLASH, Item.getIdFromItem(Items.SPLASH_POTION)));
        map.putAll(CustomItems.makePotionImageProperties(p_makeAutoImageProperties_0_, TYPE_POTION_LINGER, Item.getIdFromItem(Items.LINGERING_POTION)));
        return map;
    }

    private static Map makePotionImageProperties(IResourcePack p_makePotionImageProperties_0_, String p_makePotionImageProperties_1_, int p_makePotionImageProperties_2_) {
        HashMap<String, CustomItemProperties> map = new HashMap<String, CustomItemProperties>();
        String s = String.valueOf(p_makePotionImageProperties_1_) + "/";
        String[] astring = new String[]{"mcpatcher/cit/potion/" + s, "mcpatcher/cit/Potion/" + s};
        String[] astring1 = new String[]{".png"};
        String[] astring2 = ResUtils.collectFiles(p_makePotionImageProperties_0_, astring, astring1);
        int i = 0;
        while (i < astring2.length) {
            String s1 = astring2[i];
            String name = StrUtils.removePrefixSuffix(s1, astring, astring1);
            Properties properties = CustomItems.makePotionProperties(name, p_makePotionImageProperties_1_, p_makePotionImageProperties_2_, s1);
            if (properties != null) {
                String s3 = String.valueOf(StrUtils.removeSuffix(s1, astring1)) + ".properties";
                CustomItemProperties customitemproperties = new CustomItemProperties(properties, s3);
                map.put(s3, customitemproperties);
            }
            ++i;
        }
        return map;
    }

    private static Properties makePotionProperties(String p_makePotionProperties_0_, String p_makePotionProperties_1_, int p_makePotionProperties_2_, String p_makePotionProperties_3_) {
        if (StrUtils.endsWith(p_makePotionProperties_0_, new String[]{"_n", "_s"})) {
            return null;
        }
        if (p_makePotionProperties_0_.equals("empty") && p_makePotionProperties_1_.equals(TYPE_POTION_NORMAL)) {
            p_makePotionProperties_2_ = Item.getIdFromItem(Items.GLASS_BOTTLE);
            Properties properties = new Properties();
            properties.put("type", "item");
            properties.put("items", "" + p_makePotionProperties_2_);
            return properties;
        }
        int[] aint = (int[])CustomItems.getMapPotionIds().get(p_makePotionProperties_0_);
        if (aint == null) {
            Config.warn("Potion not found for image: " + p_makePotionProperties_3_);
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        while (i < aint.length) {
            int j = aint[i];
            if (p_makePotionProperties_1_.equals(TYPE_POTION_SPLASH)) {
                j |= 0x4000;
            }
            if (i > 0) {
                stringbuffer.append(" ");
            }
            stringbuffer.append(j);
            ++i;
        }
        int k = 16447;
        if (p_makePotionProperties_0_.equals("water") || p_makePotionProperties_0_.equals("mundane")) {
            k |= 0x40;
        }
        Properties properties1 = new Properties();
        properties1.put("type", "item");
        properties1.put("items", "" + p_makePotionProperties_2_);
        properties1.put("damage", stringbuffer.toString());
        properties1.put("damageMask", "" + k);
        if (p_makePotionProperties_1_.equals(TYPE_POTION_SPLASH)) {
            properties1.put(KEY_TEXTURE_SPLASH, p_makePotionProperties_0_);
        } else {
            properties1.put(KEY_TEXTURE_DRINKABLE, p_makePotionProperties_0_);
        }
        return properties1;
    }

    private static Map getMapPotionIds() {
        if (mapPotionIds == null) {
            mapPotionIds = new LinkedHashMap();
            mapPotionIds.put("water", CustomItems.getPotionId(0, 0));
            mapPotionIds.put("awkward", CustomItems.getPotionId(0, 1));
            mapPotionIds.put("thick", CustomItems.getPotionId(0, 2));
            mapPotionIds.put("potent", CustomItems.getPotionId(0, 3));
            mapPotionIds.put("regeneration", CustomItems.getPotionIds(1));
            mapPotionIds.put("movespeed", CustomItems.getPotionIds(2));
            mapPotionIds.put("fireresistance", CustomItems.getPotionIds(3));
            mapPotionIds.put("poison", CustomItems.getPotionIds(4));
            mapPotionIds.put("heal", CustomItems.getPotionIds(5));
            mapPotionIds.put("nightvision", CustomItems.getPotionIds(6));
            mapPotionIds.put("clear", CustomItems.getPotionId(7, 0));
            mapPotionIds.put("bungling", CustomItems.getPotionId(7, 1));
            mapPotionIds.put("charming", CustomItems.getPotionId(7, 2));
            mapPotionIds.put("rank", CustomItems.getPotionId(7, 3));
            mapPotionIds.put("weakness", CustomItems.getPotionIds(8));
            mapPotionIds.put("damageboost", CustomItems.getPotionIds(9));
            mapPotionIds.put("moveslowdown", CustomItems.getPotionIds(10));
            mapPotionIds.put("leaping", CustomItems.getPotionIds(11));
            mapPotionIds.put("harm", CustomItems.getPotionIds(12));
            mapPotionIds.put("waterbreathing", CustomItems.getPotionIds(13));
            mapPotionIds.put("invisibility", CustomItems.getPotionIds(14));
            mapPotionIds.put("thin", CustomItems.getPotionId(15, 0));
            mapPotionIds.put("debonair", CustomItems.getPotionId(15, 1));
            mapPotionIds.put("sparkling", CustomItems.getPotionId(15, 2));
            mapPotionIds.put("stinky", CustomItems.getPotionId(15, 3));
            mapPotionIds.put("mundane", CustomItems.getPotionId(0, 4));
            mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
            mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
            mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
            mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
            mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
            mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
            mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
            mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
        }
        return mapPotionIds;
    }

    private static int[] getPotionIds(int p_getPotionIds_0_) {
        return new int[]{p_getPotionIds_0_, p_getPotionIds_0_ + 16, p_getPotionIds_0_ + 32, p_getPotionIds_0_ + 48};
    }

    private static int[] getPotionId(int p_getPotionId_0_, int p_getPotionId_1_) {
        return new int[]{p_getPotionId_0_ + p_getPotionId_1_ * 16};
    }

    private static int getPotionNameDamage(String p_getPotionNameDamage_0_) {
        String s = "effect." + p_getPotionNameDamage_0_;
        for (ResourceLocation resourcelocation : Potion.REGISTRY.getKeys()) {
            Potion potion = Potion.REGISTRY.getObject(resourcelocation);
            String s1 = potion.getName();
            if (!s.equals(s1)) continue;
            return Potion.getIdFromPotion(potion);
        }
        return -1;
    }

    private static List makePropertyList(CustomItemProperties[][] p_makePropertyList_0_) {
        ArrayList<ArrayList<CustomItemProperties>> list = new ArrayList<ArrayList<CustomItemProperties>>();
        if (p_makePropertyList_0_ != null) {
            int i = 0;
            while (i < p_makePropertyList_0_.length) {
                CustomItemProperties[] acustomitemproperties = p_makePropertyList_0_[i];
                ArrayList<CustomItemProperties> list1 = null;
                if (acustomitemproperties != null) {
                    list1 = new ArrayList<CustomItemProperties>(Arrays.asList(acustomitemproperties));
                }
                list.add(list1);
                ++i;
            }
        }
        return list;
    }

    private static CustomItemProperties[][] propertyListToArray(List p_propertyListToArray_0_) {
        CustomItemProperties[][] acustomitemproperties = new CustomItemProperties[p_propertyListToArray_0_.size()][];
        int i = 0;
        while (i < p_propertyListToArray_0_.size()) {
            List list = (List)p_propertyListToArray_0_.get(i);
            if (list != null) {
                CustomItemProperties[] acustomitemproperties1 = list.toArray(new CustomItemProperties[list.size()]);
                Arrays.sort(acustomitemproperties1, new CustomItemsComparator());
                acustomitemproperties[i] = acustomitemproperties1;
            }
            ++i;
        }
        return acustomitemproperties;
    }

    private static void addToItemList(CustomItemProperties p_addToItemList_0_, List p_addToItemList_1_) {
        if (p_addToItemList_0_.items != null) {
            int i = 0;
            while (i < p_addToItemList_0_.items.length) {
                int j = p_addToItemList_0_.items[i];
                if (j <= 0) {
                    Config.warn("Invalid item ID: " + j);
                } else {
                    CustomItems.addToList(p_addToItemList_0_, p_addToItemList_1_, j);
                }
                ++i;
            }
        }
    }

    private static void addToEnchantmentList(CustomItemProperties p_addToEnchantmentList_0_, List p_addToEnchantmentList_1_) {
        if (p_addToEnchantmentList_0_.type == 2 && p_addToEnchantmentList_0_.enchantmentIds != null) {
            int i = 0;
            while (i < 256) {
                if (p_addToEnchantmentList_0_.enchantmentIds.isInRange(i)) {
                    CustomItems.addToList(p_addToEnchantmentList_0_, p_addToEnchantmentList_1_, i);
                }
                ++i;
            }
        }
    }

    private static void addToList(CustomItemProperties p_addToList_0_, List p_addToList_1_, int p_addToList_2_) {
        while (p_addToList_2_ >= p_addToList_1_.size()) {
            p_addToList_1_.add(null);
        }
        ArrayList<CustomItemProperties> list = (ArrayList<CustomItemProperties>)p_addToList_1_.get(p_addToList_2_);
        if (list == null) {
            list = new ArrayList<CustomItemProperties>();
            p_addToList_1_.set(p_addToList_2_, list);
        }
        list.add(p_addToList_0_);
    }

    public static IBakedModel getCustomItemModel(ItemStack p_getCustomItemModel_0_, IBakedModel p_getCustomItemModel_1_, ResourceLocation p_getCustomItemModel_2_, boolean p_getCustomItemModel_3_) {
        if (!p_getCustomItemModel_3_ && p_getCustomItemModel_1_.isGui3d()) {
            return p_getCustomItemModel_1_;
        }
        if (itemProperties == null) {
            return p_getCustomItemModel_1_;
        }
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(p_getCustomItemModel_0_, 1);
        if (customitemproperties == null) {
            return p_getCustomItemModel_1_;
        }
        IBakedModel ibakedmodel = customitemproperties.getBakedModel(p_getCustomItemModel_2_, p_getCustomItemModel_3_);
        return ibakedmodel != null ? ibakedmodel : p_getCustomItemModel_1_;
    }

    public static boolean bindCustomArmorTexture(ItemStack p_bindCustomArmorTexture_0_, EntityEquipmentSlot p_bindCustomArmorTexture_1_, String p_bindCustomArmorTexture_2_) {
        if (itemProperties == null) {
            return false;
        }
        ResourceLocation resourcelocation = CustomItems.getCustomArmorLocation(p_bindCustomArmorTexture_0_, p_bindCustomArmorTexture_1_, p_bindCustomArmorTexture_2_);
        if (resourcelocation == null) {
            return false;
        }
        Config.getTextureManager().bindTexture(resourcelocation);
        return true;
    }

    private static ResourceLocation getCustomArmorLocation(ItemStack p_getCustomArmorLocation_0_, EntityEquipmentSlot p_getCustomArmorLocation_1_, String p_getCustomArmorLocation_2_) {
        String s1;
        ResourceLocation resourcelocation;
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(p_getCustomArmorLocation_0_, 3);
        if (customitemproperties == null) {
            return null;
        }
        if (customitemproperties.mapTextureLocations == null) {
            return customitemproperties.textureLocation;
        }
        Item item = p_getCustomArmorLocation_0_.getItem();
        if (!(item instanceof ItemArmor)) {
            return null;
        }
        ItemArmor itemarmor = (ItemArmor)item;
        String s = itemarmor.getArmorMaterial().getName();
        int i = p_getCustomArmorLocation_1_ == EntityEquipmentSlot.LEGS ? 2 : 1;
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("texture.");
        stringbuffer.append(s);
        stringbuffer.append("_layer_");
        stringbuffer.append(i);
        if (p_getCustomArmorLocation_2_ != null) {
            stringbuffer.append("_");
            stringbuffer.append(p_getCustomArmorLocation_2_);
        }
        return (resourcelocation = (ResourceLocation)customitemproperties.mapTextureLocations.get(s1 = stringbuffer.toString())) == null ? customitemproperties.textureLocation : resourcelocation;
    }

    public static ResourceLocation getCustomElytraTexture(ItemStack p_getCustomElytraTexture_0_, ResourceLocation p_getCustomElytraTexture_1_) {
        if (itemProperties == null) {
            return p_getCustomElytraTexture_1_;
        }
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(p_getCustomElytraTexture_0_, 4);
        if (customitemproperties == null) {
            return p_getCustomElytraTexture_1_;
        }
        return customitemproperties.textureLocation == null ? p_getCustomElytraTexture_1_ : customitemproperties.textureLocation;
    }

    private static CustomItemProperties getCustomItemProperties(ItemStack p_getCustomItemProperties_0_, int p_getCustomItemProperties_1_) {
        CustomItemProperties[] acustomitemproperties;
        if (itemProperties == null) {
            return null;
        }
        if (p_getCustomItemProperties_0_ == null) {
            return null;
        }
        Item item = p_getCustomItemProperties_0_.getItem();
        int i = Item.getIdFromItem(item);
        if (i >= 0 && i < itemProperties.length && (acustomitemproperties = itemProperties[i]) != null) {
            int j = 0;
            while (j < acustomitemproperties.length) {
                CustomItemProperties customitemproperties = acustomitemproperties[j];
                if (customitemproperties.type == p_getCustomItemProperties_1_ && CustomItems.matchesProperties(customitemproperties, p_getCustomItemProperties_0_, null)) {
                    return customitemproperties;
                }
                ++j;
            }
        }
        return null;
    }

    private static boolean matchesProperties(CustomItemProperties p_matchesProperties_0_, ItemStack p_matchesProperties_1_, int[][] p_matchesProperties_2_) {
        Item item = p_matchesProperties_1_.getItem();
        if (p_matchesProperties_0_.damage != null) {
            int i = CustomItems.getItemStackDamage(p_matchesProperties_1_);
            if (i < 0) {
                return false;
            }
            if (p_matchesProperties_0_.damageMask != 0) {
                i &= p_matchesProperties_0_.damageMask;
            }
            if (p_matchesProperties_0_.damagePercent) {
                int j = item.getMaxDamage();
                i = (int)((double)(i * 100) / (double)j);
            }
            if (!p_matchesProperties_0_.damage.isInRange(i)) {
                return false;
            }
        }
        if (p_matchesProperties_0_.stackSize != null && !p_matchesProperties_0_.stackSize.isInRange(p_matchesProperties_1_.func_190916_E())) {
            return false;
        }
        int[][] aint = p_matchesProperties_2_;
        if (p_matchesProperties_0_.enchantmentIds != null) {
            if (p_matchesProperties_2_ == null) {
                aint = CustomItems.getEnchantmentIdLevels(p_matchesProperties_1_);
            }
            boolean flag = false;
            int k = 0;
            while (k < aint.length) {
                int l = aint[k][0];
                if (p_matchesProperties_0_.enchantmentIds.isInRange(l)) {
                    flag = true;
                    break;
                }
                ++k;
            }
            if (!flag) {
                return false;
            }
        }
        if (p_matchesProperties_0_.enchantmentLevels != null) {
            if (aint == null) {
                aint = CustomItems.getEnchantmentIdLevels(p_matchesProperties_1_);
            }
            boolean flag1 = false;
            int i1 = 0;
            while (i1 < aint.length) {
                int k1 = aint[i1][1];
                if (p_matchesProperties_0_.enchantmentLevels.isInRange(k1)) {
                    flag1 = true;
                    break;
                }
                ++i1;
            }
            if (!flag1) {
                return false;
            }
        }
        if (p_matchesProperties_0_.nbtTagValues != null) {
            NBTTagCompound nbttagcompound = p_matchesProperties_1_.getTagCompound();
            int j1 = 0;
            while (j1 < p_matchesProperties_0_.nbtTagValues.length) {
                NbtTagValue nbttagvalue = p_matchesProperties_0_.nbtTagValues[j1];
                if (!nbttagvalue.matches(nbttagcompound)) {
                    return false;
                }
                ++j1;
            }
        }
        if (p_matchesProperties_0_.hand != 0) {
            if (p_matchesProperties_0_.hand == 1 && renderOffHand) {
                return false;
            }
            if (p_matchesProperties_0_.hand == 2 && !renderOffHand) {
                return false;
            }
        }
        return true;
    }

    private static int getItemStackDamage(ItemStack p_getItemStackDamage_0_) {
        Item item = p_getItemStackDamage_0_.getItem();
        return item instanceof ItemPotion ? CustomItems.getPotionDamage(p_getItemStackDamage_0_) : p_getItemStackDamage_0_.getItemDamage();
    }

    private static int getPotionDamage(ItemStack p_getPotionDamage_0_) {
        NBTTagCompound nbttagcompound = p_getPotionDamage_0_.getTagCompound();
        if (nbttagcompound == null) {
            return 0;
        }
        String s = nbttagcompound.getString("Potion");
        if (s == null) {
            return 0;
        }
        Integer integer = mapPotionDamages.get(s);
        if (integer == null) {
            return -1;
        }
        int i = integer;
        if (p_getPotionDamage_0_.getItem() == Items.SPLASH_POTION) {
            i |= 0x4000;
        }
        return i;
    }

    private static Map<String, Integer> makeMapPotionDamages() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        CustomItems.addPotion("water", 0, false, map);
        CustomItems.addPotion("awkward", 16, false, map);
        CustomItems.addPotion("thick", 32, false, map);
        CustomItems.addPotion("mundane", 64, false, map);
        CustomItems.addPotion("regeneration", 1, true, map);
        CustomItems.addPotion("swiftness", 2, true, map);
        CustomItems.addPotion("fire_resistance", 3, true, map);
        CustomItems.addPotion("poison", 4, true, map);
        CustomItems.addPotion("healing", 5, true, map);
        CustomItems.addPotion("night_vision", 6, true, map);
        CustomItems.addPotion("weakness", 8, true, map);
        CustomItems.addPotion("strength", 9, true, map);
        CustomItems.addPotion("slowness", 10, true, map);
        CustomItems.addPotion("leaping", 11, true, map);
        CustomItems.addPotion("harming", 12, true, map);
        CustomItems.addPotion("water_breathing", 13, true, map);
        CustomItems.addPotion("invisibility", 14, true, map);
        return map;
    }

    private static void addPotion(String p_addPotion_0_, int p_addPotion_1_, boolean p_addPotion_2_, Map<String, Integer> p_addPotion_3_) {
        if (p_addPotion_2_) {
            p_addPotion_1_ |= 0x2000;
        }
        p_addPotion_3_.put("minecraft:" + p_addPotion_0_, p_addPotion_1_);
        if (p_addPotion_2_) {
            int i = p_addPotion_1_ | 0x20;
            p_addPotion_3_.put("minecraft:strong_" + p_addPotion_0_, i);
            int j = p_addPotion_1_ | 0x40;
            p_addPotion_3_.put("minecraft:long_" + p_addPotion_0_, j);
        }
    }

    private static int[][] getEnchantmentIdLevels(ItemStack p_getEnchantmentIdLevels_0_) {
        NBTTagList nbttaglist1;
        Item item = p_getEnchantmentIdLevels_0_.getItem();
        if (item == Items.ENCHANTED_BOOK) {
            ItemEnchantedBook itemenchantedbook = (ItemEnchantedBook)Items.ENCHANTED_BOOK;
            nbttaglist1 = ItemEnchantedBook.getEnchantments(p_getEnchantmentIdLevels_0_);
        } else {
            nbttaglist1 = p_getEnchantmentIdLevels_0_.getEnchantmentTagList();
        }
        NBTTagList nbttaglist = nbttaglist1;
        if (nbttaglist != null && nbttaglist.tagCount() > 0) {
            int[][] aint = new int[nbttaglist.tagCount()][2];
            int i = 0;
            while (i < nbttaglist.tagCount()) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                short j = nbttagcompound.getShort("id");
                short k = nbttagcompound.getShort("lvl");
                aint[i][0] = j;
                aint[i][1] = k;
                ++i;
            }
            return aint;
        }
        return EMPTY_INT2_ARRAY;
    }

    public static boolean renderCustomEffect(RenderItem p_renderCustomEffect_0_, ItemStack p_renderCustomEffect_1_, IBakedModel p_renderCustomEffect_2_) {
        if (enchantmentProperties == null) {
            return false;
        }
        if (p_renderCustomEffect_1_ == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(p_renderCustomEffect_1_);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        int i = 0;
        while (i < aint.length) {
            CustomItemProperties[] acustomitemproperties;
            int j = aint[i][0];
            if (j >= 0 && j < enchantmentProperties.length && (acustomitemproperties = enchantmentProperties[j]) != null) {
                int k = 0;
                while (k < acustomitemproperties.length) {
                    CustomItemProperties customitemproperties = acustomitemproperties[k];
                    if (set == null) {
                        set = new HashSet<Integer>();
                    }
                    if (set.add(j) && CustomItems.matchesProperties(customitemproperties, p_renderCustomEffect_1_, aint) && customitemproperties.textureLocation != null) {
                        texturemanager.bindTexture(customitemproperties.textureLocation);
                        float f = customitemproperties.getTextureWidth(texturemanager);
                        if (!flag) {
                            flag = true;
                            GlStateManager.depthMask(false);
                            GlStateManager.depthFunc(514);
                            GlStateManager.disableLighting();
                            GlStateManager.matrixMode(5890);
                        }
                        Blender.setupBlend(customitemproperties.blend, 1.0f);
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(f / 2.0f, f / 2.0f, f / 2.0f);
                        float f1 = customitemproperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f / 8.0f;
                        GlStateManager.translate(f1, 0.0f, 0.0f);
                        GlStateManager.rotate(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                        p_renderCustomEffect_0_.func_191965_a(p_renderCustomEffect_2_, -1);
                        GlStateManager.popMatrix();
                    }
                    ++k;
                }
            }
            ++i;
        }
        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            texturemanager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        }
        return flag;
    }

    public static boolean renderCustomArmorEffect(EntityLivingBase p_renderCustomArmorEffect_0_, ItemStack p_renderCustomArmorEffect_1_, ModelBase p_renderCustomArmorEffect_2_, float p_renderCustomArmorEffect_3_, float p_renderCustomArmorEffect_4_, float p_renderCustomArmorEffect_5_, float p_renderCustomArmorEffect_6_, float p_renderCustomArmorEffect_7_, float p_renderCustomArmorEffect_8_, float p_renderCustomArmorEffect_9_) {
        if (enchantmentProperties == null) {
            return false;
        }
        if (Config.isShaders() && Shaders.isShadowPass) {
            return false;
        }
        if (p_renderCustomArmorEffect_1_ == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(p_renderCustomArmorEffect_1_);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        int i = 0;
        while (i < aint.length) {
            CustomItemProperties[] acustomitemproperties;
            int j = aint[i][0];
            if (j >= 0 && j < enchantmentProperties.length && (acustomitemproperties = enchantmentProperties[j]) != null) {
                int k = 0;
                while (k < acustomitemproperties.length) {
                    CustomItemProperties customitemproperties = acustomitemproperties[k];
                    if (set == null) {
                        set = new HashSet<Integer>();
                    }
                    if (set.add(j) && CustomItems.matchesProperties(customitemproperties, p_renderCustomArmorEffect_1_, aint) && customitemproperties.textureLocation != null) {
                        texturemanager.bindTexture(customitemproperties.textureLocation);
                        float f = customitemproperties.getTextureWidth(texturemanager);
                        if (!flag) {
                            flag = true;
                            if (Config.isShaders()) {
                                ShadersRender.renderEnchantedGlintBegin();
                            }
                            GlStateManager.enableBlend();
                            GlStateManager.depthFunc(514);
                            GlStateManager.depthMask(false);
                        }
                        Blender.setupBlend(customitemproperties.blend, 1.0f);
                        GlStateManager.disableLighting();
                        GlStateManager.matrixMode(5890);
                        GlStateManager.loadIdentity();
                        GlStateManager.rotate(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                        float f1 = f / 8.0f;
                        GlStateManager.scale(f1, f1 / 2.0f, f1);
                        float f2 = customitemproperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f / 8.0f;
                        GlStateManager.translate(0.0f, f2, 0.0f);
                        GlStateManager.matrixMode(5888);
                        p_renderCustomArmorEffect_2_.render(p_renderCustomArmorEffect_0_, p_renderCustomArmorEffect_3_, p_renderCustomArmorEffect_4_, p_renderCustomArmorEffect_6_, p_renderCustomArmorEffect_7_, p_renderCustomArmorEffect_8_, p_renderCustomArmorEffect_9_);
                    }
                    ++k;
                }
            }
            ++i;
        }
        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }
        return flag;
    }

    public static boolean isUseGlint() {
        return useGlint;
    }

    public static void setRenderOffHand(boolean p_setRenderOffHand_0_) {
        renderOffHand = p_setRenderOffHand_0_;
    }
}

