package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Car {
	private long id;
	private String company;
	
	@NotNull
	@Size(max=10)
	private String model;
	
	private List<Driver> drivers = new ArrayList<>();
	private List<Wheel> wheels = new ArrayList<>();

	// -- generated; don't forget to annotate getters for id and relationships
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}

	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@ManyToMany(targetEntity=Driver.class)	
	public List<Driver> getDrivers() {
		return drivers;
	}
	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}
	@OneToMany(targetEntity=Wheel.class)
	@JoinColumn(name="car_id")
	public List<Wheel> getWheels() {
		return wheels;
	}
	public void setWheels(List<Wheel> wheels) {
		this.wheels = wheels;
	}

	@Override
	public String toString() {
		return "Car #" + id;
	}
	
}
