package com.vista;

import com.grupoMontana.xml.modelo.TipoActividad;
import com.grupoMontana.xml.modelo.TipoRuta;
import com.grupoMontana.xml.modelo.TipoSenderista;
import com.grupoMontana.xml.logica.GrupoMontanaLibreria;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) throws DatatypeConfigurationException {
        //Instanciamos la librería y cargamos los datos
        GrupoMontanaLibreria gestion = new GrupoMontanaLibreria();
        try {
            gestion.cargarDatos();
            System.out.println("Datos cargados correctamente del XML.");
        } catch (Exception e) {
            System.out.println("Error crítico cargando datos: " + e.getMessage());
            return; // Si falla la carga, cerramos el programa
        }
        Scanner sc = new Scanner(System.in);
        int opcion = -1;
        // 2. Bucle del Menú
        do {
            System.out.println("==========================================");
            System.out.println("            /\\            /\\           \n" +
                    "           /  \\          /  \\          \n" +
                    "      /\\  /    \\    /\\  /    \\    /\\    \n" +
                    "     /  \\/      \\  /  \\/      \\  /  \\   \n" +
                    "    /              \\/              \\  \\  \n" +
                    "   /   /\\     /\\           /\\       \\  \\ \n" +
                    "  /   /  \\   /  \\   /\\    /  \\       \\  \\\n" +
                    " /___/____\\_/____\\_/__\\__/____\\_______\\__\\");
            System.out.println("1.Ver Edad Media de los Senderistas");
            System.out.println("2.Ver Actividad más Popular");
            System.out.println("3.Buscar Senderista por Nombre");
            System.out.println("4.Dar de alta nuevo Socio");
            System.out.println("5.Crear nueva actividad");
            System.out.println("6.PRUEBA SACAR LISTA ACTIVIDADES");
            System.out.println("7.PRUEBA SACAR LISTA SENDERISTAS");
            System.out.println("8.BORRAR SENDERISTA POR ID");
            System.out.println("9.AÑADIR O ELIMINAR PARTICIPANTES EN UNA ACTIVIDAD");
            System.out.println("10.PRUEBA SACAR LISTA SENDERISTAS");
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
                        System.out.println(">> ID: " + popular.getId() + " | Valoracion: " + popular.getValoracion() +
                                " | Nombre: " + popular.getComentarios());
                    } else {
                        System.out.println(">> No hay actividades registradas.");
                    }
                    break;

                case 3:
                    System.out.println("\n BUSCAR SENDERISTA POR NOMBRE ---");
                    System.out.print("Introduce el nombre a buscar: ");
                    sc.nextLine();
                    String nombreBuscado = sc.nextLine();
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

                case 4:
                    System.out.println("\n--- ALTA DE NUEVO SOCIO ---");
                    TipoSenderista nuevoSocio = new TipoSenderista();
                    //LIMPIEZA BUFFER
                    sc.nextLine();
                    //PETICION DE DATOS
                    System.out.print("Introduce el ID (ej: S-999): ");
                    String id = sc.nextLine();
                    nuevoSocio.setId(id);
                    System.out.print("Introduce el Nombre y Apellidos: ");
                    String nombre = sc.nextLine();
                    nuevoSocio.setNombre(nombre);
                    System.out.println("Introduzca el sexo del socio");
                    String sexo = sc.nextLine();
                    nuevoSocio.setSexo(sexo);
                    System.out.print("Introduce la Edad: ");
                    int edad = Integer.parseInt(sc.nextLine());
                    nuevoSocio.setEdad(edad);
                    // 4. Llamamos a tu librería para que lo guarde
                    gestion.altaSenderista(nuevoSocio);
                    break;

                case 5: // O el número que corresponda
                    System.out.println("\n--- CREACION NUEVA ACTIVIDAD ---");
                    sc.nextLine(); // Limpieza buffer inicial

                    System.out.print("Introduce el ID de la Actividad (ej: ACT-01): ");
                    String idLeido = sc.nextLine();

                    if (gestion.buscarActividadPorId(idLeido) != null) {

                        System.out.println("ERROR: Ya existe una actividad con el ID '" + idLeido + "'.");
                        System.out.println("No se puede crear. Volviendo al menú...");
                    }
                    else {
                        TipoActividad nuevaActividad = new TipoActividad();
                        nuevaActividad.setId(idLeido); // Asignamos el ID que ya leímos y validamos

                        System.out.println("Introduzca el id de ruta (ej: R-000)");
                        String idRuta = sc.nextLine();
                        nuevaActividad.setRutaId(idRuta);

                        System.out.println("Introduzca la fecha (ej: AAAA-MM-DD)");
                        String fechaString = sc.next();
                        try {
                            XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaString);
                            nuevaActividad.setFecha(xcal);
                        } catch (Exception e) {
                            System.out.println("⚠️ Formato de fecha incorrecto. Se dejará vacío.");
                        }

                        System.out.println("\n--- DATOS DE DURACION ---");
                        System.out.print("Horas empleadas: ");
                        int horas = sc.nextInt();
                        System.out.print("Minutos (0-59): ");
                        int minutos = sc.nextInt();

                        TipoActividad.TiempoEmpleado tiempo = new TipoActividad.TiempoEmpleado();
                        tiempo.setHoras(horas);
                        tiempo.setMinutos(minutos);
                        nuevaActividad.setTiempoEmpleado(tiempo);
                        System.out.println("\n--- AÑADIR PARTICIPANTES ---");
                        TipoActividad.Participantes listaParticipantes = new TipoActividad.Participantes();

                        while (true) {
                            System.out.print("Introduce ID del Senderista (Ej- S-001 o 0 para terminar): ");
                            String idPart = sc.next();
                            if (idPart.equals("0")) {
                                break;
                            }
                            if (gestion.buscarSenderistaPorId(idPart) != null) {
                                listaParticipantes.getIdSenderista().add(idPart);
                                System.out.println("   ✅ Senderista " + idPart + " añadido correctamente.");
                            } else {
                                System.out.println("   ❌ Senderista no encontrado en la base de datos.");
                            }
                        }
                        nuevaActividad.setParticipantes(listaParticipantes);

                        System.out.print("Valoración de la actividad (1-5): ");
                        nuevaActividad.setValoracion(sc.nextInt());
                        sc.nextLine();
                        System.out.print("Comentarios: ");
                        nuevaActividad.setComentarios(sc.nextLine());
                        gestion.crearActividad(nuevaActividad);
                    }
                    break;

                case 6:
                    System.out.println("\n--- LISTADO DE ACTIVIDADES ---");

                    //COGER LISTA DE LIBRERIA
                    List<TipoActividad> misActividades = gestion.getListaActividades();
                    //EVITAR QUE ESTE VACIA
                    if (misActividades.isEmpty()) {
                        System.out.println("No hay actividades registradas todavía.");
                    } else {
                        //FOR PARA RECORRER LA LISTA
                        for (TipoActividad act : misActividades) {
                            System.out.println("------------------------------------------------");
                            System.out.println("ID Actividad: " + act.getId());
                            System.out.println("Ruta ID:      " + act.getRutaId());
                            System.out.println("Fecha:        " + act.getFecha());
                            System.out.println("Comentario:   " + act.getComentarios());
                            System.out.println("Valoración:   " + act.getValoracion() + "/5");

                            // MOSTRAR EL TIEMPO
                            if (act.getTiempoEmpleado() != null) {
                                System.out.println("Duración:     " + act.getTiempoEmpleado().getHoras() + "h "
                                        + act.getTiempoEmpleado().getMinutos() + "m");
                            }

                            //MOSTRAR PARTICIPANTES (LISTA)
                            System.out.print("Participantes: ");
                            if (act.getParticipantes() != null && !act.getParticipantes().getIdSenderista().isEmpty()) {
                                //BUSCAR DENTRO DE LA SUB LISTA ID SENDERISTAS
                                for (String idPart : act.getParticipantes().getIdSenderista()) {
                                    System.out.print("[" + idPart + "] ");
                                }
                                System.out.println();
                            } else {
                                System.out.println("(Ninguno)");
                            }
                        }
                    }
                    break;
                case 7:
                    List<TipoSenderista> listaSenderistas = gestion.getListaSenderistas();
                    if (listaSenderistas.isEmpty()) {
                        System.out.println("No hay actividades registradas todavía.");
                    } else {
                        //FOR PARA RECORRER LA LISTA
                        for (TipoSenderista senderista : listaSenderistas) {
                            System.out.println("------------------------------------------------");
                            System.out.println("ID Senderista: " + senderista.getId());
                            System.out.println("Nombre:        " + senderista.getNombre());
                            System.out.println("Sexo:          " + senderista.getSexo());
                            System.out.println("Edad:          " + senderista.getEdad());
                        }
                    }
                    break;
                case 8:
                    System.out.println("\n--- BAJA DE SENDERISTA ---");
                    // PEDIMOS ID QUE VAMOS A BUSCAR Y BORRAR
                    System.out.print("Introduce el ID del senderista a eliminar: (Ej: S-000)");
                    String idBorrar = sc.next();
                    // POR SER UN BORRADO PERMANENTE PREGUNTAMOS CONFIRMACION
                    System.out.print("¿Estás seguro? Se borrará permanentemente (S/N): ");
                    String confirmacion = sc.next();

                    if (confirmacion.equalsIgnoreCase("S")) {
                        //LLAMDA AL METODO DE LIBRERIA
                        boolean eliminado = gestion.bajaSenderista(idBorrar);
                        if (eliminado) {
                            System.out.println("Senderista eliminado y cambios guardados.");
                        } else {
                            System.out.println("No se encontró ningún senderista con el ID: " + idBorrar);
                        }
                    } else {
                        System.out.println("Operación cancelada.");
                    }
                    break;
                case 9:
                    System.out.println("\n--- GESTIONAR PARTICIPANTES ---");
                    System.out.print("Introduce el ID de la Actividad (ej:ACT-00: ");
                    String idAct = sc.next();

                    // 1. Buscamos la actividad (Usamos tu método de la librería)
                    TipoActividad actEdicion = gestion.buscarActividadPorId(idAct);

                    if (actEdicion == null) {
                        System.out.println("Error: Actividad no encontrada.");
                        break;
                    }

                    boolean editando = true;
                    while (editando) {
                        System.out.println("\nActividad: " + actEdicion.getId());
                        System.out.println("1. Añadir Participante | 2. Eliminar Participante | 0. Salir");
                        int op = sc.nextInt();
                        if (op == 0) break;
                        System.out.print("Introduce ID Senderista (Ej:S-000: ");
                        String idSend = sc.next();
                        // COMPROBACIÓN: ¿Existe el socio? (Igual que hacíamos en el Case 6)
                        if (gestion.buscarSenderistaPorId(idSend) == null) {
                            System.out.println("Error: Ese senderista no existe en la base de datos.");
                            continue;
                        }

                        if (op == 1) {
                            if (gestion.agregarParticipante(actEdicion, idSend)) {
                                System.out.println("enderista añadido correctamente.");
                            } else {
                                System.out.println("No se pudo añadir (probablemente ya estaba inscrito).");
                            }
                        } else if (op == 2) {
                            // Llamamos al método booleano
                            if (gestion.eliminarParticipante(actEdicion, idSend)) {
                                System.out.println("Senderista eliminado de la actividad.");
                            } else {
                                System.out.println("No se pudo eliminar (no estaba en la lista).");
                            }
                        }
                    }
                    break;
                case 10:
                    List<TipoRuta> listaRutas = gestion.getListaRutas();
                    if (listaRutas.isEmpty()) {
                        System.out.println("No hay actividades registradas todavía.");
                    } else {
                        //FOR PARA RECORRER LA LISTA
                        for (TipoRuta ruta : listaRutas) {
                            System.out.println("------------------------------------------------");
                            System.out.println("ID ruta:            " + ruta.getId());
                            System.out.println("Nombre:             " + ruta.getNombre());
                            System.out.println("Distancia:          " + ruta.getDistanciaKm());
                            System.out.println("Desnivel positivo:  " + ruta.getDesnivelPositivo());
                            System.out.println("Dificultar:         91" + ruta.getDificultad());
                        }
                    }
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


}