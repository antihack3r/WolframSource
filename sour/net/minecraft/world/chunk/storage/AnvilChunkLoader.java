/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader
implements IChunkLoader,
IThreadedFileIO {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ChunkPos, NBTTagCompound> chunksToRemove = Maps.newConcurrentMap();
    private final Set<ChunkPos> field_193415_c = Collections.newSetFromMap(Maps.newConcurrentMap());
    private final File chunkSaveLocation;
    private final DataFixer field_193416_e;
    private boolean savingExtraData;

    public AnvilChunkLoader(File chunkSaveLocationIn, DataFixer dataFixerIn) {
        this.chunkSaveLocation = chunkSaveLocationIn;
        this.field_193416_e = dataFixerIn;
    }

    @Override
    @Nullable
    public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
        ChunkPos chunkpos = new ChunkPos(x, z);
        NBTTagCompound nbttagcompound = this.chunksToRemove.get(chunkpos);
        if (nbttagcompound == null) {
            DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, x, z);
            if (datainputstream == null) {
                return null;
            }
            nbttagcompound = this.field_193416_e.process(FixTypes.CHUNK, CompressedStreamTools.read(datainputstream));
        }
        return this.checkedReadChunkFromNBT(worldIn, x, z, nbttagcompound);
    }

    @Override
    public boolean func_191063_a(int p_191063_1_, int p_191063_2_) {
        ChunkPos chunkpos = new ChunkPos(p_191063_1_, p_191063_2_);
        NBTTagCompound nbttagcompound = this.chunksToRemove.get(chunkpos);
        return nbttagcompound != null ? true : RegionFileCache.func_191064_f(this.chunkSaveLocation, p_191063_1_, p_191063_2_);
    }

    @Nullable
    protected Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound compound) {
        if (!compound.hasKey("Level", 10)) {
            LOGGER.error("Chunk file at {},{} is missing level data, skipping", (Object)x, (Object)z);
            return null;
        }
        NBTTagCompound nbttagcompound = compound.getCompoundTag("Level");
        if (!nbttagcompound.hasKey("Sections", 9)) {
            LOGGER.error("Chunk file at {},{} is missing block data, skipping", (Object)x, (Object)z);
            return null;
        }
        Chunk chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
        if (!chunk.isAtLocation(x, z)) {
            LOGGER.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", (Object)x, (Object)z, (Object)x, (Object)z, (Object)chunk.xPosition, (Object)chunk.zPosition);
            nbttagcompound.setInteger("xPos", x);
            nbttagcompound.setInteger("zPos", z);
            chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
        }
        return chunk;
    }

    @Override
    public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {
        worldIn.checkSessionLock();
        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.setTag("Level", nbttagcompound1);
            nbttagcompound.setInteger("DataVersion", 1139);
            this.writeChunkToNBT(chunkIn, worldIn, nbttagcompound1);
            this.addChunkToPending(chunkIn.getChunkCoordIntPair(), nbttagcompound);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save chunk", (Throwable)exception);
        }
    }

    protected void addChunkToPending(ChunkPos pos, NBTTagCompound compound) {
        if (!this.field_193415_c.contains(pos)) {
            this.chunksToRemove.put(pos, compound);
        }
        ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
    }

    @Override
    public boolean writeNextIO() {
        boolean lvt_3_1_;
        if (this.chunksToRemove.isEmpty()) {
            if (this.savingExtraData) {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.chunkSaveLocation.getName());
            }
            return false;
        }
        ChunkPos chunkpos = this.chunksToRemove.keySet().iterator().next();
        try {
            this.field_193415_c.add(chunkpos);
            NBTTagCompound nbttagcompound = this.chunksToRemove.remove(chunkpos);
            if (nbttagcompound != null) {
                try {
                    this.writeChunkData(chunkpos, nbttagcompound);
                }
                catch (Exception exception) {
                    LOGGER.error("Failed to save chunk", (Throwable)exception);
                }
            }
            lvt_3_1_ = true;
        }
        finally {
            this.field_193415_c.remove(chunkpos);
        }
        return lvt_3_1_;
    }

    private void writeChunkData(ChunkPos pos, NBTTagCompound compound) throws IOException {
        DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, pos.chunkXPos, pos.chunkZPos);
        CompressedStreamTools.write(compound, dataoutputstream);
        dataoutputstream.close();
    }

    @Override
    public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
    }

    @Override
    public void chunkTick() {
    }

    @Override
    public void saveExtraData() {
        try {
            this.savingExtraData = true;
            while (this.writeNextIO()) {
            }
        }
        finally {
            this.savingExtraData = false;
        }
    }

    public static void registerFixes(DataFixer fixer) {
        fixer.registerWalker(FixTypes.CHUNK, new IDataWalker(){

            @Override
            public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn) {
                if (compound.hasKey("Level", 10)) {
                    NBTTagCompound nbttagcompound = compound.getCompoundTag("Level");
                    if (nbttagcompound.hasKey("Entities", 9)) {
                        NBTTagList nbttaglist = nbttagcompound.getTagList("Entities", 10);
                        int i = 0;
                        while (i < nbttaglist.tagCount()) {
                            nbttaglist.set(i, fixer.process(FixTypes.ENTITY, (NBTTagCompound)nbttaglist.get(i), versionIn));
                            ++i;
                        }
                    }
                    if (nbttagcompound.hasKey("TileEntities", 9)) {
                        NBTTagList nbttaglist1 = nbttagcompound.getTagList("TileEntities", 10);
                        int j = 0;
                        while (j < nbttaglist1.tagCount()) {
                            nbttaglist1.set(j, fixer.process(FixTypes.BLOCK_ENTITY, (NBTTagCompound)nbttaglist1.get(j), versionIn));
                            ++j;
                        }
                    }
                }
                return compound;
            }
        });
    }

    private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound compound) {
        compound.setInteger("xPos", chunkIn.xPosition);
        compound.setInteger("zPos", chunkIn.zPosition);
        compound.setLong("LastUpdate", worldIn.getTotalWorldTime());
        compound.setIntArray("HeightMap", chunkIn.getHeightMap());
        compound.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
        compound.setBoolean("LightPopulated", chunkIn.isLightPopulated());
        compound.setLong("InhabitedTime", chunkIn.getInhabitedTime());
        ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = worldIn.provider.func_191066_m();
        ExtendedBlockStorage[] extendedBlockStorageArray = aextendedblockstorage;
        int n = aextendedblockstorage.length;
        int n2 = 0;
        while (n2 < n) {
            ExtendedBlockStorage extendedblockstorage = extendedBlockStorageArray[n2];
            if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Y", (byte)(extendedblockstorage.getYLocation() >> 4 & 0xFF));
                byte[] abyte = new byte[4096];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = extendedblockstorage.getData().getDataForNBT(abyte, nibblearray);
                nbttagcompound.setByteArray("Blocks", abyte);
                nbttagcompound.setByteArray("Data", nibblearray.getData());
                if (nibblearray1 != null) {
                    nbttagcompound.setByteArray("Add", nibblearray1.getData());
                }
                nbttagcompound.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().getData());
                if (flag) {
                    nbttagcompound.setByteArray("SkyLight", extendedblockstorage.getSkylightArray().getData());
                } else {
                    nbttagcompound.setByteArray("SkyLight", new byte[extendedblockstorage.getBlocklightArray().getData().length]);
                }
                nbttaglist.appendTag(nbttagcompound);
            }
            ++n2;
        }
        compound.setTag("Sections", nbttaglist);
        compound.setByteArray("Biomes", chunkIn.getBiomeArray());
        chunkIn.setHasEntities(false);
        NBTTagList nbttaglist1 = new NBTTagList();
        int i = 0;
        while (i < chunkIn.getEntityLists().length) {
            for (Entity entity : chunkIn.getEntityLists()[i]) {
                NBTTagCompound nbttagcompound2;
                if (!entity.writeToNBTOptional(nbttagcompound2 = new NBTTagCompound())) continue;
                chunkIn.setHasEntities(true);
                nbttaglist1.appendTag(nbttagcompound2);
            }
            ++i;
        }
        compound.setTag("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();
        for (TileEntity tileentity : chunkIn.getTileEntityMap().values()) {
            NBTTagCompound nbttagcompound3 = tileentity.writeToNBT(new NBTTagCompound());
            nbttaglist2.appendTag(nbttagcompound3);
        }
        compound.setTag("TileEntities", nbttaglist2);
        List<NextTickListEntry> list = worldIn.getPendingBlockUpdates(chunkIn, false);
        if (list != null) {
            long j = worldIn.getTotalWorldTime();
            NBTTagList nbttaglist3 = new NBTTagList();
            for (NextTickListEntry nextticklistentry : list) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(nextticklistentry.getBlock());
                nbttagcompound1.setString("i", resourcelocation == null ? "" : resourcelocation.toString());
                nbttagcompound1.setInteger("x", nextticklistentry.position.getX());
                nbttagcompound1.setInteger("y", nextticklistentry.position.getY());
                nbttagcompound1.setInteger("z", nextticklistentry.position.getZ());
                nbttagcompound1.setInteger("t", (int)(nextticklistentry.scheduledTime - j));
                nbttagcompound1.setInteger("p", nextticklistentry.priority);
                nbttaglist3.appendTag(nbttagcompound1);
            }
            compound.setTag("TileTicks", nbttaglist3);
        }
    }

    private Chunk readChunkFromNBT(World worldIn, NBTTagCompound compound) {
        int i = compound.getInteger("xPos");
        int j = compound.getInteger("zPos");
        Chunk chunk = new Chunk(worldIn, i, j);
        chunk.setHeightMap(compound.getIntArray("HeightMap"));
        chunk.setTerrainPopulated(compound.getBoolean("TerrainPopulated"));
        chunk.setLightPopulated(compound.getBoolean("LightPopulated"));
        chunk.setInhabitedTime(compound.getLong("InhabitedTime"));
        NBTTagList nbttaglist = compound.getTagList("Sections", 10);
        int k = 16;
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[16];
        boolean flag = worldIn.provider.func_191066_m();
        int l = 0;
        while (l < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(l);
            byte i1 = nbttagcompound.getByte("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(i1 << 4, flag);
            byte[] abyte = nbttagcompound.getByteArray("Blocks");
            NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
            NibbleArray nibblearray1 = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
            extendedblockstorage.getData().setDataFromNBT(abyte, nibblearray, nibblearray1);
            extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));
            if (flag) {
                extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
            }
            extendedblockstorage.removeInvalidBlocks();
            aextendedblockstorage[i1] = extendedblockstorage;
            ++l;
        }
        chunk.setStorageArrays(aextendedblockstorage);
        if (compound.hasKey("Biomes", 7)) {
            chunk.setBiomeArray(compound.getByteArray("Biomes"));
        }
        NBTTagList nbttaglist1 = compound.getTagList("Entities", 10);
        int j1 = 0;
        while (j1 < nbttaglist1.tagCount()) {
            NBTTagCompound nbttagcompound1 = nbttaglist1.getCompoundTagAt(j1);
            AnvilChunkLoader.readChunkEntity(nbttagcompound1, worldIn, chunk);
            chunk.setHasEntities(true);
            ++j1;
        }
        NBTTagList nbttaglist2 = compound.getTagList("TileEntities", 10);
        int k1 = 0;
        while (k1 < nbttaglist2.tagCount()) {
            NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(k1);
            TileEntity tileentity = TileEntity.create(worldIn, nbttagcompound2);
            if (tileentity != null) {
                chunk.addTileEntity(tileentity);
            }
            ++k1;
        }
        if (compound.hasKey("TileTicks", 9)) {
            NBTTagList nbttaglist3 = compound.getTagList("TileTicks", 10);
            int l1 = 0;
            while (l1 < nbttaglist3.tagCount()) {
                NBTTagCompound nbttagcompound3 = nbttaglist3.getCompoundTagAt(l1);
                Block block = nbttagcompound3.hasKey("i", 8) ? Block.getBlockFromName(nbttagcompound3.getString("i")) : Block.getBlockById(nbttagcompound3.getInteger("i"));
                worldIn.scheduleBlockUpdate(new BlockPos(nbttagcompound3.getInteger("x"), nbttagcompound3.getInteger("y"), nbttagcompound3.getInteger("z")), block, nbttagcompound3.getInteger("t"), nbttagcompound3.getInteger("p"));
                ++l1;
            }
        }
        return chunk;
    }

    @Nullable
    public static Entity readChunkEntity(NBTTagCompound compound, World worldIn, Chunk chunkIn) {
        Entity entity = AnvilChunkLoader.createEntityFromNBT(compound, worldIn);
        if (entity == null) {
            return null;
        }
        chunkIn.addEntity(entity);
        if (compound.hasKey("Passengers", 9)) {
            NBTTagList nbttaglist = compound.getTagList("Passengers", 10);
            int i = 0;
            while (i < nbttaglist.tagCount()) {
                Entity entity1 = AnvilChunkLoader.readChunkEntity(nbttaglist.getCompoundTagAt(i), worldIn, chunkIn);
                if (entity1 != null) {
                    entity1.startRiding(entity, true);
                }
                ++i;
            }
        }
        return entity;
    }

    @Nullable
    public static Entity readWorldEntityPos(NBTTagCompound compound, World worldIn, double x, double y, double z, boolean attemptSpawn) {
        Entity entity = AnvilChunkLoader.createEntityFromNBT(compound, worldIn);
        if (entity == null) {
            return null;
        }
        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
        if (attemptSpawn && !worldIn.spawnEntityInWorld(entity)) {
            return null;
        }
        if (compound.hasKey("Passengers", 9)) {
            NBTTagList nbttaglist = compound.getTagList("Passengers", 10);
            int i = 0;
            while (i < nbttaglist.tagCount()) {
                Entity entity1 = AnvilChunkLoader.readWorldEntityPos(nbttaglist.getCompoundTagAt(i), worldIn, x, y, z, attemptSpawn);
                if (entity1 != null) {
                    entity1.startRiding(entity, true);
                }
                ++i;
            }
        }
        return entity;
    }

    @Nullable
    protected static Entity createEntityFromNBT(NBTTagCompound compound, World worldIn) {
        try {
            return EntityList.createEntityFromNBT(compound, worldIn);
        }
        catch (RuntimeException var3) {
            return null;
        }
    }

    public static void spawnEntity(Entity entityIn, World worldIn) {
        if (worldIn.spawnEntityInWorld(entityIn) && entityIn.isBeingRidden()) {
            for (Entity entity : entityIn.getPassengers()) {
                AnvilChunkLoader.spawnEntity(entity, worldIn);
            }
        }
    }

    @Nullable
    public static Entity readWorldEntity(NBTTagCompound compound, World worldIn, boolean p_186051_2_) {
        Entity entity = AnvilChunkLoader.createEntityFromNBT(compound, worldIn);
        if (entity == null) {
            return null;
        }
        if (p_186051_2_ && !worldIn.spawnEntityInWorld(entity)) {
            return null;
        }
        if (compound.hasKey("Passengers", 9)) {
            NBTTagList nbttaglist = compound.getTagList("Passengers", 10);
            int i = 0;
            while (i < nbttaglist.tagCount()) {
                Entity entity1 = AnvilChunkLoader.readWorldEntity(nbttaglist.getCompoundTagAt(i), worldIn, p_186051_2_);
                if (entity1 != null) {
                    entity1.startRiding(entity, true);
                }
                ++i;
            }
        }
        return entity;
    }
}

