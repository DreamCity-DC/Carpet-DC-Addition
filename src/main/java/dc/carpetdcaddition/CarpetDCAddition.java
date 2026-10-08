package dc.carpetdcaddition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;


public final class CarpetDCAddition implements ModInitializer, CarpetExtension {
    public static final String MOD_ID = "carpet_dc_addition";

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(DCSettings.class);
    }

    @Override
    public String version() {
        return FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow()
                .getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public Map<String, String> canHasTranslations(String language) {
        String path = "assets/" + MOD_ID + "/lang/";
        Map<String, String> translations = new HashMap<>(
                Translations.getTranslationFromResourcePath(path + "en_us.json"));

        translations.putAll(Translations.getTranslationFromResourcePath(path + language + ".json"));
        return translations;
    }
}
