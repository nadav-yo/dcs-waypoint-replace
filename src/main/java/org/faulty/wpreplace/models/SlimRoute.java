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
public final class SlimRoute {
    private int id;
    private double x;
    private double y;
    private int alt;
    private double speed;
    private double eta;

    public static List<SlimRoute> fromLuaRoute(LuaTable points) {
        List<SlimRoute> slimRoutes = new ArrayList<>();
        for (LuaValue key : points.keys()) {
            LuaValue luaValue = points.get(key);
            SlimRoute slimRoute = new SlimRoute(
                    key.toint(),
                    luaValue.get("x").todouble(),
                    luaValue.get("y").todouble(),
                    luaValue.get("alt").toint(),
                    luaValue.get("speed").todouble(),
                    luaValue.get("ETA").todouble()
            );
            slimRoutes.add(slimRoute);
        }
        return slimRoutes;
    }

    public void updateEta(SlimRoute source) {
        Point3D point1 = new Point3D(source.x, source.y, source.alt);
        Point3D point2 = new Point3D(this.x, this.y, this.alt);
        double distance = point1.distance(point2);
        eta = distance / this.speed;
    }
}
