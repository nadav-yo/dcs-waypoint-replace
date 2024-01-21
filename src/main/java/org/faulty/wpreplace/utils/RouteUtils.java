package org.faulty.wpreplace.utils;

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
}
