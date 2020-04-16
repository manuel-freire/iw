package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


/**
 * A team consist of a group of students who compete together
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="StTeam.byClass",
	query="SELECT stt FROM StTeam stt JOIN stt.stClass stc "
			+ "WHERE stc.id = :classId"),
	@NamedQuery(name="StTeam.ranking",
	query="SELECT stt FROM StTeam stt JOIN stt.stClass stc "
			+ "WHERE stc.id = :classId "
			+ "ORDER BY stt.elo DESC")
})

public class StTeam {
	
	private long id;
	private String teamName;
	private StClass stClass;
	
	private int elo;
	private int correct;
	private int gold;
	private int silver;
	private int bronze;

	private List<User> members = new ArrayList<>();
	private List<Achievement> achievementTeam;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	@ManyToOne(targetEntity = StClass.class)
	@JoinColumn(name = "teamList")
	public StClass getStClass() {
		return stClass;
	}

	public void setStClass(StClass stClass) {
		this.stClass = stClass;
	}

	public int getElo() {
		return elo;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getSilver() {
		return silver;
	}

	public void setSilver(int silver) {
		this.silver = silver;
	}

	public int getBronze() {
		return bronze;
	}

	public void setBronze(int bronze) {
		this.bronze = bronze;
	}

	@OneToMany(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "team")
	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}	
	
	@OneToMany(targetEntity = Achievement.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "team")
	public List<Achievement> getAchievementTeam() {
		return achievementTeam;
	}

	public void setAchievementTeam(List<Achievement> achivementTeam) {
		this.achievementTeam = achivementTeam;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- EQUIPO ---\n");
		stb.append(this.teamName + "\n");
		for (int i = 0; i < this.members.size(); i++) {
			stb.append(this.members.get(i).toString() + "\n");
		}
		for (int i = 0; i < this.achievementTeam.size(); i++) {
			stb.append(this.achievementTeam.get(i).toString() + "\n");
		}
		
	    return stb.toString();
	}
}
