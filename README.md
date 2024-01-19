# Waypoint Replace Script for DCS Missions

## Overview

This script is designed to modify Digital Combat Simulator (DCS) missions by copying all waypoints of blue coalition country 1 plane groups to be identical to group 1. The script takes a file path to the original mission as input, opens, reads the mission file, performs modifications, and repacks the mission.

## Prerequisites

Before running the script, ensure that you have the following:

- **Java Runtime Environment (JRE):** The script is written in Java, and you need a JRE installed on your system.

## Running the Script

To run the script, use the following command:

```bash
java waypoint-replace-$version.jar $sourceMissionPath
```
<b> Note! <br/>
* Replace `$version` with the actual version downloaded <br/> 
* Replace `$sourceMissionPath` with the full path to the .miz file
</b>


## Options
* outputFile (default: null): Filename to save the modified mission file (output.miz). if none provide, a suffix of new_ will be added
* unitType (default: plane): Unit type to replace waypoints (e.g., helicopter)
* sourceGroup (default: 1): Source group to copy to all other groups
* countryId (default: 1) : Country ID to replace waypoints.
if no output.file provided, mission will be saved as new_$mission.miz

## Example
```bash
java -DunitType=helicopter -DcountryId=4 -DoutputFile=modified.miz -jar waypoint-replace-0.0.7.jar "C:\Users\me\Saved Games\DCS.openbeta\Missions\temp1.miz"
```
This example replaces waypoints for helicopters in country ID 4 and saves the modified mission as modified.miz.

## Custom LuaWriter
The script utilizes a custom and very basic LuaWriter to write the modified mission file.

## License
This script is licensed under the MIT License.