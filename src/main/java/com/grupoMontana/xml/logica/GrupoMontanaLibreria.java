package com.grupoMontana.xml.logica;

import java.io.File;
import java.util.List;

import jakarta.xml.bind.*; // JAXB
import com.grupoMontana.xml.modelo.*; // CLASES CREADAS POR MAVEN.


public class GrupoMontanaLibreria {
    //VARIABLES PARA GUARDAR TODOS LOS DATOS DEL GRUPO DE MONTAÑA
    private GrupoMontanaData datos;
    //ARCHIVO CON EL QUE VAMOS A TRABAJAR
    private File archivoXML = new File("src/main/resources/grupoMontanaData.xml");

    //CONSTRUCTOR
    public GrupoMontanaLibreria() {
        super();
    }

    // METODO PARA LEER EL XML Y CONVERTIRLO EN OBJETOS JAVA.
    public void cargarDatos() throws JAXBException {
        // PRIMERO QUE EL ARCHIVO EXISTA
        if (!this.archivoXML.exists()) {
            throw new JAXBException("No se encuentra el fichero XML");
        }
        //CONTEXTO
        JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
        //UNMARSHALLER
        Unmarshaller lector = contexto.createUnmarshaller();
        //LEER Y GUARDAR EN LA VARIABLE DE CLASE.
        this.datos = (GrupoMontanaData) lector.unmarshal(this.archivoXML);
        System.out.println("✅ Datos cargados correctamente en memoria.");
    }

    /**
     *
     */
    public void guardarDatos() {
        try {
            //Preparamos el contexto JAXB
            JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
            //Creamos el Marshaller
            Marshaller marshaller = contexto.createMarshaller();
            //Le decimos que ponga saltos de línea y sangría para que el XML se lea bien
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Escribimos el objeto 'datos' en 'archivoXML'
            marshaller.marshal(datos, archivoXML);
            System.out.println("Cambios guardados en el fichero XML.");
        } catch (JAXBException e) {
            System.out.println("Error crítico guardando datos: " + e.getMessage());
        }
    }
    //METODOS

    /**
     * Busca un senderista por su ID usando un bucle clásico.
     *
     * @param idBusqueda El ID que queremos encontrar (ej: "S-001")
     * @return El objeto TipoSenderista si existe, o null si no se encuentra.
     */
    public TipoSenderista buscarSenderistaPorId(String idBusqueda) {
        //Obtenemos la lista completa de senderistas
        List<TipoSenderista> listaSenderistas = datos.getListadoSenderistas().getSenderista();
        //Iteramos por la lista
        for (TipoSenderista senderista : listaSenderistas) {
            //Comprobamos si el ID coincide
            if (senderista.getId().equals(idBusqueda)) {
                return senderista; // ¡Encontrado! Devolvemos el objeto y salimos del método.
            }
        }
        //Si termina el bucle y no hemos salido, es que no estaba.
        return null;
    }

    public TipoSenderista buscarSenderistaPorNombre(String nombreBusqueda) {
        // 1. Recorremos toda la lista
        for (TipoSenderista senderista : datos.getListadoSenderistas().getSenderista()) {
            // 2. Comparamos el nombre (ignorando mayúsculas/minúsculas)
            // Usamos trim() para quitar espacios sobrantes por si acaso
            if (senderista.getNombre().trim().equalsIgnoreCase(nombreBusqueda.trim())) {
                return senderista; // ¡Encontrado! Devolvemos el objeto entero
            }
        }
        return null; // No existe
    }

    // MEDIA EDAD GRUPO MONTAÑA
    public double edadMediaSenderistas() {
        double sumaEdades = 0;
        List<TipoSenderista> listaSenderistas = datos.getListadoSenderistas().getSenderista();
        for (TipoSenderista senderista : listaSenderistas) {
            sumaEdades += senderista.getEdad();
        }
        return sumaEdades / listaSenderistas.size();
    }


