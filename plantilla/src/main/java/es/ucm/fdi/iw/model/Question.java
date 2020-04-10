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
//@NamedQueries({
//	@NamedQuery(name="Question.byContest",
//	query="SELECT q FROM Question q "
//			+ "WHERE q.contest = :contestId")
//})

public class Question {
	
	private long id;
	private String text;
	private List<Answer> answers;
	private List<Contest> contest;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@OneToMany(targetEntity = Answer.class)
	@JoinColumn(name = "question")
	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	@ManyToOne(targetEntity = Contest.class)
	@JoinColumn(name = "questions")
	public List<Contest> getContest() {
		return contest;
	}
	
	public void setContest(List<Contest> contest) {
		this.contest = contest;
	}	
	
	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("--- PREGUNTA ---\n");
		stb.append( this.text + "\n");
		for (int i = 0; i < this.answers.size(); i++) {
			stb.append(Integer.toString(i+1) + ": " +this.answers.get(i).toString());
		}
		
	    return stb.toString();
	}
}
