package org.faulty.wpreplace;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.NONE)
public class EnvUtils {

    static String getEnvOrDefault(String variableName, String defaultValue) {
        String value = System.getProperty(variableName);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    static int getEnvIntOrDefault(String variableName, int defaultValue) {
        String unitCountString = System.getProperty(variableName);
        try {
            return (unitCountString != null && !unitCountString.isEmpty()) ? Integer.parseInt(unitCountString) : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Error parsing {}. Using default value {}.", variableName, defaultValue);
            return defaultValue;
        }
    }
}
