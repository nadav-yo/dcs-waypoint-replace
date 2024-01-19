package org.faulty.wpreplace;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public final class LuaWriter {
    private LuaWriter() {
        // this is a helper class
    }

    static String luaTableToString(LuaTable table, int depth) {
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("\t".repeat(Math.max(0, depth - 1)));
        scriptBuilder.append("{\n");
        for (LuaValue key : table.keys()) {
            LuaValue value = table.get(key);
            scriptBuilder.append("\t".repeat(Math.max(0, depth)));
            if (key.isnumber() || key.isboolean()) {
                scriptBuilder.append("[").append(key.tojstring()).append("] = ");
            } else {
                scriptBuilder.append("[\"").append(key.tojstring()).append("\"] = ");
            }

            if (value.istable()) {
                scriptBuilder.append("\n");
                scriptBuilder.append(luaTableToString(value.checktable(), depth + 1));
            } else if (value.isboolean() || value.isnumber()) {
                scriptBuilder.append(value.tojstring()).append(",");
            }else {
                String val = value.tojstring().replaceAll("(?<!\\\\)\\n(?!\\\\)", "\\\\\n")
                        .replaceAll("\"", "\\\\\"");
                scriptBuilder.append("\"").append(val).append("\",");
            }

            scriptBuilder.append("\n");
        }
        scriptBuilder.append("\t".repeat(Math.max(0, depth - 1)));
        scriptBuilder.append("}");
        if (depth != 1) {
            scriptBuilder.append(",");
        }
        return scriptBuilder.toString();
    }
}
