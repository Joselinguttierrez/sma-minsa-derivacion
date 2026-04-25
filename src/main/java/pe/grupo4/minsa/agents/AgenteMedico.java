package pe.grupo4.minsa.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import pe.grupo4.minsa.core.DfUtils;
import pe.grupo4.minsa.core.Protocol;

/**
 * AGENTE MEDICO - Plataforma 2
 *
 * Rol: Evalua clinicamente si puede aceptar a un paciente derivado.
 *      Considera el nivel de urgencia para tomar la decision.
 *
 * Logica de evaluacion:
 *   - URGENCIA_CRITICA  → siempre acepta (es una emergencia de vida)
 *   - URGENCIA_MODERADA → acepta el 80% de veces
 *   - URGENCIA_LEVE     → acepta el 50% de veces (puede ser derivado a otro)
 */
public class AgenteMedico extends Agent {

    @Override
    protected void setup() {
        DfUtils.registrar(this, Protocol.SERVICIO_MEDICO, "medico-" + getLocalName());
        System.out.println("[MEDICO] 👨‍⚕️ " + getLocalName() + " listo para evaluar pacientes.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    evaluarPaciente(msg);
                } else {
                    block();
                }
            }
        });
    }

    /**
     * Evalua al paciente y responde AGREE (acepta) o REFUSE (rechaza).
     * Formato del contenido: EVALUAR_PACIENTE|urgencia|nombrePaciente|tipoCama
     */
    private void evaluarPaciente(ACLMessage msg) {
        String contenido = msg.getContent();
        String[] partes  = contenido.split("\\" + Protocol.SEP);
        String urgencia  = partes.length > 1 ? partes[1] : Protocol.URGENCIA_CRITICA;
        String paciente  = partes.length > 2 ? partes[2] : "Paciente";
        String tipoCama  = partes.length > 3 ? partes[3] : Protocol.TIPO_UCI;

        System.out.println("\n[MEDICO] 🔬 Evaluando: " + paciente
                + " | Urgencia: " + urgencia
                + " | Tipo cama: " + tipoCama);

        boolean acepta = decidirAceptacion(urgencia);

        ACLMessage respuesta = msg.createReply();

        if (acepta) {
            respuesta.setPerformative(ACLMessage.AGREE);
            respuesta.setContent(Protocol.PACIENTE_ACEPTADO + Protocol.SEP + paciente);
            System.out.println("[MEDICO] ✅ Paciente " + paciente + " ACEPTADO para ingreso.");
        } else {
            respuesta.setPerformative(ACLMessage.REFUSE);
            respuesta.setContent(Protocol.PACIENTE_RECHAZADO + Protocol.SEP + paciente);
            System.out.println("[MEDICO] ❌ Paciente " + paciente
                    + " RECHAZADO (urgencia leve, buscar otro centro).");
        }

        send(respuesta);
    }

    /**
     * Logica de decision segun la urgencia.
     */
    private boolean decidirAceptacion(String urgencia) {
        switch (urgencia) {
            case Protocol.URGENCIA_CRITICA:
                return true;                        // 100% acepta
            case Protocol.URGENCIA_MODERADA:
                return Math.random() < 0.80;        // 80% acepta
            default:
                return Math.random() < 0.50;        // 50% acepta (LEVE)
        }
    }

    @Override
    protected void takeDown() {
        DfUtils.desregistrar(this);
    }
}
