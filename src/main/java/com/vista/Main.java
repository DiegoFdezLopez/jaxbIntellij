package com.vista;

import com.grupoMontana.xml.modelo.TipoActividad;
import com.grupoMontana.xml.modelo.TipoRuta;
import com.grupoMontana.xml.modelo.TipoSenderista;
import logica.GrupoMontanaLibreria;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //Creacion obj libreria
        GrupoMontanaLibreria gestion = new GrupoMontanaLibreria();

        //CARGA DE DATOS
        try {
            gestion.cargarDatos();
            System.err.println("Datos cargados correctamente del XML.");
        } catch (Exception e) {
            System.out.println("Error critico cargando datos: " + e.getMessage());
            return; // Si falla la carga se cierra el programa
        }

        Scanner sc = new Scanner(System.in);
        int opcion = -1;

        //MENU
        do {
            System.out.println("\n=============================================");
            System.out.println("      GESTION CLUB DE MONTANA      ");
            System.out.println("===============================================");
            System.out.println("1. Ver Edad Media de los Senderistas");
            System.out.println("2. Ver Actividad mas Popular");
            System.out.println("3. Buscar Senderista por Nombre");
            System.out.println("4. Alta de Nuevo Socio");
            System.out.println("5. Crear Nueva Actividad");
            System.out.println("6. Listar Todas las Actividades");
            System.out.println("7. Listar Senderistas");
            System.out.println("8. Baja de Senderista");
            System.out.println("9. Gestionar Participantes en Actividad");
            System.out.println("10.Listar Catalogo de Rutas");
            System.out.println("0. Salir");
            System.out.println("===============================================");
            System.out.println("Elige una opcion: ");

            // Control de errores al leer entero
            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    System.out.println("\n --- LA EDAD MEDIA DE LOS SENDERISTAS ---");
                    double media = gestion.edadMediaSenderistas();
                    //AYUDA DE IA PARA EL FORMATO DE LA MEDIA Y LOS DECIMALES
                    System.out.printf("La edad media del club es: %.2f años.", media);
                    break;

                case 2:
                    System.out.println("\n--- ACTIVIDAD MAS POPULAR ---");
                    TipoActividad popular = gestion.actividadMasPopular();
                    if (popular != null) {
                        System.out.println("La actividad mas popular fue la ruta a: " + popular.getNombreRuta());
                        System.out.println("Fecha: " + popular.getFecha());
                        System.out.println("Comentario: " + popular.getComentarios());
                    } else {
                        System.out.println("No hay actividades con participantes.");
                    }
                    break;

                case 3:
                    System.out.println("\n--- BUSCAR SENDERISTA POR NOMBRE ---");
                    System.out.print("Introduce el nombre: ");
                    String nombreBuscado = sc.nextLine();

                    TipoSenderista encontrado = gestion.buscarSenderistaPorNombre(nombreBuscado);

                    if (encontrado != null) {
                        System.out.println("ENCONTRADO:");
                        System.out.println("Nombre: " + encontrado.getNombre());
                        System.out.println("Email:  " + encontrado.getEmail());
                        System.out.println("Edad:   " + encontrado.getEdad() + " años");
                    } else {
                        System.out.println("No se ha encontrado a nadie llamado '" + nombreBuscado + "'.");
                    }
                    break;

                case 4:
                    System.out.println("\n--- ALTA DE NUEVO SOCIO ---");
                    TipoSenderista nuevoSocio = new TipoSenderista();

                    System.out.print("Introduce el Email: ");
                    String email = sc.nextLine();

                    // Comprobamos si ya existe antes de seguir
                    if (gestion.buscarSenderistaPorEmail(email) != null) {
                        System.out.println("Error: Ya existe un socio con ese email.");
                        break;
                    }
                    nuevoSocio.setEmail(email);

                    System.out.print("Nombre y Apellidos: ");
                    nuevoSocio.setNombre(sc.nextLine());

                    System.out.print("Sexo (HOMBRE/MUJER): ");
                    nuevoSocio.setSexo(sc.nextLine().toUpperCase());

                    System.out.print("Edad: ");
                    try {
                        nuevoSocio.setEdad(Integer.parseInt(sc.nextLine()));
                        gestion.altaSenderista(nuevoSocio); // Guardar
                        System.out.println("Socio registrado correctamente.");
                    } catch (Exception e) {
                        System.out.println("Error en el formato de edad. Cancelando alta.");
                    }
                    break;

                case 5:
                    System.out.println("\\n--- CREAR NUEVA ACTIVIDAD ---");

                    TipoActividad nuevaActividad = new TipoActividad();

                    //ELEGIR RUTA POR NOMBRE
                    System.out.print("Nombre de la Ruta realizada: ");
                    String nombreRuta = sc.nextLine();

                    //VALIDAR QUE LA RUTA EXISTE
                    if (gestion.buscarRutaPorNombre(nombreRuta) == null) {
                        System.out.println("Esa ruta no existe en el catalogo. Creala antes o revisa el nombre.");
                        break;
                    }
                    nuevaActividad.setNombreRuta(nombreRuta);

                    //FECHA AYUDA DE LA IA PARA EL TEMA DE GREGORIANCALENDAR
                    System.out.print("Fecha (AAAA-MM-DD): ");
                    String fechaString = sc.nextLine();
                    try {
                        XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaString);
                        nuevaActividad.setFecha(xcal);
                    } catch (Exception e) {
                        System.out.println("Formato fecha incorrecto. Se dejara vacia.");
                    }

                    //TIEMPO
                    System.out.print("Horas empleadas: ");
                    int horas = Integer.parseInt(sc.nextLine());
                    System.out.print("Minutos: ");
                    int minutos = Integer.parseInt(sc.nextLine());

                    TipoActividad.TiempoEmpleado tiempo = new TipoActividad.TiempoEmpleado();
                    tiempo.setHoras(horas);
                    tiempo.setMinutos(minutos);
                    nuevaActividad.setTiempoEmpleado(tiempo);

                    //PARTICIPANTES
                    System.out.println("\n--- AÑADIR PARTICIPANTES ---");
                    TipoActividad.Participantes listaPart = new TipoActividad.Participantes();

                    while (true) {
                        System.out.print("Email del participante (o 'fin' para terminar): ");
                        String emailPart = sc.nextLine();
                        if (emailPart.equalsIgnoreCase("fin")) break;

                        if (gestion.buscarSenderistaPorEmail(emailPart) != null) {
                            listaPart.getEmailParticipante().add(emailPart);
                            System.out.println("Añadido.");
                        } else {
                            System.out.println("Ese email no esta registrado como socio.");
                        }
                    }
                    nuevaActividad.setParticipantes(listaPart);

                    // 5. VALORACION
                    System.out.print("Valoracion (1-5): ");
                    nuevaActividad.setValoracion(Integer.parseInt(sc.nextLine()));
                    System.out.print("Comentarios: ");
                    nuevaActividad.setComentarios(sc.nextLine());

                    gestion.crearActividad(nuevaActividad);
                    System.out.println("Actividad guardada.");
                    break;

                case 6:
                    System.out.println("\n--- LISTADO DE ACTIVIDADES ---");
                    List<TipoActividad> listaAct = gestion.getListaActividades();

                    if (listaAct.isEmpty()) {
                        System.out.println("No hay actividades.");
                    } else {
                        for (TipoActividad act : listaAct) {
                            System.out.println("------------------------------------------------");
                            System.out.println("Ruta:       " + act.getNombreRuta());
                            System.out.println("Fecha:      " + act.getFecha());
                            System.out.println("Valoracion: " + act.getValoracion() + "/5");

                            System.out.print("Participantes: ");
                            if (act.getParticipantes() != null) {
                                for (String emailParticipante : act.getParticipantes().getEmailParticipante()) {
                                    TipoSenderista senderista = gestion.buscarSenderistaPorEmail(emailParticipante);
                                    if (senderista != null) {
                                        System.out.print("[" + senderista.getNombre() + "] ");
                                    } else {
                                        System.out.print("[" + emailParticipante + "] ");
                                    }
                                }
                            }
                            System.out.println();
                            System.out.println("Comentario: " + act.getComentarios());
                        }
                    }
                    break;

                case 7:
                    System.out.println("\n--- LISTADO DE SOCIOS ---");
                    List<TipoSenderista> listaSend = gestion.getListaSenderistas();
                    for (TipoSenderista s : listaSend) {
                        System.out.println("--------------------------------");
                        System.out.println("Nombre: " + s.getNombre());
                        System.out.println("Email:  " + s.getEmail());
                        System.out.println("Edad:   " + s.getEdad());
                        System.out.println("Sexo:   " + s.getSexo());
                    }
                    break;

                case 8:
                    System.out.println("\n--- BAJA DE SENDERISTA ---");
                    System.out.print("Introduce el EMAIL del socio a borrar: ");
                    String emailBorrar = sc.nextLine();

                    System.out.print("Seguro? (S/N): ");
                    if(sc.nextLine().equalsIgnoreCase("S")) {
                        if (gestion.bajaSenderista(emailBorrar)) {
                            System.out.println("Socio eliminado correctamente.");
                        } else {
                            System.out.println("No se encontro ese email.");
                        }
                    }
                    break;


                // PROPUEST IA PARA LISTAR LAS ACTIVIDADES CON UN NUMERO PARA ELEGIR.
                case 9:
                    System.out.println("\n--- GESTIONAR PARTICIPANTES ---");
                    List<TipoActividad> todas = gestion.getListaActividades();
                    if(todas.isEmpty()) {
                        System.out.println("No hay actividades para editar.");
                        break;
                    }
                    System.out.println("Selecciona que actividad quieres editar:");
                    for (int i = 0; i < todas.size(); i++) {
                        System.out.println("[" + i + "] " + todas.get(i).getNombreRuta() + " (" + todas.get(i).getFecha() + ")");
                    }

                    System.out.print("Numero de actividad: ");
                    int indice = -1;
                    try {
                        indice = Integer.parseInt(sc.nextLine());
                    } catch(Exception e) { indice = -1; }

                    if (indice >= 0 && indice < todas.size()) {
                        TipoActividad actEdicion = todas.get(indice);

                        System.out.println("EDITANDO: " + actEdicion.getNombreRuta());
                        System.out.println("1. Anadir Participante | 2. Eliminar Participante | 0. Salir");
                        int subOp = Integer.parseInt(sc.nextLine());

                        if (subOp == 0) break;

                        System.out.print("Email del socio: ");
                        String mailSocio = sc.nextLine();

                        // Verificar que el socio existe antes de intentar nada
                        if (gestion.buscarSenderistaPorEmail(mailSocio) == null) {
                            System.out.println("Ese socio no existe en la base de datos.");
                            break;
                        }

                        if (subOp == 1) {
                            if (gestion.agregarParticipante(actEdicion, mailSocio)) {
                                System.out.println("Anadido a la actividad.");
                            } else {
                                System.out.println("Ya estaba inscrito.");
                            }
                        } else if (subOp == 2) {
                            if (gestion.eliminarParticipante(actEdicion, mailSocio)) {
                                System.out.println("Eliminado de la actividad.");
                            } else {
                                System.out.println("No estaba en la lista.");
                            }
                        }
                    } else {
                        System.out.println("Indice incorrecto.");
                    }
                    break;

                case 10:
                    System.out.println("\n--- CATALOGO DE RUTAS ---");
                    for (TipoRuta r : gestion.getListaRutas()) {
                        System.out.println("--------------------------------");
                        System.out.println("Ruta:       " + r.getNombre());
                        System.out.println("Distancia:  " + r.getDistanciaKm() + " km");
                        System.out.println("Dificultad: " + r.getDificultad());
                    }
                    break;

                case 0:
                    System.out.println("Saliendo... Hasta la proxima!");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}