package org.faulty.wpreplace.models;

import lombok.Data;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

@Data
public class MissionDetails {
    private final Date date;
    private final String theatre;
    private final MissionWeather weather;

    public static MissionDetails fromLuaMission(LuaValue missionLua) {
        return new MissionDetails(getDate(missionLua.get("date").checktable()),
                missionLua.get("theatre").tojstring(),
                MissionWeather.fromLua(missionLua.get("weather").checktable()));
    }

    private static Date getDate(LuaTable luaDate) {
        return new Date(
                luaDate.get("Year").toint(),
                luaDate.get("Month").toint(),
                luaDate.get("Day").toint()
        );
    }

    @Data
    public static class Date {
        private final int year;
        private final int month;
        private final int day;

        @Override
        public String toString() {
            return day + "/" + month + "/" + year;
        }
    }
}
