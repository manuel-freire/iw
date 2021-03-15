package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Wheel {
	
	public enum Position { FrontRight, FrontLeft, RearRight, RearLeft };
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	private Car car;
	private Position position;

	@Override
	public String toString() {
		return "Wheel #" + id;
	}		
}
