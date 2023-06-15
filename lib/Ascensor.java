import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* El ascensor es un hilo, en el comienzo del la simulacion revisa si hay 
pasajeros en su piso y si los puede levantar, si y el estado es detenido,
el revisa si hay pasajeros en otro pisos y va hacia ellos */
public class Ascensor implements Runnable {
    // Valores Ascensor
    EstadoAscensor estado = EstadoAscensor.DETENIDO; // DETENIDO, SUBIENDO, BAJANDO, ENTRANDO
    public List<Persona> pasajeros = new ArrayList<Persona>(); 
    private int _pesoActualkg = 0;
    private int _pesoMaximokg = 400; // kg
    private int _tick = 0;
    private int _cantidadPersonasMaximas = 5;

    public void revisarPasajeros(){}
    
    @Override
    public void run() {
        try {
            
            // Ascensor Segun el estado hace tiene una logica

            // El ascensor, revisa asi hay pedidos, si los hay revisa los que puede atender segun su estado
            // los atiende y sigue setea su estado segun lo que este haciendo 
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    // DETENIDO, revisa si hay pedidos si los hay cambia su estado para su proximo tick 
    public void Detendio(){

    }
    
    // ENTRANDO, esta tomando pasajeros, lo
}