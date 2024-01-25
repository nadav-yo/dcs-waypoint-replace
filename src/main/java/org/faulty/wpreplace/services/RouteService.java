package org.faulty.wpreplace.services;

import lombok.Getter;
import org.faulty.wpreplace.models.RouteDetails;
import org.faulty.wpreplace.utils.RouteUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RouteService {

    @Getter
    LuaValue route;
    private String coalition;
    private int countryId;
    private String unitType;
    @Getter
    private int groupId;

    public void setRoute(LuaValue route, String coalition, int countryId, String unitType, int groupId) {
        this.route = route;
        this.coalition = coalition;
        this.countryId = countryId;
        this.unitType = unitType;
        this.groupId = groupId;
    }

    public String printDetails() {
        return String.format("coalition %s, country %d, unitType %s group %d ", coalition, countryId, unitType, groupId);
    }

    public Map<Integer, LuaValue> getFriendlyGroupRoutes(LuaValue mission) {
        Map<Integer, LuaValue> routes = new HashMap<>();
        LuaTable groups = RouteUtils.getGroups(mission, coalition, countryId, unitType);
        if (groups == null) {
            return Map.of();
        }
        for (LuaValue key : groups.keys()) {
            routes.put(key.toint(), groups.get(key).get("route").get("points"));
        }
        return routes;
    }

    public void updateRoute(List<RouteDetails> items) {
        RouteDetails last = null;
        LuaTable points = route.get("points").checktable();
        items.forEach(item -> {
            LuaValue luaValue = points.get(item.getId());
            if (!luaValue.isnil()) {

            }
        });
    }
}
