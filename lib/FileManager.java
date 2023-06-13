import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    // Reads csv files and return a string array
    public ArrayList<String[]> readCSV(String filePath) {
        String line;
        String csvSplitBy = ",";
        ArrayList<String[]> result = new ArrayList<String[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                result.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // lee un archivo csv y crea una lista con personas
    public List<Persona> csvToPerson(String filePath, boolean skipFirstLine) {
        String line;
        String csvSplitBy = ",";
        List<Persona> result = new ArrayList<Persona>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                if(skipFirstLine){
                    skipFirstLine = false;
                    continue;
                }
                String[] data = line.split(csvSplitBy);
                int[] values = new int[data.length];
                int i = 0;
                for (String string : data) {
                    values[i] = Integer.parseInt(string);
                    i++;
                }
                Persona persona = new Persona(values[0], values[1], values[2], values[3], values[4]);
                result.add(persona);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // TODO crear funciones que creen un record de lo que esta pasando 
    // tick a tick, donde se suben en que asensor donde bajan
    // cuantas personas  etc
}
