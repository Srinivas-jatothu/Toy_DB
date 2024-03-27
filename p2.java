import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class p2 {

    public static List<String> parseQuery(String query) {
        List<String> intermediateCode = new ArrayList<>();
        String[] lines = query.split("\\n");
        //print the lines
        for (String line : lines) {
            System.out.println(line);
        }

        String tableName = "";
        int numOfAttributes = 0;

        for (String line : lines) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens[0].equalsIgnoreCase("create")) {
                tableName = tokens[2];
                numOfAttributes = Integer.parseInt(tokens[3]);
                intermediateCode.add("create_table " + tableName);
            } else if (tokens[0].equalsIgnoreCase("insert")) {
                String dataRow = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
                intermediateCode.add("insert_into " + tableName + " (" + dataRow + ")");
            } else if (numOfAttributes > 0) {
                String attrType = tokens[0];
                String attrName = tokens[1];
                intermediateCode.add("add_attribute " + tableName + " " + attrType + " " + attrName);
                numOfAttributes--;
            }
        }

        return intermediateCode;
    }

    public static void main(String[] args) {
        // Example usage'
        String query = "create table mytab 4\n" +
                "double c\n" +
                "int d\n" +
                "char e\n" +
                "char d\n" +
                "insert into mytab (12,12,e,abc)\n"+
                "create table Srinivas 2\n" +               
                "char e\n" +
                "char d\n" +
                "insert into Srinivas (e,abc)";
        List<String> intermediateCode = parseQuery(query);
        // for (String code : intermediateCode) {
        //     System.out.println(code);
        // }

        try (FileWriter writer = new FileWriter("output.txt")) {
            for (String code : intermediateCode) {
                writer.write(code + "\n");
            }
            System.out.println("Output written to output.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    
    }
}
