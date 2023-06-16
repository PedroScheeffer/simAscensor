import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Planificador {
    String pathDefault = "files/archivoEntrada.csv";
    int tick = 0; // Tick de la simulacion 
    // TODO Ver de implementar un semaforo contador para pausar y continuar los ascensores
    List<Persona> todasLasPersonas = new ArrayList<>();
    List<Persona> esperandoAscensor = new ArrayList<>();
    // Semaforos para Threads 
    static Semaphore semaphoroLevantarPasajero = new Semaphore(1);
    static Semaphore semaphoroEntrandoAscensor = new Semaphore(1);

    static Planificador _instancPlanificador;

    public Planificador() {
        // Se crea y le el archivo csv
        FileManager fm = new FileManager();
        todasLasPersonas = fm.csvToPerson(pathDefault, true);  // TODO mover a una variable para no recalcular 
    } 
    public static Planificador GetPlanificador() {
        if(Planificador._instancPlanificador == null){
            return Planificador._instancPlanificador = new Planificador();
        } else {
            return Planificador._instancPlanificador;
        }
    }

    public void Simular() {
        int ticksTotales = 10;
        // TODO EMPEZAR ASCENSORES
        
        // Empieza Simulacion
        for (tick = 0; tick < ticksTotales; tick++) {
            System.out.println("Tick: " + tick);
            esperandoAscensor = procesarPersonas(todasLasPersonas, tick);
            for (Persona persona : esperandoAscensor) {
                System.out.println(persona.toString());
            }
        }
    }

    public List<Persona> procesarPersonas(List<Persona> todasLasPersonas, int tick) {
        List<Persona> entranAhora = new ArrayList<Persona>();
         
        for (Persona persona : todasLasPersonas) {
            if (persona.tick == tick) {
                entranAhora.add(persona);
                if(todasLasPersonas.size() == 0){
                    break;
                }
            }else if(persona.tick > tick){
                break;
            }
        }
        return entranAhora;
    }
}
