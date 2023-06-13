import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
public class Planificador {

    String pathDefault = "files/archivoEntrada.csv";
    int tick = 0; // Tick de la simulacion
    List<Persona> todasLasPersonas = new ArrayList<>();
    List<Persona> esperandoAscensor = new ArrayList<>();
    static Semaphore semaphoroLevantarPasajero = new Semaphore(1);
    static Semaphore semaphoroEntrandoAscensor = new Semaphore(1);
    
    public Planificador() {
        FileManager fm = new FileManager();
        todasLasPersonas = fm.csvToPerson(pathDefault, true);  // TODO mover a una variable para no recalcular
    } 

    public void Simular() {
        int ticksTotales = 10;
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
