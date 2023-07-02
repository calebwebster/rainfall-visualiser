package au.calebwebster.rainfallvisualiser;

import java.util.ArrayList;

public class RainfallDataset {
    
    private final String filename;
    private final String productCode;
    private final ArrayList<RainfallRecord> records;
    
    public RainfallDataset(String filename, String productCode, String stationNumber) {
        this.filename = filename;
        this.productCode = productCode;
        this.records = new ArrayList<>();
    }
    
    /**
     * Creates a new data record and add it to the model.
     * @param year year.
     * @param month month.
     * @param totalRainfall total rainfall.
     * @param minRainfall minimum rainfall.
     * @param maxRainfall maximum rainfall.
     */
    public void addRecord(int year, int month, double totalRainfall, double minRainfall, double maxRainfall) {
        RainfallRecord record = new RainfallRecord(year, month, totalRainfall, minRainfall, maxRainfall);
        records.add(record);
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public ArrayList<RainfallRecord> getRecords() {
        return records;
    }
    
    public String toString() {
        return filename;
    }
    
}
