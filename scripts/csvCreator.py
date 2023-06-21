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
    cantidad_Ticks = 20
    max_llamados_xtick = 3
    cantidad_pisos = 10
    entry = []
    entry.append(["tick","id","ubicacion","destino","peso","acensor"])
    i = 0;
    for y in range(0, cantidad_Ticks):
        for x in range(0, random.randint(1, max_llamados_xtick)):
            ubicacion = formula_ubicacion(cantidad_pisos)
            line = [y,                                              # tick
                    1000 + i,                                       # id
                    ubicacion,                                      # ubication
                    formula_destino(cantidad_pisos,ubicacion),      # destination
                    math.trunc(30 + (100 * random.random())),       # weight
                    -1,]                                            # lift
            entry.append(line)
            i = 1 + i;
    return entry
def formula_destino(cantidad_pisos, ubicacion):
    destino = ubicacion
    while destino == ubicacion:
        destino = random.randint(0, cantidad_pisos)
    return destino
def formula_ubicacion(cantidad_pisos):
    return random.randint(0, cantidad_pisos)
    
if __name__ == "__main__":
    writeCSV(createEntrys())