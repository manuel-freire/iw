package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.model.*;
import es.ucm.fdi.iw.repository.*;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/guess")
public class GuessController {

  private static final Logger log = LogManager.getLogger(GuessController.class);

  private final SongRepository songRepo;
  private final SongLayerRepository layerRepo;
  private final DailyGameRepository dailyRepo;
  private final AttemptRepository attemptRepo;
  private final ScoreRepository scoreRepo;

  public GuessController(
      SongRepository songRepo,
      SongLayerRepository layerRepo,
      DailyGameRepository dailyRepo,
      AttemptRepository attemptRepo,
      ScoreRepository scoreRepo) {
    this.songRepo = songRepo;
    this.layerRepo = layerRepo;
    this.dailyRepo = dailyRepo;
    this.attemptRepo = attemptRepo;
    this.scoreRepo = scoreRepo;
  }

  @ModelAttribute
  public void populateModel(HttpSession session, Model model) {
    User u = (User) session.getAttribute("u");
    model.addAttribute("u", u);
    model.addAttribute("logged", u != null);
  }

  // ---------------- GET /guess ----------------
  @GetMapping
  @Transactional(readOnly = true)
  public String page(HttpSession session, Model model) {

    User u = (User) session.getAttribute("u");
    log.debug("Acceso a GET /guess por usuario={}", u != null ? u.getId() : "anon");

    DailyGame dg = getOrCreateDaily(LocalDate.now());
    Song song = dg.getSong();
    List<SongLayer> layers = layerRepo.findBySongOrderByIdxAsc(song);

    if (layers.isEmpty()) {
      log.warn("La canción del daily id={} no tiene capas asociadas", song.getId());
      model.addAttribute("msg", "No hay capas para la canción del día. Revisa import.sql 🙂");
      return "guess";
    }

    Attempt at = null;
    if (u != null) {
      at = attemptRepo.findByUserAndDailyGame(u, dg).orElse(null);
      log.debug("Attempt recuperado para user={} daily={}: {}",
          u.getId(), dg.getId(), at != null ? "sí" : "no");
    }

    int layerIndex = (at == null) ? 0 : at.getCurrentLayer();
    int tries = (at == null) ? 0 : at.getTries();
    boolean success = (at != null && at.isSuccess());
    boolean finished = success || tries >= dg.getMaxTries();

    layerIndex = clamp(layerIndex, 0, layers.size() - 1);

    model.addAttribute("dailyGame", dg);
    model.addAttribute("song", song);
    model.addAttribute("layerIndex", layerIndex);
    model.addAttribute("currentLayer", layers.get(layerIndex));
    model.addAttribute("maxLayer", layers.size() - 1);
    model.addAttribute("tries", tries);
    model.addAttribute("maxTries", dg.getMaxTries());
    model.addAttribute("finished", finished);
    model.addAttribute("success", success);

    Object msg = session.getAttribute("guessMsg");
    if (msg != null) {
      log.debug("Mensaje de sesión en /guess: {}", msg);
      model.addAttribute("msg", msg.toString());
      session.removeAttribute("guessMsg");
    }

    if (u == null) {
      log.debug("Usuario anónimo accediendo a /guess");
      model.addAttribute("loginWarning", true);
    }

    model.addAttribute("songList", songRepo.findAll());
    return "guess";
  }

  // ---------------- POST /guess/nav ----------------
  @PostMapping("/nav")
  @Transactional
  public String nav(@RequestParam String dir, HttpSession session) {

    User u = (User) session.getAttribute("u");
    if (u == null) {
      log.info("Intento de navegación de capas sin login");
      session.setAttribute("guessMsg", "Inicia sesión para jugar.");
      return "redirect:/login";
    }

    DailyGame dg = getOrCreateDaily(LocalDate.now());
    List<SongLayer> layers = layerRepo.findBySongOrderByIdxAsc(dg.getSong());
    int max = Math.max(0, layers.size() - 1);

    Attempt at = attemptRepo.findByUserAndDailyGame(u, dg)
        .orElseGet(() -> {
          log.info("No existía attempt para user={} daily={}. Se crea uno nuevo desde /nav", u.getId(), dg.getId());
          return createAttempt(u, dg);
        });

    if (at.isSuccess() || at.getTries() >= dg.getMaxTries()) {
      log.info("Usuario {} intentó navegar en un daily ya terminado (daily={})", u.getId(), dg.getId());
      session.setAttribute("guessMsg", "Ya terminaste el daily de hoy.");
      return "redirect:/guess";
    }

    int previousLayer = at.getCurrentLayer();
    int layerIndex = previousLayer;

    if ("prev".equals(dir)) {
      layerIndex--;
    } else if ("next".equals(dir)) {
      layerIndex++;
    } else {
      log.warn("Dirección de navegación inválida '{}' para user={} daily={}", dir, u.getId(), dg.getId());
    }

    layerIndex = clamp(layerIndex, 0, max);
    at.setCurrentLayer(layerIndex);
    attemptRepo.save(at);

    log.debug("Usuario {} movió capa en daily={}: {} -> {}", u.getId(), dg.getId(), previousLayer, layerIndex);

    return "redirect:/guess";
  }

