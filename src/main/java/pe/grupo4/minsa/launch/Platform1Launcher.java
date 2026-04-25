package pe.grupo4.minsa.launch;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

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
            System.out.println("║   SISTEMA DE DERIVACION DE PACIENTES - MINSA     ║");
            System.out.println("║   PLATAFORMA 1: Central MINSA                    ║");
            System.out.println("╚══════════════════════════════════════════════════╝\n");

            // ── Crear Agente Central ──────────────────────────────────────
            AgentController central = mainContainer.createNewAgent(
                "AgenteCentral",
                "pe.grupo4.minsa.agents.AgenteCentral",
                new Object[]{}
            );
            central.start();
            Thread.sleep(8000); // Esperar 8 segundos para que P2 se conecte, esperar que la Central se registre en el DF

            // ── Paciente 1: Carlos Garcia - COVID critico ─────────────────
            AgentController paciente1 = mainContainer.createNewAgent(
                    "Paciente-CarlosGarcia",
                    "pe.grupo4.minsa.agents.AgentePaciente",
                    new Object[]{
                            "Carlos Garcia Quispe",
                            "URGENCIA_CRITICA",
                            "disnea,saturacion_baja_82pct,fiebre_40C,tos_seca",
                            "Lima-San_Juan_de_Lurigancho",
                            "UCI"
                    }
            );

            paciente1.start();

            Thread.sleep(6000);// Esperar que se resuelva la primera derivacion

            AgentController paciente2 = mainContainer.createNewAgent(
                    "Paciente-RosaAlvarado",
                    "pe.unmsm.minsa.agents.AgentePaciente",
                    new Object[]{
                            "Rosa Alvarado Flores",
                            "URGENCIA_CRITICA",
                            "traumatismo_craneal,fractura_costillas,hemorragia_interna",
                            "Lima-La_Victoria",
                            "UCI"
                    }
            );
            paciente2.start();

        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
