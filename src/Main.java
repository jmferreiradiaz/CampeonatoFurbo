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

/***
 * @author  José Miguel Ferreira Díaz
 */
public class Main {

    static List<String> listadoEquipos, listadoOctavos = new ArrayList<>(1), listadoCuartos = new ArrayList<>(1),
            listadoSemiFinal = new ArrayList<>(1), listadoFinal = new ArrayList<>(1);

    static int golesA,golesB;

    static String equipoA, equipoB;

    static boolean octavosFinalizado = false, cuartosFinalizado = false, semiFinalFinalizada = false, finalFinalizada = false;

    public static void main(String[] args) {
        try{
            //Conexión BBDD
            ManagerBBDD.conectar();

            //Borrar tablas
            ManagerBBDD.EliminarTablas();
            //region Crear tablas
            String []camposEquipo = {"equipo varchar(25)", "ganados int", "empatados int", "perdidos int",
            "golesMarcados int", "golesRecibidos int"};
            ManagerBBDD.crearTabla("Equipos", camposEquipo);

            String []campos = {"equipoA varchar(25)", "equipoB varchar(25)", "golesA int", "golesB int"};
            ManagerBBDD.crearTabla("Octavos", campos);
            ManagerBBDD.crearTabla("Cuartos", campos);
            ManagerBBDD.crearTabla("Semifinales", campos);
            ManagerBBDD.crearTabla("Final", campos);
            //endregion

            //Insertar los equipos en la tabla equipos
            insertarEquiposListado();

            //Hacemos un sorteo para ver que 16 equipos van a octavos
            while(listadoOctavos.size() < 16){
                listadoOctavos.add(sacarEquipoAleatorio(listadoEquipos));
            }

            while(true){
                /***
                 * Los partidos se jugaran 1 por 1, es decir el usuario deberá pulsar 8 veces la opción de octavos hasta que los 16 equipos hayán jugado
                 * e igual con las demás opciones del menú. No sé si lo querias así o con un while dentro del menú donde se jueguen todos los partidos de golpe.
                 */
                int opc = menu();
                switch (opc) {
                    case 1:
                        if (listadoOctavos.size() == 0){
                            octavosFinalizado = true;
                        }
                        if (listadoOctavos.size() >= 2 && !octavosFinalizado){
                            //Empieza el partido
                            partido(listadoOctavos);
                            if (golesA > golesB){
                                //Añadimos al equipo ganador al listado de los cuartos.
                                listadoCuartos.add(equipoA);
                            } else {
                                //Añadimos al equipo ganador al listado de los cuartos.
                                listadoCuartos.add(equipoB);
                            }
                            insertarResultados("Octavos");
                        } else {
                            ManagerBBDD.listarTabla("Octavos");
                        }
                        break;
                    case 2:
                        if(octavosFinalizado){
                            if (listadoCuartos.size() == 0){
                                cuartosFinalizado = true;
                            }
                            if (listadoCuartos.size() >= 2 && !cuartosFinalizado){
                                partido(listadoCuartos);
                                if (golesA > golesB){
                                    //Añadimos al equipo ganador al listado de la semifinal.
                                    listadoSemiFinal.add(equipoA);
                                } else {
                                    //Añadimos al equipo ganador al listado de los semifinal.
                                    listadoSemiFinal.add(equipoB);
                                }
                                insertarResultados("Cuartos");
                            } else {
                                ManagerBBDD.listarTabla("Cuartos");
                            }
                        } else {
                            System.out.println("Todavía no se han acabado los Octavos");
                        }

                        break;
                    case 3:
                        if (cuartosFinalizado){
                            if (listadoSemiFinal.size() == 0){
                                semiFinalFinalizada = true;
                            }
                            if (listadoSemiFinal.size() >= 2 && !semiFinalFinalizada){
                                partido(listadoSemiFinal);
                                if (golesA > golesB){
                                    //Añadimos al equipo ganador al listado de la final.
                                    listadoFinal.add(equipoA);
                                } else {
                                    //Añadimos al equipo ganador al listado de la final.
                                    listadoFinal.add(equipoB);
                                }
                                insertarResultados("Semifinales");
                            } else {
                                ManagerBBDD.listarTabla("Semifinales");
                            }
                        }else {
                            System.out.println("Todavía no se han acabado los cuartos");
                        }
                        break;
                    case 4:
                        if (semiFinalFinalizada){
                            if (listadoFinal.size() >= 2 && !finalFinalizada){
                                partido(listadoFinal);
                                finalFinalizada = true;
                                insertarResultados("Final");
                            } else {
                                ManagerBBDD.listarTabla("Final");
                            }
                        }else {
                            System.out.println("Todavía no se ha acabado la Semifinal");
                        }
                        break;
                    case 5:
                        //Borramos todo el contenido de las tablas
                        ManagerBBDD.borrarTabla("Equipos", "");
                        ManagerBBDD.borrarTabla("Octavos", "");
                        ManagerBBDD.borrarTabla("Cuartos", "");
                        ManagerBBDD.borrarTabla("Semifinales", "");
                        ManagerBBDD.borrarTabla("Final", "");
                        //Volvemos a meter los listados del fichero en la tabla equipos
                        insertarEquiposListado();

                        //Reiniciamos los booleanos
                        octavosFinalizado = false;
                        cuartosFinalizado = false;
                        semiFinalFinalizada = false;
                        finalFinalizada = false;

                        //Hacemos un sorteo para ver que 16 equipos van a octavos
                        while(listadoOctavos.size() < 16){
                            listadoOctavos.add(sacarEquipoAleatorio(listadoEquipos));
                        }
                        break;
                    case 6:
                        System.exit(0);
                        break;
                    default:
                }
            }



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
        if (!octavosFinalizado){
            System.out.println("1. Iniciar Octavos");
        } else {
            System.out.println("1. Resultado Octavos");
        }
        if (!cuartosFinalizado){
            System.out.println("2. Iniciar Cuartos");
        } else {
            System.out.println("1. Resultado Cuartos");
        }
        if (!semiFinalFinalizada){
            System.out.println("3. Iniciar Semifinales");
        } else {
            System.out.println("1. Resultado Semifinal");
        }
        if (!finalFinalizada){
            System.out.println("4. Iniciar la Final");
        } else {
            System.out.println("1. Resultado Final");
        }
        System.out.println("5. Reiniciar");
        System.out.println("6. Salir");


        // Leemos la opción de teclado
        opc = sc.nextInt();

        return opc;
    }

    /***
     * Método que lee con un escaner los equipos del fichero y los mete en un ArrayList<String>
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
    private static String sacarEquipoAleatorio(List<String> listado){
        int myRand =  (int)(Math.random()*listado.size());
        String equipo = listado.get(myRand);
        listado.remove(myRand);
        //System.out.println("Equipo aleatorio = "+equipo);
        return equipo;
    }

    /***
     * Método que inserta los resultados del partido en la BBDD
     * @param tabla
     * @throws SQLException
     */
    private static void insertarResultados(String tabla) throws SQLException {
        String [] columnas = {"equipoA", "equipoB", "golesA", "golesB"};
        String [] columnasValores = {equipoA, equipoB, Integer.toString(golesA), Integer.toString(golesB)};
        ManagerBBDD.insertarTablas(tabla, columnas, columnasValores);
        ManagerBBDD.mostrarSelect(tabla,columnas);
    }

    /***
     * Método que ejecuta el algoritmo del partido entre los 2 equipos aleatorios extraidos del
     * listado
     * @param listado
     */
    private static void partido(List<String> listado) throws SQLException {

        //Reiniciamos los goles
        golesA = 0;
        golesB = 0;

        //sacamos dos equipos aleatorios del listado pasado
        equipoA = sacarEquipoAleatorio(listado);
        equipoB = sacarEquipoAleatorio(listado);

        System.out.println(equipoA+" VS "+equipoB);

        int posesionA = 50;
        int posesionB = 50;
        long start = System.currentTimeMillis();
        //El partido dura 90 segundos
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

        //Actualizamos el resultado de los equipos en la tabla equipos
        String []camposEquipo = {"ganados", "empatados", "perdidos",
                "golesMarcados", "golesRecibidos"};
        //Recogemos los datos del EquipoA
        int ganadosA = ManagerBBDD.returnSelectTablaEquipo(new String[]{"ganados"}, equipoA);
        int empatadosA = ManagerBBDD.returnSelectTablaEquipo(new String[]{"empatados"}, equipoA);
        int perdidosA = ManagerBBDD.returnSelectTablaEquipo(new String[]{"perdidos"}, equipoA);
        int golesMarcadosA = ManagerBBDD.returnSelectTablaEquipo(new String[]{"golesMarcados"}, equipoA);
        int golesRecibidosA = ManagerBBDD.returnSelectTablaEquipo(new String[]{"golesRecibidos"}, equipoA);

        //Recogemos los datos del EquipoB
        int ganadosB = ManagerBBDD.returnSelectTablaEquipo(new String[]{"ganados"}, equipoB);
        int empatadosB = ManagerBBDD.returnSelectTablaEquipo(new String[]{"empatados"}, equipoB);
        int perdidosB = ManagerBBDD.returnSelectTablaEquipo(new String[]{"perdidos"}, equipoB);
        int golesMarcadosB = ManagerBBDD.returnSelectTablaEquipo(new String[]{"golesMarcados"}, equipoB);
        int golesRecibidosB = ManagerBBDD.returnSelectTablaEquipo(new String[]{"golesRecibidos"}, equipoB);

        if (golesA > golesB){
            System.out.println("Gana "+equipoA);
            //Actualizamos el resultado del perdedor
            String []camposEquipoValuesPerdedor = {String.valueOf(ganadosB), String.valueOf(empatadosB), String.valueOf(perdidosB + 1),
                    String.valueOf(golesMarcadosB + golesB), String.valueOf(golesRecibidosB + golesA)};
            ManagerBBDD.modificarTabla("Equipos",camposEquipo,camposEquipoValuesPerdedor,"equipo LIKE '"+equipoB+"'");

            //Actualizamos el resultado del ganador
            String []camposEquipoValuesGanador = {String.valueOf(ganadosA + 1), String.valueOf(empatadosA), String.valueOf(perdidosA),
                    String.valueOf(golesMarcadosA + golesA), String.valueOf(golesRecibidosA + golesB)};
            ManagerBBDD.modificarTabla("Equipos",camposEquipo,camposEquipoValuesGanador,"equipo LIKE '"+equipoA+"'");
        } else {
            System.out.println("Gana "+equipoB);
            //Actualizamos el resultado del perdedor
            String []camposEquipoValuesPerdedor = {String.valueOf(ganadosA), String.valueOf(empatadosA), String.valueOf(perdidosA + 1),
                    String.valueOf(golesMarcadosA + golesA), String.valueOf(golesRecibidosA + golesB)};
            ManagerBBDD.modificarTabla("Equipos",camposEquipo,camposEquipoValuesPerdedor,"equipo LIKE '"+equipoB+"'");

            //Actualizamos el resultado del ganador
            String []camposEquipoValuesGanador = {String.valueOf(ganadosB + 1), String.valueOf(empatadosB), String.valueOf(perdidosB),
                    String.valueOf(golesMarcadosB + golesB), String.valueOf(golesRecibidosB + golesA)};
            ManagerBBDD.modificarTabla("Equipos",camposEquipo,camposEquipoValuesGanador,"equipo LIKE '"+equipoA+"'");
        }
    }

}