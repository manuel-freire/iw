package es.ucm.fdi.iw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ucm.fdi.iw.model.MIDISequence;

public interface MIDISequenceRepository extends JpaRepository<MIDISequence, Long>  {
}
