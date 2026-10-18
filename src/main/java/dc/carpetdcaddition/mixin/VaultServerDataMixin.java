/*
 * Adapted from Carpet TIS Addition's vaultBlacklistDisabled rule:
 * https://github.com/TISUnion/Carpet-TIS-Addition/blob/master/versions/1.20.6/src/main/java/carpettisaddition/mixins/rule/vaultBlacklistDisabled/VaultServerDataMixin.java
 *
 * Copyright (C) 2024 Fallen_Breath and contributors
 *
 * Carpet TIS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet TIS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet TIS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package dc.carpetdcaddition.mixin;

import dc.carpetdcaddition.DCSettings;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.vault.VaultServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VaultServerData.class)
public abstract class VaultServerDataMixin {
    @Inject(method = "addToRewardedPlayers", at = @At("HEAD"), cancellable = true)
    private void dc$skipRewardedPlayerRecord(Player player, CallbackInfo ci) {
        if (DCSettings.bypassVaultBlacklist(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "hasRewardedPlayer", at = @At("HEAD"), cancellable = true)
    private void dc$bypassRewardedPlayerCheck(
            Player player, CallbackInfoReturnable<Boolean> cir) {
        if (DCSettings.bypassVaultBlacklist(player)) {
            cir.setReturnValue(false);
        }
    }
}
