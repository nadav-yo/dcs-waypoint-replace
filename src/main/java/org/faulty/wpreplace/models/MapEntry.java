package org.faulty.wpreplace.models;

import lombok.Getter;
import org.faulty.wpreplace.utils.MissionMap;

public class MapEntry {
    private final MissionMap missionMap;
    private final double width;
    private final double height;

    @Getter
    private int ratio;

    public MapEntry(String theatre) {
        missionMap = MissionMap.valueOf(theatre);
        width = missionMap.getMaxX() - missionMap.getMinX();
        height = missionMap.getMaxY() - missionMap.getMinY();
        ratio = missionMap.getRatio();
    }

    public double getMinX() {
        return missionMap.getMinX();
    }

    public double getMinY() {
        return missionMap.getMinY();
    }

    public double getAdjustedWidth() {
        return width / ratio;
    }

    public double getAdjustedHeight() {
        return height / ratio;
    }

    public void setRatio(int newRatio) {
        ratio = Math.min(missionMap.getRatio() * 2, (Math.max(missionMap.getRatio() / 3, newRatio)));
    }

    public String getFilePath() {
        return "\\Mods\\terrains\\" + missionMap.getName() + "\\MissionGenerator\\nodesMap.png";
    }
}
