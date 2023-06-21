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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Planificador.GetPlanificador().semaforoTick.acquire();
                bajarPasajeros();
                // semaforo que controla el uso de los tiks
                System.out.println("Ascensor: " + _id +
                        " Piso: " + _ubicacion + " Destino: " + _destino +
                        " Pasajeros: " + pasajeros.size() + " Peso: " + _pesoActualkg + " kg");
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
                // TODO agregar if donde reviso si baja gente o sube, si lo hace el ascensor no
                // se mueve
                irDestino();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Simular tiempo de trabajo
            try {
                Thread.sleep(50); // Trabajo del ascensor durante medio segundo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // DETENIDO, revisa si hay pedidos si los hay y los puede tomar y cambia su
    // estado para su proximo tick
    public void Detendio() {
        // si tiene pasajeros cambia el destino para el del pasajero
        if (!pasajeros.isEmpty()) {
            actualizarDestino(pasajeros.get(0), estado);
            if (pasajeros.get(0).destino > this._ubicacion) { // cambio el estado del ascensor
                this.estado = EstadoAscensor.SUBIENDO;
            } else {
                this.estado = EstadoAscensor.BAJANDO;
            }
        } else {
            if (Planificador.GetPlanificador().esperandoAscensor.size() == 0) {
                if (_destino != 0 || _ubicacion != 0) {
                    _destino = 0;
                }
                estado = EstadoAscensor.DETENIDO;
            }
        }
        // revisa los pedidos actuales y lev anta los pasajeros
        levantarPasajeros(this.estado);
        // termina su turno
    }

    // SUBIENDO, el ascensor sube un piso, baja los pasajeros de ese piso
    // revisa si hay más pedidos y si no los hay cambia de estado
    public void Subiendo() {
        levantarPasajeros(this.estado);
        cambioaDetenido();
    }

    // BAJANDO, el ascensor baja un piso, deja los psajeroes en su destino
    // y sube los pasajeros de ese piso
    public void Bajando() {
        levantarPasajeros(this.estado);
        cambioaDetenido();
    }

    private void cambioaDetenido() {
        if (pasajeros.isEmpty() && _ubicacion == _destino) {
            estado = EstadoAscensor.DETENIDO;
        }
    }

    private void levantarPasajeros(EstadoAscensor estado) {
        Planificador planificador = Planificador.GetPlanificador();
        switch (estado) {
            case DETENIDO:
                procesarPasajeros(planificador.esperandoAscensor, estado);
                if (this.pasajeros.isEmpty()) {
                    buscarPasajerosOtrosPisos();
                }
                break;
            case SUBIENDO:
            case BAJANDO:
                procesarPasajeros(planificador.esperandoAscensor, estado);
                break;
            default:
                break;
        }
    }

    private void procesarPasajeros(List<Persona> personas, EstadoAscensor estado) {
        Planificador planificador = Planificador.GetPlanificador();
        Iterator<Persona> iterator = planificador.esperandoAscensor.iterator();

        while (iterator.hasNext()) {
            Persona persona = iterator.next();
            if (puedeSubir(persona, estado)) {
                pasajeros.add(persona);
                _pesoActualkg = _pesoActualkg + persona.peso;
                actualizarPasajeroSubida(persona);
                actualizarDestino(persona, estado);
                iterator.remove();
            }
        }
    }

    // actualiza la informacion de pasajero
    private void actualizarPasajeroSubida(Persona persona) {
        persona.ascensor = this;
        System.out.println(String.format("%s subió al ascensor %s en el piso %s", persona.id, _id, _ubicacion));
    }

    private void actualizarDestino(Persona persona, EstadoAscensor estado) {
        if ((estado == EstadoAscensor.SUBIENDO && persona.destino > _destino) ||
                (estado == EstadoAscensor.BAJANDO && persona.destino < _destino) ||
                estado == EstadoAscensor.DETENIDO) {
            _destino = persona.destino;
        }
    }

    public Boolean puedeSubir(Persona persona, EstadoAscensor estado) {
        if ((pasajeros.size() + 1) >= _cantidadPersonasMaximas) {
            return false;
        }
        if ((persona.peso + _pesoActualkg) > _pesoMaximokg) {
            return false;
        }
        switch (estado) {
            case DETENIDO:
                return persona.ubicacion == _ubicacion;
            case SUBIENDO:
                return persona.ubicacion == _ubicacion && persona.destino > this._ubicacion;
            case BAJANDO:
                return persona.ubicacion == _ubicacion && persona.destino < this._ubicacion;
            default:
                return false;
        }
    }

// revisa denuevo los pasajeros esperando y levante el que este más cerca
// cuando un ascensor es asignado, cambia la variable ascensor de persona
// asi sabemos que hay un ascenor yendo a levantar esa persona
private void buscarPasajerosOtrosPisos() {
    if (!Planificador.GetPlanificador().esperandoAscensor.isEmpty()) {
        for (int i = 0; i < Planificador.GetPlanificador().esperandoAscensor.size(); i++) {
            Persona pasajeroDestino = Planificador.GetPlanificador().esperandoAscensor.get(i);
            if (pasajeroDestino.ascensor == null) {
                this._destino = pasajeroDestino.ubicacion;
                pasajeroDestino.ascensor = this;
                if (pasajeroDestino.ubicacion > this._ubicacion) { // cambio el estado del ascensor
                    this.estado = EstadoAscensor.SUBIENDO;
                } else {
                    this.estado = EstadoAscensor.BAJANDO;
                }
                break;
            }
        }
    }
}

    private void bajarPasajeros() {
        if (pasajeros.isEmpty()) {
            return;
        }

        Iterator<Persona> it = pasajeros.iterator();
        while (it.hasNext()) {
            Persona persona = it.next();
            if (persona.destino == _ubicacion) {
                actualizarPasajeroBajada(persona);
                _pesoActualkg -= persona.peso;
                System.out.println(persona.id + " Bajo del ascensor " + _id + " piso " + _ubicacion);
                it.remove();
            }
        }
    }

    // escribe que ascensor llevo la persona, cuantos ticks demoro
    // de que pisos se movio
    private void actualizarPasajeroBajada(Persona persona) {
        System.out.println("el pasajero " + persona.id + " bajó del ascensor " + _id + " en el piso " + _ubicacion
                + " del " + persona.ubicacion + " y demoro " + (Planificador.GetPlanificador().tick - persona.tick)
                + " ticks");
    }

    // el ascensor se mueve a su destino
    private void irDestino() {
        String text = ("Movimiento Ascensor: " + _id + " Piso: " + this._ubicacion + " Destino: " + _destino);
        if (_destino != _ubicacion) {
            if (_destino > _ubicacion) {
                _ubicacion++;
            } else {
                _ubicacion--;
            }
        }
        System.out.println(text + " --> Nuevo Piso: " + this._ubicacion);
    }
}
