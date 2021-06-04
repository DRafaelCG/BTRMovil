package com.grupocsi.btrmovil;

public class ClsEnvio {
	private int idEnvio;
	private int idExpediente;
	
	public ClsEnvio(){
		setIdEnvio(0);
		setIdExpediente(0);
	}

	int getIdEnvio() {
		return idEnvio;
	}

	void setIdEnvio(int idEnvio) {
		this.idEnvio = idEnvio;
	}

	int getIdExpediente() {
		return idExpediente;
	}

	void setIdExpediente(int idExpediente) {
		this.idExpediente = idExpediente;
	}
	
}
