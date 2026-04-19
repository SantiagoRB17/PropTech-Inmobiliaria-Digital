package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.model.Operacion;
import com.proyecto.inmobiliaria.model.enums.EstadoOperacion;
import com.proyecto.inmobiliaria.model.enums.Finalidad;
import com.proyecto.inmobiliaria.model.enums.TipoOperacion;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import com.proyecto.inmobiliaria.repository.OperacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final InmuebleRepository inmuebleRepository;
    private final ClienteRepository clienteRepository;
    private final AsesorRepository asesorRepository;

    /**
     * Registra una nueva operación comercial.
     */
    public Operacion registrar(Operacion operacion) {
        Inmueble inmueble = obtenerInmuebleValidado(operacion.getCodigoInmueble());

        if (!inmueble.isDisponible()) {
            throw new RuntimeException("El inmueble " + inmueble.getCodigo() + " no está disponible.");
        }
        validarCompatibilidadTipoFinalidad(operacion.getTipo(), inmueble.getFinalidad());

        if (clienteRepository.buscarPorId(operacion.getIdCliente()) == null) {
            throw new RuntimeException("Cliente no encontrado: " + operacion.getIdCliente());
        }
        if (!asesorRepository.existePorId(operacion.getIdAsesor())) {
            throw new RuntimeException("Asesor no encontrado: " + operacion.getIdAsesor());
        }

        if (operacion.getIdOperacion() == null || operacion.getIdOperacion().isBlank()) {
            operacion.setIdOperacion(UUID.randomUUID().toString());
        }
        operacion.setEstado(EstadoOperacion.EN_PROCESO);
        operacionRepository.guardar(operacion);

        clienteRepository.buscarPorId(operacion.getIdCliente())
                .getInmueblesNegociados().add(operacion.getCodigoInmueble());

        return operacion;
    }

    public Operacion buscarPorId(String idOperacion) {
        Operacion operacion = operacionRepository.buscarPorId(idOperacion);
        if (operacion == null) {
            throw new RuntimeException("Operación no encontrada: " + idOperacion);
        }
        return operacion;
    }

    public List<Operacion> listarTodas() {
        return operacionRepository.listarTodas();
    }

    public List<Operacion> listarHistorial() {
        return operacionRepository.listarHistorial();
    }

    /**
     * Cierra una operación exitosamente:
     * - Marca el inmueble como no disponible
     * - Incrementa los cierres del asesor
     * - Genera alerta de cierre
     */
    public Operacion cerrar(String idOperacion) {
        Operacion operacion = buscarPorId(idOperacion);
        if (operacion.getEstado() != EstadoOperacion.EN_PROCESO) {
            throw new RuntimeException("Solo se pueden cerrar operaciones EN_PROCESO.");
        }

        Inmueble inmueble = obtenerInmuebleValidado(operacion.getCodigoInmueble());
        inmueble.setDisponible(false);
        inmuebleRepository.guardar(inmueble);

        asesorRepository.buscarPorId(operacion.getIdAsesor()).setCierresRealizados(
                asesorRepository.buscarPorId(operacion.getIdAsesor()).getCierresRealizados() + 1
        );

        operacion.setEstado(EstadoOperacion.CERRADA);
        operacionRepository.guardar(operacion);
        return operacion;
    }

    public Operacion cancelar(String idOperacion) {
        Operacion operacion = buscarPorId(idOperacion);
        if (operacion.getEstado() == EstadoOperacion.CERRADA) {
            throw new RuntimeException("No se puede cancelar una operación ya cerrada.");
        }
        operacion.setEstado(EstadoOperacion.CANCELADA);
        operacionRepository.guardar(operacion);
        return operacion;
    }


    private Inmueble obtenerInmuebleValidado(String codigoInmueble) {
        Inmueble inmueble = inmuebleRepository.buscarPorId(codigoInmueble);
        if (inmueble == null) {
            throw new RuntimeException("Inmueble no encontrado: " + codigoInmueble);
        }
        return inmueble;
    }

    /**
     * Una operación de VENTA solo aplica a inmuebles con Finalidad.VENTA.
     * Una operación de ARRIENDO solo aplica a inmuebles con Finalidad.ARRIENDO.
     */
    private void validarCompatibilidadTipoFinalidad(TipoOperacion tipo, Finalidad finalidad) {
        if (tipo == TipoOperacion.VENTA && finalidad != Finalidad.VENTA) {
            throw new RuntimeException("No se puede registrar una VENTA sobre un inmueble en arriendo.");
        }
        if (tipo == TipoOperacion.ARRIENDO && finalidad != Finalidad.ARRIENDO) {
            throw new RuntimeException("No se puede registrar un ARRIENDO sobre un inmueble en venta.");
        }
    }
}
