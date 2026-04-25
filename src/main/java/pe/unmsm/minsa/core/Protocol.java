package pe.unmsm.minsa.core;

/**
 * Constantes del protocolo de comunicacion entre agentes.
 * Define los mensajes FIPA-ACL que se intercambian en el sistema.
 *
 * CONTEXTO: Sistema de Derivacion de Pacientes - MINSA Peru
 * Simulacion de la crisis hospitalaria durante el COVID-19 (2020-2021)
 */
public class Protocol {

    // ── Ontologia comun ────────────────────────────────────────────────────
    public static final String ONTOLOGY = "minsa-derivacion-pacientes";

    // ── Niveles de urgencia del paciente ──────────────────────────────────
    public static final String URGENCIA_CRITICA  = "URGENCIA_CRITICA";   // UCI obligatoria
    public static final String URGENCIA_MODERADA = "URGENCIA_MODERADA";  // Emergencia
    public static final String URGENCIA_LEVE     = "URGENCIA_LEVE";      // General

    // ── Tipos de cama ─────────────────────────────────────────────────────
    public static final String TIPO_UCI        = "UCI";
    public static final String TIPO_EMERGENCIA = "EMERGENCIA";
    public static final String TIPO_GENERAL    = "GENERAL";

    // ── Mensajes entre agentes ────────────────────────────────────────────
    public static final String SOLICITUD_CAMA       = "SOLICITUD_CAMA";
    public static final String CAMA_DISPONIBLE      = "CAMA_DISPONIBLE";
    public static final String CAMA_NO_DISPONIBLE   = "CAMA_NO_DISPONIBLE";
    public static final String EVALUAR_PACIENTE     = "EVALUAR_PACIENTE";
    public static final String PACIENTE_ACEPTADO    = "PACIENTE_ACEPTADO";
    public static final String PACIENTE_RECHAZADO   = "PACIENTE_RECHAZADO";
    public static final String SOLICITUD_TRASLADO   = "SOLICITUD_TRASLADO";
    public static final String TRASLADO_CONFIRMADO  = "TRASLADO_CONFIRMADO";

    // ── Servicios registrados en el DF (Paginas Amarillas) ────────────────
    public static final String SERVICIO_CENTRAL    = "central-minsa";
    public static final String SERVICIO_HOSPITAL   = "hospital-minsa";
    public static final String SERVICIO_MEDICO     = "medico-minsa";
    public static final String SERVICIO_AMBULANCIA = "ambulancia-minsa";
    public static final String SERVICIO_PACIENTE   = "paciente-minsa";

    // ── Separador de campos en los mensajes ───────────────────────────────
    public static final String SEP = "|";
}
