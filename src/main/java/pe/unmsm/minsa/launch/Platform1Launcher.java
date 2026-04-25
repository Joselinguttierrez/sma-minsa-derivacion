package pe.unmsm.minsa.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * PLATAFORMA 1 - Central MINSA
 *
 * Levanta el contenedor principal (Main Container) con la GUI de JADE.
 * Crea los agentes: AgenteCentral + AgentePaciente(s)
 *
 * Ejecutar con:
 *   mvn exec:java -Dexec.mainClass="pe.unmsm.minsa.launch.Platform1Launcher"
 *
 * IMPORTANTE: Ejecutar este ANTES que Platform2Launcher.
 */
public class Platform1Launcher {

    public static void main(String[] args) {

        Runtime rt = Runtime.instance();

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");
        profile.setParameter(Profile.GUI, "true");           // Abre la GUI de JADE
        profile.setParameter(Profile.PLATFORM_ID, "MINSA-SMA");

        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE DERIVACION DE PACIENTES - MINSA    ║");
            System.out.println("║   PLATAFORMA 1: Central MINSA                    ║");
            System.out.println("╚══════════════════════════════════════════════════╝\n");

            // ── Crear Agente Central ──────────────────────────────────────
            AgentController central = mainContainer.createNewAgent(
                "AgenteCentral",
                "pe.unmsm.minsa.agents.AgenteCentral",
                new Object[]{}
            );
            central.start();
            Thread.sleep(8000); // Esperar 8 segundos para que P2 se conecte, esperar que la Central se registre en el DF

            // ── Paciente 1: Juan Quispe - COVID critico ───────────────────
            // Simula un caso de la pandemia 2020: saturacion baja, necesita UCI
            AgentController paciente1 = mainContainer.createNewAgent(
                "Paciente-JuanQuispe",
                "pe.unmsm.minsa.agents.AgentePaciente",
                new Object[]{
                    "Juan Quispe",
                    "URGENCIA_CRITICA",
                    "disnea,saturacion_baja_88pct,fiebre_39C",
                    "Lima-Breña",
                    "UCI"
                }
            );
            paciente1.start();

            Thread.sleep(6000);// Esperar que se resuelva la primera derivacion

            // ── Paciente 2: Maria Lopez - urgencia moderada ───────────────
            AgentController paciente2 = mainContainer.createNewAgent(
                "Paciente-MariaLopez",
                "pe.unmsm.minsa.agents.AgentePaciente",
                new Object[]{
                    "Maria Lopez",
                    "URGENCIA_MODERADA",
                    "dolor_pecho,mareos,presion_alta",
                    "Lima-Jesus_Maria",
                    "EMERGENCIA"
                }
            );
            paciente2.start();

        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
