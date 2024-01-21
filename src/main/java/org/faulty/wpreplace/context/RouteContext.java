package org.faulty.wpreplace.context;

import lombok.Getter;
import org.luaj.vm2.LuaValue;
import org.springframework.stereotype.Component;

@Component
public class RouteContext {

    @Getter LuaValue route;
    @Getter private String coalition;
    @Getter private int countryId;
    private String unitType;
    @Getter private int groupId;

    public void setRoute(LuaValue route, String coalition, int countryId, String unitType, int groupId){
        this.route = route;
        this.coalition = coalition;
        this.countryId = countryId;
        this.unitType = unitType;
        this.groupId = groupId;
    }

    public String printDetails() {
        return String.format("Loaded coalition %s, country %d, group %d, for unit %s ", coalition, countryId, groupId, unitType);
    }

    public boolean isRouteLoaded() {
        return route != null;
    }
}
