# Waypoint Replace Script for DCS Missions

## Overview

This script is designed to modify Digital Combat Simulator (DCS) missions by copying all waypoints of blue coalition country 1 plane groups to be identical to group 1. The script takes a file path to the original mission as input, opens, reads the mission file, performs modifications, and repacks the mission.

## Prerequisites

Before running the script, ensure that you have the following:

- **Java Runtime Environment (JRE):** The script is written in Java, and you need a JRE installed on your system.

## Running the Script

To run the script, use the following command:

```bash
java "C:\Users\me\Saved Games\DCS.openbeta\Missions\temp1.miz" waypoint-replace-$version.jar
```

## Options
* outputFile (default: null): Filename to save the modified mission file (output.miz).
* unitType (default: plane): Unit type to replace waypoints (e.g., helicopter). 
* countryId (default: 1): Country ID to replace waypoints.
if no output.file provided, mission will be saved as new_$mission.miz

## Example
```bash
java -DunitType=helicopter -DcountryId=4 -DoutputFile=modified.miz -jar waypoint-replace-$version.jar "C:\Users\me\Saved Games\DCS.openbeta\Missions\temp1.miz"
```
This example replaces waypoints for helicopters in country ID 4 and saves the modified mission as modified.miz.

## Custom LuaWriter
The script utilizes a custom and very basic LuaWriter to write the modified mission file.

## License
This script is licensed under the MIT License.

Replace `$version` with the actual version of your script or application.