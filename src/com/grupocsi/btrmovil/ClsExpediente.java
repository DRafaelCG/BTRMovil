package com.grupocsi.btrmovil;

public class ClsExpediente {
	int idExpediente;
	String folio;
	String nombre;
	ClsExpediente(){
		setIdExpediente(0);
		setFolio("");
		setNombre("");	
	}
	
	int getIdExpediente() {
		return idExpediente;
	}
	void setIdExpediente(int idExpediente) {
		this.idExpediente = idExpediente;
	}
	String getFolio() {
		return folio;
	}
	void setFolio(String folio) {
		this.folio = folio;
	}
	String getNombre() {
		return nombre;
	}
	void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
