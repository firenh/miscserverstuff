package fireopal.miscserverstuff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class MiscServerStuff implements ModInitializer {
	private static final String MODID = "miscserverstuff";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final int DISTANCE_FROM_SPAWN_FOR_PHANTOMS = 150;
	public static final Identifier SMALL_FIREBALL_LOOT_TABLE_ID = id("entities/small_fireball");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello from mod `miscserverstuff`!");
	}

	public static Identifier id(String id) {
		return new Identifier(MODID, id);
	}
}
