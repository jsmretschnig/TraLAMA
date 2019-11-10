# -*- coding: utf-8 -*-
"""
Copyright (C) 2019 Jakob Smretschnig
This program is made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html
SPDX-License-Identifier: EPL-2.0

@file    csv2xml.py
@name    SimulationRoute
@author  Jakob Smretschnig <jakob.smretschnig@tum.de>
@date    2019-08-01
@version 1.0

"""

import os, sys, getopt
from lxml import etree as ET
from collections import OrderedDict as OD

if 'SUMO_HOME' in os.environ:
    tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
    sys.path.append(tools) # modify the python variable sys.path
    print("Parsing CSV Trips to XML ...\n")
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")

# no we can import the libraries
import sumolib

# parses an xml edge element and selects the attributes
def parseXML(xml):
    tree = ET.fromstring(str(xml))
    id_ = tree.attrib["id"]
    # from_ = tree.attrib["from"]
    # to_ = tree.attrib["to"]
    return id_

# returns the nearest edge to a given geo-coordinate
def nearbyEdges(lat, lon, net):
    import pyproj
    import rtree

    radius = 5 # find out what makes sense
    x, y = net.convertLonLat2XY(lon, lat)
    edges = net.getNeighboringEdges(x, y, radius)
    # pick the closest edge
    if len(edges) > 0:
        sortedEdges = sorted(edges, key=lambda x: x[1])
        closestEdge, closestDist = sortedEdges[0]
        # distancesAndEdges = sorted([(dist, edge) for edge, dist in edges])
        # closestDist, closestEdge = distancesAndEdges[0]
        return closestEdge


def csvToXml(net, csvFile='../data/demand/staticTrips_Kirchheim.csv', xmlFile='../data/out/staticEdge.trips.xml', simLength=10800):
    import csv

    """
       Make sure that the first row of .csv DOES HAVE field names
       The CSV should be structured as follows: OriginLat,OriginLong,DestLat,DestLong,StartTime
    """

    BEGIN = 86400.0
    END = 0.0

    # csv must be with , delimeter and numbers with . (english version)
    with open(csvFile) as csvfile:
        reader = csv.reader(csvfile, delimiter=',', quotechar='|')
        root = ET.Element("routes") # for the xml output
        count = 0
        for row in reader:
            if len(row) == 0 or row[0] == 'OriginLat':
                continue
            origin = nearbyEdges(float(row[0]), float(row[1]), net)
            destination = nearbyEdges(float(row[2]), float(row[3]), net)
            if origin is None or destination is None:
                continue
            origin_id = parseXML(origin)
            destination_id = parseXML(destination)

            time = row[4].split(":") # we assume the format hh:mm:ss or seconds with 2 decimal places e.g. 1.36
            if len(time) == 1:
                floatTime = float(time[0])
            else:
                floatTime = (float(time[0])*60 + float(time[1])) * 60 + float(time[2]) # float time in [seconds]
            
            if floatTime < BEGIN:
                BEGIN = floatTime
            if floatTime > END:
                END = floatTime

            attributes = OD({'id':str(count), 'depart':str(floatTime), 'from':str(origin_id), 'to':str(destination_id)})
            trip = ET.SubElement(root, "trip", attributes)
            count += 1

            tree = ET.ElementTree(root)
            tree.write(xmlFile, pretty_print=True, encoding="UTF-8", xml_declaration=True)

            if count == simLength:
                break

            # print(' '.join(row))
            # print("Origin: " + str(origin))
            # print("Destination: " + str(destination) + "\n")
    if BEGIN == 86400.0 or END == 0.0:
        print("-RESULT-" + "empty")
    else:
        print("-RESULT-" + str(BEGIN) + "," + str(END) + "|")

def cmdMain(argv):
    inputNetwork = ''
    inputCSVfile = ''
    outputXMLfile = ''
    simLength = 10800

    required = 'csv2xml.py -n <inputNetwork> -c <inputCSV> -o <outputXML> -l <simLength>'

    try:
        # print('Number of arguments:', len(sys.argv), 'arguments.')
        if len(sys.argv) != 9:
            raise getopt.GetoptError('wrong number of arguments')
        opts, args = getopt.getopt(argv, "hn:c:o:l:", ["ifile=", "ofile="])
    except getopt.GetoptError:
        print(required)
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print(required)
            sys.exit()
        elif opt in ("-n", "--net"):
            inputNetwork = arg
        elif opt in ("-c", "--csv"):
            inputCSVfile = arg
        elif opt in ("-o", "--xml"):
            outputXMLfile = arg
        elif opt in ("-l", "--simLength"):
            try:
                simLength = int(arg)
            except:
                simLength = 10800

    net = sumolib.net.readNet(inputNetwork)
    csvToXml(net, inputCSVfile, outputXMLfile, simLength)

if __name__ == '__main__':
    #csvToXml(sumolib.net.readNet('../ACPS/data/out/selectedLayer.net.xml'), '../data/demand/staticTrips.csv', simLength=1000)
    cmdMain(sys.argv[1:])
