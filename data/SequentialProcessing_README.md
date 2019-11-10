# Sequential Processing in SUMO  
*We assume that we are **inside** the Kirchheim folder*

netconvert --> polyconvert --> randomtrips --> sumo-gui --> traceExporter  

`netconvert --osm Kirchheim.osm -o Kirchheim.net.xml`
--> create a .net file (SUMO network)

`polyconvert --net-file Kirchheim.net.xml --osm Kirchheim.osm --type-file ../sumo/osmPolyconvert.typ.xml -o Kirchheim.poly.xml`
--> created .poly

**Traffic Demand**
`python ../../tools/randomTrips.py -n Kirchheim.net.xml -o Kirchheim.trips.xml -r Kirchheim.rou.xml -e 50 -l`
--> created .trips, .rou.alt, .rou
--> generates route-file with DUAROUTER automatically because the -r parameter is given

**OR**

python ../../tools/randomTrips.py -n Kirchheim.net.xml -o Kirchheim.trips.xml -r Kirchheim.rou.xml
--> creates more than 50 vehicles

*use `--validate` to only generate trips where a route can be found* 


**modify .sumocfg & call sumo-gui**
<input>
    <net-file value="Kirchheim.net.xml"/>
    <route-files value="Kirchheim.rou.xml"/>
    <additional-files value="Kirchheim.poly.xml"/>
</input>
--> sumo-gui -c Kirchheim.sumocfg

OR

sumo-gui -b 0 -e 1000 --step-length 0.1 -n Kirchheim.net.xml -r Kirchheim.rou.xml -a Kirchheim.poly.xml 
--> call sumo-gui with all parameters

OR

**use GUI settings for SUMO**
sumo-gui -c Kirchheim/Kirchheim.sumocfg -g iGUIsettings.cfg 


####
fringe-factor could be useful (to )
additional files for different modes of traffic (e.g. busses, pedestrians, vehicles)


### OUTPUT ###
sumo-gui -c Kirchheim.sumocfg --output-prefix TIME_ --full-output ../out/fullOut.xml
sumo-gui -c Kirchheim.sumocfg --output-prefix TIME_ --emission-output ../out/emissionOut.xml
sumo-gui -c Kirchheim.sumocfg --output-prefix TIME_ --fcd-output ../out/fcdOut.xml
sumo-gui -c Kirchheim.sumocfg --output-prefix TIME_ --fcd-output.geo --fcd-output ../out/geoOut.xml --quit-on-end
--> do not use --human-readable-time here, traceExporter doesn't know how to handle that

sumo-gui -c Kirchheim.sumocfg --remote-port 4040

**Create a GPX file**
../../tools/traceExporter.py --fcd-input ../out/geoOut.xml --base-date 1560704995 --gpx-output ../out/gpxOut.gpx
