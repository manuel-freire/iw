package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


/**
 * Each goal is associated to an achievement
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="Goal.forUser",
	query="SELECT g FROM Goal g "
			+ "WHERE g.target = 'USER'"),
	@NamedQuery(name="Goal.forTeam",
	query="SELECT g FROM Goal g "
			+ "WHERE g.target = 'TEAM'"),
	
})

public class Goal {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	private long id;
	private String description;
	private String levels;
	private String target;
	private String key;	

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- META ---\n");
		stb.append("Objetivo: " + this.description + "\n");
		stb.append("Clave: " + this.key + "\n");
		stb.append("Niveles: " + this.levels + "\n");
		stb.append("Target: " + this.target + "\n");
		
	    return stb.toString();
	}
}
