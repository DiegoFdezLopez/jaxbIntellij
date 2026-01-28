package com.vista;

import com.grupoMontana.xml.modelo.TipoActividad;
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
            System.out.println("1.Ver Edad Media de los Senderistas");
            System.out.println("2.Ver Actividad más Popular");
            System.out.println("3.Buscar Senderista por Nombre");
            System.out.println("4.Dar de alta nuevo Socio");
            System.out.println("5.Crear nueva actividad");
            System.out.println("6.PRUEBA SACAR LISTA ACTIVIDADES");
            System.out.println("7.PRUEBA SACAR LISTA SENDERISTAS");
            System.out.println("8.BORRAR SENDERISTA POR ID");
            System.out.println("0. Salir");
            System.out.println("==========================================");
            System.out.print("Elige una opción: ");
            opcion = sc.nextInt();

            // 3. Procesamos la opcion
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
                    System.out.println("\n BUSCAR SENDERISTA POR NOMBRE ---");
                    System.out.print("Introduce el nombre a buscar: ");
                    sc.nextLine();
                    String nombreBuscado = sc.nextLine();

                    //LLAMADA AL METODO
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

                case 5:
                    System.out.println("\n--- CREACION NUEVA ACTIVIDAD ---");
                    TipoActividad nuevaActividad = new TipoActividad();
                    sc.nextLine(); // Limpieza buffer inicial

                    //ID DE LA ACTIVIDAD
                    System.out.print("Introduce el ID de la Actividad (ej: ACT-01): "); // NUEVO
                    nuevaActividad.setId(sc.nextLine());

                    //ID DE LA RUTA
                    System.out.println("Introduzca el id de ruta (ej: R-000)");
                    String idRuta = sc.nextLine();
                    nuevaActividad.setRutaId(idRuta); //

                    //FECHA
                    System.out.println("Introduzca la fecha (ej: AAAA-MM-DD)");
                    String fechaString = sc.next();
                    try {
                        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaString);
                        nuevaActividad.setFecha(xcal);
                    } catch (Exception e) {
                        System.out.println("Formato de fecha incorrecto. Se dejará vacío.");
                    }

                    //DURACION
                    System.out.println("\n--- DATOS DE DURACION ---");
                    System.out.print("Horas empleadas: ");
                    int horas = sc.nextInt();
                    System.out.print("Minutos (0-59): ");
                    int minutos = sc.nextInt();
                    TipoActividad.TiempoEmpleado tiempo = new TipoActividad.TiempoEmpleado();
                    tiempo.setHoras(horas);
                    tiempo.setMinutos(minutos);
                    nuevaActividad.setTiempoEmpleado(tiempo);

                    //PARTICIPANTES
                    System.out.println("\n--- AÑADIR PARTICIPANTES ---");
                    TipoActividad.Participantes listaParticipantes = new TipoActividad.Participantes();

                    while (true) {
                        System.out.print("Introduce ID del Senderista (Ej- S-001 o 0 para terminar): ");
                        String idLeido = sc.next();

                        // SI ES CERO SALIMOS DEL BUCLE
                        if (idLeido.equals("0")) {
                            break;
                        }

                        //SI NO ES CERO BUSCAMOS EL SENDERISTA
                        if(gestion.buscarSenderistaPorId(idLeido) != null){
                            listaParticipantes.getIdSenderista().add(idLeido);
                            System.out.println("Senderista " + idLeido + " añadido correctamente.");
                        } else {
                            System.out.println("Senderista no encontrado.");
                        }
                    }
                    nuevaActividad.setParticipantes(listaParticipantes);

                    //VALORACIÓN
                    System.out.print("Valoración de la actividad (1-5): ");
                    nuevaActividad.setValoracion(sc.nextInt());
                    //LIMPIAR BUFFER
                    sc.nextLine();
                    //COMENTARIOS
                    System.out.print("Comentarios: "); // NUEVO
                    nuevaActividad.setComentarios(sc.nextLine()); // NUEVO
                    // 7. GUARDAR FINAL (Faltaba llamar a la librería)
                    gestion.crearActividad(nuevaActividad); // NUEVO

                    break;
                case 6:
                    System.out.println("\n--- 📋 LISTADO DE ACTIVIDADES ---");

                    //COGER LISTA DE LIBRERIA
                    List<TipoActividad> misActividades = gestion.getListaActividades();
                    //EVITAR QUE ESTE VACIA
                    if (misActividades.isEmpty()) {
                        System.out.println("⚠️ No hay actividades registradas todavía.");
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
                        System.out.println("⚠️ No hay actividades registradas todavía.");
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