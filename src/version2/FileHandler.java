package version2;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileHandler {
    
    /**
     * Reads a csv file and returns a list of the lines it contains.
     * @param filename name of csv file to read.
     * @return list of strings.
     * @throws IOException if file is not found.
     * @throws CsvException if there is another error with the csv.
     */
    public static List<String[]> readCSVData(String filename) throws IOException, CsvException {
        Reader reader = Files.newBufferedReader(Paths.get(filename));
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> data = csvReader.readAll();
        csvReader.close();
        return data;
    }
    
    /**
     * Writes a list of csv lines to a csv file.
     * @param filename name of csv file to write.
     * @param lines list of string arrays containing values to write.
     * @throws IOException if file is not found.
     */
    public static void writeCSVData(String filename, List<String[]> lines) throws IOException {
        
        Writer writer = Files.newBufferedWriter(Paths.get(filename));
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeAll(lines);
        csvWriter.close();
    }
    
    /**
     * Lists files in a directory.
     * @param dir directory to search.
     * @return array of file names found.
     * @throws DirectoryNotFoundException if directory does not exist.
     */
    public static String[] listFilesInDirectory(String dir) throws DirectoryNotFoundException {
        File directory = new File(dir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new DirectoryNotFoundException("Directory \"" + dir + "\" not found.");
        }
        File[] files = directory.listFiles();
        String[] filenames;
        if (files == null) {
            filenames = new String[0];
        } else {
            filenames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    filenames[i] = files[i].getName();
                }
            }
        }
        return filenames;
    }
    
}
