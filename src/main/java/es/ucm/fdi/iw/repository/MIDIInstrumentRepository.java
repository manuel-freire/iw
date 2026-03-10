package es.ucm.fdi.iw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.ucm.fdi.iw.model.MIDIInstrument;

import java.util.Optional;

public interface MIDIInstrumentRepository extends JpaRepository<MIDIInstrument, Long>  {
    Optional<MIDIInstrument> findByProgram(int program);
}
