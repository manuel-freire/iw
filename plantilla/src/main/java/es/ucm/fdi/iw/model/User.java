package es.ucm.fdi.iw.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A user; can be a Student or a Teacher
 *
 * Teacher can create classes and contest and organize students into groups
 * Students can participate in contest and get points for their teams and themselves
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="User.byUsername",
	query="SELECT u FROM User u "
			+ "WHERE u.username = :username AND u.enabled = 1"),
	@NamedQuery(name="User.byToken",
	query="SELECT u FROM User u "
			+ "WHERE u.token = :token AND u.enabled = 1"),			
	@NamedQuery(name="User.hasUsername",
	query="SELECT COUNT(u) "
			+ "FROM User u "
			+ "WHERE u.username = :username"),
	@NamedQuery(name="User.byClass",
	query="SELECT u FROM User u JOIN u.stClass st "
			+ "WHERE u.roles = 'USER' "
			+ "AND u.enabled = 1 "
			+ "AND st.id = :classId"),
	@NamedQuery(name="User.numStudents",
	query="SELECT COUNT(u) FROM User u JOIN u.stClass st "
			+ "WHERE u.roles = 'USER' "
			+ "AND u.enabled = 1 "
			+ "AND st.id = :classId"),
	@NamedQuery(name="User.userInClass",
	query="SELECT u FROM User u JOIN u.stClass st "
			+ "WHERE u.username = :username "
			+ "AND st.id = :classId "
			+ "AND u.enabled = 1 "),
	@NamedQuery(name="User.byTeam",
	query="SELECT u FROM User u JOIN u.team t "
			+ "WHERE u.roles = 'USER' "
			+ "AND u.enabled = 1 "
			+ "AND t.id = :teamId "
			+ "ORDER BY u.elo DESC"),
	@NamedQuery(name="User.ranking",
	query="SELECT u FROM User u JOIN u.stClass st "
			+ "WHERE u.roles = 'USER' "
			+ "AND u.enabled = 1 "
			+ "AND st.id = :classId "
			+ "ORDER BY u.elo DESC")
})

public class User {

	public enum Role {
		USER,			// used for logged-in, non-priviledged users
		ADMIN,			// used for maximum priviledged users		
		MODERATOR,		// remove or add roles as needed
	}
	
	// do not change these fields
	private long id;
	private String username;
	private String password;

	private String token; // used to login via qr code

	private String roles; // split by ',' to separate roles
	private byte enabled;
	
	// application-specific fields
	private String firstName;
	private String lastName;
	
	// achievement/ranking fields
	private int elo;
	private int correct;
	private int passed;
	private int perfect;	
	private int top;	
	
	private StTeam team;
	private StClass stClass;
	// admin fields
	private List<StClass> stClassList;
	private List<Contest> contestList;
	// user fields
	private List<Result> resultList;
	private List<Achievement> achievementUser;

	/**
	 * Checks whether this user has a given role.
	 * @param role to check
	 * @return true iff this user has that role.
	 */
	public boolean hasRole(final Role role) {
		final String roleName = role.name();
		return Arrays.stream(roles.split(",")).anyMatch(r -> r.equals(roleName));
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	@Column(nullable = false)
	public String getPassword() {
		return password;
	}

	public void createAndSetRandomToken(final int length) {
		final char[] chars = "ABCDEFGHJKLMNPQabcdefghijkmnopq123456789".toCharArray();
		final StringBuilder token = new StringBuilder();
		final Random r = new Random();
		for (int i = 0; i < length; i++) {
			token.append(chars[r.nextInt(chars.length)]);
		}
		this.token = token.toString();
	}

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	/**
	 * Sets the password to an encoded value. NEVER STORE
	 * PLAINTEXT PASSWORDS
	 * 
	 * @param encodedPassword to set as user's password
	 */
	public void setPassword(final String encodedPassword) {
		this.password = encodedPassword;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(final String roles) {
		this.roles = roles;
	}

	public byte getEnabled() {
		return enabled;
	}

	public void setEnabled(final byte enabled) {
		this.enabled = enabled;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public int getElo() {
		return elo;
	}

	public void setElo(final int elo) {
		this.elo = elo;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(final int correct) {
		this.correct = correct;
	}

	public int getPassed() {
		return passed;
	}

	public void setPassed(final int passed) {
		this.passed = passed;
	}

	public int getPerfect() {
		return perfect;
	}

	public void setPerfect(final int perfect) {
		this.perfect = perfect;
	}

	public int getTop() {
		return top;
	}

	public void setTop(final int top) {
		this.top = top;
	}

	@ManyToOne(targetEntity = StTeam.class)
	@JoinColumn(name = "members")
	public StTeam getTeam() {
		return team;
	}

	public void setTeam(final StTeam team) {
		this.team = team;
	}

	@ManyToOne(targetEntity = StClass.class)
	@JoinColumn(name = "students")
	public StClass getStClass() {
		return stClass;
	}

	public void setStClass(final StClass stClass) {
		this.stClass = stClass;
	}

	@OneToMany(targetEntity = StClass.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "teacher")
	public List<StClass> getStClassList() {
		return stClassList;
	}

	public void setStClassList(final List<StClass> stClassList) {
		this.stClassList = stClassList;
	}

	@OneToMany(targetEntity = Contest.class)
	@JoinColumn(name = "teacher")
	public List<Contest> getContests() {
		return contestList;
	}

	public void setContests(final List<Contest> contests) {
		this.contestList = contests;
	}

	@OneToMany(targetEntity = Result.class)
	@JoinColumn(name = "user")
	public List<Result> getResultList() {
		return resultList;
	}

	public void setResultList(final List<Result> resultList) {
		this.resultList = resultList;
	}

	@OneToMany(targetEntity = Achievement.class)
	@JoinColumn(name = "student")
	public List<Achievement> getAchievementUser() {
		return achievementUser;
	}

	public void setAchievementUser(final List<Achievement> achivementUser) {
		this.achievementUser = achivementUser;
	}

	@Override
	public String toString() {
		final StringBuilder stb = new StringBuilder();
		
		stb.append("--- USUARIO ---\n");
		stb.append("Nombre: " + this.getFirstName() + "\n");
		stb.append("Apellidos: " + this.getLastName() + "\n");
		stb.append("Usuario: " + this.getUsername() + "\n");
		stb.append("Elo: " + Double.toString(this.getElo()) + "\n");
		
	    return stb.toString();
	}
}
