package org.faulty.wpreplace.models;

import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@Data
public class UnitDetails {
    private final int id;
    private final String name;
    private final String skill;
    private final String callSigns;
    private final String unit;
    private final float x;
    private final float y;
    private final int alt;
    private final float heading;
    private final LuaTable luaUnit;

    public static List<UnitDetails> fromLuaGroup(LuaTable luaGroup) {
        List<UnitDetails> unitDetails = new ArrayList<>();
        LuaTable units = luaGroup.get("units").checktable();
        for (LuaValue key : units.keys()) {
            LuaTable luaUnit = units.get(key).checktable();
            unitDetails.add(new UnitDetails(
                    luaUnit.get("unitId").toint(),
                    luaUnit.get("name").tojstring(),
                    luaUnit.get("skill").tojstring(),
                    getCallsigns(luaUnit),
                    luaUnit.get("type").tojstring(),
                    luaUnit.get("x").tofloat(),
                    luaUnit.get("y").tofloat(),
                    luaUnit.get("alt").toint(),
                    luaUnit.get("heading").tofloat(),
                    luaUnit)
            );
        }
        return unitDetails;
    }

    private static String getCallsigns(LuaTable luaGroup) {
        LuaTable callsign = luaGroup.get("callsign").checktable();
        List<String> callSigns = new ArrayList<>();
        for (LuaValue key : callsign.keys()) {
            if (callsign.get(key).isstring()) {
                callSigns.add(callsign.get(key).tojstring());
            }
        }
        return String.join(",", callSigns);
    }
}
