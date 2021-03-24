package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class Driver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	
	@ManyToMany
	private List<Car> rides = new ArrayList<Car>();
	
	// -- generated; don't forget to annotate getters for id and relationships
	@Override
	public String toString() {
		return "Driver #" + id;
	}	
}
