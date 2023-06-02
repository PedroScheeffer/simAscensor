public class FileManager {
    // Reads csv files and return a string array
    public static String readCSV(String filePath) {
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Persona> csvToPerson(String filePath) {
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                Persona persona = new Persona(data[0], data[1], data[2], data[3], data[4], data[5])
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
