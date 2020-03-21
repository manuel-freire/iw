package es.ucm.fdi.iw.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


/**
 * A question can be included in several contest. It can be answered with different options. Each option has
 * an associated score.
 *
 * @author aitorcay
 */

@Entity
//@NamedQueries({
//	@NamedQuery(name="Question.byContest",
//	query="SELECT q FROM Question q "
//			+ "WHERE q.contest = :contestId")
//})

public class Question {
	
	private long id;
	private String description;
//	private List<String> options;
//	private List<Integer> scores;
//	private Contest contest;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

//	public List<String> getOptions() {
//		return options;
//	}
//
//	public void setOptions(List<String> options) {
//		this.options = options;
//	}

//	public List<Integer> getScores() {
//		return scores;
//	}
//
//	public void setScores(List<Integer> scores) {
//		this.scores = scores;
//	}

//	@ManyToMany(targetEntity = Contest.class)
//	@JoinColumn(name = "id")
//	public Contest getContest() {
//		return contest;
//	}
//
//	public void setContest(Contest contest) {
//		this.contest = contest;
//	}	
}
