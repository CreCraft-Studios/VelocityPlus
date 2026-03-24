package com.crecraftstudios.velocityplus.json;

import com.crecraftstudios.velocityplus.VelocityPlus;
import com.crecraftstudios.velocityplus.utils.ExceptionUtils;
import com.crecraftstudios.velocityplus.utils.IOUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Messages extends Json {

    public Messages() {
        super("messages");
    }

    public Component getMessage(String key, String... format) {
        String msg="<red>[ERROR]</red>";
        if (!this.get().has(key))
            VelocityPlus.get().logger.error("Message key %s not found".formatted(key));

        msg = this.get().get(key).getAsString();
        if (format !=null && format.length>0)
            msg = msg.formatted((Object[]) format);

        return MiniMessage.miniMessage().deserialize(msg);
    }

    @Override
    public void load() {
        try {
            Path target = VelocityPlus.get().directory.toAbsolutePath().resolve(this.fileName);
            File file = new File(VelocityPlus.get().directory+"/"+this.fileName);

            if (Files.notExists(file.toPath())) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(this.fileName)) {
                    if (in==null) {
                        VelocityPlus.get().logger.error("messages.json not found in jar file");
                        return;
                    }

                    Files.createDirectories(target.getParent());
                    Files.copy(in, target);
                }
            } else target = new File(VelocityPlus.get().directory+"/"+this.fileName).toPath();

            this.json=IOUtils.loadJsonObject(target.toString());
        } catch(IOException err) {
            ExceptionUtils.printException(err);
        }
    }

    public static class Keys {
        public static class Commands {
            public static final String FIND_ONLINE="command.find.online";
            public static final String FIND_ONLINE_BUT_NOT_IN_SERVER="command.find.online-but-not-in-server";
            public static final String ALERT="command.alert";
            public static final String HUB_FAILED="command.hub.failed";
            public static final String HUB_CONSOLE="command.hub.console";
            public static final String MAINTENANCE_UNKNOWN="command.maintenance.unknown";
            public static final String MESSAGE_PLAYER="command.message.player";
            public static final String MESSAGE_CONSOLE="command.message.console";
            public static final String LOBBY_SET="command.lobby.set";
            public static final String LOBBY_CONSOLE_EXECUTED="command.lobby.console-executed";
            public static final String LOBBY_NOT_FOUND="command.lobby.not-found";
            public static final String WHITELIST_ENABLED="command.whitelist.enabled";
            public static final String WHITELIST_DISABLED="command.whitelist.disabled";
            public static final String WHITELIST_ERROR="command.whitelist.error";
            public static final String WHITELIST_ADD_PLAYER="command.whitelist.add-player";
            public static final String WHITELIST_REMOVE_PLAYER="command.whitelist.remove-player";
            public static final String WHITELIST_NOT_FOUND="command.whitelist.not-found";
            public static final String PLAYER_NOW_BANNED="command.ban.player-banned";
            public static final String PLAYER_NOW_UNBANNED="command.unban.player";
        }

        public static class Message {
            public static final String NOT_WHITELISTED = "messages.not-whitelisted";
            public static final String MAINTENANCE_CURRENT = "messages.maintenance.current";
            public static final String MAINTENANCE_CURRENT_PING = "messages.maintenance.current.ping";
            public static final String MAINTENANCE_ENTERING = "messages.maintenance.entering";
            public static final String MAINTENANCE_ENTERING_WILL_BE_KICKED = "messages.maintenance.entering.will-be-kicked";
            public static final String MAINTENANCE_ENTERING_WONT_BE_KICKED = "messages.maintenance.entering.wont-be-kicked";
            public static final String REMOVED_FROM_WHITELIST = "messages.whitelist.kicked";
            public static final String PERM_BANNED = "messages.ban.perm";
            public static final String TEMP_BANNED = "messages.ban.temp";
            public static final String MOJANG_PLAYER_NOT_FOUND = "messages.mojang.player-not-found";
        }
    }
}