package es.ucm.fdi.iw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ucm.fdi.iw.model.MIDIGame;
import java.util.Optional;

public interface MIDIGameRepository extends JpaRepository<MIDIGame, Long>  {
    Optional<MIDIGame> findByLobbyCode(String lobbyCode);

    boolean exexistsByLobbyCode(String lobbyCode);
}
