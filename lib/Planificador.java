import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Planificador {

    String pathDefault = "files/archivoEntrada.csv";
    int tick = 0; // Tick de la simulacion

    public void Simular() {
        for (tick = 0; tick < 10; tick++) {
            System.out.println("Tick: " + tick);
            List<Persona> entradas = procesarPersonas(pathDefault, tick);
            for (Persona persona : entradas) {
                System.out.println(persona.toString());
            }
        }
    }

    public List<Persona> procesarPersonas(String filePath, int tick) {
        FileManager fm = new FileManager();
        List<Persona> entranAhora = new ArrayList<Persona>();
        List<Persona> todasLasPersonas = fm.csvToPerson(filePath, true);
        
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
