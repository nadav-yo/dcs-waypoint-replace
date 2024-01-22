package org.faulty.wpreplace.models;

import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@Data
public final class SlimRoute {
    private final int id;
    private final float x;
    private final float y;
    private final int alt;

    public static List<SlimRoute> fromLuaRuote(LuaTable points) {
        List<SlimRoute> slimRoutes = new ArrayList<>();
        for (LuaValue key : points.keys()) {
            SlimRoute slimRoute = new SlimRoute(
                    key.toint(),
                    points.get(key).get("x").tofloat(),
                    points.get(key).get("x").tofloat(),
                    points.get(key).get("alt").toint()
            );
            slimRoutes.add(slimRoute);
        }
        return slimRoutes;
    }

}
