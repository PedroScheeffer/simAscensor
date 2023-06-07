import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String path = "files/archivoEntrada.csv";
        FileManager managerFiles = new FileManager();
        
        List<Persona> listaPersonas = managerFiles.csvToPerson(path, true);
        for (Persona persona : listaPersonas) {
            System.out.println(persona.id);
        }

    }
}
