package org.faulty.wpreplace.commands;

import java.util.ArrayList;
import java.util.List;

import org.faulty.wpreplace.context.MissionMizService;
import org.faulty.wpreplace.context.RouteContext;
import org.faulty.wpreplace.utils.LuaWriter;
import org.faulty.wpreplace.utils.RouteUtils;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class RouteCommand extends BaseCommand {

    @Autowired
    private MissionMizService missionContext;
    @Autowired
    private RouteContext routeContext;

    @ShellMethodAvailability("isMissionLoaded")
    @ShellMethod(key = "route get", value = "Single selector", group = "Route")
    public String routeGet() {
        String coalition = getCoalition();
        int countryId = getIntInput("country id:", "1");
        int groupId = getIntInput("group id:", "1");
        String unitType = getSingleUnitType();
        LuaTable groups = RouteUtils.getGroups(missionContext.getMission(), coalition, countryId, unitType);
        if (groups == null) {
            return "Group not found!";
        }
        LuaValue route = groups.get(groupId).get("route");
        routeContext.setRoute(route, coalition, countryId, unitType, groupId);
        return routeContext.printDetails();
    }

    @ShellMethodAvailability("isMissionAndRouteLoaded")
    @ShellMethod(key = "route show", value = "Multi selector", group = "Route")
    public String showRoute() {
        LuaTable route = routeContext.getRoute().get("points").checktable();
        return "Showing results for " + routeContext.printDetails() + "\n"
                + LuaWriter.luaTableToString(compactPoints(route));
    }

    public LuaTable compactPoints(LuaTable points) {
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

    @ShellMethodAvailability("isMissionAndRouteLoaded")
    @ShellMethod(key = "route copy", value = "Multi selector", group = "Route")
    public String routeCopy() {
        String coalition = getCoalition();
        int countryId = Integer.parseInt(getStringInput("country id:", "1"));
        List<String> unitTypes = selectMultipleUnitTypes();
        if (unitTypes.isEmpty()) {
            return "No units selected";
        }
        unitTypes.forEach(unitType -> RouteUtils.setRoute(missionContext.getMission(), routeContext.getRoute(), coalition, countryId, unitType));
        return String.format("Route set for coalition %s, country %d, units '%s'", coalition, countryId, String.join(",", unitTypes));
    }

    private Availability isMissionLoaded() {
        return missionContext.isMissionLoaded() ? Availability.available()
                : Availability.unavailable("No mission loaded");
    }

    private Availability isMissionAndRouteLoaded() {
        if (!missionContext.isMissionLoaded()) {
            return Availability.unavailable("No mission loaded");
        } else if (routeContext.getRoute() == null) {
            return Availability.unavailable("No route loaded");
        }
        return Availability.available();
    }

}
