
import java.io.*;
import java.sql.*;
import java.util.Locale;

public class Oracle2CSV {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("./OracleExport cvs example.sql servername:1531:oraclesid username password");
            System.exit(255);
        }
        String mode = args[0];
        String sqlfile = args[1];
        String conn_str = args[2];
        String conn_user = args[3];
        String conn_password = args[4];

        String delimiter = ";";
        String quoteChar = "\"";
        boolean quoteAlways = true;

        // Set Locale
        Locale.setDefault(Locale.ENGLISH);

        String decimalSeparator = "."; // TODO: get from locale
        
        /* Not working
         String dateFormat = "YYYY-MM-DD HH24:MI:SS";
         */

        String sql = "";
        try {
            sql = readFileAsString(sqlfile);
        } catch (Exception ex) {
            System.err.println("Could not open file " + sqlfile); // TODO: Add error
            System.exit(255);
        }

        // Remove ; from SQL string if it exists
        sql = sql.replaceFirst("; *$", "");

        try {
            Connection con = null;
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@" + conn_str, conn_user, conn_password);
            Statement s = con.createStatement();
            /* TODO: Not working - seems oralce JDCB thin client use locale settings from Java 
             String sql_nls_date_format = "alter session set nls_date_format='"+ dateFormat+"'";
             s.executeQuery(sql_nls_date_format);
			
             String sql_nls_decimal_separator = "alter session set nls_numeric_characters='" + decimalSeparator + "'";
             s.executeQuery(sql_nls_date_format);
             */

            //s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);

            // get result set meta data
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            for (int i = 1; i < numberOfColumns + 1; i++) {
                String value = rsMetaData.getColumnName(i);

                if (quoteAlways || value.indexOf(quoteChar) > -1 || value.indexOf(delimiter) > -1) {
                    System.out.print(quoteChar + value.replace(quoteChar, quoteChar + quoteChar) + quoteChar);
                } else {
                    System.out.print(value);
                }

                if (i != numberOfColumns) {
                    System.out.print(delimiter);
                }
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    String value;

                    if (rs.getString(i) != null) {
                        value = rs.getString(i);

                    } else {
                        value = "";
                    }

                    if ((rsMetaData.getColumnType(i) != java.sql.Types.NUMERIC && quoteAlways) || value.indexOf(quoteChar) > -1 || value.indexOf(delimiter) > -1) {
                        System.out.print(quoteChar + value.replace(quoteChar, quoteChar + quoteChar) + quoteChar);
                    } else if (rsMetaData.getColumnType(i) == java.sql.Types.NUMERIC && value.indexOf(decimalSeparator) == 0) {
                        System.out.print('0' + value);
                    } else {
                        System.out.print(value);
                    }

                    if (i != numberOfColumns) {
                        System.out.print(delimiter);
                    }
                }
                System.out.println();

            }
            s.close();
            con.close();
            System.exit(0);

        } catch (Exception e) {
            System.err.println("NOT OK");
            e.printStackTrace();
            System.exit(255);
        }
        // TODO: Do a final block and close the connection
    }

    private static String readFileAsString(String filePath) throws java.io.IOException {
        byte[] buffer = new byte[(int) new File(filePath).length()];
        BufferedInputStream f = null;
        try {
            f = new BufferedInputStream(new FileInputStream(filePath));
            f.read(buffer);
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ignored) {
                }
            }
        }
        return new String(buffer);
    }

}
