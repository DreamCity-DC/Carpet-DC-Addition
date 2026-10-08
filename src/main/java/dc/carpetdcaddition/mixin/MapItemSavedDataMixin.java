package dc.carpetdcaddition.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dc.carpetdcaddition.DCSettings;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Ports Paper's "only mark dirty if a change occurs" map-decoration fix:
 * https://github.com/PaperMC/Paper/blob/a4b87cf896e18419005ab172e8b1694ccc1abe7c/paper-server/patches/sources/net/minecraft/world/level/saveddata/maps/MapItemSavedData.java.patch#L88-L94
 *
 * Vanilla's frame update tries to remove a player's map marker when that player has no
 * matching map in the inventory. Even if no marker exists (normal for static image maps),
 * removeDecoration marks every holder's decorations dirty. Those holders can repeatedly
 * receive decoration-only packets although neither pixels nor markers changed.
 *
 * The upstream fix only marks decorations dirty when Map.remove returned a real marker.
 * Preserve the original removal/count bookkeeping and notification of actual changes.
 */
@Mixin(MapItemSavedData.class)
public abstract class MapItemSavedDataMixin {
    @WrapWithCondition(
            method = "removeDecoration",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;setDecorationsDirty()V")
    )
    private boolean dc$markOnlyChangedDecorations(
            MapItemSavedData map, @Local(ordinal = 0) MapDecoration removedDecoration) {
        return !DCSettings.optimizeMapPixelsUpdate || removedDecoration != null;
    }
}
