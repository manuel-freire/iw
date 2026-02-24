package es.ucm.fdi.iw.controller;

import es.ucm.fdi.iw.model.*;
import es.ucm.fdi.iw.repository.*;

import jakarta.servlet.http.HttpSession;
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

  private final SongRepository songRepo;
  private final SongLayerRepository layerRepo;
  private final DailyGameRepository dailyRepo;
  private final AttemptRepository attemptRepo;
  private final ScoreRepository scoreRepo;

  public GuessController(SongRepository songRepo,
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

    DailyGame dg = getOrCreateDaily(LocalDate.now());
    Song song = dg.getSong();
    List<SongLayer> layers = layerRepo.findBySongOrderByIdxAsc(song);

    if (layers.isEmpty()) {
      model.addAttribute("msg", "No hay capas para la canción del día. Revisa import.sql 🙂");
      return "guess";
    }

    User u = (User) session.getAttribute("u");

    Attempt at = null;
    if (u != null) {
      at = attemptRepo.findByUserAndDailyGame(u, dg).orElse(null);
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
      model.addAttribute("msg", msg.toString());
      session.removeAttribute("guessMsg");
    }

    if (u == null) {
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
      session.setAttribute("guessMsg", "Inicia sesión para jugar.");
      return "redirect:/login";
    }

    DailyGame dg = getOrCreateDaily(LocalDate.now());
    List<SongLayer> layers = layerRepo.findBySongOrderByIdxAsc(dg.getSong());
    int max = Math.max(0, layers.size() - 1);

    Attempt at = attemptRepo.findByUserAndDailyGame(u, dg)
        .orElseGet(() -> createAttempt(u, dg));

    if (at.isSuccess() || at.getTries() >= dg.getMaxTries()) {
      session.setAttribute("guessMsg", "Ya terminaste el daily de hoy.");
      return "redirect:/guess";
    }

    int layerIndex = at.getCurrentLayer();
    if ("prev".equals(dir)) layerIndex--;
    if ("next".equals(dir)) layerIndex++;

    layerIndex = clamp(layerIndex, 0, max);
    at.setCurrentLayer(layerIndex);
    attemptRepo.save(at);

    return "redirect:/guess";
  }

  // ---------------- POST /guess/submit ----------------
  @PostMapping("/submit")
  @Transactional
  public String submit(@RequestParam String answer, HttpSession session) {

    User u = (User) session.getAttribute("u");
    if (u == null) {
      session.setAttribute("guessMsg", "Inicia sesión para jugar.");
      return "redirect:/login";
    }

    DailyGame dg = getOrCreateDaily(LocalDate.now());
    Song song = dg.getSong();
    List<SongLayer> layers = layerRepo.findBySongOrderByIdxAsc(song);
    int maxLayer = Math.max(0, layers.size() - 1);

    Attempt at = attemptRepo.findByUserAndDailyGame(u, dg)
        .orElseGet(() -> createAttempt(u, dg));

    if (at.isSuccess() || at.getTries() >= dg.getMaxTries()) {
      session.setAttribute("guessMsg", "Ya terminaste el daily de hoy.");
      return "redirect:/guess";
    }

    at.setGuess(answer);

    boolean ok = String.valueOf(song.getId()).equals(answer.trim());
    if (ok) {
      at.setSuccess(true);

      int points = calcPoints(dg, at, maxLayer);
      updateScore(u, ok, points);

      session.setAttribute("guessMsg", "✅ Correcto! +" + points + " puntos");
    } else {
      at.setTries(at.getTries() + 1);

      int newLayer = Math.min(at.getCurrentLayer() + 1, maxLayer);
      at.setCurrentLayer(newLayer);

      updateScore(u, false, 0);

      if (at.getTries() >= dg.getMaxTries()) {
        session.setAttribute("guessMsg", "❌ Sin intentos. Era: " + song.getTitle() + " - " + song.getArtist());
      } else if (newLayer == maxLayer) {
        session.setAttribute("guessMsg", "Fallaste. Última capa desbloqueada");
      } else {
        session.setAttribute("guessMsg", "Fallaste. Siguiente capa desbloqueada");
      }
    }

    attemptRepo.save(at);
    return "redirect:/guess";
  }

  // ---------------- helpers ----------------

  private Attempt createAttempt(User u, DailyGame dg) {
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
        throw new IllegalStateException("No hay canciones en BD");
      }
      Song chosen = songs.get(ThreadLocalRandom.current().nextInt(songs.size()));

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
    } else {
      sc.setCurrentStreak(0);
    }

    scoreRepo.save(sc);
  }

  private int calcPoints(DailyGame dg, Attempt at, int maxLayer) {
    // ejemplo simple: más puntos si aciertas con menos capas y menos intentos
    int layerPenalty = at.getCurrentLayer();      // 0..max
    int tryPenalty = at.getTries() * 2;           // más duro por fallar
    int base = 10;
    return Math.max(1, base - layerPenalty - tryPenalty);
  }

  private static int clamp(int v, int min, int max) {
    return Math.max(min, Math.min(max, v));
  }

  private static String normalize(String s) {
    return s == null ? "" : s.trim().toLowerCase();
  }
}
