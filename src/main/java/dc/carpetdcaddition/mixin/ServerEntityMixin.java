package dc.carpetdcaddition.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dc.carpetdcaddition.DCSettings;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Set;

/**
 * Ports the map-related behavior of Paper's ServerEntity patch.
 * Upstream source (Paper 26.3):
 * https://github.com/PaperMC/Paper/blob/a4b87cf896e18419005ab172e8b1694ccc1abe7c/paper-server/patches/sources/net/minecraft/server/level/ServerEntity.java.patch#L38-L52
 *
 * Vanilla 26.3 calls the item-frame map branch even with no tracking players when the
 * chunk is entity-ticking, then iterates every player in that dimension. That bypasses
 * the frame's tracking range. Each newly created map/player HoldingPlayer starts with
 * a full 128x128 dirty rectangle, so a large map wall can send unnecessary full maps to
 * distant players and saturate a shared uplink. Existing holders do not necessarily
 * receive full pixels again; packets can also contain only decorations or be absent.
 *
 * Paper restricts map recipients to the frame's existing tracking set and avoids work
 * without recipients. Applyed these map changes. 
 */
@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow
    @Final
    private ServerEntity.Synchronizer synchronizer;

    @WrapOperation(
            method = "sendChanges",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/MapItem;getSavedData(Lnet/minecraft/world/level/saveddata/maps/MapId;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;")
    )
    private MapItemSavedData dc$skipUntrackedMap(
            MapId id, Level level, Operation<MapItemSavedData> original) {
        if (DCSettings.optimizeMapPixelsUpdate && dc$trackingPlayers().isEmpty()) {
            // Null takes vanilla's existing no-map-data branch. Do not cancel sendChanges:
            // frame metadata and other entity updates must retain their vanilla behavior.
            return null;
        }
        return original.call(id, level);
    }

    @WrapOperation(
            method = "sendChanges",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;players()Ljava/util/List;")
    )
    private List<ServerPlayer> dc$mapRecipients(
            ServerLevel level, Operation<List<ServerPlayer>> original) {
        if (!DCSettings.optimizeMapPixelsUpdate) {
            return original.call(level);
        }
        return dc$trackingPlayers().stream().map(ServerPlayerConnection::getPlayer).toList();
    }

    @Unique
    private Set<ServerPlayerConnection> dc$trackingPlayers() {
        // Vanilla creates ServerEntity with its actual ChunkMap.TrackedEntity synchronizer.
        return ((TrackedEntityAccessor) this.synchronizer).dc$getTrackingPlayers();
    }
}
