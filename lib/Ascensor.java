import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Ascensor implements Runnable {
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

@Override
public void run() {
    try {
        Thread.sleep(500);
    } catch (Exception e) {
    }
    while (!Thread.currentThread().isInterrupted()) {
        try {
            Planificador.GetPlanificador().semaforoTick.acquire();
            // semaforo que controla el uso de los tiks
            System.out.println("Ascensor " + _id + " trabajando en el tick " + Planificador.GetPlanificador().tick);
            Planificador.GetPlanificador().lockLevantarPasajero.lock();
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
        Planificador.GetPlanificador().lockLevantarPasajero.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Simular tiempo de trabajo
        try {
            Thread.sleep(500); // Trabajo del ascensor durante medio segundo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}

    // DETENIDO, revisa si hay pedidos si los hay y los puede tomar y cambia su
    // estado para su proximo tick
    public void Detendio() {
        if (Planificador.GetPlanificador().esperandoAscensor.size() == 0) {
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

// Refactored levantarPasajeros function to include print statements
private void levantarPasajeros(EstadoAscensor estado) {
    switch (estado) {
        case DETENIDO:
            for (Iterator<Persona> iterator = Planificador.GetPlanificador().esperandoAscensor.iterator(); iterator.hasNext();) {
                Persona persona = iterator.next();
                if (puedeSubir(persona)) {
                    pasajeros.add(persona);
                    this._destino = persona.destino;
                    Planificador.GetPlanificador().esperandoAscensor.remove(persona);
                    if (persona.destino > this._ubicacion) {
                        this.estado = EstadoAscensor.SUBIENDO;
                    } else {
                        this.estado = EstadoAscensor.BAJANDO;
                    }
                    System.out.println(persona.id + " subió al ascensor");
                    levantarPasajeros(estado);
                    break;
                }
                if (this.pasajeros.isEmpty()) {
                    buscarPasajerosOtrosPisos();
                }
            }
            break;
        case SUBIENDO:
            for (Iterator<Persona> iterator = Planificador.GetPlanificador().esperandoAscensor.iterator(); iterator.hasNext();) {
                Persona persona = iterator.next();
                if (puedeSubir(persona)) {
                    pasajeros.add(persona);
                    iterator.remove();
                    if (persona.destino > _destino) {
                        _destino = persona.destino;
                    }
                    System.out.println(persona.id + " subió al ascensor");
                }
            }
            break;
        case BAJANDO:
            for (Iterator<Persona> iterator = Planificador.GetPlanificador().esperandoAscensor.iterator(); iterator.hasNext();) {
                Persona persona = iterator.next();
                if (puedeSubir(persona)) {
                    pasajeros.add(persona);
                    iterator.remove();
                    if (persona.destino < _destino) {
                        _destino = persona.destino;
                    }
                    System.out.println(persona.id + " subio del ascensor");
                }
            }
            break;
        default:
            break;
    }
}

    // revisa denuevo los pasajeros esperando y levante el que este más cerca 
    private void buscarPasajerosOtrosPisos() {
        if(!Planificador.GetPlanificador().esperandoAscensor.isEmpty()){
            Persona pasajeroDestino = Planificador.GetPlanificador().esperandoAscensor.get(0);
            this._destino = pasajeroDestino.ubicacion;
            if (pasajeroDestino.ubicacion > this._ubicacion) { // cambio el estado del ascensor
                this.estado = EstadoAscensor.SUBIENDO;
            } else {
                this.estado = EstadoAscensor.BAJANDO;
            }
        }
    }

// Remove passengers that have reached their destination
private void bajarPasajeros() {
    if (pasajeros.isEmpty()) {
        return;
    } else {
        Iterator<Persona> it = pasajeros.iterator();
        while (it.hasNext()) {
            Persona persona = it.next();
            if (persona.destino == _ubicacion) {
                it.remove();
                System.out.println(persona.id + " bajó del ascensor");
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
