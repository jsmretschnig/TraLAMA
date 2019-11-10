#
# @author  Nathalie Pett <nathalie.pett@tum.de>
# 
# modified by Jakob Smretschnig <jakob.smretschnig@tum.de>

import os, sys, getopt
import subprocess
import xml.etree.ElementTree as ET
from lxml import etree

VALID_AREA_IDS = {'aschheim_west',
                  'ebersberg_east',
                  'feldkirchen_west',
                  'heimstetten_industrial_1',
                  'heimstetten_industrial_2',
                  'heimstetten_residential',
                  'kirchheim_industrial_east',
                  'kirchheim_industrial_west',
                  'kirchheim_residential',
                  'unassigned_edges'}

if 'SUMO_HOME' in os.environ:
    tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
    sys.path.append(tools)
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")

def set_default_veh_color(inroute, outroute):
    routes_tree = ET.parse(inroute)
    root = routes_tree.getroot()
    for vehicle in root.iter('vehicle'):
        vehicle.set('color', '102, 179, 255') # light blue
    routes_tree.write(outroute)

# if argument invalid: default color is set for all vehicles
def set_origin_dest_veh_color(color_by, inroute, taz, outroute):
    set_default_veh_color(inroute, outroute)

    routes_tree = ET.parse(inroute)
    routes_root = routes_tree.getroot()
    taz_tree = ET.parse(taz)
    taz_root = taz_tree.getroot()

    heimstetten_edges = str()
    kirchheim_edges = str()

    for taz in taz_root.iter('taz'):
        if (taz.get('id') == 'heimstetten_origin_dest' or taz.get('id') == 'heimstetten_industrial_origin_dest'):
            heimstetten_edges += taz.get('edges')
        elif (taz.get('id') == 'kirchheim_origin_dest'):
            kirchheim_edges += taz.get('edges')

    for vehicle in routes_root.iter('vehicle'):
        route_edges = vehicle.find('route').get('edges').split()

        idx = None
        if (color_by == 'origin'):
            idx = 0
        elif (color_by == 'destination'):
            idx = len(route_edges) - 1
        else:
            return

        if (route_edges[idx] in heimstetten_edges):
            vehicle.set('color', '255, 51, 153') # magenta
        elif (route_edges[idx] in kirchheim_edges):
            vehicle.set('color', '255, 153, 51') # orange

    routes_tree.write(outroute)
    return True

# added by Jakob Smretschnig
def cmdMain(argv):
    inputRoute = ''
    inputTaz = ''
    outputRoute = ''

    required = 'setVehicleColor.py -i <inputRoute> -t <inputTAZ> -o <outputRoute>'

    try:
        # print('Number of arguments:', len(sys.argv), 'arguments.')
        if len(sys.argv) != 7:
            raise getopt.GetoptError('wrong number of arguments')
        opts, args = getopt.getopt(argv, "hi:t:o:", ["ifile=", "ofile="])
    except getopt.GetoptError:
        print(required)
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print(required)
            sys.exit()
        elif opt in ("-i", "--route-in"):
            inputRoute = arg
        elif opt in ("-t", "--taz"):
            inputTaz = arg
        elif opt in ("-o", "--route-out"):
            outputRoute = arg

    set_origin_dest_veh_color("origin", inputRoute, inputTaz, outputRoute)

if __name__ == '__main__':
    cmdMain(sys.argv[1:])