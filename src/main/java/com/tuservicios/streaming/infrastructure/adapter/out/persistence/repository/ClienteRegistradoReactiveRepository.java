package com.tuservicios.streaming.infrastructure.adapter.out.persistence.repository;
import com.tuservicios.streaming.infrastructure.adapter.out.persistence.entity.ClienteRegistradoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClienteRegistradoReactiveRepository
      extends R2dbcRepository<ClienteRegistradoEntity, Long> {

   Mono<Boolean> existsByTelefono(String telefono);

   @Query("""
            UPDATE clientes
            SET nombre   = :nombre,
                apellido = :apellido,
                telefono = :telefono
            WHERE id = :id
            """)
   Mono<Integer> updateCliente(Long id, String nombre, String apellido, String telefono);
}