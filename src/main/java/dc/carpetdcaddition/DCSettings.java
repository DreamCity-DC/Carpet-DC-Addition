package dc.carpetdcaddition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;

public final class DCSettings {
    private DCSettings() {
    }

    @Rule(categories = {"dc", RuleCategory.OPTIMIZATION, RuleCategory.BUGFIX})
    public static boolean optimizeMapPixelsUpdate = false;
}
