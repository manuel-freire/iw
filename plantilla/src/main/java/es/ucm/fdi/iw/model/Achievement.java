package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
//@NamedQueries({
//	@NamedQuery(name="Achievement.byStudent",
//	query="SELECT a FROM Achievement a "
//			+ "WHERE a.student = :studentId")
//})

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
	
	@OneToOne(targetEntity=Goal.class)
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
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		if (this.student != null)
			stb.append("Usuario: " + this.student.getUsername() + "\n");
		if (this.team != null)
			stb.append("Equipo: " + this.team.getTeamName() + "\n");
		stb.append("{}" + this.goal + "\n");
		stb.append("Nivel: " + this.level + "\n");
		stb.append("Puntuación: " + this.progress + "\n");
		
	    return stb.toString();
	}
}
