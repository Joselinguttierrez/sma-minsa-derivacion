package pe.grupo4.minsa.agents;

import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import pe.grupo4.minsa.core.DfUtils;
import pe.grupo4.minsa.core.Protocol;

/**
 * AGENTE CENTRAL - Plataforma 1
 *
 * Rol: Coordinador principal del sistema MINSA.
 *      Recibe solicitudes de pacientes, busca hospitales disponibles
 *      en el DF y coordina el traslado via ambulancia.
 *
 * Flujo que maneja:
 *   1. Recibe INFORM del AgentePaciente
 *   2. Busca hospitales en el DF (Paginas Amarillas)
 *   3. Envia REQUEST a cada hospital
 *   4. Recibe AGREE del AgenteHospital (cama disponible)
 *   5. Envia REQUEST a AgenteAmbulancia para el traslado
 *   6. Recibe confirmacion final del traslado
 */
public class AgenteCentral extends Agent {

    @Override
    protected void setup() {
        DfUtils.registrar(this, Protocol.SERVICIO_CENTRAL, "central-minsa-principal");

        System.out.println("\nCENTRAL MINSA ACTIVA - Esperando solicitudes de derivacion...\n");

        // Comportamiento ciclico: siempre escuchando mensajes
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    procesarMensaje(msg);
                } else {
                    block(); // Espera hasta que llegue un mensaje
                }
            }
        });
    }

    /**
     * Decide que hacer segun el tipo de mensaje recibido.
     */
    private void procesarMensaje(ACLMessage msg) {
        String contenido = msg.getContent();
        int performativa = msg.getPerformative();
        String remitente = msg.getSender().getLocalName();

        System.out.println("\n[CENTRAL] Mensaje de " + remitente
                + " [" + ACLMessage.getPerformative(performativa) + "]: " + contenido);

        if (performativa == ACLMessage.INFORM
                && (contenido.startsWith(Protocol.URGENCIA_CRITICA)
                ||  contenido.startsWith(Protocol.URGENCIA_MODERADA)
                ||  contenido.startsWith(Protocol.URGENCIA_LEVE))) {
            // Solicitud nueva de un paciente
            procesarSolicitudPaciente(contenido);

        } else if (performativa == ACLMessage.AGREE
                && contenido.startsWith(Protocol.CAMA_DISPONIBLE)) {
            // Un hospital acepto al paciente
            procesarHospitalAcepto(contenido);

        } else if (performativa == ACLMessage.REFUSE
                && contenido.startsWith(Protocol.CAMA_NO_DISPONIBLE)) {
            // Un hospital rechazo (sin camas)
            System.out.println("[CENTRAL] Hospital " + remitente
                    + " no tiene camas disponibles. Esperando otro hospital...");

        } else if (performativa == ACLMessage.INFORM
                && contenido.startsWith(Protocol.TRASLADO_CONFIRMADO)) {
            // La ambulancia confirmo el traslado
            System.out.println("[CENTRAL] " + contenido);
            System.out.println("[CENTRAL] ¡DERIVACION COMPLETADA EXITOSAMENTE!\n");
        }
    }

    /**
     * Paso 1: Recibe datos del paciente y consulta hospitales en el DF.
     * Formato del contenido:
     *   URGENCIA_CRITICA|sintomas|ubicacion|tipoCama|nombrePaciente
     */
    private void procesarSolicitudPaciente(String contenido) {
        String[] partes = contenido.split("\\" + Protocol.SEP);
        String urgencia       = partes[0];
        String sintomas       = partes.length > 1 ? partes[1] : "desconocido";
        String ubicacion      = partes.length > 2 ? partes[2] : "Lima";
        String tipoCama       = partes.length > 3 ? partes[3] : Protocol.TIPO_UCI;
        String nombrePaciente = partes.length > 4 ? partes[4] : "Paciente";

        System.out.println("[CENTRAL] Paciente: " + nombrePaciente + " | Urgencia: " + urgencia);

        // Reintentar hasta 5 veces si no hay hospitales
        addBehaviour(new jade.core.behaviours.WakerBehaviour(this, 3000) {
            int intentos = 0;
            @Override
            protected void onWake() {
                List<AID> hospitales = DfUtils.buscar(myAgent, Protocol.SERVICIO_HOSPITAL);
                intentos++;
                if (!hospitales.isEmpty()) {
                    System.out.println("[CENTRAL] Encontré " + hospitales.size() + " hospital(es). Enviando consultas...");
                    for (AID hospital : hospitales) {
                        ACLMessage solicitud = new ACLMessage(ACLMessage.REQUEST);
                        solicitud.addReceiver(hospital);
                        solicitud.setOntology(Protocol.ONTOLOGY);
                        solicitud.setContent(Protocol.SOLICITUD_CAMA + Protocol.SEP + tipoCama + Protocol.SEP + urgencia + Protocol.SEP + nombrePaciente);
                        myAgent.send(solicitud);
                        System.out.println("[CENTRAL] ➡  Consulta enviada a: " + hospital.getLocalName());
                    }
                } else if (intentos < 5) {
                    System.out.println("[CENTRAL] Sin hospitales aún. Reintentando en 3 segundos... (" + intentos + "/5)");
                    reset(3000);
                } else {
                    System.out.println("[CENTRAL] No se encontraron hospitales después de 5 intentos.");
                }
            }
        });
    }

    /**
     * Paso 2: Un hospital acepto. Solicitar ambulancia para el traslado.
     * Formato del contenido:
     *   CAMA_DISPONIBLE|NombreHospital|camasDisponibles
     */
    private void procesarHospitalAcepto(String contenido) {
        String[] partes = contenido.split("\\" + Protocol.SEP);
        String hospitalNombre = partes.length > 1 ? partes[1] : "Hospital";
        String camas          = partes.length > 2 ? partes[2] : "1";

        System.out.println("[CENTRAL] Hospital " + hospitalNombre
                + " acepta al paciente. Camas disponibles: " + camas);
        System.out.println("[CENTRAL] Coordinando traslado...");

        List<AID> ambulancias = DfUtils.buscar(this, Protocol.SERVICIO_AMBULANCIA);

        if (ambulancias.isEmpty()) {
            System.out.println("[CENTRAL] No hay ambulancias disponibles.");
            return;
        }

        ACLMessage traslado = new ACLMessage(ACLMessage.REQUEST);
        traslado.addReceiver(ambulancias.get(0));
        traslado.setOntology(Protocol.ONTOLOGY);
        traslado.setContent(Protocol.SOLICITUD_TRASLADO + Protocol.SEP + hospitalNombre);
        send(traslado);
        System.out.println("[CENTRAL] ➡  Solicitud de traslado enviada a la ambulancia.");
    }

    @Override
    protected void takeDown() {
        DfUtils.desregistrar(this);
        System.out.println("[CENTRAL] Central MINSA desactivada.");
    }
}
