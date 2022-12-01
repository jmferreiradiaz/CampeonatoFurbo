import java.sql.*;

public class ManagerBBDD {
    private static String servidor = "jdbc:mysql://dns11036.phdns11.es/ad2223_jmferreira";
    private static Connection connection;
    private static Statement st = null;

    /***
     * Método que conecta con la base de datos
     * @throws SQLException
     */

    public static void conectar() throws SQLException {
        try{
            connection = DriverManager.getConnection(servidor,"ad2223_jmferreira","jmferdia623d");
            if (connection != null) {
                st = connection.createStatement();
                System.out.println("Conectado");
                System.out.println(st.toString());
            }
        } catch (SQLException e) {
            System.out.println("No se pudo conectar a la base de datos");
            e.printStackTrace();
        }
    }

    public static void crearTabla(String tabla, String []campos) throws SQLException {
        String sql = "CREATE TABLE "+tabla+"(";

        for(int i = 0; i < campos.length; i++){
            if (i == campos.length - 1){
                sql += campos[i];
            } else {
                sql += campos[i] + ",";
            }
        }
        sql += ")";
        System.out.println(sql);
        st.executeUpdate(sql);
    }

    public static void borrarTablas() throws SQLException {
        st.executeUpdate("DROP TABLE Equipos, Octavos, Cuartos, Final");
    }

    public static void insertarTablas(String tabla, String[] campos, String[] camposValores) throws SQLException {
        String sql = "INSERT INTO "+tabla+"(";

        for(int i = 0; i < campos.length; i++){
            if (i == campos.length - 1){
                sql += campos[i];
            } else {
                sql += campos[i] + ",";
            }
        }
        sql += ")";
        System.out.println(sql);
        st.executeUpdate(sql);
    }
}
