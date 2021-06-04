package com.grupocsi.btrmovil;


public class ClsEjecutivo{
	private int idUsuario;
	private int idBroker;
	private String nombreDeUsuario;
	private String password;
	private String pNombre;
	private String sNombre;
	private String apPaterno;
	private String apMaterno;
	private String fechaAlta;
	private String email;

	public ClsEjecutivo(){
		setIdUsuario(0);
		setIdBroker(0);
		setNombreDeUsuario("");
		setPassword("");
		setpNombre("");
		setsNombre("");
		setApPaterno("");
		setApMaterno("");
		setFechaAlta("");
		setEmail("");
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getIdBroker() {
		return idBroker;
	}

	public void setIdBroker(int idBroker) {
		this.idBroker = idBroker;
	}

	public String getNombreDeUsuario() {
		return nombreDeUsuario;
	}

	public void setNombreDeUsuario(String nombreDeUsuario) {
		this.nombreDeUsuario = nombreDeUsuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getpNombre() {
		return pNombre;
	}

	public void setpNombre(String pNombre) {
		this.pNombre = pNombre;
	}

	public String getsNombre() {
		return sNombre;
	}

	public void setsNombre(String sNombre) {
		this.sNombre = sNombre;
	}

	public String getApPaterno() {
		return apPaterno;
	}

	public void setApPaterno(String apPaterno) {
		this.apPaterno = apPaterno;
	}

	public String getApMaterno() {
		return apMaterno;
	}

	public void setApMaterno(String apMaterno) {
		this.apMaterno = apMaterno;
	}

	public String getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(String fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
