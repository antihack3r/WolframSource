/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.util.Iterator;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.src.BlockPosM;
import net.minecraft.util.math.BlockPos;
import shadersmod.client.Iterator3d;

public class IteratorRenderChunks
implements Iterator<RenderChunk> {
    private ViewFrustum viewFrustum;
    private Iterator3d Iterator3d;
    private BlockPosM posBlock = new BlockPosM(0, 0, 0);

    public IteratorRenderChunks(ViewFrustum viewFrustum, BlockPos posStart, BlockPos posEnd, int width, int height) {
        this.viewFrustum = viewFrustum;
        this.Iterator3d = new Iterator3d(posStart, posEnd, width, height);
    }

    @Override
    public boolean hasNext() {
        return this.Iterator3d.hasNext();
    }

    @Override
    public RenderChunk next() {
        BlockPos blockpos = this.Iterator3d.next();
        this.posBlock.setXyz(blockpos.getX() << 4, blockpos.getY() << 4, blockpos.getZ() << 4);
        RenderChunk renderchunk = this.viewFrustum.getRenderChunk(this.posBlock);
        return renderchunk;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Not implemented");
    }
}

