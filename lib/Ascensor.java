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
                Planificador.GetPlanificador().semaforoAscensores.acquire();
                // semaforo que controla el uso de los tiks
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Planificador.GetPlanificador().semaforoAscensores.release();
            }
            // Avisa que termino el tick
            synchronized (Planificador.GetPlanificador().semaforoAscensores) {
                Planificador.GetPlanificador().semaforoAscensores.notify();
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
                        Planificador.GetPlanificador().esperandoAscensor.remove(persona); // Remove persona from
                                                                                          // esperandoAscensor list
                        if (persona.destino > this._ubicacion) { // cambio el estado del ascensor
                            this.estado = EstadoAscensor.SUBIENDO;
                        } else {
                            this.estado = EstadoAscensor.BAJANDO;
                        }
                        levantarPasajeros(estado); // reviso los pedidos denueevo en diferente estado
                        break;
                    }
                    if (this.pasajeros.isEmpty()) {
                        buscarPasajerosOtrosPisos();
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
