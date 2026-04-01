# PropTech — Plataforma de Gestión Inmobiliaria

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=flat-square&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-3.9.14-C71A36?style=flat-square&logo=apachemaven&logoColor=white"/>
  <img src="https://img.shields.io/badge/Estado-En_desarrollo-yellow?style=flat-square"/>
</p>

<p align="center">
  API REST backend que simula una plataforma inmobiliaria moderna — gestiona inmuebles, clientes, asesores, visitas y operaciones de negocio usando estructuras de datos implementadas desde cero en Java.
</p>

---

## Descripción

PropTech va más allá de un CRUD convencional. Aplica **7 estructuras de datos** para resolver problemas reales dentro del contexto inmobiliario: búsquedas directas en O(1), historial con capacidad de deshacer, colas de prioridad para visitas urgentes, consultas por rango de precios con árbol BST y un grafo que modela las relaciones entre clientes e inmuebles para generar recomendaciones inteligentes.

```
Cliente → Asesor → Inmueble
    ↕        ↕         ↕
 Visita  Operación  Alerta
    └───── Grafo ────┘
```

---

## Funcionalidades

- **Gestión de inmuebles** — registro, actualización y filtrado por precio, zona, tipo y disponibilidad
- **Perfil de clientes** — preferencias, favoritos, historial de interacciones y estado de búsqueda
- **Agenda de visitas** — cola FIFO para visitas estándar, cola de prioridad para citas VIP o urgentes
- **Sistema de deshacer** — revertir cambios recientes en publicaciones mediante una pila
- **Recomendaciones inteligentes** — sugerencias basadas en el perfil del cliente y similitud por grafo
- **Alertas automáticas** — contratos próximos a vencer, inmuebles sin actividad, clientes sin seguimiento
- **Detección de anomalías** — inmuebles con muchas visitas sin cierre, asesores sobrecargados, cambios de precio frecuentes
- **Reportes y rankings** — actividad por zona, efectividad de asesores, simulación de demanda

---

## Estructuras de datos

Cada estructura está implementada manualmente y justificada por el problema concreto que resuelve.

| Estructura | Ubicación | Problema que resuelve | Complejidad |
|-----------|-----------|----------------------|-------------|
| `HashMap` | Todos los repositorios | Búsqueda directa por identificador | O(1) prom. |
| `ArrayList` | Historial, favoritos | Historial ordenado por inserción | O(1) append |
| `Pila` | `PilaAcciones` | Deshacer cambios en publicaciones | O(1) push/pop |
| `Cola` | `ColaAtencion` | Atención de solicitudes FIFO | O(1) enqueue/dequeue |
| `Cola de prioridad` | `ColaPrioridadVisitas` | Visitas urgentes o VIP primero | O(log n) |
| `BST` | `ArbolBST` | Consultas por rango de precios | O(log n) prom. |
| `Grafo` | `GrafoClienteInmueble` | Red cliente–inmueble y recomendaciones | O(V+E) BFS |

---

## Tecnologías

- **Java 21** — lenguaje principal
- **Spring Boot 4.0.5** — arquitectura MVC por capas, API REST
- **Maven** — gestión de dependencias
- **Lombok** — reducción de código repetitivo
- **Almacenamiento en memoria** — sin base de datos; los datos viven en las estructuras propias

---

## Estructura del proyecto

```
src/main/java/com/proptech/inmobiliaria/
├── model/          # Entidades del dominio + enums (Inmueble, Cliente, Asesor, Visita...)
├── controller/     # Endpoints REST (@RestController)
├── service/        # Lógica de negocio (@Service)
├── repository/     # Acceso a datos en memoria (@Repository)
└── util/           # Implementaciones propias de estructuras de datos
    ├── ArbolBST.java
    ├── GrafoClienteInmueble.java
    ├── PilaAcciones.java
    ├── ColaAtencion.java
    └── ColaPrioridadVisitas.java
```

---

## Instalación y ejecución

**Requisitos:** Java 21+, Maven 3.9.14+

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/proptech-inmobiliaria.git
cd proptech-inmobiliaria

# Ejecutar
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.  

---

## Endpoints

<details>
<summary><strong>Inmuebles</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/inmuebles` | Listar todos los inmuebles |
| `GET` | `/api/inmuebles/{codigo}` | Obtener inmueble por código |
| `POST` | `/api/inmuebles` | Registrar un nuevo inmueble |
| `PUT` | `/api/inmuebles/{codigo}` | Actualizar un inmueble |
| `DELETE` | `/api/inmuebles/{codigo}` | Eliminar un inmueble |
| `GET` | `/api/inmuebles/filtrar` | Filtrar por precio, zona, tipo, habitaciones |
| `GET` | `/api/inmuebles/rango-precio` | Consulta por rango de precios *(BST)* |
| `POST` | `/api/inmuebles/{codigo}/deshacer` | Deshacer último cambio *(Pila)* |

</details>

<details>
<summary><strong>Clientes</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/clientes/{id}` | Obtener perfil del cliente |
| `POST` | `/api/clientes` | Registrar un nuevo cliente |
| `PUT` | `/api/clientes/{id}` | Actualizar datos del cliente |
| `GET` | `/api/clientes/{id}/historial` | Historial de interacciones |
| `GET` | `/api/clientes/{id}/recomendaciones` | Recomendaciones personalizadas *(Grafo)* |

</details>

<details>
<summary><strong>Visitas y operaciones</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/visitas` | Agendar una visita |
| `PUT` | `/api/visitas/{id}/estado` | Actualizar estado de visita |
| `GET` | `/api/visitas/pendientes` | Cola de visitas pendientes *(FIFO)* |
| `GET` | `/api/visitas/prioritarias` | Visitas urgentes o VIP *(Cola de prioridad)* |
| `GET` | `/api/alertas` | Alertas activas del sistema |
| `GET` | `/api/anomalias` | Comportamientos inusuales detectados |
| `GET` | `/api/reportes/zonas` | Ranking de zonas por actividad |
| `GET` | `/api/reportes/asesores` | Ranking de asesores por efectividad |

</details>

---

## Autores

| Nombre | GitHub |
|--------|--------|
| Nicolas Muñoz Viveros | — |
| Santiago Ramirez Bernal | https://github.com/SantiagoRB17 |
| Samuel Saith Calle Santa | https://github.com/Samuelk64 |

---

<p align="center">
  <sub>Estructuras de Datos — Universidad del Quindío · 2026-1</sub>
</p>
