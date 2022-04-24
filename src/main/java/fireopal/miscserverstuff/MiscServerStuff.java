package fireopal.miscserverstuff;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscServerStuff implements ModInitializer {
	private static final String MODID = "miscserverstuff";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final int DISTANCE_FROM_SPAWN_FOR_PHANTOMS = 150;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello from mod `miscserverstuff`!");
	}
}
