package org.faulty.wpreplace.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

// based on map MissionGenerator/nodesMap.lua
@Getter
@AllArgsConstructor
public enum MissionMap {
    Caucasus("Caucasus", -418619.187500, 113728.156250, 26382.500000, 943187.062500, 300),
    MarianaIslands("MarianaIslands", -296316.656250, -1072729.625000, 1096283.375000, 1252670.375000, 1500),
    Nevada("Nevada", -497851.625000, -328660.906250, -167608.921875, 210510.859375, 300),
    Normandy("Normandy", -144731.375000, -294537.312500, 260772.625000, 355702.687500, 300),
    SinaiMap("Sinai",  -326784.000000, -458752.000000, 393216.000000, 787248.000000, 600),
    Syria("Syria",  -257003.953125, -419498.875000, 248852.046875, 368981.125000, 750),
    ;

    private final String name;
    private final double minY;
    private final double minX;
    private final double maxY;
    private final double maxX;
    private final int ratio;

}
