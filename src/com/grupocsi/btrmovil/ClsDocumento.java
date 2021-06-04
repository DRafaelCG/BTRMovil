package com.grupocsi.btrmovil;

import java.util.ArrayList;

public class ClsDocumento {
	private int idDocumento;
	private String nombreDocumento;
	private ArrayList<ClsImagen> imagenes;
	
	public ClsDocumento(){
		idDocumento = 0;
		nombreDocumento =  "";
		imagenes = new ArrayList<ClsImagen>();
	}
	
	int getIdDocumento() {
		return idDocumento;
	}
	void setIdDocumento(int idDocumento) {
		this.idDocumento = idDocumento;
	}
	String getNombreDocumento() {
		return nombreDocumento;
	}
	void setNombreDocumento(String nombreDocumento) {
		this.nombreDocumento = nombreDocumento;
	}
	ArrayList<ClsImagen> getImagenes() {
		return imagenes;
	}
	/*****************************************************************
	 * Generalmente no se pone en arreglos
	 * ****/
/*	void setImagenes(ArrayList<ClsImagen> imagenes) {
		this.imagenes = imagenes;
	}*/

}
