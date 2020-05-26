package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;


/**
 * An achievement is unlocked by a student once a goal is reached
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="Achievement.byStudent",
	query="SELECT a FROM Achievement a JOIN a.student s "
			+ "WHERE s.id = :userId"),
	@NamedQuery(name="Achievement.byTeam",
	query="SELECT a FROM Achievement a JOIN a.team t "
			+ "WHERE t.id = :teamId"),
	@NamedQuery(name="Achievement.byStudentRanking",
	query="SELECT a FROM Achievement a JOIN a.student s "
			+ "WHERE s.id = :userId "
			+ "AND (a.goal.key = 'TOP')"),
	@NamedQuery(name="Achievement.byTeamRanking",
	query="SELECT a FROM Achievement a JOIN a.team t "
			+ "WHERE t.id = :teamId "
			+ "AND (a.goal.key = 'TOP')")
})

public class Achievement {
	
	private long id;
	private int progress;
	private int level;
	private Goal goal;
	
	private User student;
	private StTeam team;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}	
	
	@ManyToOne(targetEntity=Goal.class)
	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}
	
	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "achievementUser")
	public User getStudent() {
		return student;
	}

	public void setStudent(User student) {
		this.student = student;
	}

	@ManyToOne(targetEntity = StTeam.class)
	@JoinColumn(name = "achievementTeam")
	public StTeam getTeam() {
		return team;
	}

	public void setTeam(StTeam team) {
		this.team = team;
	}
	
	public String currentLevel() {
		return this.goal.getLevels().split(",")[this.level];
	}
	
	public String currentObjective() {		
		return this.goal.getDescription().replace("XXX", currentLevel());
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- LOGRO ---\n");
		if (this.student != null)
			stb.append("Usuario: " + this.student.getUsername() + "\n");
		if (this.team != null)
			stb.append("Equipo: " + this.team.getTeamName() + "\n");
		stb.append("{}" + this.goal);
		stb.append("Nivel: " + this.level + "\n");
		stb.append("Puntuación: " + this.progress + "\n");
		
	    return stb.toString();
	}
}
