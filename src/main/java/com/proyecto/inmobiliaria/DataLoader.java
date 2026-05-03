package com.proyecto.inmobiliaria;

import com.proyecto.inmobiliaria.model.*;
import com.proyecto.inmobiliaria.model.enums.*;
import com.proyecto.inmobiliaria.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Carga datos de ejemplo al arrancar la aplicación.
 * Llama directamente a los Services para respetar las validaciones de negocio.
 *
 * Orden de carga:
 *   1. Asesores  (requisito para inmuebles)
 *   2. Inmuebles (requisito para visitas y operaciones)
 *   3. Clientes  (requisito para visitas y operaciones)
 *   4. Visitas
 *   5. Operaciones (una activa y una cerrada)
 *   6. Interacciones grafo (favoritos + consultas)
 *   7. Alertas de ejemplo
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final AsesorService asesorService;
    private final InmuebleService inmuebleService;
    private final ClienteService clienteService;
    private final VisitaService visitaService;
    private final OperacionService operacionService;
    private final AlertaService alertaService;

    @Override
    public void run(String... args) {
        System.out.println("=== PropTech DataLoader: cargando datos de prueba ===");

        cargarAsesores();
        cargarInmuebles();
        cargarClientes();
        cargarVisitas();
        cargarOperaciones();
        registrarInteraccionesGrafo();
        generarAlertasEjemplo();

        System.out.println("=== DataLoader completo ✓ ===");
    }

    //  1. Asesores
    private void cargarAsesores() {
        asesorService.registrar(Asesor.builder()
                .identificacion("ASE-001")
                .nombre("Carlos Mejía")
                .contacto("carlos.mejia@proptech.co")
                .especialidad("Residencial")
                .zonaAsignada("Medellín")
                .cierresRealizados(5)
                .build());

        asesorService.registrar(Asesor.builder()
                .identificacion("ASE-002")
                .nombre("Ana López")
                .contacto("ana.lopez@proptech.co")
                .especialidad("Comercial")
                .zonaAsignada("Bogotá")
                .cierresRealizados(8)
                .build());

        asesorService.registrar(Asesor.builder()
                .identificacion("ASE-003")
                .nombre("Juan Torres")
                .contacto("juan.torres@proptech.co")
                .especialidad("Residencial")
                .zonaAsignada("Cali")
                .cierresRealizados(3)
                .build());

        System.out.println("  → 3 asesores cargados");
    }

    //  2. Inmuebles
    private void cargarInmuebles() {
        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-001")
                .direccion("Calle 10 # 43-55")
                .ciudad("Medellín")
                .barrio("El Poblado")
                .tipo(TipoInmueble.CASA)
                .finalidad(Finalidad.VENTA)
                .precio(450_000_000)
                .area(180)
                .habitaciones(4)
                .banos(3)
                .estado(EstadoInmueble.USADO)
                .disponible(true)
                .codigoAsesor("ASE-001")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-002")
                .direccion("Carrera 65 # 34-12")
                .ciudad("Medellín")
                .barrio("Laureles")
                .tipo(TipoInmueble.APARTAMENTO)
                .finalidad(Finalidad.ARRIENDO)
                .precio(2_500_000)
                .area(75)
                .habitaciones(2)
                .banos(2)
                .estado(EstadoInmueble.NUEVO)
                .disponible(true)
                .codigoAsesor("ASE-001")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-003")
                .direccion("Carrera 7 # 72-41")
                .ciudad("Bogotá")
                .barrio("Chapinero")
                .tipo(TipoInmueble.OFICINA)
                .finalidad(Finalidad.VENTA)
                .precio(380_000_000)
                .area(120)
                .habitaciones(0)
                .banos(2)
                .estado(EstadoInmueble.REMODELADO)
                .disponible(true)
                .codigoAsesor("ASE-002")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-004")
                .direccion("Calle 140 # 7-50")
                .ciudad("Bogotá")
                .barrio("Usaquén")
                .tipo(TipoInmueble.LOCAL_COMERCIAL)
                .finalidad(Finalidad.ARRIENDO)
                .precio(4_200_000)
                .area(60)
                .habitaciones(0)
                .banos(1)
                .estado(EstadoInmueble.USADO)
                .disponible(true)
                .codigoAsesor("ASE-002")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-005")
                .direccion("Avenida 2N # 24-51")
                .ciudad("Cali")
                .barrio("Ciudad Jardín")
                .tipo(TipoInmueble.APARTAMENTO)
                .finalidad(Finalidad.VENTA)
                .precio(220_000_000)
                .area(90)
                .habitaciones(3)
                .banos(2)
                .estado(EstadoInmueble.NUEVO)
                .disponible(true)
                .codigoAsesor("ASE-003")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-006")
                .direccion("Calle 30 # 52-15")
                .ciudad("Medellín")
                .barrio("Guayabal")
                .tipo(TipoInmueble.BODEGA)
                .finalidad(Finalidad.ARRIENDO)
                .precio(6_000_000)
                .area(400)
                .habitaciones(0)
                .banos(2)
                .estado(EstadoInmueble.USADO)
                .disponible(true)
                .codigoAsesor("ASE-001")
                .build());

        // INM-007 estará disponible al crear pero será cerrada por la operación OP-002
        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-007")
                .direccion("Carrera 11 # 93-20")
                .ciudad("Bogotá")
                .barrio("Rosales")
                .tipo(TipoInmueble.CASA)
                .finalidad(Finalidad.VENTA)
                .precio(750_000_000)
                .area(280)
                .habitaciones(5)
                .banos(4)
                .estado(EstadoInmueble.REMODELADO)
                .disponible(true)
                .codigoAsesor("ASE-002")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-008")
                .direccion("Km 3 vía Pance")
                .ciudad("Cali")
                .barrio("Pance")
                .tipo(TipoInmueble.LOTE)
                .finalidad(Finalidad.VENTA)
                .precio(180_000_000)
                .area(800)
                .habitaciones(0)
                .banos(0)
                .estado(EstadoInmueble.NUEVO)
                .disponible(true)
                .codigoAsesor("ASE-003")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-009")
                .direccion("Carrera 52 # 49-18")
                .ciudad("Medellín")
                .barrio("El Centro")
                .tipo(TipoInmueble.APARTAMENTO)
                .finalidad(Finalidad.ARRIENDO)
                .precio(1_800_000)
                .area(55)
                .habitaciones(2)
                .banos(1)
                .estado(EstadoInmueble.USADO)
                .disponible(true)
                .codigoAsesor("ASE-001")
                .build());

        inmuebleService.registrar(Inmueble.builder()
                .codigo("INM-010")
                .direccion("Calle 5 # 34-22")
                .ciudad("Cali")
                .barrio("Olímpico")
                .tipo(TipoInmueble.CASA)
                .finalidad(Finalidad.VENTA)
                .precio(310_000_000)
                .area(140)
                .habitaciones(3)
                .banos(2)
                .estado(EstadoInmueble.USADO)
                .disponible(true)
                .codigoAsesor("ASE-003")
                .build());

        System.out.println("  → 10 inmuebles cargados");
    }

    //  3. Clientes

    private void cargarClientes() {
        clienteService.registrar(Cliente.builder()
                .identificacion("CLI-001")
                .nombre("María García")
                .correo("maria.garcia@gmail.com")
                .telefono("3001234567")
                .tipoCliente(TipoCliente.COMPRADOR)
                .presupuesto(500_000_000)
                .zonasInteres(List.of("Medellín", "El Poblado"))
                .tipoDeseado(TipoInmueble.CASA)
                .habitacionesMin(3)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build());

        clienteService.registrar(Cliente.builder()
                .identificacion("CLI-002")
                .nombre("Carlos Rodríguez")
                .correo("carlos.rodriguez@empresa.co")
                .telefono("3107654321")
                .tipoCliente(TipoCliente.VIP)
                .presupuesto(1_000_000_000)
                .zonasInteres(List.of("Bogotá", "Chapinero", "Rosales"))
                .tipoDeseado(TipoInmueble.OFICINA)
                .habitacionesMin(0)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build());

        clienteService.registrar(Cliente.builder()
                .identificacion("CLI-003")
                .nombre("Ana Martínez")
                .correo("ana.martinez@gmail.com")
                .telefono("3159876543")
                .tipoCliente(TipoCliente.ARRENDATARIO)
                .presupuesto(3_000_000)
                .zonasInteres(List.of("Medellín", "Laureles"))
                .tipoDeseado(TipoInmueble.APARTAMENTO)
                .habitacionesMin(2)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build());

        clienteService.registrar(Cliente.builder()
                .identificacion("CLI-004")
                .nombre("Luis Pérez")
                .correo("luis.perez@hotmail.com")
                .telefono("3204567890")
                .tipoCliente(TipoCliente.COMPRADOR)
                .presupuesto(300_000_000)
                .zonasInteres(List.of("Cali", "Ciudad Jardín"))
                .tipoDeseado(TipoInmueble.APARTAMENTO)
                .habitacionesMin(2)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build());

        clienteService.registrar(Cliente.builder()
                .identificacion("CLI-005")
                .nombre("Sofía Herrera")
                .correo("sofia.herrera@empresa.co")
                .telefono("3112345678")
                .tipoCliente(TipoCliente.VIP)
                .presupuesto(800_000_000)
                .zonasInteres(List.of("Bogotá", "Rosales", "Usaquén"))
                .tipoDeseado(TipoInmueble.CASA)
                .habitacionesMin(4)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build());

        System.out.println("  → 5 clientes cargados");
    }

    //  4. Visitas

    private void cargarVisitas() {
        LocalDate hoy = LocalDate.now();

        visitaService.solicitarVisita(Visita.builder()
                .idVisita("VIS-001")
                .codigoInmueble("INM-001")
                .idCliente("CLI-001")
                .idAsesor("ASE-001")
                .fecha(hoy.plusDays(1))
                .hora(LocalTime.of(10, 0))
                .observaciones("Cliente interesada en la terraza y el garaje.")
                .build());

        visitaService.solicitarVisita(Visita.builder()
                .idVisita("VIS-002")
                .codigoInmueble("INM-003")
                .idCliente("CLI-002")
                .idAsesor("ASE-002")
                .fecha(hoy.plusDays(2))
                .hora(LocalTime.of(14, 0))
                .observaciones("Cliente VIP, preparar presentación completa.")
                .build());

        visitaService.solicitarVisita(Visita.builder()
                .idVisita("VIS-003")
                .codigoInmueble("INM-005")
                .idCliente("CLI-004")
                .idAsesor("ASE-003")
                .fecha(hoy.plusDays(1))
                .hora(LocalTime.of(11, 0))
                .observaciones("Primera visita, cliente nuevo.")
                .build());

        visitaService.solicitarVisita(Visita.builder()
                .idVisita("VIS-004")
                .codigoInmueble("INM-009")
                .idCliente("CLI-003")
                .idAsesor("ASE-001")
                .fecha(hoy.plusDays(3))
                .hora(LocalTime.of(9, 0))
                .observaciones("Prefiere el piso alto.")
                .build());

        visitaService.solicitarVisita(Visita.builder()
                .idVisita("VIS-005")
                .codigoInmueble("INM-002")
                .idCliente("CLI-003")
                .idAsesor("ASE-001")
                .fecha(hoy.plusDays(4))
                .hora(LocalTime.of(15, 0))
                .observaciones("Segunda opción de la cliente.")
                .build());

        // VIS-006 y VIS-007: agregan visitas adicionales a INM-001 para alcanzar umbrales de detección
        // Resultado: INM-001=3 visitas (umbral INMUEBLE_SIN_CIERRE), CLI-003=3 visitas (CLIENTE_SIN_SEGUIMIENTO), ASE-001=5 visitas (sobrecarga)
        visitaService.solicitarVisita(Visita.builder()
                .idVisita("VIS-006")
                .codigoInmueble("INM-001")
                .idCliente("CLI-003")
                .idAsesor("ASE-001")
                .fecha(hoy.plusDays(5))
                .hora(LocalTime.of(10, 30))
                .observaciones("La cliente quiere comparar con el INM-002.")
                .build());

        visitaService.solicitarVisita(Visita.builder()
                .idVisita("VIS-007")
                .codigoInmueble("INM-001")
                .idCliente("CLI-005")
                .idAsesor("ASE-001")
                .fecha(hoy.plusDays(6))
                .hora(LocalTime.of(16, 0))
                .observaciones("Cliente VIP interesada en la zona El Poblado.")
                .build());

        System.out.println("  → 7 visitas cargadas");
    }

    //  5. Operaciones

    private void cargarOperaciones() {
        // OP-001: arriendo activo (EN_PROCESO)
        operacionService.registrar(Operacion.builder()
                .idOperacion("OP-001")
                .codigoInmueble("INM-004")
                .idCliente("CLI-002")
                .idAsesor("ASE-002")
                .fecha(LocalDate.now().minusDays(5))
                .tipo(TipoOperacion.ARRIENDO)
                .valorAcordado(4_000_000)
                .comision(400_000)
                .build());

        // OP-002: venta cerrada (INM-007 quedará no disponible)
        operacionService.registrar(Operacion.builder()
                .idOperacion("OP-002")
                .codigoInmueble("INM-007")
                .idCliente("CLI-005")
                .idAsesor("ASE-002")
                .fecha(LocalDate.now().minusDays(15))
                .tipo(TipoOperacion.VENTA)
                .valorAcordado(730_000_000)
                .comision(21_900_000)
                .build());
        operacionService.cerrar("OP-002");

        System.out.println("  → 2 operaciones cargadas (1 activa, 1 cerrada)");
    }

    //  6. Interacciones del grafo

    private void registrarInteraccionesGrafo() {
        // CLI-001: consultó INM-001 e INM-010; INM-001 en favoritos
        clienteService.registrarConsulta("CLI-001", "INM-001");
        clienteService.registrarConsulta("CLI-001", "INM-010");
        clienteService.agregarFavorito("CLI-001", "INM-001");

        // CLI-002: consultó INM-003 e INM-007; INM-003 en favoritos
        clienteService.registrarConsulta("CLI-002", "INM-003");
        clienteService.registrarConsulta("CLI-002", "INM-007");
        clienteService.agregarFavorito("CLI-002", "INM-003");

        // CLI-003: consultó INM-002 e INM-009; INM-009 en favoritos
        clienteService.registrarConsulta("CLI-003", "INM-002");
        clienteService.registrarConsulta("CLI-003", "INM-009");
        clienteService.agregarFavorito("CLI-003", "INM-009");

        // CLI-004: consultó INM-005 e INM-008; INM-005 en favoritos
        clienteService.registrarConsulta("CLI-004", "INM-005");
        clienteService.registrarConsulta("CLI-004", "INM-008");
        clienteService.agregarFavorito("CLI-004", "INM-005");

        // CLI-005: consultó INM-007 e INM-010; INM-010 en favoritos
        clienteService.registrarConsulta("CLI-005", "INM-007");
        clienteService.registrarConsulta("CLI-005", "INM-010");
        clienteService.agregarFavorito("CLI-005", "INM-010");

        System.out.println("  → Interacciones del grafo registradas (10 aristas)");
    }
    
    //  7. Alertas de ejemplo

    private void generarAlertasEjemplo() {
        alertaService.generarAlerta(
                TipoAlerta.ALTA_DEMANDA,
                "El inmueble INM-001 ha recibido múltiples consultas esta semana.",
                NivelAtencion.ALTA,
                "INM-001");

        alertaService.generarAlerta(
                TipoAlerta.SIN_VISITAS,
                "El inmueble INM-006 lleva 30 días sin recibir ninguna visita.",
                NivelAtencion.MEDIA,
                "INM-006");

        alertaService.generarAlerta(
                TipoAlerta.VISITA_PENDIENTE,
                "La visita VIS-001 aún no ha sido confirmada por el asesor.",
                NivelAtencion.BAJA,
                "VIS-001");

        alertaService.generarAlerta(
                TipoAlerta.INMUEBLE_SIN_CIERRE,
                "INM-003 tiene 4 visitas realizadas sin ninguna operación registrada.",
                NivelAtencion.CRITICA,
                "INM-003");

        alertaService.generarAlerta(
                TipoAlerta.CLIENTE_SIN_SEGUIMIENTO,
                "El cliente CLI-004 tiene visitas sin operación asociada.",
                NivelAtencion.ALTA,
                "CLI-004");

        alertaService.generarAlerta(
                TipoAlerta.CONTRATO_POR_VENCER,
                "El contrato de arriendo del local INM-004 vence en 30 días.",
                NivelAtencion.MEDIA,
                "OP-001");

        System.out.println("  → 6 alertas de ejemplo generadas");
    }
}
