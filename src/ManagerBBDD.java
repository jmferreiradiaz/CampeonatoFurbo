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

    /***
     * Crea las tablas con los datos pasados como parámetros
     * @param tabla
     * @param campos
     * @throws SQLException
     */
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
        st.executeUpdate(sql);
    }

    /***
     * Elimina las tablas con un DROP TABLE si existen
     * @throws SQLException
     */
    public static void EliminarTablas() throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        //Comprobamos si la Tabla Final existe
        ResultSet tables = dbm.getTables(null, null, "Final", null);
        if (tables.next()) {
            //Borramos las tablas
            st.executeUpdate("DROP TABLE Equipos, Octavos, Cuartos, Semifinales, Final");
        }
        else {
            System.out.println("No hay ninguna tabla creada todavía");
        }
    }

    /**
     * Se borran los datos de la tabla cuando se da la condición, si no se borra todo.
     * Después muestra la tabla
     * @param tabla
     * @param condicion
     * @throws SQLException
     */
    public static void borrarTabla(String tabla, String condicion) throws SQLException{
        String sql;
        if (condicion != ""){
            sql = "DELETE FROM "+tabla+" WHERE "+condicion;
        } else {
            sql = "DELETE FROM "+tabla;
        }
        st.executeUpdate(sql);
    }

    /***
     * Se insertan los datos pasados como parámetros en las tablas
     * @param tabla
     * @param campos
     * @param camposValores
     * @throws SQLException
     */
    public static void insertarTablas(String tabla, String[] campos, String[] camposValores) throws SQLException {
        String sql = "INSERT INTO "+tabla+"(";

        for(int i = 0; i < campos.length; i++){
            if (i == campos.length - 1){
                sql += campos[i];
            } else {
                sql += campos[i] + ",";
            }
        }
        sql += ") VALUES (";

        for(int i = 0; i < camposValores.length; i++){
            if (i == camposValores.length - 1){
                sql += camposValores[i];
            } else {
                sql += "'"+camposValores[i] + "',";
            }
        }
        sql += ")";
        st.executeUpdate(sql);
    }

    /**
     * Modifica las columnas de la tabla pasadas en el array campos
     * con los nuevos valores del array valores cuando se da la condición.
     * Después muestra la tabla
     * @param tabla
     * @param campos
     * @param valores
     * @param condicion
     * @throws SQLException
     */
    public static void modificarTabla(String tabla, String[]campos, String[]valores, String condicion) throws SQLException{
        String sql = "UPDATE "+tabla+" SET ";

        for(int i = 0; i < campos.length; i++){
            if (i == campos.length - 1){
                sql += campos[i]+" = "+valores[i];
            } else {
                sql += campos[i] +" = "+valores[i]+", ";
            }
        }
        sql+= " WHERE "+condicion;
        st.executeUpdate(sql);
    }

    /***
     * Método que lista todos los datos de la tabla que pasemos como parámetro
     * @param tabla
     * @throws SQLException
     */
    public static void listarTabla(String tabla) throws SQLException {
        String sql = "SELECT * FROM "+tabla;
        ResultSet rs;
        rs = st.executeQuery(sql);
        System.out.println();
        System.out.println(tabla);
        while(rs.next()){
            ResultSetMetaData md = rs.getMetaData();
            int numCol = md.getColumnCount();
            for (int i = 1; i <= numCol; i++){
                System.out.print(rs.getString(i)+"   ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /***
     * Imprime en la consola el SELECT con las columnas pasadas como parámetros
     * @param tabla
     * @param columnas
     * @throws SQLException
     */
    public static void mostrarSelect (String tabla, String[] columnas) throws SQLException{
        String sql = "SELECT * FROM "+tabla;
        ResultSet rs;
        rs = st.executeQuery(sql);
        while(rs.next()) {
            for (String columna: columnas
            ) {
                System.out.print(rs.getString(columna)+"\t");
            }
            System.out.println();
        }
    }

    /***
     * Devuelve los datos deseados de la tabla Equipos
     * @param columnas
     * @param equipo
     * @return
     * @throws SQLException
     */
    public static int returnSelectTablaEquipo (String[] columnas, String equipo) throws SQLException{
        String sql = "SELECT * FROM Equipos WHERE equipo LIKE '"+equipo+"'";
        ResultSet rs;
        String resultado = null;
        rs = st.executeQuery(sql);
        while(rs.next()) {
            for (String columna: columnas
            ) {
                resultado = rs.getString(columna);
            }
            System.out.println();
        }
        return Integer.parseInt(resultado);
    }
}
