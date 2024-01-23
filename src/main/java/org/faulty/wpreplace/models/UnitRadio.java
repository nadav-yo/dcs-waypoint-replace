package org.faulty.wpreplace.models;

import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@Data
public class UnitRadio {
    private final String type;
    private final List<Channel> channels1;
    private final List<Channel> channels2;

    public static UnitRadio fromLuaGroup(LuaTable luaUnit) {
        LuaValue radio = luaUnit.get("Radio");
        if (radio.isnil()) {
            return null;
        }
        LuaTable radios = radio.checktable();
        return new UnitRadio(
                luaUnit.get("type").tojstring(),
                getChannels(radios, 1),
                getChannels(radios, 2)
        );
    }

    private static List<Channel> getChannels(LuaTable unitRadios, int radio) {
        List<Channel> radioChannels = new ArrayList<>();
        LuaTable channels = unitRadios.get(radio).checktable().get("channels").checktable();
        for (LuaValue key : channels.keys()) {
            LuaValue channel = channels.get(key);
            if (channel.isint()) {
                radioChannels.add(new Channel(key.toint(), channel.toint()));
            }
        }
        return radioChannels;
    }

    @Data
    public static final class Channel {
        private final int index;
        private final int frequency;
    }
}
