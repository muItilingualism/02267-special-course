#! /usr/local/bin/python3
import simplekml
import sys

id = sys.argv[1]

f = "tracks/"+id+".csv"
to = "tracks/kml/"+id+".kml"
name = id
description = id

kml = simplekml.Kml()
kml.document.name = name
kml.document.description = description

gps = open(f, 'r')

cds = []
for line in gps:
    coordinate = line.split(",")
    if 'null' in coordinate[0]:
        continue
    if 'null' in coordinate[1]:
        continue
    if 'null' in coordinate[2]:
        coordinate[2] = "0"
    floatCoords = (float(coordinate[0]),float(coordinate[1]),float(coordinate[2]) * 0.3048)
    cds.append(floatCoords)

ls = kml.newlinestring(name="path", description="path")
ls.coords = cds
ls.extrude = 0
ls.tessellate = 1
ls.altitudemode = simplekml.AltitudeMode.clamptoground
ls.style.linestyle.width = 5
ls.style.linestyle.color = simplekml.Color.darkorange

kml.save(to)
