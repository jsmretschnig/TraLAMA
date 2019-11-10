# Traffic Demand

### Static Routes (CSV)  
OriginLat,OriginLong,DestLat,DestLong,StartTime  
48.169989,11.742861,48.174795,11.758061,09:41  
--> convert to trips.xml using the network-file, convertLonLat2XY and getNeighboringEdges

### Workflow: CSV --> trips.xml --> CSV  
**1. Create validated random trips and parse them to a .csv file**  
*This .csv file can be later used as a static traffic demand input*  

*Generate random trips*  
`PYTHON tools/randomTrips.py --validate -n .net.xml -o .trips.xml -r .rou.xml`  

*Convert .xml trips (edge-ids) to .csv trips (geo-coordinates)*  
`PYTHON tools/randomTripsGeo.py -n .net.xml -t .trips.xml -o .csv`

**2. The .csv file has to be parsed to an .trips.xml file, where we can then generate a .rou.xml file**  
*Convert .csv trips (geo-coordinates) to .xml trips (edge-ids)*  
`PYTHON tools/parseCSVTrips.py -n .net.xml -c .csv -o 2.trips.xml`  

*Convert .xml trips (edge-ids) to .rou routes (edge-id lists)*  
`DUAROUTER --repair --repair.from --repair.to --defaults-override --departpos "random" --departlane "random" --arrivalpos "random" --arrivallane "current" --ignore-errors -n .net.xml  -t 2.trips.xml -o 2.rou.xml`

### Other Formats  
**Trips.xml**  

The syntax of a single trip definition is: `<trip id="<ID>" depart="" from="<ORIGIN_EDGE_ID>" to="<DESTINATION_EDGE_ID>" [type="<VEHICLE_TYPE>"] [color="<COLOR>"]/>`.  

```
<routes>
    <trip id="0" depart="0.00" from="606474649#0" to="4776802#4"/> #id, time, from=edge_id, to=edge_id
</routes>
```


**Network.xml**  
```
<edge id="<ID>" from="<FROM_NODE_ID>" to="<TO_NODE_ID>" priority="<PRIORITY>">
    ... one or more lanes ...
</edge>
```


**Route.xml**  
```
<vehicle id="30" depart="30.00">
    <route edges="edge_id edge_id edge_id"/>
</vehicle>`
```
