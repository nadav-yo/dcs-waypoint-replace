package org.faulty.wpreplace.services;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.faulty.wpreplace.models.Error;
import org.faulty.wpreplace.utils.LuaReader;
import org.faulty.wpreplace.utils.ZipUtils;
import org.luaj.vm2.LuaValue;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Log4j2
@Getter
@Component
public class MissionService {

    LuaValue mission;
    String mizFilePath;

    public Error loadMission(String mizFilePath) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(mizFilePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals("mission")) {
                    this.mission = LuaReader.loadMissionFromEntry(zipInputStream);
                    break;
                }
            }
            this.mizFilePath = mizFilePath;
            return null;
        } catch (FileNotFoundException e) {
            log.debug("File not found {}", mizFilePath, e);
            return new Error("File not found");
        } catch (IOException e) {
            log.error("Error encountered while loading mission {}", mizFilePath, e);
            return new Error("Error encountered while loading mission: " + e.getMessage());
        }
    }

    public Error saveMission(String destFile) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(mizFilePath))) {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destFile))) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().equals("mission")) {
                        IOUtils.consume(zipInputStream);
                        ZipUtils.writeModifiedMissionEntry(mission, zipOutputStream);
                    } else {
                        ZipUtils.copyEntry(entry, zipOutputStream, zipInputStream);
                    }
                }
            }
            return null;
        } catch (IOException e) {
            log.error("Conversion failed due to:", e);
            return new Error("Failed to save file due to " + e.getMessage());
        }
    }
}
