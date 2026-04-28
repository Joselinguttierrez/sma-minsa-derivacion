package pe.grupo4.minsa.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * PLATAFORMA 2 - Red de Hospitales
 *
 * Levanta un contenedor remoto que se conecta al Main Container de P1.
 * Crea los agentes: AgenteHospital(es) + AgenteMedico + AgenteAmbulancia
 *
 * Ejecutar con (misma PC):
 *   mvn exec:java -Dexec.mainClass="pe.grupo4.minsa.launch.Platform2Launcher" -Dexec.args="localhost 1099"
 *
 * Ejecutar con (PCs distintas - reemplazar con IP de P1):
 *   mvn exec:java -Dexec.mainClass="pe.grupo4.minsa.launch.Platform2Launcher" -Dexec.args="192.168.1.10 1099"
 *
 * IMPORTANTE: Ejecutar DESPUES de Platform1Launcher.
 */
public class Platform2Launcher {

    public static void main(String[] args) {

        // Leer IP y puerto de P1 desde los argumentos (o usar localhost por defecto)
        String host = (args != null && args.length > 0) ? args[0] : "localhost";
        String port = (args != null && args.length > 1) ? args[1] : "1099";

        Runtime rt = Runtime.instance();

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);
        profile.setParameter(Profile.CONTAINER_NAME, "Hospitales-Container");

        AgentContainer container = null;
        int intentos = 0;
        while (container == null && intentos < 5) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            container = rt.createAgentContainer(profile);
            intentos++;
            if (container == null) {
                System.out.println("Reintentando conexion a P1... intento " + intentos);
            }
        }

        if (container == null) {
            System.out.println("No se pudo conectar a P1. Asegurate que Platform1Launcher este corriendo.");
            return;
        }

        try {
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE DERIVACION DE PACIENTES - MINSA    ║");
            System.out.println("║   PLATAFORMA 2: Red de Hospitales                ║");
            System.out.println("║   Conectando a P1 en " + host + ":" + port + "           ║");
            System.out.println("╚══════════════════════════════════════════════════╝\n");

            // ── Lista de hospitales reales del Perú con camas aproximadas ──
            String[][] hospitales = new String[][]{
                {"Dos de Mayo", "0", "2", "5"},
                {"Edgardo Rebagliati Martins", "5", "10", "20"},
                {"Arzobispo Loayza", "2", "8", "15"},
                {"Guillermo Almenara", "4", "12", "25"},
                {"Hipólito Unanue", "1", "6", "14"},
                {"María Auxiliadora", "1", "4", "10"},
                {"Sergio E. Bernales", "2", "5", "12"},
                {"Alberto Sabogal Sologuren", "3", "7", "18"},
                {"Carlos Lanfranco La Hoz", "1", "3", "8"},
                {"Víctor Larco Herrera", "2", "4", "9"},
                {"Daniel Alcides Carrión", "3", "9", "16"},
                {"Cayetano Heredia", "4", "10", "22"},
                {"José Casimiro Ulloa", "2", "6", "16"},
                {"Naval Cirujano Mayor Santiago Távara", "1", "3", "8"},
                {"Policlínico PNP", "1", "3", "8"},
                {"Instituto Nacional del Niño San Borja", "2", "5", "12"},
                {"Ramiro Prialé", "2", "6", "14"},
                {"Carlos Alberto Seguin Escobedo", "2", "7", "18"},
                {"Honorio Delgado Espinoza", "3", "9", "20"},
                {"Virgen de la Puerta", "2", "8", "16"},
                {"San Bartolomé", "2", "7", "18"},
                {"Hospital Nacional de la Mujer", "2", "6", "14"},
                {"Hospital Nacional de la Madre y el Niño", "2", "6", "14"},
                {"Hospital Nacional de Niños San Borja", "2", "5", "13"},
                {"Hospital Nacional Guillermo Billinghurst", "1", "4", "10"},
                {"Regional de Cusco", "2", "5", "13"},
                {"Regional Lambayeque", "3", "8", "15"},
                {"Regional Docente de Trujillo", "2", "6", "14"},
                {"Regional de Ica", "1", "4", "9"},
                {"Regional de Arequipa", "3", "7", "17"},
                {"Regional de Huancavelica", "0", "3", "6"},
                {"Regional de Puno", "1", "4", "10"},
                {"Regional de Junín", "2", "6", "12"},
                {"Regional de Tacna", "1", "4", "11"},
                {"Regional de Cajamarca", "1", "4", "10"},
                {"Regional de Piura", "2", "6", "14"},
                {"Regional de Tumbes", "1", "3", "9"},
                {"Regional de Ayacucho", "1", "4", "11"},
                {"Regional de Huánuco", "1", "4", "10"},
                {"Regional de Ucayali", "1", "4", "11"},
                {"Regional de Madre de Dios", "0", "3", "7"},
                {"Regional de San Martín", "1", "5", "12"},
                {"Regional de Loreto", "1", "5", "13"},
                {"Regional de Apurímac", "1", "4", "10"},
                {"Regional de Moquegua", "1", "4", "9"},
                {"Regional de Pasco", "1", "4", "10"},
                {"Regional de Áncash", "1", "5", "11"},
                {"Regional de Amazonas", "1", "4", "9"},
                {"Regional de Chachapoyas", "1", "3", "8"},
                {"Regional de Bagua", "1", "3", "8"}
            };

            for (String[] hospitalData : hospitales) {
                String nombre = hospitalData[0];
                String agenteNombre = "Hospital-" + nombre.replaceAll("\\s+", "").replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u").replace("ñ", "n").replace("í", "i").replace("ú", "u").replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
                AgentController hospital = container.createNewAgent(
                    agenteNombre,
                    "pe.grupo4.minsa.agents.AgenteHospital",
                    new Object[]{nombre, hospitalData[1], hospitalData[2], hospitalData[3]}
                );
                hospital.start();
                Thread.sleep(300);
            }

            // ── Agentes Medicos ────────────────────────────────────────────
            int cantidadMedicos = hospitales.length * 2; // 2 medicos por cada hospital
            for (int i = 1; i <= cantidadMedicos; i++) {
                String medicoNombre = String.format("Medico-%04d", i);
                AgentController medico = container.createNewAgent(
                    medicoNombre,
                    "pe.grupo4.minsa.agents.AgenteMedico",
                    new Object[]{}
                );
                medico.start();
                Thread.sleep(150);
            }

            // ── Agentes Ambulancia ─────────────────────────────────────────
            int cantidadAmbulancias = hospitales.length * 3; // 3 ambulancias por cada hospital
            for (int i = 1; i <= cantidadAmbulancias; i++) {
                String ambulanciaNombre = String.format("Ambulancia-%04d", i);
                AgentController ambulancia = container.createNewAgent(
                    ambulanciaNombre,
                    "pe.grupo4.minsa.agents.AgenteAmbulancia",
                    new Object[]{}
                );
                ambulancia.start();
                Thread.sleep(150);
            }

            System.out.println("\n Plataforma 2 lista. 50 hospitales, " + cantidadMedicos + " medicos, " + cantidadAmbulancias + " ambulancias registrados.\n");

        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
