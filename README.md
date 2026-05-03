# PropTech — Plataforma de Gestión Inmobiliaria

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=flat-square&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/React-18.3.1-61DAFB?style=flat-square&logo=react&logoColor=white"/>
  <img src="https://img.shields.io/badge/Vite-5.2-646CFF?style=flat-square&logo=vite&logoColor=white"/>
  <img src="https://img.shields.io/badge/Estado-En_desarrollo-yellow?style=flat-square"/>
</p>

<p align="center">
  Plataforma inmobiliaria completa con backend en Spring Boot y frontend en React/Vite — gestiona inmuebles, clientes, asesores, visitas y operaciones usando 7 estructuras de datos implementadas desde cero en Java.
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

- **Gestión de inmuebles** — registro, actualización, eliminación y filtrado por precio, zona, tipo y disponibilidad
- **Perfil de clientes** — preferencias, favoritos (quitar favorito), historial de interacciones y estado de búsqueda
- **Agenda de visitas** — cola FIFO para visitas estándar, cola de prioridad para citas VIP o urgentes; cambio de estado inline
- **Sistema de deshacer** — revertir cambios recientes en publicaciones mediante una pila
- **Recomendaciones inteligentes** — sugerencias por historial (BFS en grafo) y por perfil del cliente; visualización del grafo
- **Alertas automáticas** — contratos próximos a vencer, inmuebles sin actividad, clientes sin seguimiento
- **Detección de anomalías** — inmuebles con muchas visitas sin cierre, asesores sobrecargados, cambios de precio frecuentes
- **Reportes y rankings** — actividad por zona, efectividad de asesores, ordenación BST por precio

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

**Backend**
- **Java 21** — lenguaje principal
- **Spring Boot 4.0.5** — arquitectura MVC por capas, API REST
- **Maven** — gestión de dependencias
- **Lombok** — reducción de código repetitivo
- **Almacenamiento en memoria** — sin base de datos; los datos viven en las estructuras propias

**Frontend**
- **React 18.3.1** — UI por componentes
- **Vite 5.2** — bundler y servidor de desarrollo
- **React Router 6** — navegación entre páginas
- **Axios 1.7** — cliente HTTP para consumir la API
- **Lucide React** — iconografía

---

## Estructura del proyecto

```
Inmobiliaria/
├── src/main/java/com/proyecto/inmobiliaria/
│   ├── PropTechApplication.java
│   ├── DataLoader.java           # Datos de ejemplo al arrancar
│   ├── model/                    # Entidades + enums
│   ├── controller/               # Endpoints REST (@RestController)
│   ├── service/                  # Lógica de negocio (@Service)
│   ├── repository/               # Acceso a datos en memoria (@Repository)
│   └── config/                   # Configuración CORS
│       └── util/                 # Estructuras de datos propias
│           ├── ArbolBST.java
│           ├── GrafoClienteInmueble.java
│           ├── PilaAcciones.java
│           ├── ColaAtencion.java
│           └── ColaPrioridadVisitas.java
│
└── frontend/
    ├── index.html
    ├── vite.config.js            # Proxy → localhost:8080
    └── src/
        ├── main.jsx
        ├── App.jsx               # Rutas (React Router)
        ├── api/
        │   └── index.js          # Todas las llamadas HTTP (Axios)
        ├── components/
        │   ├── layout/           # AppShell, Sidebar, TopBar
        │   └── ui/               # DSBadge, StatusBadge, Modal, KpiCard, GrafoPanel
        └── pages/
            ├── Dashboard.jsx
            ├── Inmuebles.jsx
            ├── InmuebleDetalle.jsx
            ├── Clientes.jsx
            ├── Asesores.jsx
            ├── Visitas.jsx
            ├── Operaciones.jsx
            ├── Alertas.jsx
            ├── Anomalias.jsx
            ├── Recomendaciones.jsx
            └── Reportes.jsx
```

---

## Instalación y ejecución

**Requisitos:** Java 21+, Maven, Node.js 18+

### Backend

```bash
# Desde la raíz del proyecto
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

La interfaz estará disponible en `http://localhost:5173`.  
El proxy de Vite redirige automáticamente todas las peticiones al backend (`/inmuebles`, `/clientes`, etc. → `http://localhost:8080`).

---

## Endpoints

