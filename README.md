# SMA-JADE: Sistema de Derivación de Pacientes MINSA 🏥

Proyecto de Sistema Multi-Agente con JADE para la gestión autónoma de derivación
de pacientes entre hospitales de la red MINSA - Perú.

> **Contexto:** Durante la pandemia COVID-19 (2020-2021), el Perú tuvo una de las
> tasas de mortalidad más altas del mundo. Pacientes críticos perdían horas buscando
> camas UCI disponibles. Este sistema simula cómo los agentes inteligentes pueden
> resolver ese problema en tiempo real.

---

## 🎯 Objetivo de la tarea

Demostrar:
- ✅ Entorno distribuido (P1 y P2)
- ✅ Mínimo 4 agentes (tenemos 5)
- ✅ Uso de páginas amarillas (DF)
- ✅ Intercambio de mensajes FIPA-ACL
- ✅ Lógica colaborativa entre agentes
- ✅ Implementación en Maven + Java

---

## 🤖 Agentes del sistema

### Plataforma 1 (P1) — Central MINSA
| Agente | Rol |
|---|---|
| `AgenteCentral` | Coordinador: recibe solicitudes, busca hospitales en DF, coordina traslado |
| `AgentePaciente` | Reporta urgencia, síntomas y necesidad de cama a la Central |

### Plataforma 2 (P2) — Red de Hospitales
| Agente | Rol |
|---|---|
| `AgenteHospital` | Gestiona disponibilidad de camas (UCI/Emergencia/General) |
| `AgenteMedico` | Evalúa clínicamente si puede aceptar al paciente |
| `AgenteAmbulancia` | Confirma el traslado físico con ETA estimado |

---

## 🔄 Flujo de mensajes FIPA-ACL

```
AgentePaciente  →  AgenteCentral    INFORM   URGENCIA_CRITICA|sintomas|ubicacion|UCI|Juan
AgenteCentral   →  DF               BUSCA    hospitales disponibles
AgenteCentral   →  AgenteHospital   REQUEST  SOLICITUD_CAMA|UCI|URGENCIA_CRITICA|Juan
AgenteHospital  →  AgenteMedico     REQUEST  EVALUAR_PACIENTE|URGENCIA_CRITICA|Juan|UCI
AgenteMedico    →  AgenteHospital   AGREE    PACIENTE_ACEPTADO|Juan
AgenteHospital  →  AgenteCentral    AGREE    CAMA_DISPONIBLE|Rebagliati|2
AgenteCentral   →  AgenteAmbulancia REQUEST  SOLICITUD_TRASLADO|Rebagliati
AgenteAmbulancia→  AgenteCentral    INFORM   TRASLADO_CONFIRMADO|Rebagliati|ETA:8min
```

---

## 🏗 Estructura del proyecto

```
sma-minsa/
├── pom.xml
├── libs/
│   └── jade.jar                    ← Descargar de jade.tilab.com
└── src/main/java/pe/unmsm/minsa/
    ├── core/
    │   ├── Protocol.java           ← Constantes de mensajes
    │   └── DfUtils.java            ← Utilidades para Páginas Amarillas
    ├── agents/
    │   ├── AgentePaciente.java
    │   ├── AgenteCentral.java
    │   ├── AgenteHospital.java
    │   ├── AgenteMedico.java
    │   └── AgenteAmbulancia.java
    └── launch/
        ├── Platform1Launcher.java  ← Levanta P1 (Central MINSA)
        └── Platform2Launcher.java  ← Levanta P2 (Hospitales)
```

---

## ⚙ Requisitos

- Java 17+
- Maven 3.8+
- JADE 4.6.0 (`jade.jar` en la carpeta `/libs`)

---

## 📥 Paso previo: Descargar JADE

1. Ir a: https://jade.tilab.com/dl.php?file=JADE-bin-4.6.0.zip  
   *(o buscar "JADE download tilab")*
2. Descomprimir el zip
3. Copiar el archivo `jade.jar` a la carpeta `libs/` del proyecto

---

## 🚀 Cómo ejecutar

### 1) Compilar
```bash
mvn clean package -DskipTests
```

### 2) Levantar P1 (Main Container + GUI de JADE)
```bash
mvn exec:java -Dexec.mainClass="pe.grupo4.minsa.launch.Platform1Launcher"
```

### 3) Levantar P2 (en otra terminal)
```bash
mvn exec:java -Dexec.mainClass="pe.grupo4.minsa.launch.Platform2Launcher" -Dexec.args="localhost 1099"
```

### En dos máquinas/VMs distintas
Reemplaza `localhost` por la IP de la máquina donde corre P1:
```bash
mvn exec:java -Dexec.mainClass="pe.grupo4.minsa.launch.Platform2Launcher" -Dexec.args="192.168.1.10 1099"
```

---

## 📋 Evidencias para la sustentación

- Registros del DF (páginas amarillas) con cada agente
- Mensajes FIPA-ACL entre contenedores P1 y P2
- Hospital "Dos de Mayo" saturado → sistema deriva a "Rebagliati"
- Confirmación de traslado con ETA por la ambulancia
- GUI de JADE mostrando ambos contenedores conectados

---

## 👥 Integrantes del grupo

*(Agregar nombres aquí)*

## 🔗 Links
- Repositorio: *(agregar link de GitHub)*
- Video demo: *(agregar link de YouTube)*
