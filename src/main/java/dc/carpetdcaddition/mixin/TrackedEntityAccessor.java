package dc.carpetdcaddition.mixin;

import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * Exposes the existing vanilla tracking set instead of calculating another map radius.
 * Paper passes this same seenBy set to ServerEntity as trackedPlayers:
 * https://github.com/PaperMC/Paper/blob/a4b87cf896e18419005ab172e8b1694ccc1abe7c/paper-server/patches/sources/net/minecraft/server/level/ChunkMap.java.patch
 * Reusing the set respects vanilla chunk tracking and changes made by Entity View Distance.
 */
@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public interface TrackedEntityAccessor {
    @Accessor("seenBy")
    Set<ServerPlayerConnection> dc$getTrackingPlayers();
}
