package org.faulty.wpreplace.models;

import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final BigDecimal heading;
    private final float speed;
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
                    BigDecimal.valueOf(Math.toDegrees(luaUnit.get("heading").tofloat())).setScale(1, RoundingMode.HALF_UP),
                    luaUnit.get("speed").tofloat(),
                    luaUnit)
            );
        }
        return unitDetails;
    }

    private static String getCallsigns(LuaTable luaGroup) {
        LuaValue callsignVal = luaGroup.get("callsign");
        if (callsignVal.isint()) {
            return Integer.toString(callsignVal.toint());
        }
        LuaTable callsign = callsignVal.checktable();
        List<String> callSigns = new ArrayList<>();
        for (LuaValue key : callsign.keys()) {
            if (callsign.get(key).isstring()) {
                callSigns.add(callsign.get(key).tojstring());
            }
        }
        return String.join(",", callSigns);
    }
}
