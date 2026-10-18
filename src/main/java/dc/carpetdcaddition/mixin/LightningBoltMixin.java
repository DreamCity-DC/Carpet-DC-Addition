/*
 * Adapted from Carpet Igny Addition's lightningBoltNoFire rule (LGPL-3.0):
 * https://github.com/liuyuexiaoyu1/Carpet-Igny-Addition/blob/50fa966269a817a8723393c38e9ae70227f87e57/src/main/java/com/liuyue/igny/mixins/rule/lightningBoltNoFire/LightningBoltMixin.java
 */
package dc.carpetdcaddition.mixin;

import dc.carpetdcaddition.DCSettings;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {
    @Inject(method = "spawnFire", at = @At("HEAD"), cancellable = true)
    private void dc$preventLightningFire(int spreadAttempts, CallbackInfo ci) {
        if (DCSettings.lightningNoFire) {
            ci.cancel();
        }
    }
}
