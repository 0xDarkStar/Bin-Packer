# Bin Packer
Bin Packer was made because I got tired of manually grouping items into trips for my games.
Bin Packer can read lists from files and group them based on the available space for each trip.


## Inputs

### JSON Parser
Made to work with Elite Dangerous journals.
All that is required is to run the main file like so: <br>
```$ java -jar BinPacker.jar --readLogs SPACE``` <br>
Obviously, make sure to replace `SPACE` with the space available for each trip. <br>
If you only want the runs for the resources that are still left over, add `true` to the end of the command.
It should look like this: <br>
```$ java -jar BinPacker.jar --readLogs SPACE true```

Note: It can take a few minutes to find the last construction site if you had a lot of activity after it.

### Text File Parser
This is made to work with any type of file, as long as it meets the following requirements:
 - Use a ` - <Item Name>: <Item Count>` format for each item
 - Include a `Cargo Space: X` anywhere in the file. X should be the amount of space available for each trip.

Run it using: <br>
```$ java -jar BinPacker.jar LIST.txt```
Replace `LIST.txt` with the file containing your list.


## Output Formats
There are currently only two styles available: "flow" and "block"

### Block Style
The style with the most info is "block".
It shows each run and how much of each item is used in the runs.
It provides all the necessary information to complete all the trips. <br>
Here is an example of how a run would look in "block".
```java
Run 1 (260):    (Remainder: 12)
  - Non-Lethal Weapons           33
  - Medical Diagnostic Equipment 48
  - Survival Equipment           56
  - Emergency Power Cells        66
  - Power Generators             57
```
It tells you:
 - The Run number
 - The space used
 - What is left over from the final item
 - How much of each item is in the run

### Flow Style
The other style is "flow".
It trades in all the info for a simple list showing groupings of items and where the remainders go. <br>
Below is an example of how the "flow" style looks.
```
 - Non-Lethal Weapons             33 \
 - Medical Diagnostic Equipment   48  \
 - Survival Equipment             56   > Run 1 (260)
 - Emergency Power Cells          66  /
 - Power Generators               69 / \ (remainder: 12)
 - Fruit and Vegetables           91    \
 - Computer Components            97     > Run 2 (260)
 - Food Cartridges               130    / \ (remainder: 70)
 - Superconductors               136       > Run 3 (260)
 - Evacuation Shelter            204      / \ (remainder: 150)
 - Copper                        423         \ Run 4-5 (260) (260) (remainder: 53)
 - Surface Stabilisers           582          \ Run 6-7 (520) (remainder: 62)
 - Polymers                      646           > Run 8-10 (520) (241)
```
Because it shows less information than "block", different symbols are used to give a better idea of what each run is and where each item belongs.
For that reason, it could be a bit harder to understand the small details. Here is a simple explanation of how it works: <br>
It uses `\` and `/` to group items into runs and `>` to show a run. <br>
If a run that uses multiple items has a remainder, a `\` is added at the end to "pick up" the remainder and move it to the next run. <br>
If a run that uses one item has a remainder, it uses a `\` instead of `>` to show the remainder flowing over to the next run. <br>
As you can tell, Flow does not say how much of each item to use.
Instead, it only lists how many runs are needed, how full each run is, and if a run has a remainder.
