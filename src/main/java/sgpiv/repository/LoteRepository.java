package sgpiv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sgpiv.enums.EstadoLote;
import sgpiv.model.Lote;

import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    // Buscar por estado
    List<Lote> findByEstadoLote(EstadoLote estadoLote);

    // Buscar lotes disponibles
    default List<Lote> findDisponibles() {
        return findByEstadoLote(EstadoLote.DISPONIBLE);
    }

    @Query("SELECT l FROM Lote l WHERE l.superficie >= :superficie AND l.estadoLote = EstadoLote.DISPONIBLE")
    Optional<List<Lote>> findLotesDisponiblesConSuperficieMinima(@Param("superficie") Double superficie);

    // Buscar por ubicación (contiene, ignorando mayúsculas)
    List<Lote> findByUbicacionContainingIgnoreCase(String ubicacion);

    // Buscar por precio menor o igual a un valor
    List<Lote> findByPrecioLessThanEqual(Float precioMaximo);

    // Buscar por rango de precio
    List<Lote> findByPrecioBetween(Float precioMin, Float precioMax);

    // Buscar por superficie mínima
    List<Lote> findBySuperficieGreaterThanEqual(Float superficieMinima);

    // Buscar disponibles ordenados por precio
    List<Lote> findByEstadoLoteOrderByPrecioAsc(EstadoLote estadoLote);

    // Buscar disponibles dentro de un rango de precio
    @Query("SELECT l FROM Lote l WHERE l.estadoLote = :estado AND l.precio BETWEEN :min AND :max")
    List<Lote> findByEstadoAndPrecioRange(
            @Param("estado") EstadoLote estado,
            @Param("min") Float min,
            @Param("max") Float max
    );

    // Verificar si existe un lote en esa ubicación
    boolean existsByUbicacionIgnoreCase(String ubicacion);



}
