package org.faulty.wpreplace.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.faulty.wpreplace.models.Error;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

@Log4j2
@Component
public class ConfService {

    private final Properties conf = new Properties();
    private final File confFile;

    public ConfService() {
        confFile = new File("wp.conf");
        createConfFileIfNotExist();
        loadConf();
    }

    public String getDcsDirectory() {
        return conf.getProperty("dcs.folder");
    }

    public Error setDcsDirectory(String path) {
        conf.setProperty("dcs.folder", path);
        return saveConf();
    }

    private void loadConf() {
        try (FileInputStream fis = new FileInputStream(confFile)) {
            conf.load(fis);
        } catch (Exception ex) {
            log.error("Error loading conf file", ex);
        }
    }

    private Error saveConf() {
        try (Writer inputStream = new FileWriter(confFile)) {
            conf.store(inputStream, "INFORMATION!!!");
            return null;
        } catch (IOException ex) {
            log.error("Error saving conf file", ex);
            return new Error(ex.getMessage());
        }
    }

    private void createConfFileIfNotExist() {
        if (!confFile.exists()) {
            try {
                Files.createFile(confFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
