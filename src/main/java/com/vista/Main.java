package com.vista;

import com.dao.GrupoMontanaDAO;
import com.logica.GrupoMontanaLibreria;
import com.grupoMontana.xml.modelo.TipoActividad;
import com.grupoMontana.xml.modelo.TipoRuta;
import com.grupoMontana.xml.modelo.TipoSenderista;

import java.util.List;

public class Main {

    // =========================================================
    // PANEL DE CONTROL: CONSTANTES DE PRUEBA
    // =========================================================
    // 1. Archivo de datos
    private static final String RUTA_XML = "src/main/resources/DiegoFdezLopezgrupoMontanaData.xml";

    // 2. Datos para probar las BÚSQUEDAS
    private static final String BUSQUEDA_NOMBRE_ERROR = "Juan";
    private static final String BUSQUEDA_NOMBRE_OK = "Diego Fernandez Lopez";
    private static final String BUSQUEDA_EMAIL_ERROR = "juan@email.com";
    private static final String BUSQUEDA_EMAIL_OK = "diego@email.com";
    private static final String BUSQUEDA_RUTA_ERROR = "camino dificil";
    private static final String BUSQUEDA_RUTA_OK = "Jultayu";

    // 3. Datos para crear un NUEVO SENDERISTA de prueba
    private static final String TEST_EMAIL = "prueba@montana.com";
    private static final String TEST_NOMBRE = "Socio Automático";
    private static final int TEST_EDAD = 35;
    private static final String TEST_SEXO = "MUJER";

    // 4. Datos para crear una NUEVA ACTIVIDAD de prueba
    private static final String TEST_RUTA_ACT = "Ruta Inventada de Prueba";
    private static final int TEST_VALORACION = 5;
    private static final String TEST_COMENTARIOS = "Actividad creada por el Test Automático";
    // =========================================================

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("  INICIANDO BATERÍA TOTAL DE PRUEBAS - SISTEMA");
        System.out.println("=====================================================\n");

        // --------------------------------------------------------
        // FASE 1: INICIALIZACIÓN (Test de DAO y Carga)
        // --------------------------------------------------------
        GrupoMontanaDAO dao = new GrupoMontanaDAO(RUTA_XML);
        GrupoMontanaLibreria gestion = null;

        try {
            dao.cargarDatos();
            gestion = new GrupoMontanaLibreria(dao);
            System.out.println("FASE 1 OK: Datos XML cargados y Lógica instanciada.\n");
        } catch (Exception e) {
            System.out.println("ERROR CRÍTICO al arrancar: " + e.getMessage());
            return;
        }

        // --------------------------------------------------------
        // FASE 2: TEST DE LECTURA DE LISTAS COMPLETAS
        // --------------------------------------------------------
        System.out.println("--- FASE 2: Comprobando carga de catálogos ---");
        System.out.println("Total Senderistas: " + gestion.getListaSenderistas().size());
        System.out.println("Total Rutas: " + gestion.getListaRutas().size());
        System.out.println("Total Actividades: " + gestion.getListaActividades().size());

        // --------------------------------------------------------
        // FASE 3: TEST DE BÚSQUEDAS (Usando Constantes)
        // --------------------------------------------------------
        System.out.println("\n--- FASE 3: Pruebas de Búsqueda ---");
        System.out.println("\n--- PRUEBAS CON DATOS ERRONEOS ---");

        TipoSenderista busquedaNombre = gestion.buscarSenderistaPorNombre(BUSQUEDA_NOMBRE_ERROR);
        System.out.println("Buscar por Nombre ('" + BUSQUEDA_NOMBRE_ERROR + "'): " + (busquedaNombre != null ? "Encontrado" : "No encontrado"));

        TipoSenderista busquedaEmail = gestion.buscarSenderistaPorEmail(BUSQUEDA_EMAIL_ERROR);
        System.out.println("Buscar por Email ('" + BUSQUEDA_EMAIL_ERROR + "'): " + (busquedaEmail != null ? "Encontrado" : "No encontrado"));

        TipoRuta busquedaRuta = gestion.buscarRutaPorNombre(BUSQUEDA_RUTA_ERROR);
        System.out.println("Buscar Ruta ('" + BUSQUEDA_RUTA_ERROR + "'): " + (busquedaRuta != null ? "Encontrada" : "No encontrada"));

        System.out.println("\n--- PRUEBAS CON DATOS CORRECTOS ---");

        busquedaNombre = gestion.buscarSenderistaPorNombre(BUSQUEDA_NOMBRE_OK);
        System.out.println("Buscar por Nombre ('" + BUSQUEDA_NOMBRE_OK + "'): " + (busquedaNombre != null ? "Encontrado" : "No encontrado"));

