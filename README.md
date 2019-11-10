# TraLAMA + JOSM + SUMO
The TraLAMA plugin for JOSM allows the user to export the displayed OpenStreetMap data to SUMO, run a traffic simulation and view the results in JOSM as additional layers.

## Getting Started
### Sources  
**GIT**  
Get the source code from the repository using  
`git clone https://github.com/jetoff41/TraLAMA`

### Prerequisites
* [Docker](https://www.docker.com) - Enterprise Container Platform for High-Velocity Innovation

#### macOS
* [XQuartz](https://www.xquartz.org) - X11
* [TraLAMA.app](http://www.tralama.de) - *can be found within the repository*

**Prepare XQuartz on macOS**  
1. Open XQuartz using the `xhost +` command
2. Go to Settings and make sure to activate the `Allow connections from network clients` option
3. Restart XQuartz  
*(this has to be done only once)*

#### Windows
* [XLaunch](https://sourceforge.net/projects/vcxsrv/) -  X Server
* [Lifeboat](https://electronjs.org/apps/lifeboat) -  Lifeboat  

**Prepare XLaunch on Windows**  
1. Start XLaunch and select `Multiple windows`, `Display number -1`, `Start no client`, tick **all** `Extra Settings` and save the configuration file.
**Prepare docker-compose on Windows**  
1. Remove the X11 parts (`/tmp/.X11-unix:/tmp/.X11-unix:rw`) within the `docker-compose.yml` file.
**Prepare Lifeboat on Windows**  
1. Start Lifeboat and select the `docker-compose.yml` file.

### Installing
0. Start Docker

**1. Build Docker Images**  
*We assume that you are inside each docker folder*

1. Build JOSM docker with `docker build . -t josm-img`  
2. Build SUMO docker with `docker build . -t sumo-img`

**Hints:**  
*This can take up to 30 minutes. To build the sumo-img successfully, the memory size for Docker needs to be at least 4GB.*

**1.1 Docker Images from Docker Hub**  
As an alternative, the docker images can be downloaded from `https://hub.docker.com/r/jetoff41/josm-tralama` and `https://hub.docker.com/r/jetoff41/sumo-tralama`. However, to start these images properly, data from this repository is required.

### Starting

**[NEW] on Windows**
1. Start Docker
2. Start XLaunch (e.g. via saved configuration file)
3. Open Lifeboat and click the *Play* button on the upper right.

**[NEW] on macOS**
1. Double-Click `TraLAMA.app` to automatically start Docker, XQuartz and the TraLAMA plugin (including JOSM and SUMO).  
*Attention: Docker usually needs some time to enter the running state. Just open `TraLAMA.app` again.*  
2. After closing JOSM, you probably have to stop the SUMO container manually using `docker stop sumo-container`.

Note: The application requires the xhost executable within `/opt/X11/bin/` and the docker-compose executable within `/Applications/Docker.app/Contents/Resources/bin/`.

### Starting (alternative)
**3 Run (simultaneously)**  
The easiest way to run both docker-containers simultaneously is to call `docker-compose up`  

_______________________________________________________________________________

## Additional Information
### Attention when using JOSM
**DO NOT** upload changes that do not represent the real world to the OpenStreetMap server. For example if you add a new traffic light for simulation purposes, an upload would cause falsified information for OpenStreetMap users unless the traffic authority places a real traffic light at this position.

### Ports
* JOSM Docker: *Port 3030*  
* SUMO Docker: *Port 4040*  

### Input Data

**Network Data**  
1. OpenStreetMap of Kirchheim  
can be found in [./data/](./data/)

**Demand Data**  
1. Static Traffic Demand Data for Kirchheim  
can be found in [./data/input-simulation/demand/](./data/input-simulation/demand/)

### Output Data
can be found in [./data/output-simulation/](./data/output-simulation/) after each simulation

_______________________________________________________________________________

## Built With
* [Docker](https://www.docker.com) - Enterprise Container Platform for High-Velocity Innovation

## Authors
* Jakob Smretschnig - <jakob.smretschnig@tum.de>

## License
* [TraLAMA](http://www.tralama.de) - JOSM Plugin: GPLv3, everything inside the SUMO container (e.g. Conversion Tools): EPLv2
* [SUMO](https://www.eclipse.org/legal/epl-v20.html) - EPL
* [JOSM](https://josm.openstreetmap.de/browser/trunk/LICENSE) - GPL

## Acknowledgments
This research was supported by the [Chair for Applied Software Engineering (Technical University of Munich)](https://ase.in.tum.de/lehrstuhl_1/). Many thanks to my advisor Mariana Avezum who provided insight and expertise that greatly improved this work.  

## References
* [docker-sumo](https://github.com/bogaotory/docker-sumo) - Bo Gao
* [docker-josm](https://github.com/mapbox/docker-josm-linux) - Young Hahn
* [sumoconvert](https://github.com/openstreetmap/josm-plugins/tree/master/sumoconvert) - Ignacio Palermo, Julio Rivera  

*Data that was prepared by the iPraktikum Team is always marked with an `i`-prefix.*

_______________________________________________________________________________

## Internal Use

### Plugin Debugging
usually: Ant build, versioned in ​SVN

https://josm.openstreetmap.de/wiki/DevelopersGuide/DevelopingPlugins#DevelopingPlugins

**IntelliJ** Configuration
1. Run/Debug Configurations: select Application, Main class has to be `org.openstreetmap.josm.gui.MainApplication`
2. File -> Project Structure:
* SDK=1.8, ProjectLanguageLevel=8, Libraries: add `/josm/core/dist/josm-custom.jar`
* Artifacts: add `JAR`
3. View -> Tool Windows -> Ant Build: select `build.xml` and then RUN
4. Terminal: go to plugin folder, run `ant install` or `ant` or `ant clean dist`
5. Run/Debug Application -> JOSM opens: go to Preferences -> Plugins and activate the sumo plugin (has to be done only once)
6. Entry Point for the plugin is `public Sumo (PluginInformation info) {...}`

*Hint*: only the `ant install` command puts a new, functioning `.jar` file to both the `josm/dist` folder and the `Users/<YourName>/Library/JOSM/plugins/` folder.

### Release
Save docker images as `.tar` files for an easier deployment  
* `docker save -o sumo-docker.tar sumo-img`
* `docker save -o josm-docker.tar josm-img`

Load docker images  
`docker load -i sumo-docker.tar`
`docker load -i josm-docker.tar`

***Advantages**: faster; no need to build*
