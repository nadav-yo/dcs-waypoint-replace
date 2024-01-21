package org.faulty.wpreplace.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipInputStream;

@NoArgsConstructor(access = AccessLevel.NONE)
public class LuaReader {

    public static LuaValue loadMissionFromEntry(ZipInputStream zipInputStream) throws IOException {
        String luaScript = IOUtils.toString(zipInputStream, Charset.defaultCharset());
        luaScript += "\nreturn mission";
        return loadMission(luaScript);
    }

    private static LuaValue loadMission(String luaScript) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load(new ByteArrayInputStream(luaScript.getBytes()), "mission", "bt", globals);
        return chunk.checkfunction().call();
    }
}