    public TipoActividad actividadMasPopular() {
        //VARIABLE PARA LA ACTIVIDAD CON MAS PARTICIPANTES
        TipoActividad actividadGanadora = null;
        int recordParticipantes = -1; // Empezamos bajo
        //RECORRER LAS ACTIVIDADES
        for (TipoActividad actividad : datos.getRegistroActividades().getActividad()) {
            //TAMAÑO DE LA LISTA DE PARTICIPANTES DENTRO DE LA ACTIVIDAD
            int participantesActuales = actividad.getParticipantes().getIdSenderista().size();
            //IR GUARDANDO LA ACTIVIDAD CON MAS PARTICIPANTES
            if (participantesActuales > recordParticipantes) {
                actividadGanadora = actividad;
                recordParticipantes = participantesActuales;
            }
        }
        // 5. Devolvemos la actividad ganadora al terminar
        return actividadGanadora;
    }

    public void altaSenderista(TipoSenderista nuevoSenderista) {
        if (nuevoSenderista == null) {
            System.out.println("Error: Datos vacíos.");
            return;
        }
        //GUARDADO EN RAM
        datos.getListadoSenderistas().getSenderista().add(nuevoSenderista);

        //GUARDADO EN DISCO DURO (PERSISTENCIA)
        this.guardarDatos();
        System.out.println("Senderista añadido y guardado.");
    }

    public void crearActividad(TipoActividad nuevaActividad) {
        if (nuevaActividad == null) {
            System.out.println("Datos de actividad vacios");
            return;
        }
        //GUARDADO EN RAM
        datos.getRegistroActividades().getActividad().add(nuevaActividad);
        //GUARDADO EN DISCO DURO (PERSISTENCIA)
        this.guardarDatos();
        System.out.println("Actividad añadida y guardada.");
    }

    //BORRAR SENDERISTA POR ID
    public boolean bajaSenderista(String idParaBorrar) {
        // LISTA DE SENDERISTAS
        List<TipoSenderista> lista = datos.getListadoSenderistas().getSenderista();
        // A QUIEN NOS VAMOS A CARGAR
        TipoSenderista encontrado = null;

        // RECORRER LA LISTA
        for (TipoSenderista s : lista) {
            if (s.getId().trim().equalsIgnoreCase(idParaBorrar.trim())) {
                encontrado = s;
                break;
            }
        }

        // SI ENCONTRAMOS, BORRAMOS, GUARDAMOS PARA QUE PERSISTA
        if (encontrado != null) {
            lista.remove(encontrado);
            this.guardarDatos();
            return true;              // EXITO
        } else {
            return false;             //NO EXISTE
        }
    }


    //METODOS PARA PROBAR
    // Método para obtener la lista completa de actividades
    public List<TipoActividad> getListaActividades() {
        return datos.getRegistroActividades().getActividad();
    }

    // Método para obtener la lista completa de senderistas
    public List<TipoSenderista> getListaSenderistas() {
        return datos.getListadoSenderistas().getSenderista();
    }



//    public TipoSenderista senderistaMasActivo(){
//        int totalActividades = datos.getRegistroActividades().getActividad().size();
//        //Map para guardar id de senderista y el numero de actividades donde aparece
//        Map<String, Integer> recuento = new HashMap<>();
//        //FOR ANIDADOS, EL PRIMERO RECORRO LAS ACTIVIDADES Y LUEGO MIRO QUIEN ESTA PRESENTE EN CADA ACTIVIDAD.
//        for(TipoActividad actividad : datos.getRegistroActividades().getActividad()){
//            for (String idParticipante: actividad.getParticipantes().getIdSenderista()){
//                if(recuento.containsKey(idParticipante)){
//                    //SI LO ENCONTRAMOS LE SUMAMOS 1 A LO QUE TENGAMOS
//                    recuento.put(idParticipante,recuento.get(idParticipante)+1);
//                }else {
//                    //SI NO ESTA PONEMOS 1 PORQUE ES SU PRIMERA APARICION
//                    recuento.put(idParticipante,1);
//                }
//            }
//        }
//        String idMasActivo =null;
//        int maxActividades=0;
//
//        //RECORRER EL MAP PARA VER CUANTAS TENGO POR SENDERISTA
//        for(String id: recuento.keySet()){
//            int cantidad = recuento.get(id);
//            //SI LA CANTIDAD DE ESTE ID ES MAYOR QUE MAXACTIVIDADES.
//            //maxActividades CAMBIA Y ESTE ID ES EL TOP EN ESTE MOMENTO.IdMasActivo CAMBIA
//            if(cantidad>maxActividades){
//                maxActividades=cantidad;
//                idMasActivo=id;
//            }
//        }


}
