import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Planificador {
    String pathDefault = "files/archivoEntrada.csv";
    int tick = 0; // Tick de la simulacion
    int cantidadAscensores = 1;
    List<Persona> todasLasPersonas = new ArrayList<>();
    List<Persona> esperandoAscensor = new ArrayList<>();

    // Locks para Threads
    ReentrantLock lockLevantarPasajero = new ReentrantLock();
    // TODO Ver de implementar un semaforo contador para pausar y continuar los
    // ascensores
    Semaphore semaforoAscensores = new Semaphore(cantidadAscensores);
    Semaphore semaforoTick = new Semaphore(0);
    static Planificador _instancPlanificador; // queremos solo un planificado

    public Planificador() {
        // Se crea y le el archivo csv
        _instancPlanificador = this;
        FileManager fm = new FileManager();
        todasLasPersonas = fm.csvToPerson(pathDefault, true); // TODO mover a una variable para no recalcular
    }

    public static Planificador GetPlanificador() {
        if (Planificador._instancPlanificador == null) {
            return Planificador._instancPlanificador = new Planificador();
        } else {
            return Planificador._instancPlanificador;
        }
    }

    public void Simular() {
        int ticksTotales = 10;

        // Empezamos los ASCENSORES
        for (int i = 0; i < cantidadAscensores; i++) {
            Thread ascensorThread = new Thread(new Ascensor(i));
            ascensorThread.start();
        }
        // TODO imprimir informacion de las personsa, de cada ascensor

        // Empieza Simulacion
        for (tick = 0; tick < ticksTotales; tick++) {
            try {
                semaforoAscensores.acquire(cantidadAscensores); // Acquire permits for all Ascensor threads
                System.out.println("Tick: " + tick);
                esperandoAscensor = procesarPersonas(todasLasPersonas, tick);
                // Log de consola
                for (Persona persona : esperandoAscensor) {
                    System.out.println(persona.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                semaforoAscensores.release(cantidadAscensores); // Release permits for all Ascensor threads
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Persona> procesarPersonas(List<Persona> todasLasPersonas, int tick) {
        List<Persona> entranAhora = new ArrayList<Persona>();
        for (Persona persona : todasLasPersonas) {
            if (persona.tick == tick) {
                entranAhora.add(persona);
                if (todasLasPersonas.size() == 0) {
                    break;
                }
            } else if (persona.tick > tick) {
                break;
            }
        }
        return entranAhora;
    }
}
