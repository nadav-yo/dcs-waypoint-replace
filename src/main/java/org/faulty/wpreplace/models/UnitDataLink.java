package org.faulty.wpreplace.models;

import jakarta.annotation.Nullable;
import lombok.Data;
import org.faulty.wpreplace.utils.LuaWriter;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

@Data
public class UnitDataLink {

    private final String type;
    private final String Link16;

    @Nullable
    public static UnitDataLink fromLuaGroup(LuaTable luaUnit) {
        LuaValue datalinks = luaUnit.get("datalinks");
        if (datalinks.isnil()) {
            return null;
        }
        LuaTable link16 = datalinks.checktable().get("Link16").checktable();
        return new UnitDataLink(
                luaUnit.get("type").tojstring(),
                LuaWriter.luaTableToString(link16)
        );
    }
}
