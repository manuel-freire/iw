package es.ucm.fdi.iw.repository;

import es.ucm.fdi.iw.model.Score;
import es.ucm.fdi.iw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
  Optional<Score> findByUser(User user);
}

