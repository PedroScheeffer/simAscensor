import csv
import random
import math


def writeCSV(entryText, path="archivoEntrada.csv"):
    with open(path, "w", newline="") as csvfile:
        writer = csv.writer(csvfile,
                            delimiter=' ',
                            quotechar='|',
                            quoting=csv.
                            QUOTE_MINIMAL)
        for textLine in entryText:
            writer.writerow(textLine)


# Creates a enrty has follow: tick, id, ubication, destination, weight, lift
def createEntrys():
    entry = []
    for y in range(0, 10):
        for x in range(0, random.randint(1, 10)):
            line = [y,                                          # tick
                    1000 + x,                                   # id
                    0,                                          # ubication
                    math.trunc(10 * random.random()),           # destination
                    math.trunc(30 + (100 * random.random())),   # weifht
                    -1,]                                        # lift
            entry.append(line)
    return entry

writeCSV(createEntrys())
