package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
	@NamedQuery(name="StClass.byId",
	query="SELECT st FROM StClass st "
			+ "WHERE st.id = :classId")
})

public class StClass {

	private long id;
	private String name;
	private User teacher;

	private List<User> students = new ArrayList<>();
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
	
	@OneToMany(targetEntity = User.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "stClass")
	public List<User> getStudents() {
		return students;
	}

	public void setStudents(List<User> students) {
		this.students = students;
	}

	@OneToMany(targetEntity = StTeam.class)
	@JoinColumn(name = "stClass")
	public List<StTeam> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<StTeam> teamList) {
		this.teamList = teamList;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- CLASE ---\n");
		stb.append("Clase: " + this.getName() + "\n");
		stb.append("Profesor/a: {}" + this.teacher.getUsername() + "\n");
		for (int i = 0; i < this.students.size(); i++) {
			stb.append(this.students.get(i).toString() + "\n");
		}
		
	    return stb.toString();
	}
}
