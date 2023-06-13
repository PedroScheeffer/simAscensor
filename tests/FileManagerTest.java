import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManagerTest {
    String filePath = "C:/Users/pedro/Desktop/Repos/simAscensor/tests/testFile.csv";
    
    @Test
    public void testReadCSV() {
        // Arrange 
        FileManager fileManager = new FileManager();

        List<Persona> personas = fileManager.csvToPerson(filePath, true);
        
        Persona expectedResult = new Persona(0,1000,0,5,106);
        Persona actualResult = personas.get(0);
        
        Assert.assertEquals(expectedResult.toString(), actualResult.toString());
    }
}
