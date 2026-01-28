package com.grupoMontana.xml.logica;

import java.io.File;
import java.util.List;

import jakarta.xml.bind.*;
import com.grupoMontana.xml.modelo.*;

public class GrupoMontanaLibreria {
    /**
     * Datos del grupo de montaña
     */
    private GrupoMontanaData datos;
    /**
     * Archivo para cargar y guardar los datos del grupo de montaña
     */
    private File archivoXML = new File("src/main/resources/grupoMontanaData.xml");

    /**
     * Constructor sin parametros
     */
    public GrupoMontanaLibreria() {
        super();
    }

    /**
     * Carga los datos del grupo desde el fichero XML asociado y los deja en memoria en el atributo datos}.
     *
     * @throws JAXBException si el archivo no existe.
     */
    public void cargarDatos() throws JAXBException {
        if (!this.archivoXML.exists()) {
            throw new JAXBException("No se encuentra el fichero XML");
        }
        //Contexto
        JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
        //Unmarshaller
        Unmarshaller lector = contexto.createUnmarshaller();
        //Leer y guardar.
        this.datos = (GrupoMontanaData) lector.unmarshal(this.archivoXML);
        System.out.println("✅ Datos cargados correctamente en memoria.");
    }

    /**
     *
     */
    public void guardarDatos() {
        try {
            //CONTEXT JAXB
            JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
            //CREACION MARSHALLER
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //buena practica
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); //pretty
            //ESCRIBIR "DATOS" EN EL ARCHIVOXML
            marshaller.marshal(datos, archivoXML);
            System.out.println("Cambios guardados en el fichero XML.");
        } catch (JAXBException e) {
            System.out.println("Error crítico guardando datos: " + e.getMessage());
        }
    }

    //METODOS

    /**
     * Muestra un listado de todas las actividades.
     *
     * @return lista completa de todas las actividades
     */
    public List<TipoActividad> getListaActividades() {
        return datos.getRegistroActividades().getActividad();
    }

    /**
     * Muestra un listado de todos los senderistas.
     *
     * @return lista completa de todos los senderistas
     */
    public List<TipoSenderista> getListaSenderistas() {
        return datos.getListadoSenderistas().getSenderista();
    }

    public List<TipoRuta> getListaRutas(){
        return datos.getCatalogoRutas().getRuta();
    }

    /**
     * Busca un senderista por su ID usando un bucle clásico.
     *
     * @param idBusqueda El ID que queremos encontrar (ej: "S-001")
     * @return El objeto TipoSenderista si existe, o null si no se encuentra.
     */
    public TipoSenderista buscarSenderistaPorId(String idBusqueda) {
        //LISTA COMPLETA SENDERISTAS
        List<TipoSenderista> listaSenderistas = datos.getListadoSenderistas().getSenderista();
        //RECORRER LA LISTA
        for (TipoSenderista senderista : listaSenderistas) {
            //COMPROBACION ID COINCIDE
            if (senderista.getId().equals(idBusqueda)) {
                return senderista; // DEVOLVEMOS EL SENDERISTA
            }
        }
        return null;
    }

    public TipoActividad buscarActividadPorId(String idBusqueda) {
        List<TipoActividad> listaActividades = datos.getRegistroActividades().getActividad();
        for (TipoActividad actividad : listaActividades) {
            if (actividad.getId().equals(idBusqueda)) {
                return actividad;
            }
        }
        return null;
    }

//================================================================================================================================================================
//================================================================================================================================================================
// Método simple: intenta añadir y guarda.
// Devuelve true si lo guardó, false si ya estaba o hubo error.

    public boolean agregarParticipante(TipoActividad actividad, String idSenderista) {
        if (actividad == null) return false;
        // Inicializamos la lista si no existe
        if (actividad.getParticipantes() == null) {
            actividad.setParticipantes(new TipoActividad.Participantes());
        }
        List<String> lista = actividad.getParticipantes().getIdSenderista();
        // Si ya está, devolvemos false (no hacemos nada)
        if (lista.contains(idSenderista)) {
            return false;
        }
        // Si no estaba, lo añadimos y guardamos
        lista.add(idSenderista);
        this.guardarDatos();
        return true;
    }

    // Método simple para eliminar
    public boolean eliminarParticipante(TipoActividad actividad, String idSenderista) {
        if (actividad == null || actividad.getParticipantes() == null) return false;
        List<String> lista = actividad.getParticipantes().getIdSenderista();
        // remove devuelve true si lo borró, false si no estaba
        boolean borrado = lista.remove(idSenderista);
        if (borrado) {
            this.guardarDatos(); // Solo guardamos si hubo cambios
        }
        return borrado;
    }
