package org.faulty.wpreplace.services;

import lombok.Getter;
import org.faulty.wpreplace.models.SlimRoute;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteContext {

    @Getter
    LuaValue route;
    private String coalition;
    private int countryId;
    private String unitType;
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

    public void updateRoute(List<SlimRoute> items) {
        SlimRoute last = null;
        LuaTable points = route.get("points").checktable();
        items.forEach(item -> {
            LuaValue luaValue = points.get(item.getId());
            if (!luaValue.isnil()) {

            }
        });
    }
}
