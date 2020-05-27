package es.ucm.fdi.iw.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


/**
 * A question can be included in several contest. It can be answered with different options. Each option has
 * an associated score.
 *
 * @author aitorcay
 */

@Entity
public class Question {
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String text;

	@OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name = "question_id")
	private List<Answer> answers;
	
	@ManyToOne
	private Contest contest;
	
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


	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	public Contest getContest() {
		return contest;
	}
	
	public void setContest(Contest contest) {
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
