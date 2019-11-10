# -*- coding: utf-8 -*-
"""
@author: Ethan Houston

-- modified and extended (with command-line interface) by Jakob Smretschnig <jakob.smretschnig@tum.de>
@name: GPXConverter
"""

import os, sys, getopt
from xml.dom import minidom
import calendar
import dateutil.parser
import pandas as pd


test = "gpx_csv_converter"


def iso_to_epoch(iso_time):
    return calendar.timegm(dateutil.parser.parse(iso_time).timetuple())


class Converter:

    def __init__(self, string, name):

        if name[-4:] != '.csv':
            name = name + '.csv'

        # parse an xml file by name
        mydoc = minidom.parseString(string)

        trkpt = mydoc.getElementsByTagName('trkpt')
        time = mydoc.getElementsByTagName('time')
        ele = mydoc.getElementsByTagName('ele')
        #hr = mydoc.getElementsByTagName('gpxtpx:hr')
        hr = mydoc.getElementsByTagName('ns3:hr')
        cad = mydoc.getElementsByTagName('ns3:cad')

        lats = []
        longs = []
        times = []
        eles = []
        hrs = []
        dates = []
        parsed_times = []
        cads = []

        for elem in trkpt:
            lats.append(elem.attributes['lat'].value)
            longs.append(elem.attributes['lon'].value)

        for elem in time:
            times.append(elem.firstChild.data)

        for elem in hr:
            hrs.append(elem.firstChild.data)

        base_time = iso_to_epoch(times[0])

        time_differences = []

        for item in times:
            time_differences.append(iso_to_epoch(item) - base_time)
            date_obj = (dateutil.parser.parse(item))
            dates.append(str(date_obj.year) + "-" + str(date_obj.month) + "-" + str(date_obj.day))
            parsed_times.append(str(date_obj.hour) + ":" + str(date_obj.minute) + ":" + str(date_obj.second))

        for elem in ele:
            eles.append(elem.firstChild.data)

        for elem in cad:
            cads.append(elem.firstChild.data)

        hrs.append(0)

        data = {'date': pd.Series(dates),
                'time': pd.Series(parsed_times),
                'latitude': pd.Series(lats),
                'longitude': pd.Series(longs),
                'elevation': pd.Series(eles),
                'heart_rate': pd.Series(hrs),
                'cadence': pd.Series(cads)}

        print(len(dates), len(parsed_times), len(lats), len(longs), len(eles), len(hrs), len(cads))

        df = pd.DataFrame(data=data)

        # modified by Jakob Smretschnig <jakob.smretschnig@tum.de>
        #df = df[['date', 'time', 'latitude', 'longitude', 'elevation', 'heart_rate', 'cadence']]
        df = df[['date', 'time', 'latitude', 'longitude']]

        df.to_csv(name, encoding='utf-8', index=False)

        print("Done!")



def gpxToCsv(files):
    inputGpx = ''
    outputCsv = ''

    required = 'gpx2csv.py -i <inputGPX> -o <outputCSV>'

    try:
        if len(sys.argv) != 5:
            raise getopt.GetoptError('wrong number of arguments')
        opts, args = getopt.getopt(files, "hi:o:", ["ifile=", "ofile="])
    except getopt.GetoptError:
        print(required)
        sys.exit(2)

    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print(required)
            sys.exit()
        elif opt in ("-i", "--gpx"):
            inputGpx = arg
        elif opt in ("-o", "--csv"):
            outputCsv = arg

    #inputPath = '../ACPS/data/demo/selectedLayer.gpx'

    with open(inputGpx, 'r') as file:
        data = file.read()
        Converter(data, outputCsv) #'../ACPS/data/out/csvForWeb.csv'

if __name__ == '__main__':
    gpxToCsv(sys.argv[1:])