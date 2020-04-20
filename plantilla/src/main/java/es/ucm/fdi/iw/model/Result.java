package es.ucm.fdi.iw.model;

import java.util.List;

import javax.persistence.ElementCollection;
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
 * A question can be included in several contest. It can be answered with different options. Each option has
 * an associated score.
 *
 * @author aitorcay
 */

@Entity
@NamedQueries({
	@NamedQuery(name="Result.getResult",
	query="SELECT r FROM Result r JOIN r.user u JOIN r.contest c "
			+ "WHERE u.id = :userId "
			+ "AND c.id = :contestId"),
	@NamedQuery(name="Result.hasAnswer",
	query="SELECT COUNT(r) FROM Result r JOIN r.user u JOIN r.contest c "
			+ "WHERE u.id = :userId "
			+ "AND c.id = :contestId"),
})

public class Result {
	
	private long id;
	private User user;
	private Contest contest;
	private List<Answer> answers;
	
	private int correct;
	private double score;
	private boolean passed;
	private boolean perfect;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "resultList")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(targetEntity = Contest.class)
	@JoinColumn(name = "results")
	public Contest getContest() {
		return contest;
	}

	public void setContest(Contest contest) {
		this.contest = contest;
	}

	@OneToMany(targetEntity = Answer.class)
	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public boolean isPerfect() {
		return perfect;
	}

	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
	}	
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- RESULTADO ---\n");
		stb.append("Usuario: " + this.user.getUsername() + "\n");
		stb.append("Prueba: " + this.contest.getName() + "\n");	
		if (this.passed) {
			stb.append("Prueba superada \n");
		} else {
			stb.append("Prueba fallida \n");			
		}	
		stb.append("Correctas: " + this.correct + "\n");
		stb.append("Puntuación: " + this.score + "\n");
		
		for (int i = 0; i < this.answers.size(); i++) {
			stb.append(Integer.toString(i+1) + ": " +this.answers.get(i).toString());
		}
		
	    return stb.toString();
	}
}
