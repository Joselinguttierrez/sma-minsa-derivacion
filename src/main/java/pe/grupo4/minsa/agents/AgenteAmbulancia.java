package pe.grupo4.minsa.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import pe.grupo4.minsa.core.DfUtils;
import pe.grupo4.minsa.core.Protocol;

/**
 * AGENTE AMBULANCIA - Plataforma 2
 *
 * Rol: Gestiona el traslado fisico del paciente al hospital asignado.
 *      Calcula un tiempo estimado de llegada (ETA) y confirma el traslado
 *      a la Central MINSA.
 *
 * Es el ultimo eslabon del flujo de derivacion.
 */
public class AgenteAmbulancia extends Agent {

    @Override
    protected void setup() {
        DfUtils.registrar(this, Protocol.SERVICIO_AMBULANCIA, "ambulancia-" + getLocalName());
        System.out.println("[AMBULANCIA] 🚑 " + getLocalName()
                + " disponible para traslados.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    procesarSolicitudTraslado(msg);
                } else {
                    block();
                }
            }
        });
    }

    /**
     * Recibe la solicitud de traslado y confirma con un ETA simulado.
     * Formato del contenido: SOLICITUD_TRASLADO|nombreHospital
     */
    private void procesarSolicitudTraslado(ACLMessage msg) {
        String contenido = msg.getContent();
        String[] partes  = contenido.split("\\" + Protocol.SEP);
        String hospital  = partes.length > 1 ? partes[1] : "Hospital destino";

        // ETA simulado: entre 5 y 20 minutos
        int eta = (int)(Math.random() * 16) + 5;

        System.out.println("\n[AMBULANCIA] 🚨 Solicitud recibida → Destino: " + hospital);
        System.out.println("[AMBULANCIA] ⏱  ETA estimado: " + eta + " minutos");
        System.out.println("[AMBULANCIA] 🔊 ¡En camino con sirenas!");

        // Confirmar el traslado a quien lo solicito (la Central)
        ACLMessage confirmacion = msg.createReply();
        confirmacion.setPerformative(ACLMessage.INFORM);
        confirmacion.setContent(Protocol.TRASLADO_CONFIRMADO
                + Protocol.SEP + hospital
                + Protocol.SEP + "ETA:" + eta + "min"
                + Protocol.SEP + getLocalName());
        send(confirmacion);

        System.out.println("[AMBULANCIA] ✅ Traslado confirmado hacia " + hospital + ".");
    }

    @Override
    protected void takeDown() {
        DfUtils.desregistrar(this);
        System.out.println("[AMBULANCIA] Ambulancia desregistrada.");
    }
}
