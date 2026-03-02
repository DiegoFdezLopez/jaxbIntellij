package com.logica;

import java.io.File;
import java.util.List;

import com.dao.GrupoMontanaDAO;
import com.grupoMontana.xml.modelo.*;
import jakarta.xml.bind.JAXBException;

/**
 * Clase encargada de la lógica de negocio y gestión de datos del Grupo de Montaña.
 * Actúa como intermediario entre la interfaz (Vista) y los datos XML.
 */
public class GrupoMontanaLibreria {

    private GrupoMontanaDAO dao;

    /**
     * Archivo para cargar y guardar los datos del grupo de montaña
     */
    private File archivoXML;

    /**
     * Constructor que recibe la ruta del archivo XML.
     *
     * @param dao Ruta física donde se encuentra el XML.
     */
    public GrupoMontanaLibreria(GrupoMontanaDAO dao) {

        this.dao = dao;
    }

    /**
     * Obtiene la lista completa de actividades registradas.
     *
     * @return Lista de objetos TipoActividad.
     */
    public List<TipoActividad> getListaActividades() {

        return dao.getDatos().getRegistroActividades().getActividad();
    }

    /**
     * Obtiene la lista completa de senderistas registrados.
     *
     * @return Lista de objetos TipoSenderista.
     */
    public List<TipoSenderista> getListaSenderistas() {

        return dao.getDatos().getListadoSenderistas().getSenderista();
    }

    /**
     * Obtiene el catálogo completo de rutas disponibles.
     *
     * @return Lista de objetos TipoRuta.
     */
    public List<TipoRuta> getListaRutas() {
        return dao.getDatos().getCatalogoRutas().getRuta();
    }

