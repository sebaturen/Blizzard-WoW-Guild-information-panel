package com.blizzardPanel.viewController;

import com.blizzardPanel.gameObject.guilds.Guild;

public class GuildController {

    public static Guild guild = new Guild.Builder(61031120).build();

    public static Guild getInstance() {
        return guild;
    }
}
