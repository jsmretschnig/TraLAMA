# -*- coding: utf-8 -*-

"""
Copyright (C) 2019 Jakob Smretschnig
This program is made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html
SPDX-License-Identifier: EPL-2.0

@file    SimulationController.py
@author  Jakob Smretschnig <jakob.smretschnig@tum.de>
@date    2019-08-01
@version 1.0

"""

import os, sys
import subprocess

from flask import Flask, request
app = Flask(__name__)

"""""""""""""""
CONSTANTS
"""""""""""""""
HOST = '0.0.0.0'   # listen on all public IPs
PORT = 4040

class StaticVariables:
  BEGIN = 0.0
  END = 1000.0
  RANDOM = True
  TRACI = 4041

name = 'selectedLayer'

dockerPath = "/sumo/bin/" # "" for local, "/sumo/bin/" for docker

pathData = '../ACPS/data/'
OSM_FILE = pathData + 'output-simulation/' + name + '.osm'
NET_FILE = pathData + 'output-simulation/' + name + '.net.xml'
POLY_FILE = pathData + 'output-simulation/' + name + '.poly.xml'
TRIPS_FILE = pathData + 'output-simulation/' + name + '.trips.xml'
ROUTE_FILE = pathData + 'output-simulation/' + name + '.rou.xml'
GEO_FILE = pathData + 'output-simulation/' + name + '.geo.xml'
GPX_FILE = pathData + 'output-simulation/' + name + '.gpx'
CSV_OUT_FILE = pathData + 'output-simulation/' + name + '.csv'

GUI_FILE = pathData + 'input-simulation/' + 'iGUI-settings-origin-dest-vehicles' + '.cfg' #'iGUIsettings.cfg'
TAZ_FILE = pathData + 'input-simulation/' + 'iAreas-of-interest' + '.taz.xml'
TYPE_FILE_NETCONVERT = pathData + 'input-simulation/' + 'osmNetconvertKirchheim' + '.typ.xml'

pathTools = '../app/tools/'
TYPE_FILE = pathTools + 'osmPolyconvert' + '.typ.xml'

toolRandomTrips = pathTools + 'randomTrips' + '.py'
toolTraceExporter = pathTools + 'traceExporter' + '.py'

toolcsv2xml = pathTools + 'csv2xml' + '.py'
toolxml2csv = pathTools + 'xml2csv' + '.py'
toolgpx2csv = pathTools + 'gpx2csv' + '.py'

toolSetVehicleColor = pathTools + 'setVehicleColor' + '.py'

pathDemand = pathData + 'input-simulation/' + 'demand/'
staticTripsKirchheimMorning = pathDemand + 'i1_morning' + '.csv'
staticTripsKirchheimNoon = pathDemand + 'i2_noon' + '.csv'
staticTripsKirchheimAfternoon = pathDemand + 'i3_afternoon' + '.csv'
staticTripsREST = pathDemand + 'staticTripsREST' + '.csv'

"""""""""""""""
SETUP SUMO
"""""""""""""""
if 'SUMO_HOME' in os.environ:
    tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
    sys.path.append(tools) # modify the python variable sys.path
    print("SUMO Interface is listening ...")
else:
    print("please declare environment variable 'SUMO_HOME'")
    #sys.exit("please declare environment variable 'SUMO_HOME'")

# no we can import the libraries
import traci

def doSubProcess(cmd):
    output = subprocess.Popen(cmd,
                              stdout=subprocess.PIPE,
                              stderr=subprocess.STDOUT)
    stdout, stderr = output.communicate()
    print("Result:\n" + str(stdout))
    print("Error: " + str(stderr) + "\n")
    if str(stderr) == 'None':
        return True
    return False

"""""""""""""""
LISTENING to HTTP Requests
"""""""""""""""

@app.route("/")
def hello():
    return "Hello TraLAMA!"