//================================================================================================================================================================
//================================================================================================================================

    /**
     * @param nombreBusqueda
     * @return
     */
    public TipoSenderista buscarSenderistaPorNombre(String nombreBusqueda) {
        //RECORRER LA LISTA DE TODOS LOS SENDERISTAS
        for (TipoSenderista senderista : datos.getListadoSenderistas().getSenderista()) {
            //IGNORAMOS MAYUS Y MINUS
            if (senderista.getNombre().trim().equalsIgnoreCase(nombreBusqueda)) {
                return senderista; // DEVOLVEMOS EL SENDERISTA ENCONTRADO
            }
        }
        return null;
    }

    /**
     * @return
     */
    // MEDIA EDAD GRUPO MONTAÑA
    public double edadMediaSenderistas() {
        double sumaEdades = 0;
        List<TipoSenderista> listaSenderistas = datos.getListadoSenderistas().getSenderista();
        for (TipoSenderista senderista : listaSenderistas) {
            sumaEdades += senderista.getEdad();
        }
        return sumaEdades / listaSenderistas.size();
    }

    /**
     * @return
     */
    public TipoActividad actividadMasPopular() {
        //VARIABLE PARA LA ACTIVIDAD CON MAS PARTICIPANTES
        TipoActividad actividadPopular = null;
        int recordParticipantes = -1;
        //RECORRER LAS ACTIVIDADES
        for (TipoActividad actividad : datos.getRegistroActividades().getActividad()) {
            //TAMAÑO DE LA LISTA DE PARTICIPANTES DENTRO DE LA ACTIVIDAD
            int participantesActuales = actividad.getParticipantes().getIdSenderista().size();
            //IR GUARDANDO LA ACTIVIDAD CON MAS PARTICIPANTES
            if (participantesActuales > recordParticipantes) {
                actividadPopular = actividad;
                recordParticipantes = participantesActuales;
            }
        }
        return actividadPopular; //DEVOLVER ACTIVIDAD CON MAS PARTICIPANTES
    }

    /**
     * @param nuevoSenderista
     */
    //ALTA SENDERISTA
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

    /**
     * Crea de una nueva actividad con datos aportados por el usuario
     *
     * @param nuevaActividad actividad a añadir.
     */
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

    /**
     * Recibe un id y borra el senderista asociado a ese id.
     *
     * @param idParaBorrar
     * @return true si puede borrar, false si no se hace ningun borrado.
     */
    //BORRAR SENDERISTA POR ID
    public boolean bajaSenderista(String idParaBorrar) {
        // LISTA DE SENDERISTAS
        List<TipoSenderista> lista = datos.getListadoSenderistas().getSenderista();
        // A QUIEN NOS VAMOS A CARGAR
        TipoSenderista encontrado = null;
        // RECORRER LA LISTA
        for (TipoSenderista s : lista) {
            if (s.getId().equalsIgnoreCase(idParaBorrar)) {
                encontrado = s;
                break;
            }
        }
        // SI ENCONTRAMOS, BORRAMOS, GUARDAMOS PARA QUE PERSISTA
        if (encontrado != null) {
            lista.remove(encontrado);
            this.guardarDatos();
            return true;
        } else {
            return false;
        }
    }

    /**
     * actualizar participantes de una actividad.
     * @Param id_actividad la actividad que vamos a modificar
     * @Param id_senderista el senderista que vamos a añadir o borrar de la actividad.
     */


}
