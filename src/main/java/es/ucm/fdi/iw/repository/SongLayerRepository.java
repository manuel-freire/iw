package es.ucm.fdi.iw.repository;

import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.model.SongLayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongLayerRepository extends JpaRepository<SongLayer, Long> {
  List<SongLayer> findBySongOrderByIdxAsc(Song song);
}

