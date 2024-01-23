package org.faulty.wpreplace.models;

import jakarta.annotation.Nullable;
import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@Data
public class UnitDataLink {

    private final String type;
    private final List<Item> Link16Settings;
    private final List<Contrib> Link16TeamMembers;
    private final List<Contrib> Link16Donors;

    @Nullable
    public static UnitDataLink fromLuaGroup(LuaTable luaUnit) {
        LuaValue datalinks = luaUnit.get("datalinks");
        if (datalinks.isnil()) {
            return null;
        }
        LuaTable link16 = datalinks.checktable().get("Link16").checktable();
        return new UnitDataLink(
                luaUnit.get("type").tojstring(),
                getSettings(link16.get("settings").checktable()),
                getContrib(link16.get("network").checktable().get("teamMembers").checktable()),
                getContrib(link16.get("network").checktable().get("donors").checktable())

        );
    }

    private static List<Item> getSettings(LuaTable settings) {
        List<Item> pylonsPayload = new ArrayList<>();
        for (LuaValue key : settings.keys()) {
            if (key.isstring()) {
                LuaValue settingsEntry = settings.get(key);
                if (settingsEntry.isstring()) {
                    pylonsPayload.add(new Item(key.tojstring(), settingsEntry.tojstring()));
                } else if (settingsEntry.isboolean()) {
                    pylonsPayload.add(new Item(key.tojstring(), String.valueOf(settingsEntry.toboolean())));
                }
            }
        }
        return pylonsPayload;
    }

    private static List<Contrib> getContrib(LuaTable contribLua) {
        List<Contrib> pylonsPayload = new ArrayList<>();
        for (LuaValue key : contribLua.keys()) {
            if (key.isstring()) {
                pylonsPayload.add(
                        new Contrib(key.toint(),
                                contribLua.get(key).get("missionUnitId").toint(),
                                contribLua.get(key).get("TDOA").toboolean()
                        ));
            }
        }
        return pylonsPayload;
    }

    @Data
    public static final class Item {
        private final String key;
        private final String value;
    }

    @Data
    public static final class Contrib {
        private final int id;
        private final int missionUnitId;
        private final boolean TDOA;
    }
}
