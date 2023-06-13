import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class PlanificadorTest {
    String filePath = "C:\\Users\\pedro\\Desktop\\Repos\\simAscensor\\tests\\testFileVariasPersonas.csv";
    Planificador planificador = new Planificador();

    @Test
    public void testProcesarPersonas() {
        String empty = "C:/Users/pedro/Desktop/Repos/simAscensor/tests/empty.csv";
        String one_person = "C:/Users/pedro/Desktop/Repos/simAscensor/tests/one_person.csv";
        String multiple_people = "C:\\Users\\pedro\\Desktop\\Repos\\simAscensor\\tests\\multiple_people.csv";


        // Test case 1: process empty file
        List<Persona> result1 = planificador.procesarPersonas(empty, 0);
        assertEquals(0, result1.size());

        // Test case 2: process file with one person at given tick
        List<Persona> result2 = planificador.procesarPersonas(one_person, 0);
        assertEquals(1, result2.size());
        assertEquals(0, result2.get(0).tick);

        // Test case 3: process file with multiple people, some at given tick
        List<Persona> result3 = planificador.procesarPersonas(multiple_people, 3);
        assertEquals(5, result3.size());
        assertEquals(3, result3.get(0).tick);
        assertEquals(3, result3.get(1).tick);

        // Test case 4: process file with multiple people, none at given tick
        List<Persona> result4 = planificador.procesarPersonas("multiple_people.csv", 4);
        assertEquals(0, result4.size());
    }
}
