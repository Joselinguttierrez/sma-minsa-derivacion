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
        profile.setParameter(Profile.GUI, "true");
        profile.setParameter(Profile.PLATFORM_ID, "MINSA-SMA");

        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE DERIVACION DE PACIENTES - MINSA     ║");
            System.out.println("║   PLATAFORMA 1: Central MINSA                    ║");
            System.out.println("╚══════════════════════════════════════════════════╝\n");

            // crear agente central
            AgentController central = mainContainer.createNewAgent(
                "AgenteCentral",
                "pe.grupo4.minsa.agents.AgenteCentral",
                new Object[]{}
            );
            central.start();
            Thread.sleep(8000); // espera de 8 segundos para conectar P2 y registre los hospitales

            // Paciente 1
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

            Thread.sleep(6000);// espera de 6 segundos para que el primer paciente sea derivado antes de ingresar el segundo paciente

            AgentController paciente2 = mainContainer.createNewAgent(
                    "Paciente-RosaAlvarado",
                    "pe.gr4upo.minsa.agents.AgentePaciente",
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
