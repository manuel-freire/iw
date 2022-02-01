package es.ucm.fdi.iw.qna.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class Answer {
    private String text;
    private float value; // 0 = none, 1 = max, can be negative
}
