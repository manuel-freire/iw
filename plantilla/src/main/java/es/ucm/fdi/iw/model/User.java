package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * A user; can be an Admin, a User, or a Moderator
 *
 * Users can log in and send each other messages.
 *
 * @author mfreire
 */
/**
 * An authorized user of the system.
 */
@Entity
@Data
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name="User.byUsername",
                query="SELECT u FROM User u "
                        + "WHERE u.username = :username AND u.enabled = 1"),
        @NamedQuery(name="User.hasUsername",
                query="SELECT COUNT(u) "
                        + "FROM User u "
                        + "WHERE u.username = :username")
})
public class User implements Transferable<User.Transfer> {


	private static Logger log = LogManager.getLogger(User.class);	

	public enum Role {
		USER,			// used for logged-in, non-priviledged users
		ADMIN,			// used for maximum priviledged users
		
		MODERATOR,		// remove or add roles as needed
	}
	
	// do not change these fields

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	/** username for login purposes; must be unique */
	@Column(nullable = false, unique = true)
	private String username;
	/** encoded password; use setPassword(SecurityConfig.encode(plaintextPassword)) to encode it  */
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String roles; // split by ',' to separate roles
	private byte enabled;

	// application-specific fields
	private String firstName;
	private String lastName;

	@OneToMany
	@JoinColumn(name = "sender_id")
	private List<Message> sent = new ArrayList<>();
	@OneToMany
	@JoinColumn(name = "recipient_id")	
	private List<Message> received = new ArrayList<>();	
	
	// utility methods
	
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

    @Getter
    @AllArgsConstructor
    public static class Transfer {
		private long id;
        private String username;
		private int totalReceived;
		private int totalSent;
    }

	@Override
    public Transfer toTransfer() {
		return new Transfer(id,	username, received.size(), sent.size());
	}
	
	@Override
	public String toString() {
		return toTransfer().toString();
	}
}
