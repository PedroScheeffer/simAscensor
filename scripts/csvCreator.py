import csv
import random
import math


def writeCSV(entryText, path="files/archivoEntrada.csv"):
    with open(path, "w", newline="") as csvfile:
        writer = csv.writer(csvfile,
                            delimiter=',',
                            quotechar='|',
                            quoting=csv.QUOTE_MINIMAL)
        for textLine in entryText:
            writer.writerow(textLine)


# Creates a enrty has follow: tick, id, ubication, destination, weight, lift
def createEntrys():
    entry = []
    entry.append(["tick","id","ubicacion","destino","peso","acensor"])
    i = 0;
    for y in range(0, 10):
        for x in range(0, random.randint(1, 10)):
            line = [y,                                          # tick
                    1000 + i,                                   # id
                    0,                                          # ubication
                    math.trunc(10 * random.random()),           # destination
                    math.trunc(30 + (100 * random.random())),   # weight
                    -1,]                                        # lift
            entry.append(line)
            i = 1 + i;
    return entry

if __name__ == "__main__":
    writeCSV(createEntrys())
