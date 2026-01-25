package com.grupoMontana.xml.logica;

import java.io.File;

import jakarta.xml.bind.*; // Importamos todo lo de JAXB
import com.grupoMontana.xml.modelo.*; // Importamos las clases que creó Maven

public class grupoMontanaLibreria {
    // 1. VARIABLES GLOBALES
    // Necesitamos una variable para guardar todos los datos del club en memoria RAM
    private GrupoMontanaData datos;

    // Necesitamos recordar qué archivo físico estamos tocando
    private File archivoXML;

    // 2. CONSTRUCTOR
    // Al crear la librería, le pasamos la ruta y cargamos los datos al momento
    public grupoMontanaLibreria(String rutaArchivo) throws JAXBException {
        this.archivoXML = new File(rutaArchivo);
        cargarDatos(); // Este método lo haremos ahora
    }

    // Método para leer el XML y convertirlo en objetos Java
    private void cargarDatos() throws JAXBException {

        // 1. Comprobamos que el archivo existe
        if (!this.archivoXML.exists()) {
            throw new JAXBException("No se encuentra el fichero XML en: " + this.archivoXML.getAbsolutePath());
        }

        // 2. Creamos el Contexto
        JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);

        // 3. Creamos el Unmarshaller
        Unmarshaller lector = contexto.createUnmarshaller();

        // 4. Leemos y guardamos en la variable de clase
        this.datos = (GrupoMontanaData) lector.unmarshal(this.archivoXML);

        System.out.println("✅ Datos cargados correctamente en memoria.");
    }
    // Buscar una persona por su ID
    public TipoSenderista buscarSenderistaPorId(String idBusqueda) {
        // Recorremos la lista de senderistas
        for (TipoSenderista s : datos.getListadoSenderistas().getSenderista()) {
            if (s.getId().equals(idBusqueda)) {
                return s; // ¡Encontrado!
            }
        }
        return null; // No existe
    }

//    public TipoSenderista buscarSenderistaPorId(String idBusqueda) {
//        return datos.getListadoSenderistas().getSenderista().stream() // 1. Convertimos la lista en un flujo
//                .filter(s -> s.getId().equals(idBusqueda))            // 2. Filtramos: Solo pasan los que coinciden
//                .findFirst()                                          // 3. Buscamos el primero (devuelve un Optional)
//                .orElse(null);                                        // 4. Si no hay nada, devuelve null
//    }
}