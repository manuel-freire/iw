package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
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
	query="SELECT stt FROM StTeam stt "
			+ "WHERE stt.stClass = :classId")
})

public class StTeam {
	
	private long id;
	private String name;
	private StClass stClass;
	
	private int elo;
	private int gold;
	private int silver;
	private int bronze;

	private List<User> members = new ArrayList<>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@OneToMany(targetEntity = User.class)
	@JoinColumn(name = "team")
	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}	
}
