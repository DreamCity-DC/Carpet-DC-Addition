package dc.carpetdcaddition.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dc.carpetdcaddition.DCSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.vault.VaultSharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Extends TIS Addition's vault activation bypass with a player-name check.
 * The activation filter receives UUIDs, so resolve players in the current level
 * without changing the stored rewarded-player set.
 */
@Mixin(VaultSharedData.class)
public abstract class VaultSharedDataMixin {
    @WrapOperation(
            method = "updateConnectedPlayersWithinRange",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"
            )
    )
    private Stream<UUID> dc$allowMatchingPlayersToConnect(
            Stream<UUID> stream,
            Predicate<? super UUID> predicate,
            Operation<Stream<UUID>> original,
            @Local(argsOnly = true) ServerLevel level) {
        String prefix = DCSettings.vaultBlacklistDisabledByNamePrefix;
        if (prefix.isEmpty() || "false".equals(prefix)) {
            return original.call(stream, predicate);
        }

        Predicate<UUID> eligible = uuid -> {
            if (predicate.test(uuid)) {
                return true;
            }

            Player player = level.getPlayerByUUID(uuid);
            return player != null && DCSettings.bypassVaultBlacklist(player);
        };
        return original.call(stream, eligible);
    }
}
