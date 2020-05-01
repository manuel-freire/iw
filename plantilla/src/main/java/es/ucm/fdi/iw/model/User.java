package es.ucm.fdi.iw.model;

import java.util.Arrays;
import java.util.List;

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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * A user; can be an Admin, a Student, or a Teacher
 *
 * Teacher can create classes and contest and organize students into groups
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="User.byUsername",
	query="SELECT u FROM User u "
			+ "WHERE u.username = :username AND u.enabled = 1"),
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

	private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public enum Role {
		USER,			// used for logged-in, non-priviledged users
		ADMIN,			// used for maximum priviledged users		
		MODERATOR,		// remove or add roles as needed
	}
	
	// do not change these fields
	private long id;
	private String username;
	private String password;
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
	public boolean hasRole(Role role) {
		String roleName = role.name();
		return Arrays.stream(roles.split(","))
				.anyMatch(r -> r.equals(roleName));
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}	

	@Column(nullable = false)
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password to an encoded value. 
	 * You can generate encoded passwords using {@link #encodePassword}.
	 * call only with encoded passwords - NEVER STORE PLAINTEXT PASSWORDS
	 * @param encodedPassword to set as user's password
	 */
	public void setPassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	/**
	 * Tests a raw (non-encoded) password against the stored one.
	 * @param rawPassword to test against
	 * @return true if encoding rawPassword with correct salt (from old password)
	 * matches old password. That is, true iff the password is correct  
	 */
	public boolean passwordMatches(String rawPassword) {
		return encoder.matches(rawPassword, this.password);
	}

	/**
	 * Encodes a password, so that it can be saved for future checking. Notice
	 * that encoding the same password multiple times will yield different
	 * encodings, since encodings contain a randomly-generated salt.
	 * @param rawPassword to encode
	 * @return the encoded password (typically a 60-character string)
	 * for example, a possible encoding of "test" is 
	 * $2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
	 */
	public static String encodePassword(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public byte getEnabled() {
		return enabled;
	}

	public void setEnabled(byte enabled) {
		this.enabled = enabled;
	}

	public static BCryptPasswordEncoder getEncoder() {
		return encoder;
	}

	public static void setEncoder(BCryptPasswordEncoder encoder) {
		User.encoder = encoder;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public int getPassed() {
		return passed;
	}

	public void setPassed(int passed) {
		this.passed = passed;
	}

	public int getPerfect() {
		return perfect;
	}

	public void setPerfect(int perfect) {
		this.perfect = perfect;
	}
	
	@ManyToOne(targetEntity = StTeam.class)
	@JoinColumn(name = "members")
	public StTeam getTeam() {
		return team;
	}

	public void setTeam(StTeam team) {
		this.team = team;
	}

	@ManyToOne(targetEntity = StClass.class)
	@JoinColumn(name = "students")
	public StClass getStClass() {
		return stClass;
	}

	public void setStClass(StClass stClass) {
		this.stClass = stClass;
	}

	@OneToMany(targetEntity = StClass.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "teacher")
	public List<StClass> getStClassList() {
		return stClassList;
	}

	public void setStClassList(List<StClass> stClassList) {
		this.stClassList = stClassList;
	}

	@OneToMany(targetEntity = Contest.class)
	@JoinColumn(name = "teacher")
	public List<Contest> getContests() {
		return contestList;
	}

	public void setContests(List<Contest> contests) {
		this.contestList = contests;
	}

	@OneToMany(targetEntity = Result.class)
	@JoinColumn(name = "user")
	public List<Result> getResultList() {
		return resultList;
	}

	public void setResultList(List<Result> resultList) {
		this.resultList = resultList;
	}

	@OneToMany(targetEntity = Achievement.class)
	@JoinColumn(name = "student")
	public List<Achievement> getAchievementUser() {
		return achievementUser;
	}

	public void setAchievementUser(List<Achievement> achivementUser) {
		this.achievementUser = achivementUser;
	}
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();
		
		stb.append("--- USUARIO ---\n");
		stb.append("Nombre: " + this.getFirstName() + "\n");
		stb.append("Apellidos: " + this.getLastName() + "\n");
		stb.append("Usuario: " + this.getUsername() + "\n");
		stb.append("Elo: " + Double.toString(this.getElo()) + "\n");
		
	    return stb.toString();
	}
}
