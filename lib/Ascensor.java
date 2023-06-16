import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
    private int _ubicacion = 0;
    private int _destino;

    public void revisarPasajeros() {
    }

    @Override
    public void run() {
        try {

            // Ascensor Segun el estado hace tiene una logica

            // El ascensor, revisa asi hay pedidos, si los hay revisa los que puede atender
            // segun su estado
            // los atiende y sigue setea su estado segun lo que este haciendo
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    // DETENIDO, revisa si hay pedidos si los hay y los puede tomar y cambia su
    // estado para su proximo tick
    public void Detendio() {
        if (Planificador.GetPlanificador().todasLasPersonas.size() == 0) {
            estado = EstadoAscensor.DETENIDO;
            // terminar hilo turno
        } else {
            // revisa los pedidos actuales y levanta los pasajeros
            revisarPedidos(EstadoAscensor.DETENIDO);
            // termina su turno
        }
    }
    // TODO implementaro SUBIENDO y BAJANDO

    // revisarPedidos, revisa la ubicacion de la personas si estan en el mismo piso,
    // DETENIDO, lo los levanta y segun el destino cambia su estado
    // SUBIENDO y BAJANDO si van al mismo sentido los levantan
    private void revisarPedidos(EstadoAscensor estado) {
        switch (estado) {
            case DETENIDO:
                // Aca necesito pausar los hilos y cuando levanto los pasajreos
                for (Iterator<Persona> iterator = Planificador.GetPlanificador().esperandoAscensor.iterator(); iterator
                        .hasNext();) {
                    Persona persona = iterator.next();
                    if (puedeSubir(persona)) { // reviso si la persona y ascensor mismo piso
                        pasajeros.add(persona); // Add persona to pasajeros list
                        this._destino = persona.destino; // cambio el destino del ascnesor
                        iterator.remove(); // Remove persona from esperandoAscensor list
                        if (persona.destino > this._ubicacion) { // cambio el estado del ascensor
                            estado = EstadoAscensor.SUBIENDO;
                        } else {
                            estado = EstadoAscensor.BAJANDO;
                        }
                        revisarPedidos(estado); // reviso los pedidos denueevo en diferente estado
                        break;
                    }
                }
                break;
            case SUBIENDO:
                // Aca necesito pausar los hilos y cuando levanto los pasajeros
                for (Iterator<Persona> iterator = Planificador.GetPlanificador().esperandoAscensor.iterator(); iterator
                        .hasNext();) {
                    Persona persona = iterator.next();
                    if (puedeSubir(persona)) {
                        pasajeros.add(persona); // Add persona to pasajeros list
                        iterator.remove(); // Remove persona from esperandoAscensor list
                        if (persona.destino > _destino) {
                            _destino = persona.destino;
                        }
                    }
                }
                break;
            case BAJANDO:
                // Aca necesito pausar los hilos y cuando levanto los pasajeros
                for (Iterator<Persona> iterator = Planificador.GetPlanificador().esperandoAscensor.iterator(); iterator
                        .hasNext();) {
                    Persona persona = iterator.next();
                    if (puedeSubir(persona)) {
                        pasajeros.add(persona); // Add persona to pasajeros list
                        iterator.remove(); // Remove persona from esperandoAscensor list
                        if (persona.destino < _destino) {
                            _destino = persona.destino;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    public Boolean puedeSubir(Persona persona) {
        if(!(_cantidadPersonasMaximas >= pasajeros.size())){
            return false;
        }
        if(!(persona.peso + _pesoActualkg < _pesoMaximokg))
        {
            return false;
        }
        switch (estado) {
            case DETENIDO:
                if (persona.ubicacion == _ubicacion) {
                    return true;
                }
                return false;
            case SUBIENDO:
                if (persona.ubicacion == _ubicacion && persona.destino > this._ubicacion) {
                    return true;
                }
                return false;
            case BAJANDO:
                if (persona.ubicacion == _ubicacion && persona.destino < this._ubicacion) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }
    // ENTRANDO, esta tomando pasajeros, tiene que pausar en ese piso hasta que el
    // psajero entra o sale
}