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

    static List<String> listadoEquipos, listadoCuartos, listadoSemifinal;

    static int golesA,golesB;
    public static void main(String[] args) {
        try{
            //Conexión BBDD
            ManagerBBDD.conectar();

            //Borrar tablas
            ManagerBBDD.borrarTablas();
            //region Crear tablas
            String []camposEquipo = {"equipo varchar(25)", "ganados int", "empatados int", "perdidos int",
            "golesMarcados int", "golesRecibidos int"};
            ManagerBBDD.crearTabla("Equipos", camposEquipo);

            String []campos = {"equipoA varchar(25)", "equipoB varchar(25)", "golesA int", "golesB int"};
            ManagerBBDD.crearTabla("Octavos", campos);
            ManagerBBDD.crearTabla("Cuartos", campos);
            ManagerBBDD.crearTabla("Final", campos);
            //endregion

            //Insertar los equipos en la tabla equipos
            insertarEquiposListado();


            int opc = menu();
                 switch (opc) {
                     case 1:
                         sacarEquipoAleatorio(listadoEquipos);
                         break;
                     case 2:
                         break;
                     case 3:
                         break;
                     case 4:
                         break;
                     case 5:
                         break;
                     case 6:
                         break;
                     default:
                 }


            //region Octavos
            leerEquiposFicheros();

            //Se asignan el equipo A y B
            if (listadoEquipos.size() >= 2){
                String equipoA = sacarEquipoAleatorio();
                String equipoB = sacarEquipoAleatorio();
                System.out.println(equipoA+" VS "+equipoB);
                partido(equipoA, equipoB);
                String [] columnasOctavos = {"equipoA", "equipoB", "golesA", "golesB"};
                String [] columnasValoresOctavos = {equipoA, equipoB, Integer.toString(golesA), Integer.toString(golesB)};
                ManagerBBDD.insertarTablas("Octavos", columnasOctavos, columnasValoresOctavos);
                ManagerBBDD.mostrarSelect("Octavos",columnasOctavos);
            }
            //endregion
        }catch (SQLException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int menu() {
        // En opc guardaremos la opción seleccionada por el usuario
        int opc;
        Scanner sc = new Scanner(System.in);

        // Imprimimos el menú con las diversas opciones
        System.out.println("1. Iniciar Octavos");
        System.out.println("2. Iniciar Cuartos");
        System.out.println("3. Iniciar Semifinales");
        System.out.println("4. Iniciar la Final");
        System.out.println("5. Reiniciar");
        System.out.println("6. Salir");


        // Leemos la opción de teclado
        opc = sc.nextInt();

        return opc;
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

        return listadoEquipos;
    }

    /***
     * Lee las personas del listado, lo recorre y lo inserta en la base de datos
     * @throws SQLException
     * @throws FileNotFoundException
     */
    private static void insertarEquiposListado() throws SQLException, FileNotFoundException {
        //Leémos los equipos del fichero y las metemos en la lista
        leerEquiposFicheros();

        //Recorremos el arrayList para insertar los equipos
        for (String equipo : leerEquiposFicheros()) {
            ManagerBBDD.insertarTablas("Equipos", new String[]{"equipo", "ganados", "empatados", "perdidos", "golesMarcados", "golesRecibidos"}
                    , new String[]{equipo, String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0)});
        }

        //Listamos los datos
        ManagerBBDD.listarTabla("Equipos");
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

    private static void partido(List<String> listado){

        //sacamos dos equipos aleatorios
        String equipoA = sacarEquipoAleatorio();
        String equipoB = sacarEquipoAleatorio();

        listado.remove();

        int posesionA = 50;
        int posesionB = 50;
        long start = System.currentTimeMillis();
        long total = TimeUnit.SECONDS.toMillis(90);
        long remain;
        while ((remain = start + total - System.currentTimeMillis()) > 0 || golesA == 0 && golesB == 0 && golesA == golesB) {
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
                    System.out.println(equipoA+ " ha marcado gol");
                } else if(posesionB >= 95){
                    golesB++;
                    posesionA = 50;
                    posesionB = 50;
                    System.out.println(equipoB+ " ha marcado gol");
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