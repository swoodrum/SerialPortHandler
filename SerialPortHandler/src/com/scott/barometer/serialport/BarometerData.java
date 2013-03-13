package com.scott.barometer.serialport;

import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class BarometerData {
	@Id
    @Column(name = "ROW_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
	
	@Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date = Calendar.getInstance();
	
	@Basic
    private int paPressure;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public int getPaPressure() {
		return paPressure;
	}

	public void setPaPressure(int paPressure) {
		this.paPressure = paPressure;
	}
}
