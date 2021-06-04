package com.grupocsi.btrmovil;

public class ClsDatosInformacion {
	static private ClsDatosInformacion instancia;
	private ClsDatosInformacion(){
	}
	String infTitulo;
	String infContenido1;
	String infTelLada;
	String infContenido2;
	String infTelLocal;
	String infContenido3;
	String infContacto;
	String infEmailContacto;

	static public ClsDatosInformacion getInstancia(){
		if (instancia == null) {
			instancia = new ClsDatosInformacion();
			instancia.infTitulo = new String();
			instancia.infContenido1 = new String();
			instancia.infTelLada = new String();
			instancia.infContenido2 = new String();
			instancia.infTelLocal = new String();
			instancia.infContenido3 = new String();
			instancia.infContacto = new String();
			instancia.infEmailContacto = new String();
		}
		return instancia;
	}
	
}
