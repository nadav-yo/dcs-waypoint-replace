package org.faulty.wpreplace.models;

import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@Data
public class MissionWeather {
    private final int atmosphereType;
    private final List<Wind> winds;
    private final boolean enableFog;
    private final int fogThickness;
    private final int fogVisibility;
    private final int visibilityDistance;
    private final int seasonTemperature;
    private final String name;
    private final List<Item> clouds;

    public static MissionWeather fromLua(LuaTable weatherLua) {
        return new MissionWeather(
                weatherLua.get("atmosphere_type").toint(),
                getWinds(weatherLua.get("wind").checktable()),
                weatherLua.get("enable_fog").toboolean(),
                weatherLua.get("fog").get("thickness").toint(),
                weatherLua.get("fog").get("visibility").toint(),
                weatherLua.get("visibility").get("distance").toint(),
                weatherLua.get("season").get("temperature").toint(),
                weatherLua.get("name").tojstring(),
                getClouds(weatherLua.get("clouds").checktable())
        );
    }

    private static List<Item> getClouds(LuaTable cloudsLua) {
        List<Item> pylonsPayload = new ArrayList<>();
        for (LuaValue key : cloudsLua.keys()) {
            pylonsPayload.add(new Item(key.tojstring(), cloudsLua.get(key).tojstring()));
        }
        return pylonsPayload;
    }

    private static List<Wind> getWinds(LuaTable windLua) {
        List<Wind> winds = new ArrayList<>();
        for (LuaValue key : windLua.keys()) {
            winds.add(new Wind(
                    key.tojstring(),
                    windLua.get(key).get("speed").toint(),
                    windLua.get(key).get("dir").toint()
            ));
        }
        return winds;
    }

    @Data
    public static class Wind {
        private final String location;
        private final int speed;
        private final int dir;
    }

    @Data
    public static final class Item {
        private final String key;
        private final String value;

        public Item(String key, Object value) {
            this.key = key;
            this.value = String.valueOf(value);
        }
    }
}
