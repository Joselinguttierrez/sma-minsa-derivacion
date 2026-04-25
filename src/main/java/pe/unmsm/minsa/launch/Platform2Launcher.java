package pe.unmsm.minsa.launch;

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
 *   mvn exec:java -Dexec.mainClass="pe.unmsm.minsa.launch.Platform2Launcher" -Dexec.args="localhost 1099"
 *
 * Ejecutar con (PCs distintas - reemplazar con IP de P1):
 *   mvn exec:java -Dexec.mainClass="pe.unmsm.minsa.launch.Platform2Launcher" -Dexec.args="192.168.1.10 1099"
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
            System.out.println("❌ No se pudo conectar a P1. Asegurate que Platform1Launcher este corriendo.");
            return;
        }

        try {
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE DERIVACION DE PACIENTES - MINSA    ║");
            System.out.println("║   PLATAFORMA 2: Red de Hospitales                ║");
            System.out.println("║   Conectando a P1 en " + host + ":" + port + "           ║");
            System.out.println("╚══════════════════════════════════════════════════╝\n");

            // ── Hospital Dos de Mayo: SIN UCI (saturado, simula COVID) ────
            AgentController h1 = container.createNewAgent(
                "Hospital-DosMayo",
                "pe.unmsm.minsa.agents.AgenteHospital",
                new Object[]{"Dos de Mayo", "0", "2", "5"}
                // UCI=0 (saturado), Emergencia=2, Generales=5
            );
            h1.start();
            Thread.sleep(500);

            // ── Hospital Rebagliati: CON UCI disponible ───────────────────
            AgentController h2 = container.createNewAgent(
                "Hospital-Rebagliati",
                "pe.unmsm.minsa.agents.AgenteHospital",
                new Object[]{"Rebagliati", "3", "6", "12"}
                // UCI=3, Emergencia=6, Generales=12
            );
            h2.start();
            Thread.sleep(500);

            // ── Hospital Loayza: recursos moderados ───────────────────────
            AgentController h3 = container.createNewAgent(
                "Hospital-Loayza",
                "pe.unmsm.minsa.agents.AgenteHospital",
                new Object[]{"Loayza", "1", "4", "8"}
            );
            h3.start();
            Thread.sleep(500);

            // ── Agente Medico ─────────────────────────────────────────────
            AgentController medico = container.createNewAgent(
                "AgenteMedico",
                "pe.unmsm.minsa.agents.AgenteMedico",
                new Object[]{}
            );
            medico.start();
            Thread.sleep(500);

            // ── Agente Ambulancia ─────────────────────────────────────────
            AgentController ambulancia = container.createNewAgent(
                "AgenteAmbulancia",
                "pe.unmsm.minsa.agents.AgenteAmbulancia",
                new Object[]{}
            );
            ambulancia.start();

            System.out.println("\n✅ Plataforma 2 lista. 3 hospitales, 1 medico, 1 ambulancia registrados.\n");

        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
