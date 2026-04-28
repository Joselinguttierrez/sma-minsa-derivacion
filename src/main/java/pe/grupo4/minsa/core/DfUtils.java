package pe.grupo4.minsa.core;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 * Utilidades para el Directory Facilitator (DF) de JADE.
 *
 * El DF actua como las "Paginas Amarillas" del sistema:
 * - Los agentes se REGISTRAN al iniciar (como publicar un anuncio)
 * - Los agentes BUSCAN a otros por tipo de servicio
 * - Los agentes se DESREGISTRAN al terminar
 */
public class DfUtils {

    /**
     * Registra un agente en el DF con un tipo y nombre de servicio.
     */
    public static void registrar(Agent agente, String tipoServicio, String nombreServicio) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agente.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipoServicio);
        sd.setName(nombreServicio);
        dfd.addServices(sd);

        try {
            DFService.register(agente, dfd);
            System.out.println("[DF] " + agente.getLocalName()
                    + " registrado como: " + tipoServicio);
        } catch (FIPAException e) {
            System.err.println("[DF] Error al registrar " + agente.getLocalName()
                    + ": " + e.getMessage());
        }
    }

    /**
     * Busca agentes en el DF segun el tipo de servicio.
     * Retorna una lista de AIDs (identificadores de agentes encontrados).
     */
    public static List<AID> buscar(Agent agente, String tipoServicio) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipoServicio);
        template.addServices(sd);

        List<AID> resultado = new ArrayList<>();
        try {
            DFAgentDescription[] encontrados = DFService.search(agente, template);
            for (DFAgentDescription d : encontrados) {
                resultado.add(d.getName());
            }
            System.out.println("[DF] 🔍 " + agente.getLocalName()
                    + " encontro " + resultado.size()
                    + " agente(s) de tipo: " + tipoServicio);
        } catch (FIPAException e) {
            System.err.println("[DF] Error en busqueda: " + e.getMessage());
        }
        return resultado;
    }

    /**
     * Desregistra el agente del DF al finalizar.
     */
    public static void desregistrar(Agent agente) {
        try {
            DFService.deregister(agente);
            System.out.println("[DF] 🗑 " + agente.getLocalName() + " desregistrado.");
        } catch (FIPAException e) {
            System.err.println("[DF] Error al desregistrar: " + e.getMessage());
        }
    }
}
