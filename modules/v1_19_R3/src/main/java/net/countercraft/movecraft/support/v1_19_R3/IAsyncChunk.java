package net.countercraft.movecraft.support.v1_19_R3;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.processing.WorldManager;
import net.countercraft.movecraft.support.AsyncChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class IAsyncChunk extends AsyncChunk<CraftChunk> {

    private final @NotNull LoadingCache<MovecraftLocation, BlockState> stateCache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<>() {
        @Override
        public BlockState load(@NotNull MovecraftLocation movecraftLocation) {
            var block = chunk.getBlock(movecraftLocation.getX(), movecraftLocation.getY(), movecraftLocation.getZ());
            return WorldManager.INSTANCE.executeMain(block::getState);
        }
    });
    private final ChunkAccess handle;

    public IAsyncChunk(@NotNull Chunk chunk) {
        super(chunk);
        handle = this.chunk.getHandle(ChunkStatus.FULL);
    }

    @NotNull
    @Override
    protected CraftChunk adapt(@NotNull org.bukkit.Chunk chunk) {
        return (CraftChunk) chunk;
    }

    @NotNull
    @Override
    public BlockState getState(@NotNull MovecraftLocation location) {
        return stateCache.getUnchecked(location);
    }

    @Override
    @NotNull
    public Material getType(@NotNull MovecraftLocation location){
        return this.getData(location).getMaterial();
    }

    @Override
    @NotNull
    public BlockData getData(@NotNull MovecraftLocation location){
        return CraftBlockData.fromData(handle.getBlockState(new BlockPos(location.getX(), location.getY(), location.getZ())));
    }

}
