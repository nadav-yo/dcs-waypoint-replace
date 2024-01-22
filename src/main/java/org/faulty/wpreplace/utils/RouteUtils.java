package org.faulty.wpreplace.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.faulty.wpreplace.models.Entry;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public static LuaTable getGroup(LuaValue missionObject, String coalition, int countryId, String unitType, int groupId) {
        try {
            return Optional.ofNullable(getGroups(missionObject, coalition, countryId, unitType))
                    .map(r -> r.get(groupId).checktable())
                    .orElse(null);
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

    public static List<Entry> getAllCoalitionCountries(LuaValue missionObject, String coalition) {
        LuaTable coalitionCountries = missionObject.get("coalition").checktable()
                .get(coalition).checktable()
                .get("country").checktable();
        List<Entry> countries = new ArrayList<>();
        for (LuaValue key : coalitionCountries.keys()) {
            countries.add(new Entry(key.toint(), coalitionCountries.get(key).get("name").tojstring()));
        }
        return countries;
    }

    public static List<Integer> getSelectedGroupIds(LuaValue missionObject, String coalition, int countryId, String unitType) {
        LuaTable groups = getGroups(missionObject, coalition, countryId, unitType);
        if (groups == null) {
            return List.of();
        }
        List<Integer> groupIds = new ArrayList<>();
        for (LuaValue key : groups.keys()) {
            groupIds.add(key.toint());
        }
        return groupIds;
    }

}
