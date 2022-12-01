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
import java.util.concurrent.TimeUnit;

public class Main {

    static List<String> listadoEquipos;

    static int golesA,golesB;
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

            //Se asignan el equipo A y B
            if (listadoEquipos.size() >= 2){
                String equipoA = sacarEquipoAleatorio();
                String equipoB = sacarEquipoAleatorio();
                System.out.println(equipoA+" VS "+equipoB);
                partido(equipoA, equipoB);
            }
            //endregion
        }catch (SQLException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /***
     * Método que lee con un escaner los equipos del fichero y los mete en un ArrayLis<String>
     * @return
     * @throws FileNotFoundException
     */
    private static List<String> leerEquiposFicheros() throws FileNotFoundException {
        File fichero = new File(".\\src\\equipos.txt");
        listadoEquipos = new ArrayList<String>();
        Scanner sc = new Scanner(new FileReader(fichero));

        while (sc.hasNextLine()){
            listadoEquipos.add(sc.nextLine());
        }

        for (String equipo : listadoEquipos) {
            System.out.println(equipo);
        }

        return listadoEquipos;
    }

    /***
     * Saca un equipo aleatorio del listado de equipos;
     * @return
     */
    private static String sacarEquipoAleatorio(){
        int myRand =  (int)(Math.random()*listadoEquipos.size());
        String equipo = listadoEquipos.get(myRand);
        listadoEquipos.remove(myRand);
        System.out.println("Equipo aleatorio = "+equipo);
        return equipo;
    }

    private static void partido(String equipoA, String equipoB){
        int posesionA = 50;
        int posesionB = 50;
        long start = System.currentTimeMillis();
        long total = TimeUnit.SECONDS.toMillis(90);
        long remain;
        while ((remain = start + total - System.currentTimeMillis()) > 0 || golesA == 0 && golesB == 0) {
            try {
                int randA = (int)(Math.random()*10)+1;
                int randB = (int)(Math.random()*10)+1;

                if (randA > randB){
                    posesionA += randA;
                    posesionB -= randA;
                } else if (randA < randB) {
                    posesionA -= randB;
                    posesionB += randB;
                }

                if (posesionA >= 95){
                    golesA++;
                    posesionA = 50;
                    posesionB = 50;
                    System.out.println("El "+equipoA+ " ha marcado gol");
                } else if(posesionB >= 95){
                    golesB++;
                    posesionA = 50;
                    posesionB = 50;
                    System.out.println("El "+equipoB+ " ha marcado gol");
                }
                long reSec = TimeUnit.MILLISECONDS.toSeconds(remain);

                System.out.println("Posesión de "+equipoA+": "+posesionA+"%. Posesión de "+equipoB+": "+posesionB+"%");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}