import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* El ascensor es un hilo, en el comienzo del la simulacion revisa si hay 
pasajeros en su piso y si los puede levantar, si y el estado es detenido,
el revisa si hay pasajeros en otro pisos y va hacia ellos */
public class Ascensor implements Runnable {
    // Valores Ascensor
    int _id;
    EstadoAscensor estado = EstadoAscensor.DETENIDO; // DETENIDO, SUBIENDO, BAJANDO, ENTRANDO
    public List<Persona> pasajeros = new ArrayList<Persona>();
    private int _pesoActualkg = 0;
    private int _pesoMaximokg = 400; // kg
    private int _cantidadPersonasMaximas = 5;
    private int _ubicacion = 0;
    private int _destino;

    Ascensor(int Id) {
        _id = Id;
    }

    public void revisarPasajeros() {
    }
/*
    @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                // Realizar el trabajo del ascensor durante un tick
                System.out.println("Ascensor " + _id + " trabajando en el tick " + Planificador.GetPlanificador().tick);
                switch (estado) {
                    case DETENIDO:
                        Detendio();
                        break;
                    case SUBIENDO:
                        Subiendo();
                        break;
                    case BAJANDO:
                        Bajando();
                        break;
                    default:
                        break;
                }

                // Simular tiempo de trabajo
                try {
                    Thread.sleep(500); // Trabajo del ascensor durante medio segundo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                // Indicar que el trabajo ha terminado
                Planificador.GetPlanificador().semaforoAscensores.release();
                // TODO actualizar datos de los pasajeros

                // Esperar a que Simular avance al siguiente tick
                try {
                    Planificador.GetPlanificador().semaforoAscensores.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
                // Realizar otras operaciones o verificar si se debe detener el ascensor
            }
*/
    @Override
    public void run() {
        try {
            // Ascensor Segun el estado hace tiene una logica
            // El ascensor, revisa asi hay pedidos, si los hay revisa los que puede atender
            // segun su estado
            // los atiende y sigue setea su estado segun lo que este haciendo
            // TODO LLAMAR
            Planificador.GetPlanificador().semaforoAscensores.acquire();

            switch (estado) {
                case DETENIDO:
                    Detendio();
                    break;
                case SUBIENDO:
                    Subiendo();
                    break;
                case BAJANDO:
                    Bajando();
                    break;
                default:
                    break;
            }
            Planificador.GetPlanificador().semaforoAscensores.acquire();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            Planificador.GetPlanificador().semaforoAscensores.release();
        }
    }

    // DETENIDO, revisa si hay pedidos si los hay y los puede tomar y cambia su
    // estado para su proximo tick
    public void Detendio() {
        if (Planificador.GetPlanificador().todasLasPersonas.size() == 0) {
            if (_destino != 0 || _ubicacion != 0) {
                _destino = 0;
            }
            irDestino();
            estado = EstadoAscensor.DETENIDO;
        } else {
            // revisa los pedidos actuales y lev anta los pasajeros
            levantarPasajeros(EstadoAscensor.DETENIDO);
        }
        // termina su turno
    }

    // SUBIENDO, el ascensor sube un piso, baja los pasajeros de ese piso
    // revisa si hay más pedidos y si no los hay cambia de estado
    public void Subiendo() {
        irDestino();
        bajarPasajeros();
        levantarPasajeros(EstadoAscensor.SUBIENDO);
        if (pasajeros.isEmpty()) {
            estado = EstadoAscensor.DETENIDO;
        }
    }

    // BAJANDO, el ascensor baja un piso, deja los psajeroes en su destino
    // y sube los pasajeros de ese piso
    public void Bajando() {
        irDestino();
        ;
        bajarPasajeros();
        levantarPasajeros(EstadoAscensor.BAJANDO);
        if (pasajeros.isEmpty()) {
            estado = EstadoAscensor.DETENIDO;
        }
    }

    // LevantarPajeros, revisa la ubicacion de la personas si estan en el mismo
    // piso,
    // DETENIDO, lo los levanta y segun el destino cambia su estado
    // SUBIENDO y BAJANDO si van al mismo sentido los levantan
    private void levantarPasajeros(EstadoAscensor estado) {
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
                        levantarPasajeros(estado); // reviso los pedidos denueevo en diferente estado
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

    // Baja los pasajeros que estan en el su destino
    private void bajarPasajeros() {
        if (pasajeros.isEmpty()) {
            return;
        } else {
            for (Persona persona : pasajeros) {
                if (persona.destino == _ubicacion)
                    ;
                {
                    pasajeros.remove(persona);
                }
            }
        }
    }

    public Boolean puedeSubir(Persona persona) {
        if (!(_cantidadPersonasMaximas >= pasajeros.size())) {
            return false;
        }
        if (!(persona.peso + _pesoActualkg < _pesoMaximokg)) {
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

    // el ascensor se mueve a su destino
    private void irDestino() {
        System.out.println("estoy en el piso: " + this._ubicacion + " y destino: " + _destino);	
        if (_destino != _ubicacion) {
            if (_destino > _ubicacion) {
                _ubicacion++;
            } else {
                _ubicacion--;
            }
        }
        System.out.println("Me movi al piso: " + this._ubicacion);
    }
}