  // ---------------- POST /guess/submit ----------------
  @PostMapping("/submit")
  @Transactional
  public String submit(@RequestParam String answer, HttpSession session) {

    User u = (User) session.getAttribute("u");
    if (u == null) {
      log.info("Intento de submit sin login");
      session.setAttribute("guessMsg", "Inicia sesión para jugar.");
      return "redirect:/login";
    }

    DailyGame dg = getOrCreateDaily(LocalDate.now());
    Song song = dg.getSong();
    List<SongLayer> layers = layerRepo.findBySongOrderByIdxAsc(song);
    int maxLayer = Math.max(0, layers.size() - 1);

    Attempt at = attemptRepo.findByUserAndDailyGame(u, dg)
        .orElseGet(() -> {
          log.info("No existía attempt para user={} daily={}. Se crea uno nuevo desde /submit", u.getId(), dg.getId());
          return createAttempt(u, dg);
        });

    if (at.isSuccess() || at.getTries() >= dg.getMaxTries()) {
      log.info("Usuario {} intentó enviar respuesta en un daily ya terminado (daily={})", u.getId(), dg.getId());
      session.setAttribute("guessMsg", "Ya terminaste el daily de hoy.");
      return "redirect:/guess";
    }

    at.setGuess(answer);

    boolean ok = String.valueOf(song.getId()).equals(answer.trim());
    if (ok) {
      at.setSuccess(true);

      int points = calcPoints(dg, at, maxLayer);
      updateScore(u, true, points);

      log.info("Usuario {} acertó la canción del daily={} y obtuvo {} puntos", u.getId(), dg.getId(), points);
      session.setAttribute("guessMsg", "✅ Correcto! +" + points + " puntos");
    } else {
      at.setTries(at.getTries() + 1);

      int newLayer = Math.min(at.getCurrentLayer() + 1, maxLayer);
      at.setCurrentLayer(newLayer);

      updateScore(u, false, 0);

      if (at.getTries() >= dg.getMaxTries()) {
        log.info("Usuario {} agotó sus intentos en daily={}. Canción correcta: {} - {}",
            u.getId(), dg.getId(), song.getTitle(), song.getArtist());
        session.setAttribute("guessMsg", "❌ Sin intentos. Era: " + song.getTitle() + " - " + song.getArtist());
      } else if (newLayer == maxLayer) {
        log.debug("Usuario {} falló en daily={} y desbloqueó la última capa", u.getId(), dg.getId());
        session.setAttribute("guessMsg", "Fallaste. Última capa desbloqueada");
      } else {
        log.debug("Usuario {} falló en daily={} y avanzó a la capa {}", u.getId(), dg.getId(), newLayer);
        session.setAttribute("guessMsg", "Fallaste. Siguiente capa desbloqueada");
      }
    }

    attemptRepo.save(at);
    return "redirect:/guess";
  }

  // ---------------- helpers ----------------

  private Attempt createAttempt(User u, DailyGame dg) {
    log.debug("Creando attempt para user={} daily={}", u.getId(), dg.getId());

    Attempt at = new Attempt();
    at.setUser(u);
    at.setDailyGame(dg);
    at.setCurrentLayer(0);
    at.setTries(0);
    at.setSuccess(false);
    at.setCreatedAt(LocalDateTime.now());
    return attemptRepo.save(at);
  }

  private DailyGame getOrCreateDaily(LocalDate today) {
    return dailyRepo.findByGameDay(today).orElseGet(() -> {
      List<Song> songs = songRepo.findAll();
      if (songs.isEmpty()) {
        log.error("No se puede crear el daily para {} porque no hay canciones en BD", today);
        throw new IllegalStateException("No hay canciones en BD");
      }

      Song chosen = songs.get(ThreadLocalRandom.current().nextInt(songs.size()));

      log.info("Creando daily para {} con songId={} ({})", today, chosen.getId(), chosen.getTitle());

      DailyGame dg = new DailyGame();
      dg.setGameDay(today);
      dg.setSong(chosen);
      // maxLayers/maxTries ya tienen defaults en la clase
      dg.setActive(true);
      return dailyRepo.save(dg);
    });
  }

  private void updateScore(User u, boolean won, int points) {
    Score sc = scoreRepo.findByUser(u).orElseGet(() -> {
      log.debug("Creando score inicial para user={}", u.getId());
      Score s = new Score();
      s.setUser(u);
      return s;
    });

    sc.setGamesPlayed(sc.getGamesPlayed() + 1);

    if (won) {
      sc.setGamesWon(sc.getGamesWon() + 1);
      sc.setTotalPoints(sc.getTotalPoints() + points);
      sc.setCurrentStreak(sc.getCurrentStreak() + 1);
      sc.setBestStreak(Math.max(sc.getBestStreak(), sc.getCurrentStreak()));

      log.debug("Score actualizado para user={}: win=true, points={}, played={}, won={}, streak={}, bestStreak={}",
          u.getId(), points, sc.getGamesPlayed(), sc.getGamesWon(), sc.getCurrentStreak(), sc.getBestStreak());
    } else {
      sc.setCurrentStreak(0);

      log.debug("Score actualizado para user={}: win=false, played={}, streak reiniciada",
          u.getId(), sc.getGamesPlayed());
    }

    scoreRepo.save(sc);
  }

  private int calcPoints(DailyGame dg, Attempt at, int maxLayer) {
    int layerPenalty = at.getCurrentLayer();
    int tryPenalty = at.getTries() * 2;
    int base = 10;

    int points = Math.max(1, base - layerPenalty - tryPenalty);

    log.debug("Cálculo de puntos para daily={}: base={}, layerPenalty={}, tryPenalty={}, result={}",
        dg.getId(), base, layerPenalty, tryPenalty, points);

    return points;
  }

  private static int clamp(int v, int min, int max) {
    return Math.max(min, Math.min(max, v));
  }

  private static String normalize(String s) {
    return s == null ? "" : s.trim().toLowerCase();
  }
}