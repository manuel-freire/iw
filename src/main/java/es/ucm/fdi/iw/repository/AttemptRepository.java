package es.ucm.fdi.iw.repository;

import es.ucm.fdi.iw.model.Attempt;
import es.ucm.fdi.iw.model.DailyGame;
import es.ucm.fdi.iw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    Optional<Attempt> findByUserAndDailyGame(User user, DailyGame dailyGame);
}
