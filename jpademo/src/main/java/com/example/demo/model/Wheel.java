package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Wheel {
	
	public enum Position { FrontRight, FrontLeft, RearRight, RearLeft };
	
	private long id;
	
	private Car car;
	private Position position;

	// -- generated; don't forget to annotate getters for id and relationships
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@ManyToOne(targetEntity=Car.class)
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position pos) {
		this.position = pos;
	}
	@Override
	public String toString() {
		return "Wheel #" + id;
	}		
}
