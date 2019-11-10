# -*- coding: utf-8 -*-
"""
Copyright (C) 2019 Jakob Smretschnig
This program is made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html
SPDX-License-Identifier: EPL-2.0

@file    xml2csv.py
@name    StaticRoute
@author  Jakob Smretschnig <jakob.smretschnig@tum.de>
@date    2019-08-01
@version 1.0

"""

import os, sys, getopt
import xml.etree.ElementTree as ET
import csv

if 'SUMO_HOME' in os.environ:
    tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
    sys.path.append(tools) # modify the python variable sys.path
    print("Parsing XML Trips to CSV ... \n")
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")

# no we can import the libraries
import sumolib


def edge2XY(edgeID, net):
    # TODO we pick startingPoint from edge as location
    edge = net.getEdge(edgeID)
    x,y = edge.getFromNode().getCoord()
    # y = edge.getToNode().getCoord()
    return x,y


def xmlToCsv(net, xmlFile='../data/demand/validated.trips.xml', csvFile='../data/out/staticTrips.csv'):
    tree = ET.parse(xmlFile)
    root = tree.getroot() # "routes" wrapper
    with open(csvFile, mode='w') as csvfile:
        writer = csv.writer(csvfile, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)

        writer.writerow(['OriginLat','OriginLong','DestLat','DestLong','StartTime'])

        for row in root:
            # print(row.attrib)
            # row.attrib['id'] --> no need
            x, y = edge2XY(row.attrib['from'], net)
            orLong, orLat = net.convertXY2LonLat(x, y)
            x,y = edge2XY(row.attrib['to'], net)
            destLong, destLat = net.convertXY2LonLat(x, y)

            writer.writerow([str(orLat),str(orLong),str(destLat),str(destLong),row.attrib['depart']])


def cmdMain(argv):
    inputNetwork = ''
    inputTripsFile = ''
    outputCSVfile = ''

    required = 'xml2csv.py -n <inputNetwork> -t <inputTrips> -o <outputCSV>'

    try:
        # print('Number of arguments:', len(sys.argv), 'arguments.')
        if len(sys.argv) != 7:
            raise getopt.GetoptError('wrong number of arguments')
        opts, args = getopt.getopt(argv, "hn:t:o:", ["ifile=", "ofile="])
    except getopt.GetoptError:
        print(required)
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print(required)
            sys.exit()
        elif opt in ("-n", "--net"):
            inputNetwork = arg
        elif opt in ("-t", "--trips"):
            inputTripsFile = arg
        elif opt in ("-o", "--csv"):
            outputCSVfile = arg

    net = sumolib.net.readNet(inputNetwork)
    xmlToCsv(net, inputTripsFile, outputCSVfile)


if __name__ == '__main__':
    # xmlToCsv(sumolib.net.readNet('../data/Kirchheim/Kirchheim.net.xml'))
    cmdMain(sys.argv[1:])
