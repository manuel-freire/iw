package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Driver {
	private long id;
	private String name;
	
	private List<Car> rides = new ArrayList<Car>();
	
	// -- generated; don't forget to annotate getters for id and relationships

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@ManyToMany(targetEntity=Car.class, mappedBy="drivers")
	public List<Car> getRides() {
		return rides;
	}
	public void setRides(List<Car> rides) {
		this.rides = rides;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Driver #" + id;
	}	
}