#start the sumo simulation
@app.route("/sumo", methods=['POST'])
def startSimulation():
    #print(request.is_json)
    payload = request.get_json(force=True)
    #print(payload)
    # print(payload["exe"]) # sumo or sumo-gui
    # print(payload["simLength"]) # e.g. 1800 (int)
    # print(payload["stepLength"]) # e.g. 0.1 (double)
    # print(payload["start"]) # start simulation automatically, GUI only !!!
    #payload = jsonify(request)
    sumoCmd = []
    if payload["exe"]=="sumo":
      # no .poly.xml
      # no auto-start
      sumoCmd.append(dockerPath + "sumo")
    else:
      sumoCmd.append(dockerPath + "sumo-gui")
      sumoCmd.append("--quit-on-end")
      print("added --quit-on-end")
      sumoCmd.append("-a")
      sumoCmd.append(POLY_FILE)
      #sumoCmd.append("-c")
      #sumoCmd.append(SUMOCFG)
      if payload["start"]=="auto":
        sumoCmd.append("-S")

    sumoCmd.append("-n")
    sumoCmd.append(NET_FILE)
    sumoCmd.append("-r")
    sumoCmd.append(ROUTE_FILE)

    sumoCmd.append("--step-length")
    sumoCmd.append(str(payload["stepLength"]))
    sumoCmd.append("-g")
    sumoCmd.append(GUI_FILE)

    if StaticVariables.RANDOM == False:
      sumoCmd.append("-b")
      sumoCmd.append(str(StaticVariables.BEGIN))
      sumoCmd.append("-e")
      sumoCmd.append(str(StaticVariables.END))

    sumoCmd.append("--fcd-output.geo")
    sumoCmd.append("--fcd-output")
    sumoCmd.append(GEO_FILE)

    if payload["statistics"]==True:
      a = 0 # do something
      #sumoCmd.append("") TODO
    
    # period will be always the same as stepLength, that's good
    # if payload["period"]!= 1.0:
    #   sumoCmd.append("--device.fcd.period")
    #   sumoCmd.append(str(payload["period"]))
    
    print(sumoCmd)
    try:
      traci.close(False)
      print("Closed old connection. Simulation Ready.")
    except:
      StaticVariables.TRACI = StaticVariables.TRACI + 1
      print("Simulation Ready")
    traci.start(sumoCmd,StaticVariables.TRACI)
    try:
      step = 0
      while step < int(payload["simLength"]):
          traci.simulationStep()
          step += 1
    except:
      return "-1"
    traci.close(False)
    return "SUMO POST request - got json"

#generate a road network file
@app.route("/netconvert")
def netconvert():
    #recommended by https://sumo.dlr.de/docs/Networks/Import/OpenStreetMap.html
    #--geometry.remove --ramps.guess \
    #--junctions.join --tls.guess-signals --tls.discard-simple --tls.join
    # or remove?
    # --remove-edges.by-vclass rail_slow,rail_fast,bicycle,pedestrian
    netConvertCmd = [dockerPath + "netconvert",
                     "--osm", OSM_FILE,
                     "--junctions.join",
                     "--output.street-names",
                     "--type-files", TYPE_FILE_NETCONVERT,
                     "--geometry.remove",
                     "--ramps.guess",
                     "--tls.guess-signals",
                     "--tls.discard-simple",
                     "--tls.join",
                     '-o', NET_FILE]
    res = doSubProcess(netConvertCmd)
    return str(res) + " - Hello netconvert!"

#generate additional polygons
@app.route("/polyconvert")
def polyconvert():
    cmd = [dockerPath + "polyconvert",
           "--net-file", NET_FILE,
           "--osm", OSM_FILE,
           "--type-file", TYPE_FILE,
           '-o', POLY_FILE]
    res = doSubProcess(cmd)
    return str(res) + " - Hello polyconvert!"

