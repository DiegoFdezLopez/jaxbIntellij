package com.vista;

import com.grupoMontana.xml.modelo.TipoActividad;
import com.grupoMontana.xml.modelo.TipoSenderista;
import com.grupoMontana.xml.modelo.GrupoMontanaData;
import com.grupoMontana.xml.logica.GrupoMontanaLibreria;

// 3. IMPORTS DE JAVA (Estándar)
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // 1. Instanciamos la librería y cargamos los datos
        GrupoMontanaLibreria gestion = new GrupoMontanaLibreria();
        try {
            gestion.cargarDatos();
            System.out.println("✅ Datos cargados correctamente del XML.");
        } catch (Exception e) {
            System.out.println("❌ Error crítico cargando datos: " + e.getMessage());
            return; // Si falla la carga, cerramos el programa
        }
        Scanner sc = new Scanner(System.in);
        int opcion = -1;

        // 2. Bucle del Menú
        do {
            System.out.println("==========================================");
            System.out.println("1.Ver Edad Media de los Senderistas");
            System.out.println("2.Ver Actividad más Popular");
            System.out.println("3.Ver Senderista más Activo (Opcional)");
            System.out.println("4.Buscar Senderista por Nombre");
            System.out.println("5.Dar de alta nuevo Socio");
            System.out.println("0. Salir");
            System.out.println("==========================================");
            System.out.print("Elige una opción: ");

            opcion = sc.nextInt();


            // 3. Procesamos la opción
            switch (opcion) {
                case 1:
                    double media = gestion.edadMediaSenderistas();
                    System.out.printf(">> La edad media del club es: %.2f años.\n", media);
                    break;

                case 2:
                    TipoActividad popular = gestion.actividadMasPopular();
                    if (popular != null) {
                        System.out.println(">> La actividad más popular es: " + popular.getComentarios());
                        System.out.println(">> ID: " + popular.getId() + " | Valoracion: " + popular.getValoracion());
                    } else {
                        System.out.println(">> No hay actividades registradas.");
                    }
                    break;

                case 3:
                    // Si implementaste el DTO del senderista más activo, descomenta esto:
                    /*
                    GrupoMontanaLibreria.ResultadoEstadistica resultado = gestion.obtenerSenderistaMasActivo();
                    if (resultado != null) {
                        System.out.println(">> Senderista Top: " + resultado.senderista.getNombre());
                        System.out.println(">> Total excursiones: " + resultado.numeroExcursiones);
                    }
                    */
                    System.out.println(">> (Descomenta el código si tienes este método listo)");
                    break;
                case 4:
                    System.out.println("\nBUSCAR SENDERISTA POR NOMBRE ---");
                    System.out.print("Introduce el nombre a buscar: ");
                    sc.nextLine();
                    String nombreBuscado = sc.nextLine();

                    // Llamamos a tu método (asegúrate de haberlo creado en la librería)
                    TipoSenderista encontrado = gestion.buscarSenderistaPorNombre(nombreBuscado);

                    if (encontrado != null) {
                        System.out.println("¡ENCONTRADO!");
                        System.out.println("--------------------------------");
                        // Asegúrate de que los gets coinciden con tu clase (getNombre, getId, etc.)
                        System.out.println("Nombre: " + encontrado.getNombre());
                        System.out.println("ID:     " + encontrado.getId());
                        System.out.println("Edad:   " + encontrado.getEdad() + " años");
                        System.out.println("--------------------------------");
                    } else {
                        System.out.println("No se ha encontrado a nadie llamado '" + nombreBuscado + "'.");
                    }
                    break;

                case 5:
                    System.out.println("\n--- ALTA DE NUEVO SOCIO ---");
                    TipoSenderista nuevoSocio = new TipoSenderista();

                    // 1. Limpiamos el buffer por seguridad (por si venimos de leer un número)
                    sc.nextLine();

                    // 2. Pedimos los datos
                    System.out.print("Introduce el ID (ej: S-999): ");
                    String id = sc.nextLine();

                    System.out.print("Introduce el Nombre y Apellidos: ");
                    String nombre = sc.nextLine();

                    System.out.println("Introduzca el sexo del socio");
                    String sexo = sc.nextLine();

                    System.out.print("Introduce la Edad: ");
                    int edad = 0;
                    // Controlamos que meta un número y no letras
                    if(sc.hasNextInt()) {
                        edad = sc.nextInt();
                    } else {
                        System.out.println("⚠️ Edad no válida. Se asignará 0.");
                        sc.next(); // Limpiamos lo que escribió mal
                    }
//GRACIAS A CHATGPT DESCUBRÍ EL LIO DE LAS FECHAS EN JAVA Y XML METODO AL FINAL DE ESTA CLASE
                    System.out.print("Introduce Fecha Alta (AAAA-MM-DD): ");
                    String texto = sc.next();

                    nuevoSocio.setFechaAlta( convertirFecha(texto) );

                    // 3. Creamos el objeto 'TipoSenderista' y lo rellenamos
                    nuevoSocio.setId(id);
                    nuevoSocio.setNombre(nombre);
                    nuevoSocio.setEdad(edad);
                    nuevoSocio.setSexo(sexo);
                    // Si tu XML tiene más campos (nacionalidad, etc.), puedes ponerlos aquí también.

                    // 4. Llamamos a tu librería para que lo guarde
                    gestion.altaSenderista(nuevoSocio);
                    break;

                case 0:
                    System.out.println("Fin");
                    break;

                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        } while (opcion != 0);
        sc.close();
    }

    //DESPUES DE UNA LOCURA DE ERRORES Y NO ENTENDER, CHATGPT ME DIO ESTA SOLUCION DE LAS FECHAS EN FORMATO XML
    public static XMLGregorianCalendar convertirFecha(String fechaTexto) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaTexto);
        } catch (Exception e) {
            System.out.println("⚠️ Fecha inválida. Se usará la actual.");
            return null;
        }
    }
}