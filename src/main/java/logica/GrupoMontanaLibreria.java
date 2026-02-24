package logica;

import java.io.File;
import java.util.List;

import jakarta.xml.bind.*;
import com.grupoMontana.xml.modelo.*;

/**
 * Clase encargada de la lógica de negocio y gestión de datos del Grupo de Montaña.
 * Actúa como intermediario entre la interfaz (Vista) y los datos XML (Modelo).
 */
public class GrupoMontanaLibreria {

    /**
     * Objeto raíz que contiene todos los datos del XML en memoria.
     */
    private GrupoMontanaData datos;

    /**
     * Archivo físico donde se leen y guardan los datos.
     */
    private File archivoXML = new File("src/main/resources/grupoMontanaData.xml");

    /**
     * Constructor por defecto.
     */
    public GrupoMontanaLibreria() {
        super();
    }

    /**
     * Carga los datos del grupo desde el fichero XML asociado y los vuelca en memoria.
     * Utiliza JAXB para el unmarshalling.
     *
     * @throws JAXBException si el archivo no existe o el formato es incorrecto.
     */
    public void cargarDatos() throws JAXBException {
        if (!this.archivoXML.exists()) {
            throw new JAXBException("No se encuentra el fichero XML");
        }
        // CONTEXTO
        JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
        // UNMARSHALLER
        Unmarshaller lector = contexto.createUnmarshaller();
        // LEER Y GUARDAR
        this.datos = (GrupoMontanaData) lector.unmarshal(this.archivoXML);
        System.out.println("Datos cargados correctamente.");
    }

    /**
     * Guarda el estado actual de los datos en memoria en el fichero XML físico.
     * Utiliza JAXB para el marshalling con formato legible (pretty print).
     * Captura las excepciones internamente y muestra error por consola si falla.
     */
    public void guardarDatos() {
        try {
            // CONTEXTO
            JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
            // CREACION MARSHALLER
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");      // buena practica
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // pretty para el estilo
            // ESCRIBIR "DATOS" EN EL ARCHIVOXML
            marshaller.marshal(datos, archivoXML);
            System.out.println("Cambios guardados en XML.");
        } catch (JAXBException e) {
            System.out.println("Error crítico guardando datos: " + e.getMessage());
        }
    }

    // ==========================================
    // GETTERS SIMPLES (Listados)
    // ==========================================

    /**
     * Obtiene la lista completa de actividades registradas.
     *
     * @return Lista de objetos TipoActividad.
     */
    public List<TipoActividad> getListaActividades() {
        return datos.getRegistroActividades().getActividad();
    }

    /**
     * Obtiene la lista completa de senderistas registrados.
     *
     * @return Lista de objetos TipoSenderista.
     */
    public List<TipoSenderista> getListaSenderistas() {
        return datos.getListadoSenderistas().getSenderista();
    }

    /**
     * Obtiene el catálogo completo de rutas disponibles.
     *
     * @return Lista de objetos TipoRuta.
     */
    public List<TipoRuta> getListaRutas(){
        return datos.getCatalogoRutas().getRuta();
    }


    /**
     * Busca un senderista utilizando su Email como identificador único.
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


    // ==========================================
    // GESTIÓN DE PARTICIPANTES (Usando Email)
    // ==========================================

    /**
     * Añade un participante a una actividad específica.
     * Verifica que el participante no esté ya inscrito (por email).
     * Si se añade con éxito, guarda los cambios en el XML.
     *
     * @param actividad La actividad donde se quiere inscribir.
     * @param emailSenderista El email del senderista a inscribir.
     * @return true si se añadió correctamente, false si ya estaba inscrito o datos nulos.
     */
    public boolean agregarParticipante(TipoActividad actividad, String emailSenderista) {
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
        this.guardarDatos();
        return true;
    }

    /**
     * Elimina un participante de una actividad.
     * Busca el email en la lista de participantes ignorando mayúsculas/minúsculas.
     * Si se elimina con éxito, guarda los cambios en el XML.
     *
     * @param actividad La actividad de la que se quiere borrar.
     * @param emailSenderista El email del senderista a eliminar.
     * @return true si se eliminó, false si no se encontró en la lista.
     */
    public boolean eliminarParticipante(TipoActividad actividad, String emailSenderista) {
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
            this.guardarDatos();
            return true;
        }
        return false;
    }

    /**
     * Busca un senderista por su nombre real (o parte del nombre).
     * Útil cuando no se conoce el email exacto.
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

    // ==========================================
    // ESTADÍSTICAS Y GESTIÓN
    // ==========================================

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
     * Protegido contra listas de participantes nulas.
     *
     * @return El objeto TipoActividad más popular, o null si no hay actividades.
     */
    public TipoActividad actividadMasPopular() {
        // VARIABLE PARA LA ACTIVIDAD CON MAS PARTICIPANTES
        TipoActividad actividadPopular = null;
        int recordParticipantes = -1;
        // RECORRER LAS ACTIVIDADES
        for (TipoActividad actividad : datos.getRegistroActividades().getActividad()) {
            int participantesActuales = 0;
            // PROTECCIÓN: Solo contamos si la lista de participantes EXISTE
            if (actividad.getParticipantes() != null) {
                // CAMBIO AQUÍ: Ya no es getIdSenderista(), ahora es getEmailParticipante()
                participantesActuales = actividad.getParticipantes().getEmailParticipante().size();
            }
            // IR GUARDANDO LA ACTIVIDAD CON MAS PARTICIPANTES
            // Usamos > para quedarnos con la primera que encontremos en caso de empate
            if (participantesActuales > recordParticipantes) {
                actividadPopular = actividad;
                recordParticipantes = participantesActuales;
            }
        }
        return actividadPopular; // DEVOLVER ACTIVIDAD CON MAS PARTICIPANTES
    }

    // ==========================================
    // ALTAS Y BAJAS (Sin IDs)
    // ==========================================

    /**
     * Registra un nuevo senderista en el sistema.
     * Verifica que el email no esté duplicado antes de añadirlo.
     * Guarda los cambios en el XML automáticamente.
     *
     * @param nuevo El objeto TipoSenderista a añadir.
     */
    public void altaSenderista(TipoSenderista nuevo) {
        if (nuevo == null) return;

        // Evitar duplicados de email antes de guardar
        if (buscarSenderistaPorEmail(nuevo.getEmail()) != null) {
            System.out.println("Error: Ya existe un senderista con ese email.");
            return;
        }

        getListaSenderistas().add(nuevo);
        this.guardarDatos();
        System.out.println("Senderista añadido: " + nuevo.getNombre());
    }

    /**
     * Elimina un senderista del sistema buscando por su email.
     * Guarda los cambios en el XML si se produce el borrado.
     *
     * @param emailParaBorrar El email del senderista a dar de baja.
     * @return true si se encontró y borró, false en caso contrario.
     */
    public boolean bajaSenderista(String emailParaBorrar) {
        TipoSenderista encontrado = buscarSenderistaPorEmail(emailParaBorrar);

        if (encontrado != null) {
            getListaSenderistas().remove(encontrado);
            this.guardarDatos();
            return true;
        }
        return false;
    }

    /**
     * Registra una nueva actividad en el sistema y guarda los cambios.
     *
     * @param nueva El objeto TipoActividad a añadir.
     */
    public void crearActividad(TipoActividad nueva) {
        if (nueva == null) return;
        getListaActividades().add(nueva);
        this.guardarDatos();
        System.out.println("Actividad creada.");
    }
}