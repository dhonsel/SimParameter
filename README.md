# SimParameter
Estimation of simulation parameters for our agent-based simulation model to simulate software evolution [SimSE](https://github.com/dhonsel/SimSE). A detailed description of the simulation model, the required parameters to instantiate the model and their extraction can be found in \[1\]. A general workflow how all tools work together can be found [here](https://github.com/dhonsel/SimSE/blob/main/docs/workflow.md).

## Requirements
This tool reads the required data of a open source project to be analyzed from a MongoDB created with [SmarkSHARK](https://github.com/smartshark/). This means that projects to be simulated must first be mined with SmartShark and then the simulation parameters are created with this tool based on the SmartSHARK database. SmarkSHARK is a collection of different plugins for different purposes. As a precondition for the generation of simulation parameters the following plugins must be executed: [vcsSHARK](https://smartshark.github.io/vcsSHARK), [mecoSHARK](https://smartshark.github.io/mecoSHARK/intro.html), [issueSHARK](https://github.com/smartshark/issueSHARK), [refSHARK](https://github.com/smartshark/refSHARK).

Some parameters require the cloned Git repository in addition to the database.

## Configuration
For each project to be analyzed, a configuration file must be created in the `config` folder. The following parameters needs to be specified.

**config.url**: The URL of the project's Git repository, exactly as stored in MongoDB.

**config.repoPath**: Path to the Git repository stored on your local machine.

**config.dirToAnalyze**: Path to source directory, if recognizable.

**config.determinedGrowthPhase**: True if a strong initial growth phase has been detected, otherwise false.

**config.sizeGrowthPhase**: Length of the detected growth phase in days. If none has been detected 0.

**config.maxPackageSplitName**: In order not to simulate every subfolder, depending on the project structure only folders up to the length specified here are analyzed.

**config.maxFilesToAnalyze**: Percent of files to analyze.

**config.minPackageSize**: Minimum number of classes per package to be considered.

**config.refTypes**: Collection of refactoring types to be analyzed. Currently supported: `move_method, inline_method, extract_method`.

To run the application program parameters to set database credentials and the config file to use are required. Please find an example for the project Deltaspike below.

    -c deltaspike.config -o output -H dbhost -p dbport -DB dbname -U dbuser -P dbpasswd -a admindb -d INFO

## Created Parameters
We have different parameter types. On the one hand, we have project parameters originating from this tool that cannot be changed at runtime. On the other hand, we have parameters that can be changed at runtime to compare different simulation runs.

### Core parameters
The core parameters to initialize the simulation model are generated for each project by this tool. The basic data for each project are the maximum size of the project, the number and change probabilities of commits, the number of rounds to simulate, and the developers (identities) to instantiate with their role specific data. Furthermore, information about bugs, their fixes, and the categories of a project are available. The code data for each project is contained in a JSON file.

### Change Coupling Graph
The change coupling graph is also generated by this tool and is stored using the dot format. The nodes represent the files of the software and the edges with their weights represent how often files are changed together in one commit. To initialize the simulation, the nodes contain additional information like the owner, the creator, all developers who touched the file and how often they touched it, and the package the file belongs to. By default, it is generated for each year. This information is used to start the simulation at a given point in time.

## Output Files
The generated simulation core data and the change coupling graph for each year are stored in the output folder set as program parameter `-o`.

## References
\[1\] Development of Agent-Based Simulation Models for Software Evolution, Daniel Honsel, 2020, http://hdl.handle.net/21.11130/00-1735-0000-0005-1318-B
