import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.BufferedWriter;


public class DBEngine {
    private Map<String, Table> tables;

    public DBEngine() {
        this.tables = new HashMap<>();
    }

    public void createTable(String tableName) {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new Table(tableName));
            System.out.println("Table '" + tableName + "' created successfully.");
        } else {
            System.out.println("Table '" + tableName + "' already exists.");
        }
    }

    public void addAttribute(String tableName, String attrType, String attrName) {
        Table table = tables.get(tableName);
        if (table != null) {
            table.addAttribute(attrType, attrName);
            System.out.println("Attribute '" + attrName + "' of type '" + attrType + "' added to table '" + tableName + "'.");
        } else {
            System.err.println("Table '" + tableName + "' does not exist.");
        }
    }

    public void insertInto(String tableName, Object[] dataRow) {
        Table table = tables.get(tableName);
        if (table != null) {
            table.insertRow(dataRow);
            System.out.println("Data inserted into table '" + tableName + "'.");
        } else {
            System.err.println("Table '" + tableName + "' does not exist.");
        }
    }

    public Table loadTable(String tableName) {
        return tables.get(tableName);
    }

    public void saveTable(Table table) {
        if (table != null) {
            System.out.println("Table '" + table.getTableName() + "' saved successfully.");
        } else {
            System.err.println("Table is null. Cannot save.");
        }
    }

    // public static void main(String[] args) {
    //     DBEngine dbEngine = new DBEngine();

    //     // Execute function call sequence
    //     dbEngine.createTable("mytab");
    //     Table t = dbEngine.loadTable("mytab");
    //     dbEngine.addAttribute(t.getTableName(), "int", "a");
    //     dbEngine.addAttribute(t.getTableName(), "float", "b");
    //     dbEngine.saveTable(t);
    //     t = dbEngine.loadTable("mytab");
    //     dbEngine.insertInto(t.getTableName(), new Object[]{12, 23.4});
    //     dbEngine.saveTable(t);

    //     // Draw table
    //     t.drawTable();

    // }
    public void parseInputFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                if (tokens[0].equalsIgnoreCase("create_table")) {
                    createTable(tokens[1]);
                } else if (tokens[0].equalsIgnoreCase("add_attribute")) {
                    addAttribute(tokens[1], tokens[2], tokens[3]);
                } else if (tokens[0].equalsIgnoreCase("insert_into")) {
                    Table t = loadTable(tokens[1]);
                    if (t != null) {
                        String[] data = line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(",");
                        insertInto(t.getTableName(), data);
                        saveTable(t);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    

    public static void main(String[] args) {
        DBEngine dbEngine = new DBEngine();
        dbEngine.parseInputFile("output.txt");

        // Draw tables
        for (Table t : dbEngine.tables.values()) {
            t.drawTable();
            t.drawTableToFile();
        }
    }
}

class Table {
    private String tableName;
    private List<String> attributes;
    private List<Object[]> data;

    public Table(String tableName) {
        this.tableName = tableName;
        this.attributes = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public void addAttribute(String attrType, String attrName) {
        attributes.add(attrName + " " + attrType);
    }

    public void insertRow(Object[] dataRow) {
        data.add(dataRow);
    }

    public String getTableName() {
        return tableName;
    }



    public void drawTableToFile() {
        int maxAttrLength = 0;
        for (String attr : attributes) {
            maxAttrLength = Math.max(maxAttrLength, attr.length());
        }
        System.out.println("Writing to file: DBEngine.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("DBEngine.txt", true))) {
            System.out.println("Writing to file: DBEngine.txt");
            writer.write("----- " + tableName + " -----\n");
            writer.write("--------------------------------------------\n");
            writer.write(String.format("|%-"+ (maxAttrLength + 5) +"s|%-7s|%-7s|\n", "Attribute", "Type", "Data"));
            writer.write("--------------------------------------------\n");
            for (Object[] row : data) {
                for (int i = 0; i < attributes.size(); i++) {
                    String[] parts = attributes.get(i).split(" ");
                    String value = (i < row.length) ? row[i].toString() : "";
                    writer.write(String.format("|%-"+ (maxAttrLength + 5) +"s|%-7s|%-7s|\n", parts[0], parts[1], value));
                }
                writer.write("--------------------------------------------\n");
            }
            writer.write("\n"); // Add a newline between tables
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // public void drawTable() {
    //     System.out.println("Table Name: " + tableName + "\n");

    //     if (!attributes.isEmpty()) {
    //         int numColumns = attributes.size();
    //         int[] columnWidths = new int[numColumns];
    //         for (int i = 0; i < numColumns; i++) {
    //             columnWidths[i] = attributes.get(i).length();
    //             for (Object[] row : data) {
    //                 String value = row[i].toString();
    //                 if (value.length() > columnWidths[i]) {
    //                     columnWidths[i] = value.length();
    //                 }
    //             }
    //         }

    //         // Print header
    //         printRowLine(columnWidths);
    //         printRow(attributes.toArray(new String[0]), columnWidths);
    //         printRowLine(columnWidths);

    //         // Print data
    //         for (Object[] row : data) {
    //             printRow(row, columnWidths);
    //         }

    //         // Print footer
    //         printRowLine(columnWidths);
    //     }
    // }

    // private void printRow(Object[] rowData, int[] columnWidths) {
    //     System.out.print("|");
    //     for (int i = 0; i < rowData.length; i++) {
    //         String value = rowData[i].toString();
    //         System.out.printf(" %" + columnWidths[i] + "s |", value);
    //     }
    //     System.out.println();
    // }

    // private void printRowLine(int[] columnWidths) {
    //     System.out.print("-");
    //     for (int width : columnWidths) {
    //         for (int i = 0; i < width + 2; i++) {
    //             System.out.print("-");
    //         }
    //         System.out.print("-");
    //     }
    //     System.out.println();
    // }

    public void drawTable() {
        int maxAttrLength = 0;
        for (String attr : attributes) {
            maxAttrLength = Math.max(maxAttrLength, attr.length());
        }
    
        System.out.println("----- " + tableName + " -----");
        System.out.println("--------------------------------------------");
        System.out.printf("|%-"+ (maxAttrLength + 5) +"s|%-7s|%-7s|\n", "Attribute", "Type", "Data");
        System.out.println("--------------------------------------------");
        for (Object[] row : data) {
            for (int i = 0; i < attributes.size(); i++) {
                String[] parts = attributes.get(i).split(" ");
                String value = (i < row.length) ? row[i].toString() : "";
                System.out.printf("|%-"+ (maxAttrLength + 5) +"s|%-7s|%-7s|\n", parts[0], parts[1], value);
            }
            System.out.println("--------------------------------------------");
        }
    }
    
}
