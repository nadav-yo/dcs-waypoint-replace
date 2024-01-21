package org.faulty.wpreplace.utils;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class RouteUtils {
    public static LuaTable getGroups(LuaValue missionObject, String coalition, int countryId, String unitType) {
        try {
            return missionObject
                    .get("coalition").checktable()
                    .get(coalition).checktable()
                    .get("country").checktable()
                    .get(countryId).checktable()
                    .get(unitType).checktable()
                    .get("group").checktable();
        } catch (Exception e) {
            return null;
        }
    }

    public static void setRoute(LuaValue mission, LuaValue route, String coalition, int countryId, String unitType) {
        LuaTable groups = getGroups(mission, coalition, countryId, unitType);
        if (groups != null) {
            for (int i = 1; i <= groups.len().toint(); i++) {
                groups.get(i).set("route", route);
            }
        }
    }

    /**
     * Compact a route points to x,y,alt only
     */
    public static LuaTable compactPoints(LuaTable points) {
        List<LuaValue> newPoints = new ArrayList<>();
        for (LuaValue key : points.keys()) {
            LuaString point = LuaString.valueOf(String.format("x=%s, y=%s, alt=%s",
                    points.get(key).get("x"),
                    points.get(key).get("y"),
                    points.get(key).get("alt")));
            newPoints.add(point);
        }
        return LuaValue.listOf(newPoints.toArray(new LuaValue[] {}));
    }

}
