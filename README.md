# SMA-JADE: Sistema Inteligente de Derivación de Pacientes MINSA🏥

Sistema Multi-Agente desarrollado con JADE para simular el proceso de derivación de pacientes entre hospitales de la red MINSA en el Perú.


## Descripción del Proyecto

Durante la emergencia sanitaria ocasionada por la COVID-19, uno de los principales problemas en el sistema de salud peruano fue la dificultad para encontrar camas disponibles, especialmente en las unidades de cuidados intensivos (UCI). En muchos casos, los pacientes debían esperar largos periodos mientras se gestionaba su traslado a un hospital con capacidad de atención.

Frente a esta problemática, el presente proyecto propone una solución basada en Sistemas Multi-Agente (SMA), utilizando la plataforma JADE. El sistema permite simular la comunicación y coordinación automática entre distintos actores del proceso de derivación hospitalaria, optimizando la búsqueda de disponibilidad y reduciendo los tiempos de respuesta.

---

## Objetivo de la tarea

Diseñar e implementar un sistema distribuido basado en agentes inteligentes que permita gestionar de manera autónoma la derivación de pacientes críticos entre hospitales de la red MINSA.

Asimismo, el proyecto busca demostrar la aplicación práctica de los conceptos fundamentales de los Sistemas Multi-Agente, tales como:

- Arquitectura distribuida.
- Comunicación mediante mensajes FIPA-ACL.
- Descubrimiento de servicios usando el Directory Facilitator (DF).
- Coordinación y cooperación entre agentes autónomos.
- Implementación utilizando Java, Maven y JADE.

---

##  Agentes del sistema

El sistema se encuentra dividido en dos plataformas distribuidas:

### Plataforma 1 (Central MINSA) —

 Esta plataforma representa el centro de coordinación encargado de recibir las solicitudes de atención y gestionar el proceso de derivación.
| Agente | Rol |
|---|---|
| `AgenteCentral` | Coordina todo el proceso de búsqueda y asignación de camas. |
| `AgentePaciente` |Genera la solicitud de atención médica, enviando la información del paciente |

### Plataforma 2 (P2) — Red de Hospitales
| Agente | Rol |
|---|---|
| `AgenteHospital` | Administra la disponibilidad de camas y responde a las solicitudes |
| `AgenteMedico` | Evalúa la condición clínica del paciente |
| `AgenteAmbulancia` | Gestiona y confirma el traslado del paciente |

---

## Flujo de mensajes FIPA-ACL

```El proceso de derivación sigue la siguiente secuencia:

1. El AgentePaciente reporta una situación crítica a la Central MINSA.
2. El AgenteCentral consulta al Directory Facilitator para localizar hospitales disponibles.
3. Se envía una solicitud al AgenteHospital correspondiente.
4, El AgenteHospital solicita al AgenteMedico la evaluación del caso.
5. Una vez aceptado el paciente, el hospital confirma la disponibilidad de cama.
6. Finalmente, el AgenteCentral coordina el traslado mediante el AgenteAmbulancia.

Este flujo reproduce de manera simplificada un escenario real de derivación hospitalaria.
```

---

## Tecnologías Utilizadas
- Java 17
- Maven 3.8 o superior
- JADE 4.6.0
---


## Estructura del Proyecto

```text
sma-minsa-derivacion/
├── pom.xml
├── README.md
├── libs/  
│   └── jade.jar                            ← Descargar de jade.tilab.com
├── src/main/java/pe/grupo4/minsa/
│   ├── agents/
│   │   ├── AgenteAmbulancia.java
│   │   ├── AgenteCentral.java
│   │   ├── AgenteHospital.java
│   │   ├── AgenteMedico.java
│   │   └── AgentePaciente.java
│   ├── core/
│   │   ├── DfUtils.java                    ← Utilidades para Páginas Amarillas
│   │   └── Protocol.java                   ← Constantes de mensajes
│   └── launch/
│       ├── Platform1Launcher.java          ← Levanta P1 (Central MINSA)
│       └── Platform2Launcher.java          ← Levanta P2 (Hospitales)
└── target/

```

---

##  Requisitos Previos

- Java JDK 17 o superior.
- Maven 3.8 o superior
- JADE 4.6.0 (`jade.jar` en la carpeta `/libs`)

---

## Paso previo: Descargar JADE

1. Descargar JADE desde el sitio oficial de TILAB: https://jade.tilab.com/dl.php?file=JADE-bin-4.6.0.zip  
   *(o buscar "JADE download tilab")*
2. Extraer el contenido del archivo descargado.
3. Copiar el archivo `jade.jar` a la carpeta `libs/` del proyecto

---

##  Cómo ejecutar el proyecto

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

### Ejecución en diferentes equipos:
Si las plataformas se ejecutan en máquinas distintas, reemplazar localhost por la dirección IP del equipo donde se encuentra la Plataforma 1.
```bash
mvn exec:java -Dexec.mainClass="pe.grupo4.minsa.launch.Platform2Launcher" -Dexec.args="192.168.1.10 1099"
```

---

## Resultados Esperados

Durante la ejecución del sistema, se podrá observar:

- Registro automático de los agentes en el Directory Facilitator.
- Intercambio de mensajes FIPA-ACL entre plataformas.
- Selección del hospital con disponibilidad de camas.
- Simulación del rechazo de hospitales saturados.
- Confirmación del traslado con tiempo estimado de llegada.

---

## Caso de Prueba

Un paciente en estado crítico requiere una cama UCI.

-El hospital "Dos de Mayo" no cuenta con disponibilidad.
-El sistema identifica al hospital "Rebagliati" como alternativa.
-Se coordina automáticamente el traslado mediante ambulancia.
-Se informa el tiempo estimado de llegada.

---

## Integrantes:

-Gutierrez Mamani, Joselin Victoria
-Chaco Flores, Jose Luis
-Layme Moya, Victor Hugo
-Matos Ramos, Franco Antonio


##  Links
- Repositorio: https://github.com/Joselinguttierrez/sma-minsa-derivacion
- Video demo: 
