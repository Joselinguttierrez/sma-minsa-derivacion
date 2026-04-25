package pe.unmsm.minsa.agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import pe.unmsm.minsa.core.DfUtils;
import pe.unmsm.minsa.core.Protocol;

import java.util.List;

/**
 * AGENTE HOSPITAL - Plataforma 2
 *
 * Rol: Representa a un hospital de la red MINSA.
 *      Gestiona su disponibilidad de camas y coordina con el medico
 *      para aceptar o rechazar pacientes derivados.
 *
 * Argumentos al crear el agente:
 *   args[0] = nombre del hospital     (ej: "Dos de Mayo")
 *   args[1] = camas UCI disponibles   (ej: "3")
 *   args[2] = camas Emergencia        (ej: "5")
 *   args[3] = camas Generales         (ej: "10")
 */
public class AgenteHospital extends Agent {

    private String nombreHospital;
    private int camasUCI;
    private int camasEmergencia;
    private int camasGenerales;

    // Guardamos datos del remitente para responderle despues de consultar al medico
    private String centralNombre;
    private String tipoCamaSolicitada;
    private String pacienteNombre;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length >= 4) {
            nombreHospital  = (String) args[0];
            camasUCI        = Integer.parseInt((String) args[1]);
            camasEmergencia = Integer.parseInt((String) args[2]);
            camasGenerales  = Integer.parseInt((String) args[3]);
        } else {
            nombreHospital  = getLocalName();
            camasUCI        = 2;
            camasEmergencia = 5;
            camasGenerales  = 10;
        }

        DfUtils.registrar(this, Protocol.SERVICIO_HOSPITAL, "hospital-" + nombreHospital);

        System.out.println("[HOSPITAL:" + nombreHospital + "] 🏥 Hospital activo");
        System.out.println("[HOSPITAL:" + nombreHospital + "]    UCI: " + camasUCI
                + " | Emergencia: " + camasEmergencia
                + " | Generales: " + camasGenerales);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    procesarMensaje(msg);
                } else {
                    block();
                }
            }
        });
    }

    private void procesarMensaje(ACLMessage msg) {
        String contenido = msg.getContent();
        int performativa = msg.getPerformative();

        System.out.println("\n[HOSPITAL:" + nombreHospital + "] 📨 Mensaje de "
                + msg.getSender().getLocalName()
                + " [" + ACLMessage.getPerformative(performativa) + "]: " + contenido);

        if (performativa == ACLMessage.REQUEST
                && contenido.startsWith(Protocol.SOLICITUD_CAMA)) {
            // La Central solicita una cama
            procesarSolicitudDeCama(msg, contenido);

        } else if (performativa == ACLMessage.AGREE
                && contenido.startsWith(Protocol.PACIENTE_ACEPTADO)) {
            // El medico acepto evaluar al paciente → informar a la Central
            informarCentralAceptacion();

        } else if (performativa == ACLMessage.REFUSE
                && contenido.startsWith(Protocol.PACIENTE_RECHAZADO)) {
            // El medico rechazo al paciente
            informarCentralRechazo();
        }
    }

    /**
     * Verifica disponibilidad de camas y, si hay, consulta al medico.
     * Formato del contenido: SOLICITUD_CAMA|tipoCama|urgencia|nombrePaciente
     */
    private void procesarSolicitudDeCama(ACLMessage msg, String contenido) {
        String[] partes = contenido.split("\\" + Protocol.SEP);
        tipoCamaSolicitada = partes.length > 1 ? partes[1] : Protocol.TIPO_UCI;
        String urgencia    = partes.length > 2 ? partes[2] : Protocol.URGENCIA_CRITICA;
        pacienteNombre     = partes.length > 3 ? partes[3] : "Paciente";
        centralNombre      = msg.getSender().getLocalName();

        int disponibles = getCamasDisponibles(tipoCamaSolicitada);

        System.out.println("[HOSPITAL:" + nombreHospital + "] 🛏  Verificando camas "
                + tipoCamaSolicitada + "... Disponibles: " + disponibles);

        if (disponibles > 0) {
            // Hay camas: consultar al medico antes de confirmar
            List<AID> medicos = DfUtils.buscar(this, Protocol.SERVICIO_MEDICO);

            if (!medicos.isEmpty()) {
                ACLMessage consultaMedico = new ACLMessage(ACLMessage.REQUEST);
                consultaMedico.addReceiver(medicos.get(0));
                consultaMedico.setOntology(Protocol.ONTOLOGY);
                consultaMedico.setContent(Protocol.EVALUAR_PACIENTE
                        + Protocol.SEP + urgencia
                        + Protocol.SEP + pacienteNombre
                        + Protocol.SEP + tipoCamaSolicitada);
                send(consultaMedico);
                System.out.println("[HOSPITAL:" + nombreHospital + "] ➡  Consultando al medico sobre el paciente...");
            } else {
                // No hay medico disponible, aceptar directamente
                System.out.println("[HOSPITAL:" + nombreHospital + "] ⚠  Sin medico. Aceptando directamente.");
                informarCentralAceptacion();
            }
        } else {
            // Sin camas: rechazar inmediatamente
            List<AID> centrales = DfUtils.buscar(this, Protocol.SERVICIO_CENTRAL);
            if (!centrales.isEmpty()) {
                ACLMessage rechazo = new ACLMessage(ACLMessage.REFUSE);
                rechazo.addReceiver(centrales.get(0));
                rechazo.setOntology(Protocol.ONTOLOGY);
                rechazo.setContent(Protocol.CAMA_NO_DISPONIBLE + Protocol.SEP + nombreHospital);
                send(rechazo);
                System.out.println("[HOSPITAL:" + nombreHospital + "] ❌ Sin camas "
                        + tipoCamaSolicitada + ". Rechazo enviado a Central.");
            }
        }
    }

    /**
     * El medico dio el visto bueno. Informar a la Central que el hospital acepta.
     */
    private void informarCentralAceptacion() {
        reducirCamas(tipoCamaSolicitada);
        int restantes = getCamasDisponibles(tipoCamaSolicitada);

        List<AID> centrales = DfUtils.buscar(this, Protocol.SERVICIO_CENTRAL);
        if (!centrales.isEmpty()) {
            ACLMessage aceptacion = new ACLMessage(ACLMessage.AGREE);
            aceptacion.addReceiver(centrales.get(0));
            aceptacion.setOntology(Protocol.ONTOLOGY);
            aceptacion.setContent(Protocol.CAMA_DISPONIBLE
                    + Protocol.SEP + nombreHospital
                    + Protocol.SEP + restantes);
            send(aceptacion);
            System.out.println("[HOSPITAL:" + nombreHospital + "] ✅ Cama asignada para "
                    + pacienteNombre + ". Camas " + tipoCamaSolicitada + " restantes: " + restantes);
        }
    }

    /**
     * El medico rechazo. Informar a la Central.
     */
    private void informarCentralRechazo() {
        List<AID> centrales = DfUtils.buscar(this, Protocol.SERVICIO_CENTRAL);
        if (!centrales.isEmpty()) {
            ACLMessage rechazo = new ACLMessage(ACLMessage.REFUSE);
            rechazo.addReceiver(centrales.get(0));
            rechazo.setOntology(Protocol.ONTOLOGY);
            rechazo.setContent(Protocol.CAMA_NO_DISPONIBLE
                    + Protocol.SEP + nombreHospital + " (medico rechazo)");
            send(rechazo);
        }
    }

    private int getCamasDisponibles(String tipo) {
        switch (tipo) {
            case Protocol.TIPO_UCI:        return camasUCI;
            case Protocol.TIPO_EMERGENCIA: return camasEmergencia;
            default:                       return camasGenerales;
        }
    }

    private void reducirCamas(String tipo) {
        switch (tipo) {
            case Protocol.TIPO_UCI:        if (camasUCI > 0)        camasUCI--;        break;
            case Protocol.TIPO_EMERGENCIA: if (camasEmergencia > 0) camasEmergencia--; break;
            default:                       if (camasGenerales > 0)  camasGenerales--;  break;
        }
    }

    @Override
    protected void takeDown() {
        DfUtils.desregistrar(this);
        System.out.println("[HOSPITAL:" + nombreHospital + "] Hospital desregistrado.");
    }
}
