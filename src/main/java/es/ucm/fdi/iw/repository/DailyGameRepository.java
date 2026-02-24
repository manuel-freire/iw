package es.ucm.fdi.iw.repository;

import es.ucm.fdi.iw.model.DailyGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyGameRepository extends JpaRepository<DailyGame, Long> {
  Optional<DailyGame> findByGameDay(LocalDate gameDay);
}
