#! /usr/local/bin/python3
import simplekml
import sys

id = sys.argv[1]

f = "tracks/"+id+".csv"
to = "tracks/kml/"+id+"_speed.txt"
name = id
description = id

gps = open(f, 'r')
speeds = open(to,'w')

for line in gps:
    coordinate = line.split(",")
    if 'null' in coordinate[3]:
        continue
    speed = float(coordinate[3]) * 3600/1000
    speeds.write(str(speed))
    speeds.write("\n")

speeds.close()
