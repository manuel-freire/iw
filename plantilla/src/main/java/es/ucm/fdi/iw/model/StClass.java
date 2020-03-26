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
 * A class consist of a group of students coordinated by a teacher
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
//	@NamedQuery(name="StClass.byTeacher",
//	query="SELECT stc FROM StClass stc "
//			+ "WHERE stc.teacher = :teacherId"),
	@NamedQuery(name="StClass.userFromClass",
	query="SELECT u "
			+ "FROM StClass st JOIN st.teacher u "
			+ "WHERE u.roles = :roles "
			+ "AND u.enabled = 1 "
			+ "AND st.id = :id")
})

public class StClass {

	private long id;
	private String name;
	private User teacher;

	private List<StTeam> teamList = new ArrayList<>();
	
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
	
	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "stClassList")
	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}

	@OneToMany(targetEntity = StTeam.class)
	@JoinColumn(name = "stClass")
	public List<StTeam> getTeams() {
		return teamList;
	}

	public void setTeams(List<StTeam> teamList) {
		this.teamList = teamList;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		
		stb.append("Clase: " + this.getName() + "\n");
		
	    return stb.toString();
	}
}
