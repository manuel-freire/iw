package es.ucm.fdi.iw.repository;

import es.ucm.fdi.iw.model.Authors;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface AuthorsRepository extends JpaRepository<Authors, Long> {
}