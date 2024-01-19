package org.faulty.wpreplace;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.*;
import java.nio.charset.Charset;
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
        File file = new File(mizFilePath);
        String destFilePath = getEnvOrDefault("outputFile", file.getParent() + File.separator + "new_" + file.getName());
        log.info("Converting {} to {}:", mizFilePath, destFilePath);

        File destFile = new File(destFilePath);
        try {
            try (FileInputStream fileInputStream = new FileInputStream(mizFilePath);
                 ZipInputStream zipInputStream = new ZipInputStream(fileInputStream)) {
                try (FileOutputStream tempFileOutputStream = new FileOutputStream(destFile);
                     ZipOutputStream zipOutputStream = new ZipOutputStream(tempFileOutputStream)) {
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

    private static void replaceMissionFile(ZipInputStream zipInputStream, ZipOutputStream zipOutputStream) throws IOException {
        log.info("Converting mission file:");
        LuaValue missionObject = loadMissionFromEntry(zipInputStream);
        LuaTable groups = getGroups(missionObject);
        LuaValue route = groups.get(1).get("route");
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
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = zipInputStream.read(buffer)) != -1) {
            zipOutputStream.write(buffer, 0, bytesRead);
        }
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

    private static LuaValue loadMissionFromEntry(ZipInputStream zipInputStream) throws IOException {
        log.info("|--- Loading mission file");
        String luaScript = IOUtils.toString(zipInputStream, Charset.defaultCharset());
        luaScript += "\nreturn mission";
        return loadMission(luaScript);
    }

    private static LuaTable getGroups(LuaValue missionObject) {
        return missionObject
                .get("coalition").checktable()
                .get("blue").checktable()
                .get("country").checktable()
                .get(getEnvIntOrDefault("countryId", 1)).checktable()
                .get(getEnvOrDefault("unitType", "plane")).checktable()
                .get("group").checktable();
    }

    private static String getEnvOrDefault(String variableName, String defaultValue) {
        String value = System.getProperty(variableName);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private static int getEnvIntOrDefault(String variableName, int defaultValue) {
        String unitCountString = System.getProperty(variableName);
        try {
            return (unitCountString != null && !unitCountString.isEmpty()) ? Integer.parseInt(unitCountString) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing unitcount. Using default value.");
            return defaultValue;
        }
    }

    private static LuaValue loadMission(String luaScript) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load(new ByteArrayInputStream(luaScript.getBytes()), "mission", "bt", globals);
        return chunk.checkfunction().call();
    }
}
