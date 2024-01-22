package org.faulty.wpreplace.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Entry {
    private int location;
    private String displayText;

    public String toString() {
        return displayText;
    }
}
