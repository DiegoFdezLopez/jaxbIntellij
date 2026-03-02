package com.dao;

import java.io.File;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import com.grupoMontana.xml.modelo.GrupoMontanaData;

/**
 * Clase encargada exclusivamente del acceso a datos (Lectura y Escritura del XML).
 */
public class GrupoMontanaDAO {

    private File archivoXML;
    private GrupoMontanaData datos;

    public GrupoMontanaDAO(String rutaArchivo) {

        this.archivoXML = new File(rutaArchivo);
    }

    /**
     * Lee el XML y carga los datos en memoria.
     *
     * @throws JAXBException Si hay un error de lectura o formato.
     */
    public void cargarDatos() throws JAXBException {
        if (!this.archivoXML.exists()) {
            throw new JAXBException("No se encuentra el fichero XML en la ruta: " + archivoXML.getAbsolutePath());
        }
        JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
        Unmarshaller lector = contexto.createUnmarshaller();
        this.datos = (GrupoMontanaData) lector.unmarshal(this.archivoXML);
    }

    /**
     * Guarda el estado actual de los datos en el XML.
     *
     * @throws JAXBException Si hay un error de escritura.
     */
    public void guardarDatos() throws JAXBException {
        JAXBContext contexto = JAXBContext.newInstance(GrupoMontanaData.class);
        Marshaller marshaller = contexto.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // Para que salga indentado y bonito
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");      // Tu buena práctica recuperada
        marshaller.marshal(this.datos, this.archivoXML);
    }

    /**
     * Devuelve los datos cargados en memoria para que la lógica pueda usarlos.
     */
    public GrupoMontanaData getDatos() {
        return this.datos;
    }
}
