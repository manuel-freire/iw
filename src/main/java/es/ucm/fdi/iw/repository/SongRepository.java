package es.ucm.fdi.iw.repository;

import es.ucm.fdi.iw.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {}
