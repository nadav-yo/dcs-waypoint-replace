package org.faulty.wpreplace.models;

import javafx.geometry.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public final class RouteDetails {
    private int id;
    private double x;
    private double y;
    private int alt;
    private double speed;
    private double eta;
    private String type;
    private String task;


    public static List<RouteDetails> fromLuaRoute(LuaTable points) {
        List<RouteDetails> slimRoutes = new ArrayList<>();
        for (LuaValue key : points.keys()) {
            LuaValue luaValue = points.get(key);
            RouteDetails slimRoute = new RouteDetails(
                    key.toint(),
                    luaValue.get("x").todouble(),
                    luaValue.get("y").todouble(),
                    luaValue.get("alt").toint(),
                    luaValue.get("speed").todouble(),
                    luaValue.get("ETA").todouble(),
                    luaValue.get("type").tojstring(),
                    getTask(luaValue.get("task"))
            );
            slimRoutes.add(slimRoute);
        }
        return slimRoutes;
    }

    private static String getTask(LuaValue luaValue) {
        if (luaValue.isstring()) {
            return luaValue.tojstring();
        }
        return luaValue.checktable().get("id").tojstring();
    }

    public void updateEta(RouteDetails source) {
        Point3D point1 = new Point3D(source.x, source.y, source.alt);
        Point3D point2 = new Point3D(this.x, this.y, this.alt);
        double distance = point1.distance(point2);
        eta = distance / this.speed;
    }
}
