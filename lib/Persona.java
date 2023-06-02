public class Persona {
    int tick; // tick
    int id; // id
    int ubicacion; // ubication
    int destino; // destination
    int peso; // weight
    Ascensor ascensor; // lift

    Persona(int tick, int id, int ubicacion, int destino, int peso, int ascensor){
        this.tick = tick;
        this.id = id;
        this.ubicacion = ubicacion;
        this.destino = destino;
        this.peso = peso;
        this.ascensor = null;  
    }
}
