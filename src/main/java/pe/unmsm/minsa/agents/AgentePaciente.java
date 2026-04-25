package pe.unmsm.minsa.agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import pe.unmsm.minsa.core.DfUtils;
import pe.unmsm.minsa.core.Protocol;

import java.util.List;

/**
 * AGENTE PACIENTE - Plataforma 1
 *
 * Rol: Representa a un paciente que necesita atencion urgente.
 * Registra sus datos en el sistema y notifica a la Central MINSA.
 *
 * Argumentos (al crear el agente):
 *   args[0] = nombre del paciente        (ej: "Juan Quispe")
 *   args[1] = nivel de urgencia          (ej: "URGENCIA_CRITICA")
 *   args[2] = sintomas                   (ej: "disnea,fiebre,saturacion_baja")
 *   args[3] = ubicacion                  (ej: "Lima-Breña")
 *   args[4] = tipo de cama requerida     (ej: "UCI")
 */
public class AgentePaciente extends Agent {

    private String nombrePaciente;
    private String nivelUrgencia;
    private String sintomas;
    private String ubicacion;
    private String tipoCama;

    @Override
    protected void setup() {
        // Leer argumentos o usar valores por defecto
        Object[] args = getArguments();
        if (args != null && args.length >= 4) {
            nombrePaciente = (String) args[0];
            nivelUrgencia  = (String) args[1];
            sintomas       = (String) args[2];
            ubicacion      = (String) args[3];
            tipoCama       = (args.length > 4) ? (String) args[4] : Protocol.TIPO_UCI;
        } else {
            // Valores por defecto (caso COVID critico)
            nombrePaciente = getLocalName();
            nivelUrgencia  = Protocol.URGENCIA_CRITICA;
            sintomas       = "disnea,saturacion_baja,fiebre_alta";
            ubicacion      = "Lima-Breña";
            tipoCama       = Protocol.TIPO_UCI;
        }

        // Registrarse en el DF (Paginas Amarillas)
        DfUtils.registrar(this, Protocol.SERVICIO_PACIENTE, "paciente-" + nombrePaciente);

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  NUEVO PACIENTE EN EL SISTEMA            ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Nombre   : " + nombrePaciente);
        System.out.println("║  Urgencia : " + nivelUrgencia);
        System.out.println("║  Sintomas : " + sintomas);
        System.out.println("║  Ubicacion: " + ubicacion);
        System.out.println("║  Necesita : Cama " + tipoCama);
        System.out.println("╚══════════════════════════════════════════╝");

        // Esperar 2 segundos para que la Central este lista antes de enviar
        addBehaviour(new WakerBehaviour(this, 2000) {
            @Override
            protected void onWake() {
                enviarSolicitudACentral();
            }
        });
    }

    /**
     * Busca la Central MINSA en el DF y le envia la solicitud de derivacion.
     * Mensaje: INFORM con datos del paciente en formato:
     *   URGENCIA_CRITICA|sintomas|ubicacion|tipoCama|nombrePaciente
     */
    private void enviarSolicitudACentral() {
        List<AID> centrales = DfUtils.buscar(this, Protocol.SERVICIO_CENTRAL);

        if (centrales.isEmpty()) {
            System.out.println("[PACIENTE:" + nombrePaciente + "] ❌ No se encontro la Central MINSA.");
            return;
        }

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(centrales.get(0));
        msg.setOntology(Protocol.ONTOLOGY);

        // Formato del mensaje
        String contenido = nivelUrgencia
                + Protocol.SEP + sintomas
                + Protocol.SEP + ubicacion
                + Protocol.SEP + tipoCama
                + Protocol.SEP + nombrePaciente;

        msg.setContent(contenido);
        send(msg);

        System.out.println("[PACIENTE:" + nombrePaciente + "] ✉  Solicitud enviada a Central MINSA.");
        System.out.println("[PACIENTE:" + nombrePaciente + "]    Contenido: " + contenido);
    }

    @Override
    protected void takeDown() {
        DfUtils.desregistrar(this);
    }
}
