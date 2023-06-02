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
    
}