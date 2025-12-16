# Bin Packer
Bin Packer was made because I got tired of manually grouping items into trips for my games.
Bin Packer can read lists from files and group them based on the available space for each trip.

## Installation
Download the latest `BinPacker.jar` from the [Releases](github.com/0xDarkStar/Bin-Packer/releases) page.

## Usage

### Read ED Journals
Read from your Elite Dangerous journals to find the last visited construction depot.
```bash
java -jar BinPacker.jar --readlogs [options]
```

**Options**
- `--journaldir <path>`: Set a custom directory to look through for journals
- `--remaining`: Show only the remaining materials
- `--storage <capacity>`: Set storage capacity (integer)
- `--output <formats>`: Set output formats (comma-separated: `block,flow`)
- `--createroutes`: Create routes to be followed for each material
- `--searchradius <range>`: Set the search radius in LY (A large radius dramatically increases completion time. It may also get rate limited...)

### Read Text File
```bash
java -jar BinPacker.jar <file> [options]
```

**Options**
- `--output <formats>`: Set output formats (comma-separated: `block,flow`)


## Output Formats
- `none`: Prevents the default (block) from being made. Use when only a route is wanted.
- `block`: Presents all information required to complete every single trip in a human-readable format.
- `flow`: Presents minimal information on runs while preserving list structure.
- `route`: Only made if the `--createroutes` option is used. Gives a route to follow for each trip, simplifying the sourcing of materials.

<!-- Leaving this info here until I figure out how to organize the wiki... -->
### Block Format
The format with the most info is "block".
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

<!-- I may need to update this explanation in the future because it is a _bit_ hard to read -->
### Flow Format
The other format is "flow".
It trades in all the info for a simple list showing groupings of items.<br>
Below is an example of how the "flow" format looks.
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

### Route Format
The route format is made to be extremely easy to follow.<br>
Just like "block", it gives the run #, how much cargo space is used, and how much is left over.<br>
Unlike "block", it tells the user what system and what station to go to and what materials to buy
at the stations.<br>
Below is an example of a route:<br>
```
Run 1 (1131): (Remainder: 39)
  Go to Col 285 Sector DR-M d7-152, Hughes-fulford Works and buy:
    - 25 units of Medical Diagnostic Equipment
    - 65 units of Power Generators
    - 105 units of Water Purifiers
    - 145 units of Computer Components
    - 242 units of Food Cartridges
  Go to HIP 84984, Jackson Landing and buy:
    - 145 units of Fruit and Vegetables
  Go to Col 285 Sector IJ-D b27-7, Alcock City and buy:
    - 161 units of Semiconductors
    - 243 units of Superconductors
  Return to the depot and deposit all materials.
```