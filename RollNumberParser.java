import java.util.ArrayList;
import java.util.List;

public class RollNumberParser {

    public static List<String> parseQuery(String query) {
        List<String> intermediateCode = new ArrayList<>();
        String[] lines = query.split("\\n");

        String tableName = "";
        int numOfAttributes = 0;
        boolean tableCreated = false;

        for (String line : lines) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens[0].equalsIgnoreCase("create")) {
                if (tokens.length != 4 || !tokens[2].matches("\\d+")) {
                    System.err.println("Invalid create table command. Format should be: create table <table_name> <num_of_attributes>");
                    return intermediateCode;
                }
                tableName = tokens[2];
                numOfAttributes = Integer.parseInt(tokens[3]);
                intermediateCode.add("create_table " + tableName);
                tableCreated = true;
            } else if (tokens[0].equalsIgnoreCase("add")) {
                if (!tableCreated) {
                    System.err.println("No table created yet. Please create a table first.");
                    return intermediateCode;
                }
                if (numOfAttributes == 0) {
                    System.err.println("No more attributes needed for table " + tableName);
                    return intermediateCode;
                }
                intermediateCode.add("add_attribute " + tableName + " " + tokens[1] + " " + tokens[2]);
                numOfAttributes--;
            } else if (tokens[0].equalsIgnoreCase("insert")) {
                if (!tableCreated) {
                    System.err.println("No table created yet. Please create a table first.");
                    return intermediateCode;
                }
                if (numOfAttributes != 0) {
                    System.err.println("Not enough attributes added for table " + tableName);
                    return intermediateCode;
                }
                intermediateCode.add("insert_into " + tableName + " (" + line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim() + ")");
            }
        }

        return intermediateCode;
    }

    public static void main(String[] args) {
        // Example usage
        String query = "create table mytab 2\n" +
                "int a\n" +
                "float b\n" +
                "insert into mytab (12,23.4)";
        List<String> intermediateCode = parseQuery(query);
        if (!intermediateCode.isEmpty()) {
            for (String code : intermediateCode) {
                System.out.println(code);
            }
        }
    }
}
