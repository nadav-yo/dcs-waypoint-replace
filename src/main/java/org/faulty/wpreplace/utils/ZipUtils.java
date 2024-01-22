package org.faulty.wpreplace.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.luaj.vm2.LuaValue;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Log4j2
@NoArgsConstructor(access = AccessLevel.NONE)
public class ZipUtils {

    public static void copyEntry(ZipEntry entry, ZipOutputStream zipOutputStream, ZipInputStream zipInputStream) throws IOException {
        log.info("Copying file {}", entry.getName());
        zipOutputStream.putNextEntry(entry);
        IOUtils.copy(zipInputStream, zipOutputStream);
        zipOutputStream.closeEntry();
    }

    public static void writeModifiedMissionEntry(LuaValue missionObject, ZipOutputStream zipOutputStream) throws IOException {
        String missionStr = "mission = \n" + LuaWriter.luaTableToString(missionObject.checktable());
        log.info("|--- Writing modified mission file");
        ZipEntry entry = new ZipEntry("mission");
        zipOutputStream.putNextEntry(entry);
        zipOutputStream.write(missionStr.getBytes());
        zipOutputStream.closeEntry();
    }

}
