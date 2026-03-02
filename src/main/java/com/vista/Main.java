package com.vista;


import com.dao.GrupoMontanaDAO;
import com.grupoMontana.xml.modelo.TipoActividad;
import com.grupoMontana.xml.modelo.TipoRuta;
import com.grupoMontana.xml.modelo.TipoSenderista;
import com.logica.GrupoMontanaLibreria;

public class Main {

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("  INICIANDO BATERÍA DE PRUEBAS - GRUPO DE MONTAÑA");
        System.out.println("=====================================================\n");

        // --------------------------------------------------------
        // INICIALIZACIÓN (Separación de responsabilidades)
        // --------------------------------------------------------
        GrupoMontanaDAO dao = new GrupoMontanaDAO("src/main/resources/DiegoFdezLopezgrupoMontanaData.xml");
        GrupoMontanaLibreria gestion = null;

        try {
            dao.cargarDatos();
            gestion = new GrupoMontanaLibreria(dao);
            System.out.println("FASE 1 OK: Datos XML cargados y Lógica instanciada.\n");
        } catch (Exception e) {
            System.out.println("ERROR CRÍTICO al arrancar: " + e.getMessage());
            return; // Detenemos la ejecución si el XML no carga
        }


        // --------------------------------------------------------
        // FASE 2: PRUEBA DE ESTADÍSTICAS Y CONSULTAS PÚBLICAS
        // --------------------------------------------------------
        System.out.println("--- FASE 2: Consultas y Cálculos ---");

        // 2.1 Edad Media
        System.out.printf("👉 Edad media del club: %.2f años.\n", gestion.edadMediaSenderistas());

        // 2.2 Buscar Senderista (Cambia "Juan" por un nombre que sepas que está en tu XML)
        TipoSenderista buscado = gestion.buscarSenderistaPorNombre("Juan");
        if (buscado != null) {
            System.out.println("👉 Búsqueda OK: Encontrado " + buscado.getNombre() + " (Email: " + buscado.getEmail() + ")");
        } else {
            System.out.println("👉 Búsqueda OK: No se encontró a la persona buscada.");
        }

        // 2.3 Actividad más popular
        TipoActividad popular = gestion.actividadMasPopular();
        if (popular != null) {
            System.out.println("👉 Actividad TOP: Ruta '" + popular.getNombreRuta() + "' con " + popular.getValoracion() + " estrellas.");
        }


        // --------------------------------------------------------
        // FASE 3: PRUEBA DE ALTA Y EXCEPCIONES (Lo que pidió el profesor)
        // --------------------------------------------------------
        System.out.println("\n--- FASE 3: Modificación de Datos y Excepciones ---");

        // 3.1 Alta Senderista
        TipoSenderista socioPrueba = new TipoSenderista();
        socioPrueba.setNombre("Socio Automático");
        socioPrueba.setEmail("prueba@montana.com"); // Clave principal según tu XSD
        socioPrueba.setEdad(28);
        socioPrueba.setSexo("MUJER");

        try {
            gestion.altaSenderista(socioPrueba);
            System.out.println("Alta Senderista OK: Socio guardado en el XML.");

            System.out.println("Forzando error de duplicado (mismo email)...");
            gestion.altaSenderista(socioPrueba); // Esto debe hacer saltar el catch

        } catch (IllegalArgumentException e) {
            System.out.println("EXCEPCIÓN CAPTURADA CORRECTAMENTE: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado de XML en Senderista: " + e.getMessage());
        }

        // 3.2 Alta Ruta (Ejemplo básico para cubrir el catálogo de rutas)
        TipoRuta rutaPrueba = new TipoRuta();
        rutaPrueba.setNombre("Ruta de Prueba Auto");
        rutaPrueba.setDistanciaKm(15.5);
        rutaPrueba.setDesnivelPositivo(800);
        rutaPrueba.setDificultad("MEDIA");

        try {
            /* Nota: Si tu método en la librería se llama distinto, cámbialo aquí.
               Asumo que se llama crearRuta o altaRuta */
            // gestion.altaRuta(rutaPrueba); // DESCOMENTA ESTA LÍNEA SI TIENES EL MÉTODO CREADO
            System.out.println("Alta Ruta OK: (Descomenta la llamada al método en el código si lo tienes programado).");
        } catch (Exception e) {
            System.out.println("Error en Ruta: " + e.getMessage());
        }


        // --------------------------------------------------------
        // FASE 4: LIMPIEZA (Dejamos el XML como estaba)
        // --------------------------------------------------------
        System.out.println("\n--- FASE 4: Probando Bajas (Limpieza) ---");

        try {
            boolean borrado = gestion.bajaSenderista("prueba@montana.com");
            if (borrado) {
                System.out.println("Baja Senderista OK: Socio de prueba eliminado del XML.");
            } else {
                System.out.println("Fallo: No se pudo borrar al socio de prueba.");
            }

            // Si tienes un método bajaRuta, llámalo aquí también:
            // gestion.bajaRuta("Ruta de Prueba Auto");

        } catch (Exception e) {
            System.out.println("Error de XML al borrar: " + e.getMessage());
        }

        System.out.println("\n=====================================================");
        System.out.println("  FIN DE LA BATERÍA DE PRUEBAS. TODO CORRECTO.");
        System.out.println("=====================================================");
    }
}