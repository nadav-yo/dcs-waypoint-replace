package org.faulty.wpreplace.commands;

import java.util.List;

import org.faulty.wpreplace.context.MissionMizService;
import org.faulty.wpreplace.context.RouteContext;
import org.faulty.wpreplace.utils.LuaWriter;
import org.faulty.wpreplace.utils.RouteUtils;
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
    @ShellMethod(key = "route get", value = "Select a source route", group = "Route")
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
    @ShellMethod(key = "route show", value = "Show source route coordinates", group = "Route")
    public String showRoute() {
        LuaTable route = routeContext.getRoute().get("points").checktable();
        return String.format("Showing %d points for %s\n%s", route.length(), routeContext.printDetails(),
                LuaWriter.luaTableToString(RouteUtils.compactPoints(route)));
    }

    @ShellMethodAvailability("isMissionAndRouteLoaded")
    @ShellMethod(key = "route copy", value = "Copy source route to choosen destinations", group = "Route")
    public String routeCopy() {
        String coalition = getCoalition();
        int countryId = Integer.parseInt(getStringInput("country id:", "1"));
        List<String> unitTypes = selectMultipleUnitTypes();
        if (unitTypes.isEmpty()) {
            return "No units selected";
        }
        unitTypes.forEach(unitType -> RouteUtils.setRoute(missionContext.getMission(), routeContext.getRoute(),
                coalition, countryId, unitType));
        return String.format("Route set for coalition %s, country %d, units '%s'", coalition, countryId,
                String.join(",", unitTypes));
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