<details>
<summary><strong>Inmuebles</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/inmuebles` | Listar todos los inmuebles |
| `GET` | `/inmuebles/{codigo}` | Obtener inmueble por código |
| `POST` | `/inmuebles` | Registrar un nuevo inmueble |
| `PUT` | `/inmuebles/{codigo}` | Actualizar un inmueble |
| `DELETE` | `/inmuebles/{codigo}` | Eliminar un inmueble |
| `GET` | `/inmuebles/filtrar` | Filtrar por precio, zona, tipo, habitaciones |
| `GET` | `/inmuebles/rango-precio` | Consulta por rango de precios *(BST)* |
| `GET` | `/inmuebles/ordenados` | Listado ordenado por precio *(BST inorden)* |
| `POST` | `/inmuebles/{codigo}/deshacer` | Deshacer último cambio *(Pila)* |

</details>

<details>
<summary><strong>Clientes</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/clientes` | Listar todos los clientes |
| `GET` | `/clientes/{id}` | Obtener perfil del cliente |
| `POST` | `/clientes` | Registrar un nuevo cliente |
| `PUT` | `/clientes/{id}` | Actualizar datos del cliente |
| `DELETE` | `/clientes/{id}` | Eliminar un cliente |
| `GET` | `/clientes/{id}/historial` | Historial de interacciones |
| `DELETE` | `/clientes/{id}/favoritos/{codigo}` | Quitar un inmueble de favoritos |

</details>

<details>
<summary><strong>Asesores</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/asesores` | Listar todos los asesores |
| `GET` | `/asesores/{id}` | Obtener asesor por ID |
| `POST` | `/asesores` | Registrar un nuevo asesor |
| `PUT` | `/asesores/{id}` | Actualizar datos del asesor |
| `DELETE` | `/asesores/{id}` | Eliminar un asesor |

</details>

<details>
<summary><strong>Visitas</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/visitas` | Listar todas las visitas |
| `GET` | `/visitas/{id}` | Obtener visita por ID |
| `POST` | `/visitas` | Agendar una nueva visita |
| `PUT` | `/visitas/{id}/estado` | Actualizar estado de visita |
| `GET` | `/visitas/prioritarias` | Cola de prioridad *(Max-Heap)* |
| `POST` | `/visitas/atender` | Atender siguiente de la cola FIFO |
| `POST` | `/visitas/despachar` | Despachar visita prioritaria del heap |

</details>

<details>
<summary><strong>Operaciones</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/operaciones` | Operaciones activas (EN_PROCESO) |
| `GET` | `/operaciones/historial` | Historial completo *(ArrayList)* |
| `POST` | `/operaciones` | Registrar una nueva operación |
| `PUT` | `/operaciones/{id}/cerrar` | Cerrar operación (→ CERRADA) |
| `PUT` | `/operaciones/{id}/cancelar` | Cancelar operación (→ CANCELADA) |

</details>

<details>
<summary><strong>Alertas</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/alertas` | Alertas activas (no resueltas) |
| `GET` | `/alertas/historial` | Historial completo de alertas |
| `PUT` | `/alertas/{id}/resolver` | Marcar alerta como resuelta |

</details>

<details>
<summary><strong>Búsqueda y Recomendaciones</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/busqueda/compatibles/{idCliente}` | Inmuebles compatibles con el perfil del cliente |
| `GET` | `/recomendaciones/{idCliente}` | Recomendaciones por historial *(BFS en grafo)* |
| `GET` | `/recomendaciones/{idCliente}/interacciones` | Inmuebles con arista en el grafo |

</details>

<details>
<summary><strong>Reportes</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/reportes/zonas` | Ranking de zonas por actividad |
| `GET` | `/reportes/asesores` | Ranking de asesores por cierres |

</details>

<details>
<summary><strong>Detección de anomalías</strong></summary>

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/deteccion/ejecutar` | Ejecutar todos los módulos de detección |
| `GET` | `/deteccion/inmuebles-sin-cierre` | Inmuebles con visitas repetidas sin operación |
| `GET` | `/deteccion/clientes-sin-seguimiento` | Clientes activos sin visitas recientes |
| `GET` | `/deteccion/asesores-sobrecarga` | Asesores con exceso de visitas asignadas |
| `GET` | `/deteccion/cambios-precio` | Inmuebles con cambios de precio frecuentes |
| `GET` | `/deteccion/concentracion-zona` | Zonas con alta concentración de visitas |

</details>

---

## Autores

| Nombre | GitHub |
|--------|--------|
| Nicolas Muñoz Viveros | https://github.com/quiso888 |
| Santiago Ramirez Bernal | https://github.com/SantiagoRB17 |
| Samuel Saith Calle Santa | https://github.com/Samuelk64 |

---

<p align="center">
  <sub>Estructuras de Datos — Universidad del Quindío · 2026-1</sub>
</p>