        busquedaEmail = gestion.buscarSenderistaPorEmail(BUSQUEDA_EMAIL_OK);
        System.out.println("Buscar por Email ('" + BUSQUEDA_EMAIL_OK + "'): " + (busquedaEmail != null ? "Encontrado" : "No encontrado"));

        busquedaRuta = gestion.buscarRutaPorNombre(BUSQUEDA_RUTA_OK);
        System.out.println("Buscar Ruta ('" + BUSQUEDA_RUTA_OK + "'): " + (busquedaRuta != null ? "Encontrada" : "No encontrada"));


        // --------------------------------------------------------
        // FASE 4: TEST DE ESTADÍSTICAS Y CÁLCULOS
        // --------------------------------------------------------
        System.out.println("\n--- FASE 4: Cálculos y Lógica de Negocio ---");
        System.out.printf("Edad media del club: %.2f años.\n", gestion.edadMediaSenderistas());

        TipoActividad popular = gestion.actividadMasPopular();
        if (popular != null) {
            System.out.println("Actividad más popular: Ruta " + popular.getNombreRuta());
        } else {
            System.out.println("No hay actividades registradas para calcular popularidad.");
        }

        // --------------------------------------------------------
        // FASE 5: TEST DE MODIFICACIÓN DE DATOS (Usando Constantes)
        // --------------------------------------------------------
        System.out.println("\n--- FASE 5: Altas y Control de Excepciones ---");

        TipoSenderista socioPrueba = new TipoSenderista();
        socioPrueba.setNombre(TEST_NOMBRE);
        socioPrueba.setEmail(TEST_EMAIL);
        socioPrueba.setEdad(TEST_EDAD);
        socioPrueba.setSexo(TEST_SEXO);

        try {
            gestion.altaSenderista(socioPrueba);
            System.out.println("Alta Senderista OK: Socio guardado en el XML.");

            System.out.println("Forzando error de duplicado (mismo email)...");
            gestion.altaSenderista(socioPrueba);
        } catch (IllegalArgumentException e) {
            System.out.println("EXCEPCIÓN CAPTURADA CORRECTAMENTE: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado de XML: " + e.getMessage());
        }

        try {
            TipoActividad nuevaActividad = new TipoActividad();
            nuevaActividad.setNombreRuta(TEST_RUTA_ACT);
            nuevaActividad.setValoracion(TEST_VALORACION);
            nuevaActividad.setComentarios(TEST_COMENTARIOS);

            gestion.crearActividad(nuevaActividad);
            System.out.println("Alta Actividad OK: Nueva actividad guardada.");
        } catch (Exception e) {
            System.out.println("Error creando actividad: " + e.getMessage());
        }

        // --------------------------------------------------------
        // FASE 6: TEST DE PARTICIPANTES EN ACTIVIDADES
        // --------------------------------------------------------
        System.out.println("\n--- FASE 6: Gestión de Participantes ---");
        try {
            List<TipoActividad> listaAct = gestion.getListaActividades();
            if (!listaAct.isEmpty()) {
                TipoActividad actTest = listaAct.get(listaAct.size() - 1);

                boolean agregado = gestion.agregarParticipante(actTest, TEST_EMAIL);
                System.out.println("Agregar Participante OK: " + (agregado ? "Añadido al XML" : "Ya estaba inscrito"));

                boolean eliminado = gestion.eliminarParticipante(actTest, TEST_EMAIL);
                System.out.println("Eliminar Participante OK: " + (eliminado ? "Borrado del XML" : "No se encontró para borrar"));
            }
        } catch (Exception e) {
            System.out.println("Error gestionando participantes: " + e.getMessage());
        }

        // --------------------------------------------------------
        // FASE 7: LIMPIEZA (Usando Constantes para asegurar precisión)
        // --------------------------------------------------------
        System.out.println("\n--- FASE 7: Bajas y Limpieza de Datos ---");
        try {
            boolean borrado = gestion.bajaSenderista(TEST_EMAIL);
            if (borrado) {
                System.out.println("Baja Senderista OK: Socio de prueba eliminado del XML.");
            } else {
                System.out.println("Fallo: No se pudo borrar al socio de prueba.");
            }
        } catch (Exception e) {
            System.out.println("Error de XML al borrar: " + e.getMessage());
        }

        System.out.println("\n=====================================================");
        System.out.println("  FIN DE LA BATERÍA DE PRUEBAS. TEST 100% COMPLETADO.");
        System.out.println("=====================================================");
    }
}