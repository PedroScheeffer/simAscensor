import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManagerTest {
    String filePath = "files/archivoEntrada.csv";
    @Test
    public void testReadCSV() {


        ArrayList<String[]> expectedResult = new ArrayList<>();
        expectedResult.add(new String[]{"1", "John", "Doe"});
        expectedResult.add(new String[]{"2", "Jane", "Smith"});

        ArrayList<String[]> result = FileManager.readCSV(filePath);

        Assertions.assertEquals(expectedResult.size(), result.size());
        for (int i = 0; i < expectedResult.size(); i++) {
            Assertions.assertArrayEquals(expectedResult.get(i), result.get(i));
        }
    }

    @Test
    public void testCsvToPerson() {


        List<Persona> expectedResult = new ArrayList<>();
        expectedResult.add(new Persona(1, "John", "Doe"));
        expectedResult.add(new Persona(2, "Jane", "Smith"));

        List<Persona> result = FileManager.csvToPerson(filePath);

        Assertions.assertEquals(expectedResult.size(), result.size());
        for (int i = 0; i < expectedResult.size(); i++) {
            Persona expectedPersona = expectedResult.get(i);
            Persona persona = result.get(i);
            Assertions.assertEquals(expectedPersona.getId(), persona.getId());
            Assertions.assertEquals(expectedPersona.getFirstName(), persona.getFirstName());
            Assertions.assertEquals(expectedPersona.getLastName(), persona.getLastName());
        }
    }
}
