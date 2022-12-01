import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try{
            //Conexión BBDD
            ManagerBBDD.conectar();

            //Borrar tablas
            ManagerBBDD.borrarTablas();
            //region Crear tablas
            String []camposEquipo = {"id int PRIMARY KEY AUTO_INCREMENT", "equipo varchar(25)", "ganados int", "empatados int", "perdidos int",
            "golesMarcados int", "golesRecibidos int"};
            ManagerBBDD.crearTabla("Equipos", camposEquipo);

            String []camposOctavos = {"equipoA varchar(25)", "equipoB varchar(25)", "golesA int", "golesB int"};
            ManagerBBDD.crearTabla("Octavos", camposOctavos);
            ManagerBBDD.crearTabla("Cuartos", camposOctavos);
            ManagerBBDD.crearTabla("Final", camposOctavos);
            //endregion

            //region Octavos
            leerEquiposFicheros();
            //endregion
        }catch (SQLException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void leerEquiposFicheros() throws FileNotFoundException {
        File fichero = new File(".\\src\\equipos.txt");
        List<String> listadoEquipos = new ArrayList<String>();
        Scanner sc = new Scanner(new FileReader(fichero));

        while (sc.hasNext()){
            listadoEquipos.add(sc.next());
        }

        for (String equipo : listadoEquipos) {
            System.out.println(equipo);
        }
    }

}