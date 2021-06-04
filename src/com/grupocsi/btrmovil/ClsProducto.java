package com.grupocsi.btrmovil;

public class ClsProducto {
	private int idProducto;
	private String descripcion;
	
	public ClsProducto(){
		setIdProducto(0);
		setDescripcion("");
	}

	int getIdProducto() {
		return idProducto;
	}

	void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}

	String getDescripcion() {
		return descripcion;
	}

	void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}