#create static trips based on a csv trips file
@app.route("/statictrips", methods=['POST'])
def statictrips():
    payload = request.get_json(force=True)
    if payload["demand"]=="morning":
      selectCsv = staticTripsKirchheimMorning
    elif payload["demand"]=="noon":
      selectCsv = staticTripsKirchheimNoon
    elif payload["demand"]=="afternoon":
      selectCsv = staticTripsKirchheimAfternoon

    cmd = ["python3", toolcsv2xml,
           "-n", NET_FILE,
           "-c", selectCsv,
           "-o", TRIPS_FILE,
           "-l", str(payload["simLength"])]
    output = subprocess.Popen(cmd,
                              stdout=subprocess.PIPE,
                              stderr=subprocess.STDOUT)
    stdout, stderr = output.communicate()
    res = str(stdout)
    print("Result:\n" + res)
    print("Error: " + str(stderr) + "\n")
    
    try:
      arr = res.rstrip().split("-RESULT-")
      if arr[1] == "empty":
        print("no trips possible")
        return "-1"
      resArr = arr[1].split("|")
      print(resArr)
      resArr = resArr[0].split(",")
      print(resArr)
      print(float(resArr[0]))
      print(float(resArr[1]))
      
      StaticVariables.BEGIN = float(resArr[0])
      StaticVariables.END = float(resArr[1])
      StaticVariables.RANDOM = False
    except:
      print("couldn't parse begin and end values")
      return "-1"
    # print(str(StaticVariables.BEGIN))
    # print(str(StaticVariables.END))
    return res

#create routes based on trips
@app.route("/duarouter")
def duarouter():
    cmd = [dockerPath + "duarouter",
           "--repair", "--repair.from", "--repair.to",
           "--defaults-override", "--departpos", "random",
           "--departlane", "random", "--arrivalpos", "random",
           "--arrivallane", "current",
           "--ignore-errors",
           "-n", NET_FILE,
           "-t", TRIPS_FILE,
           '-o', ROUTE_FILE]
    res = doSubProcess(cmd)
    return str(res) + " - Hello duarouter!"

#create random routes
@app.route("/randomroutes", methods=['POST'])
def randomroutes():
    payload = request.get_json(force=True)
    cmd = ["python3", toolRandomTrips,
           "-n", NET_FILE,
           "-o", TRIPS_FILE,
           "-r", ROUTE_FILE,
           "-e", str(payload["simLength"]),
           "-p", str(payload["stepLength"])]
    res = doSubProcess(cmd)
    StaticVariables.RANDOM = True
    return str(res) + " - Hello randomroutes!"

#set vehicle color
@app.route("/vehiclecolor")
def vehiclecolor():
    cmd = ["python3", toolSetVehicleColor,
           "-i", ROUTE_FILE,
           "-t", TAZ_FILE,
           "-o", ROUTE_FILE]
    res = doSubProcess(cmd)
    return str(res) + " - Hello vehiclecolor!"

#convert the simulation output to gpx
@app.route("/geoconverter")
def geoconverter():
    cmd = ["python3", toolTraceExporter,
           "--fcd-input", GEO_FILE,
           "--gpx-output", GPX_FILE]
    res = doSubProcess(cmd)
    return str(res) + " - Hello generategpx!"

#convert the gpx output to csv
@app.route("/gpxconverter")
def gpxconverter():
    cmd = ["python3", toolgpx2csv,
           "-i", GPX_FILE,
           "-o", CSV_OUT_FILE]
    res = doSubProcess(cmd)
    return str(res) + " - Hello gpx2csv!"

#currently not in use; only for data preparation
@app.route("/xmlconverter")
def xmlconverter():
    cmd = ["python3", toolxml2csv,
           "-n", NET_FILE,
           "-t", TRIPS_FILE,
           '-o', staticTripsREST]
    res = doSubProcess(cmd)
    return str(res) + " - Hello xml2csv!"

if __name__ == "__main__":
    app.run(host=HOST, port=PORT)
