public class Persona {
    int tick; // tick
    int id; // id
    int ubicacion; // ubication
    int destino; // destination
    int peso; // weight
    Ascensor ascensor; // lift

    public Persona(int tick, int id, int ubicacion, int destino, int peso){
        this.tick = tick;
        this.id = id;
        this.ubicacion = ubicacion;
        this.destino = destino;
        this.peso = peso;
        this.ascensor = null;  
    }
    public String toString(){
        String result = Integer.toString(tick) + Integer.toString(id) + Integer.toString(ubicacion) + Integer.toString(destino) + Integer.toString(peso);
        return result;
    }
}
