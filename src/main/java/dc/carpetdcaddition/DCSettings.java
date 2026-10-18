package dc.carpetdcaddition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;
import net.minecraft.world.entity.player.Player;

public final class DCSettings {
    private DCSettings() {
    }

    @Rule(categories = {"dc", RuleCategory.OPTIMIZATION, RuleCategory.BUGFIX})
    public static boolean optimizeMapPixelsUpdate = false;

    @Rule(
            categories = {"dc", RuleCategory.FEATURE},
            options = {"false", "bot_"},
            strict = false
    )
    public static String vaultBlacklistDisabledByNamePrefix = "false";

    @Rule(categories = {"dc", RuleCategory.FEATURE})
    public static boolean lightningNoFire = false;

    public static boolean bypassVaultBlacklist(Player player) {
        String prefix = vaultBlacklistDisabledByNamePrefix;
        return !prefix.isEmpty()
                && !"false".equals(prefix)
                && player.getScoreboardName().startsWith(prefix);
    }
}
