package org.faulty.wpreplace.models;

import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@Data
public class UnitPayload {
    private final String type;
    private final List<String> pylons;
    private final int fuel;
    private final int flare;
    private final int chaff;
    private final int gun;
    private final int ammoType;

    public static UnitPayload fromLuaGroup(LuaTable luaUnit) {
        LuaTable payload = luaUnit.get("payload").checktable();
        return new UnitPayload(
                luaUnit.get("type").tojstring(),
                getPylons(payload),
                payload.get("fuel").toint(),
                payload.get("flare").toint(),
                payload.get("chaff").toint(),
                payload.get("gun").toint(),
                payload.get("ammo_type").toint()
        );
    }

    private static List<String> getPylons(LuaTable luaUnit) {
        List<String> pylonsPayload = new ArrayList<>();
        LuaTable pylons = luaUnit.get("pylons").checktable();
        for (LuaValue key : pylons.keys()) {
            LuaValue clsid = pylons.get(key).checktable().get("CLSID");
            if (clsid.isstring()) {
                pylonsPayload.add(clsid.tojstring());
            }
        }
        return pylonsPayload;
    }
}