    /**
     * Busca un senderista utilizando su Email.
     * La búsqueda ignora mayúsculas y minúsculas.
     *
     * @param email El correo electrónico del senderista a buscar.
     * @return El objeto TipoSenderista si existe, o null si no se encuentra.
     */
    public TipoSenderista buscarSenderistaPorEmail(String email) {
        if (email == null) return null;
        for (TipoSenderista s : getListaSenderistas()) {
            // Comparamos ignorando mayúsculas
            if (s.getEmail().trim().equalsIgnoreCase(email.trim())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Busca un senderista por su nombre.
     *
     * @param nombreBusqueda El nombre completo o parcial a buscar.
     * @return El objeto senderista encontrado o null si no existe coincidencia.
     */
    public TipoSenderista buscarSenderistaPorNombre(String nombreBusqueda) {
        if (nombreBusqueda == null) return null;

        // RECORRER LA LISTA DE TODOS LOS SENDERISTAS
        for (TipoSenderista senderista : getListaSenderistas()) {
            // IGNORAMOS MAYUS Y MINUS Y ESPACIOS
            if (senderista.getNombre().trim().equalsIgnoreCase(nombreBusqueda.trim())) {
                return senderista;
            }
        }
        return null;
    }

    /**
     * Busca una ruta en el catálogo por su nombre.
     *
     * @param nombreRuta El nombre de la ruta a buscar.
     * @return El objeto TipoRuta si existe, o null si no se encuentra.
     */
    public TipoRuta buscarRutaPorNombre(String nombreRuta) {
        if (nombreRuta == null) return null;

        for (TipoRuta r : getListaRutas()) {
            if (r.getNombre().trim().equalsIgnoreCase(nombreRuta.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Añade un participante a una actividad específica.
     * Verifica que el participante no esté ya inscrito.
     * Si se añade con éxito, guarda los cambios en el XML.
     *
     * @param actividad       La actividad donde se quiere inscribir.
     * @param emailSenderista El email del senderista a inscribir.
     * @return true si se añadió correctamente, false si ya estaba inscrito o datos nulos.
     */
    public boolean agregarParticipante(TipoActividad actividad, String emailSenderista) throws JAXBException {
        if (actividad == null || emailSenderista == null) return false;
        // Iniciar la lista de participantes si está vacía (null safety)
        if (actividad.getParticipantes() == null) {
            actividad.setParticipantes(new TipoActividad.Participantes());
        }
        List<String> listaEmails = actividad.getParticipantes().getEmailParticipante();
        // Si ya está apuntado, no hacemos nada
        // (Usamos un bucle manual para ignorar mayúsculas al comparar emails)
        for (String emailExistente : listaEmails) {
            if (emailExistente.equalsIgnoreCase(emailSenderista)) {
                return false; // Ya existe
            }
        }
        // Si no estaba, lo añadimos y guardamos
        listaEmails.add(emailSenderista);
        dao.guardarDatos();
        return true;
    }

    /**
     * Elimina un participante de una actividad.
     * Busca el email en la lista de participantes ignorando mayúsculas/minúsculas.
     * Si se elimina con éxito, guarda los cambios en el XML.
     *
     * @param actividad       La actividad de la que se quiere borrar.
     * @param emailSenderista El email del senderista a eliminar.
     * @return true si se eliminó, false si no se encontró en la lista.
     */
    public boolean eliminarParticipante(TipoActividad actividad, String emailSenderista) throws JAXBException {
        if (actividad == null || actividad.getParticipantes() == null) return false;
        List<String> listaEmails = actividad.getParticipantes().getEmailParticipante();
        // Búsqueda manual para borrar ignorando mayúsculas
        String emailParaBorrar = null;
        for (String email : listaEmails) {
            if (email.equalsIgnoreCase(emailSenderista)) {
                emailParaBorrar = email;
                break;
            }
        }

        if (emailParaBorrar != null) {
            listaEmails.remove(emailParaBorrar);
            dao.guardarDatos();
            return true;
        }
        return false;
    }


    /**
     * Calcula la edad media de todos los senderistas registrados.
     *
     * @return La media de edad como double, o 0 si no hay senderistas.
     */
    public double edadMediaSenderistas() {
        List<TipoSenderista> lista = getListaSenderistas();
        if (lista.isEmpty()) return 0;

        double sumaEdades = 0;
        for (TipoSenderista s : lista) {
            sumaEdades += s.getEdad();
        }
        return sumaEdades / lista.size();
    }

    /**
     * Determina cuál es la actividad con mayor número de participantes registrados.
     *
     * @return El objeto TipoActividad más popular, o null si no hay actividades.
     */
    public TipoActividad actividadMasPopular() {
        // VARIABLE PARA LA ACTIVIDAD CON MAS PARTICIPANTES
        TipoActividad actividadPopular = null;
        int recordParticipantes = -1;
        // RECORRER LAS ACTIVIDADES
        for (TipoActividad actividad : dao.getDatos().getRegistroActividades().getActividad()) {
            int participantesActuales = 0;
            // PROTECCIÓN: Solo contamos si la lista de participantes EXISTE
            if (actividad.getParticipantes() != null) {
                participantesActuales = actividad.getParticipantes().getEmailParticipante().size();
            }
            if (participantesActuales > recordParticipantes) {
                actividadPopular = actividad;
                recordParticipantes = participantesActuales;
            }
        }
        return actividadPopular; // DEVOLVER ACTIVIDAD CON MAS PARTICIPANTES
    }

    /**
     * Registra un nuevo senderista en el sistema.
     * Verifica que el email no esté duplicado antes de añadirlo.
     * Guarda los cambios en el XML automáticamente.
     *
     * @param nuevoSenderista El objeto TipoSenderista a añadir.
     */
    public void altaSenderista(TipoSenderista nuevoSenderista) throws JAXBException {
        if (nuevoSenderista == null || nuevoSenderista.getEmail() == null || nuevoSenderista.getEmail().isEmpty()) {
            //CAMBIO DEL PRINTLN POR LANZAMIENTO DE EXCEPCION
            throw new IllegalArgumentException("Los datos del senderista no pueden estar vacios");
        }
        for (TipoSenderista s : dao.getDatos().getListadoSenderistas().getSenderista()) {
            if (s.getEmail().equalsIgnoreCase(nuevoSenderista.getEmail())) {
                throw new IllegalArgumentException("Ya tenemos un senderista con este email");
            }
        }
        //GUARDADO EN RAM
        dao.getDatos().getListadoSenderistas().getSenderista().add(nuevoSenderista);
        //GUARDADO EN DISCO DURO
        dao.guardarDatos();
    }

    /**
     * Elimina un senderista del sistema buscando por su email.
     * Guarda los cambios en el XML si se produce el borrado.
     *
     * @param emailParaBorrar El email del senderista a dar de baja.
     * @return true si se encontró y borró, false en caso contrario.
     */
    public boolean bajaSenderista(String emailParaBorrar) throws JAXBException {
        List<TipoSenderista> listaSenderistas = dao.getDatos().getListadoSenderistas().getSenderista();
        TipoSenderista encontrado = null;
        for (TipoSenderista s : listaSenderistas) {
            if (s.getEmail().equalsIgnoreCase(emailParaBorrar)) {
                encontrado = s;
                break;
            }
        }
        if (encontrado != null) {
            listaSenderistas.remove(encontrado);
            dao.guardarDatos();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Registra una nueva actividad en el sistema y guarda los cambios.
     *
     * @param nuevaActividad El objeto TipoActividad a añadir.
     */
    public void crearActividad(TipoActividad nuevaActividad) throws JAXBException {
        if (nuevaActividad == null) {
            throw new IllegalArgumentException("Los datos de la actividad no pueden estar vacios");
        }
        dao.getDatos().getRegistroActividades().getActividad().add(nuevaActividad);
        dao.guardarDatos();
    }
}