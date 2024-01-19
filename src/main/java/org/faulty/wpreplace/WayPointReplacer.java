package org.faulty.wpreplace;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Log4j2
public class WayPointReplacer {
    public static void main(String[] args) {
        if (args.length < 1) {
            log.error("Please provide the filename");
            System.exit(1);
        }
        String mizFilePath = args[0];
        File destFile = getDestFile(mizFilePath);
        log.info("Converting {} to {}:", mizFilePath, destFile);

        try {
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(mizFilePath))) {
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destFile))) {
                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        if (entry.getName().equals("mission")) {
                            replaceMissionFile(zipInputStream, zipOutputStream);
                        } else {
                            copyEntry(entry, zipOutputStream, zipInputStream);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Conversion failed due to:", e);
            System.exit(1);
        }
    }

    private static File getDestFile(String mizFilePath) {
        File file = new File(mizFilePath);
        String outputFile = EnvUtils.getEnvOrDefault("outputFile", file.getParent() + File.separator + "new_" + file.getName());
        return new File(outputFile);
    }

    private static void replaceMissionFile(ZipInputStream zipInputStream, ZipOutputStream zipOutputStream) throws IOException {
        log.info("Converting mission file:");
        log.info("|--- Loading mission file");
        LuaValue missionObject = LuaReader.loadMissionFromEntry(zipInputStream);
        LuaTable groups = getGroups(missionObject);
        LuaValue route = groups.get(EnvUtils.getEnvIntOrDefault("sourceGroup", 1)).get("route");
        log.info("|--- Rewriting all groups");
        for (int i = 1; i <= groups.len().toint(); i++) {
            groups.get(i).set("route", route);
        }
        writeModifiedMissionEntry(missionObject, zipOutputStream);
        log.info("|--- Done!");
    }

    private static void copyEntry(ZipEntry entry, ZipOutputStream zipOutputStream, ZipInputStream zipInputStream) throws IOException {
        log.info("Copying file {}", entry.getName());
        zipOutputStream.putNextEntry(entry);
        IOUtils.copy(zipInputStream, zipOutputStream);
        zipOutputStream.closeEntry();
    }

    private static void writeModifiedMissionEntry(LuaValue missionObject, ZipOutputStream zipOutputStream) throws IOException {
        String missionStr = "mission = \n" + LuaWriter.luaTableToString(missionObject.checktable(), 1);
        log.info("|--- Writing modified mission file");
        ZipEntry entry = new ZipEntry("mission");
        zipOutputStream.putNextEntry(entry);
        zipOutputStream.write(missionStr.getBytes());
        zipOutputStream.closeEntry();
    }

    private static LuaTable getGroups(LuaValue missionObject) {
        return missionObject
                .get("coalition").checktable()
                .get("blue").checktable()
                .get("country").checktable()
                .get(EnvUtils.getEnvIntOrDefault("countryId", 1)).checktable()
                .get(EnvUtils.getEnvOrDefault("unitType", "plane")).checktable()
                .get("group").checktable();
    }
}
