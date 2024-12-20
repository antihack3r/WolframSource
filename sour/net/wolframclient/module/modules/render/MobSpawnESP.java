/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.wolframclient.compatibility.WBlock;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketReceiveEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.RotationUtils;
import org.lwjgl.opengl.GL11;

public class MobSpawnESP
extends ModuleListener {
    private final HashMap<Chunk, ChunkScanner> scanners = new HashMap();
    private ExecutorService pool;

    public MobSpawnESP() {
        super("MobSpawnESP", Module.Category.RENDER, "Highlights areas where mobs can spawn.\n" + ChatFormatting.YELLOW + "yellow" + ChatFormatting.RESET + " - mobs can spawn at night\n" + ChatFormatting.RED + "red" + ChatFormatting.RESET + " - mobs can always spawn");
    }

    @Override
    protected void onEnable2() {
        this.pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new MinPriorityThreadFactory());
    }

    @Override
    protected void onDisable2() {
        for (ChunkScanner scanner : new ArrayList<ChunkScanner>(this.scanners.values())) {
            if (scanner.displayList != 0) {
                GL11.glDeleteLists((int)scanner.displayList, (int)1);
            }
            this.scanners.remove(scanner.chunk);
        }
        this.pool.shutdownNow();
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        WorldClient world = WMinecraft.getWorld();
        BlockPos eyesBlock = new BlockPos(RotationUtils.getEyesPos());
        int chunkX = eyesBlock.getX() >> 4;
        int chunkZ = eyesBlock.getZ() >> 4;
        int chunkRange = 4;
        ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        int x = chunkX - chunkRange;
        while (x <= chunkX + chunkRange) {
            int z = chunkZ - chunkRange;
            while (z <= chunkZ + chunkRange) {
                chunks.add(world.getChunkFromChunkCoords(x, z));
                ++z;
            }
            ++x;
        }
        for (Chunk chunk : chunks) {
            if (this.scanners.containsKey(chunk)) continue;
            ChunkScanner scanner = new ChunkScanner(chunk);
            this.scanners.put(chunk, scanner);
            scanner.future = this.pool.submit(() -> scanner.scan());
        }
        for (ChunkScanner scanner : new ArrayList<ChunkScanner>(this.scanners.values())) {
            if (Math.abs(((ChunkScanner)scanner).chunk.xPosition - chunkX) <= chunkRange && Math.abs(((ChunkScanner)scanner).chunk.zPosition - chunkZ) <= chunkRange) continue;
            if (scanner.displayList != 0) {
                GL11.glDeleteLists((int)scanner.displayList, (int)1);
            }
            if (scanner.future != null) {
                scanner.future.cancel(true);
            }
            this.scanners.remove(scanner.chunk);
        }
        Comparator<ChunkScanner> c = Comparator.comparingInt(s -> Math.abs(((ChunkScanner)s).chunk.xPosition - chunkX) + Math.abs(((ChunkScanner)s).chunk.zPosition - chunkZ));
        List sortedScanners = this.scanners.values().stream().filter(s -> ((ChunkScanner)s).doneScanning).filter(s -> !((ChunkScanner)s).doneCompiling).sorted(c).limit(1L).collect(Collectors.toList());
        for (ChunkScanner scanner : sortedScanners) {
            if (scanner.displayList == 0) {
                scanner.displayList = GL11.glGenLists((int)1);
            }
            scanner.compileDisplayList();
        }
    }

    @EventTarget
    public void onPacketInput(NetworkManagerPacketReceiveEvent event) {
        Chunk chunk;
        Packet<INetHandlerPlayClient> change;
        EntityPlayerSP player = WMinecraft.getPlayer();
        WorldClient world = WMinecraft.getWorld();
        if (player == null || world == null) {
            return;
        }
        Packet packet = event.getPacket();
        if (packet instanceof SPacketBlockChange) {
            change = (SPacketBlockChange)packet;
            BlockPos pos = ((SPacketBlockChange)change).getBlockPosition();
            chunk = world.getChunkFromBlockCoords(pos);
        } else if (packet instanceof SPacketMultiBlockChange) {
            change = (SPacketMultiBlockChange)packet;
            SPacketMultiBlockChange.BlockUpdateData[] changedBlocks = ((SPacketMultiBlockChange)change).getChangedBlocks();
            if (changedBlocks.length == 0) {
                return;
            }
            BlockPos pos = changedBlocks[0].getPos();
            chunk = world.getChunkFromBlockCoords(pos);
        } else if (packet instanceof SPacketChunkData) {
            SPacketChunkData chunkData = (SPacketChunkData)packet;
            chunk = world.getChunkFromChunkCoords(chunkData.getChunkX(), chunkData.getChunkZ());
        } else {
            return;
        }
        ArrayList<Chunk> chunks = new ArrayList<Chunk>();
        int x = chunk.xPosition - 1;
        while (x <= chunk.xPosition + 1) {
            int z = chunk.zPosition - 1;
            while (z <= chunk.zPosition + 1) {
                chunks.add(world.getChunkFromChunkCoords(x, z));
                ++z;
            }
            ++x;
        }
        for (Chunk chunk2 : chunks) {
            ChunkScanner scanner = this.scanners.get(chunk2);
            if (scanner == null) {
                return;
            }
            scanner.reset();
            scanner.future = this.pool.submit(() -> scanner.scan());
        }
    }

    @EventTarget
    public void onWorldRender(WorldRenderEvent event) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glLineWidth((float)2.0f);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2884);
        GL11.glPushMatrix();
        GL11.glTranslated((double)(-RenderManager.renderPosX), (double)(-RenderManager.renderPosY), (double)(-RenderManager.renderPosZ));
        for (ChunkScanner scanner : new ArrayList<ChunkScanner>(this.scanners.values())) {
            if (scanner.displayList == 0) continue;
            GL11.glCallList((int)scanner.displayList);
        }
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
    }

    private class ChunkScanner {
        public Future future;
        private final Chunk chunk;
        private final Set<BlockPos> red = new HashSet<BlockPos>();
        private final Set<BlockPos> yellow = new HashSet<BlockPos>();
        private int displayList;
        private boolean doneScanning;
        private boolean doneCompiling;

        public ChunkScanner(Chunk chunk) {
            this.chunk = chunk;
        }

        private void scan() {
            BlockPos min = new BlockPos(this.chunk.xPosition << 4, 0, this.chunk.zPosition << 4);
            BlockPos max = new BlockPos((this.chunk.xPosition << 4) + 15, 255, (this.chunk.zPosition << 4) + 15);
            Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(min, max).spliterator(), false);
            WorldClient world = WMinecraft.getWorld();
            List blocks = stream.filter(pos -> !WBlock.getMaterial(pos).blocksMovement() && !(WBlock.getBlock(pos) instanceof BlockLiquid) && WBlock.isFullyOpaque(pos.down())).collect(Collectors.toList());
            if (Thread.interrupted()) {
                return;
            }
            this.red.addAll(blocks.stream().filter(pos -> world.getLightFor(EnumSkyBlock.BLOCK, (BlockPos)pos) < 8).filter(pos -> world.getLightFor(EnumSkyBlock.SKY, (BlockPos)pos) < 8).collect(Collectors.toList()));
            if (Thread.interrupted()) {
                return;
            }
            this.yellow.addAll(blocks.stream().filter(pos -> !this.red.contains(pos)).filter(pos -> world.getLightFor(EnumSkyBlock.BLOCK, (BlockPos)pos) < 8).collect(Collectors.toList()));
            this.doneScanning = true;
        }

        private void compileDisplayList() {
            GL11.glNewList((int)this.displayList, (int)4864);
            GL11.glColor4f((float)1.0f, (float)0.0f, (float)0.0f, (float)0.5f);
            GL11.glBegin((int)1);
            new ArrayList<BlockPos>(this.red).forEach(pos -> {
                GL11.glVertex3d((double)pos.getX(), (double)((double)pos.getY() + 0.01), (double)pos.getZ());
                GL11.glVertex3d((double)(pos.getX() + 1), (double)((double)pos.getY() + 0.01), (double)(pos.getZ() + 1));
                GL11.glVertex3d((double)(pos.getX() + 1), (double)((double)pos.getY() + 0.01), (double)pos.getZ());
                GL11.glVertex3d((double)pos.getX(), (double)((double)pos.getY() + 0.01), (double)(pos.getZ() + 1));
            });
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)0.0f, (float)0.5f);
            new ArrayList<BlockPos>(this.yellow).forEach(pos -> {
                GL11.glVertex3d((double)pos.getX(), (double)((double)pos.getY() + 0.01), (double)pos.getZ());
                GL11.glVertex3d((double)(pos.getX() + 1), (double)((double)pos.getY() + 0.01), (double)(pos.getZ() + 1));
                GL11.glVertex3d((double)(pos.getX() + 1), (double)((double)pos.getY() + 0.01), (double)pos.getZ());
                GL11.glVertex3d((double)pos.getX(), (double)((double)pos.getY() + 0.01), (double)(pos.getZ() + 1));
            });
            GL11.glEnd();
            GL11.glEndList();
            this.doneCompiling = true;
        }

        private void reset() {
            if (this.future != null) {
                this.future.cancel(true);
            }
            this.red.clear();
            this.yellow.clear();
            this.doneScanning = false;
            this.doneCompiling = false;
        }
    }

    private static class MinPriorityThreadFactory
    implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public MinPriorityThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "pool-min-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, String.valueOf(this.namePrefix) + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 1) {
                t.setPriority(1);
            }
            return t;
        }
    }
}

