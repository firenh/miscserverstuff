package fireopal.miscserverstuff.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class MyCommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            BroadcastCommand.register(dispatcher);
        });
    }
}
