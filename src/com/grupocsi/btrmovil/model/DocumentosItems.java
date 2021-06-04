package com.grupocsi.btrmovil.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable

public class DocumentosItems {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private int idDocumento;
	@DatabaseField
	private String docNombre;
	@DatabaseField
	private String nombre1;
	@DatabaseField(canBeNull= true)
	private String nombre2;
	@DatabaseField
	private String apPaterno;
	@DatabaseField
	private String apMaterno;
	@DatabaseField
	private String fechaNacimiento;
	@DatabaseField
	private String RFC;
	@DatabaseField
	private String telCelular;
	@DatabaseField
	private String correoElectronico;
	@DatabaseField
	private int idProducto;
	@DatabaseField
	private String prodDescripcion;
	@DatabaseField
	private String imagen;
	@DatabaseField
	private int idReg;
	@DatabaseField
	private String fecha;
	@DatabaseField(defaultValue = "false")
	private Boolean enviado;
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getIdDocumento(){
		return idDocumento;
	}
	
	public void setIdDocumento(int idDocumento){
		this.idDocumento = idDocumento;
	}
	
	public String getDocNombre(){
		return docNombre;
	}
	
	public void setDocNombre(String docNombre){
		this.docNombre = docNombre;
	}
	
	public String getNombre1(){
		return nombre1;
	}
	
	public void setNombre1(String nombre1){
		this.nombre1 = nombre1;
	}
	
	public String getNombre2(){
		return nombre2;
	}
	
	public void setNombre2(String nombre2){
		this.nombre2 = nombre2;
	}
	
	public String getApPaterno(){
		return apPaterno;
	}
	
	public void setApPaterno(String apPaterno){
		this.apPaterno = apPaterno;
	}
	
	public String getApMaterno(){
		return apMaterno;
	}
	
	public void setApMaterno(String apMaterno){
		this.apMaterno = apMaterno;
	}
	
	public String getRFC(){
		return RFC;
	}
	
	public void setRFC(String RFC){
		this.RFC = RFC;
	}
	
	public int getIdProducto(){
		return idProducto;
	}
	
	public void setIdProducto(int idProducto){
		this.idProducto = idProducto;
	}
	
	public String getImagen(){
		return imagen;
	}
	
	public void setImagen(String imagen){
		this.imagen = imagen;
	}
	
	public int getIdReg(){
		return idReg;
	}
	
	public void setIdReg(int idReg){
		this.idReg = idReg;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getProdDescripcion() {
		return prodDescripcion;
	}

	public void setProdDescripcion(String prodDescripcion) {
		this.prodDescripcion = prodDescripcion;
	}
	
	public Boolean getEnviado(){
		return enviado;
	}
	
	public void setEnviado(Boolean enviado){
		this.enviado = enviado;
	}
	
	public String getFechaNacimiento(){
		return fechaNacimiento;
	}
	
	public void setFechaNacimiento(String fechaNacimiento){
		this.fechaNacimiento = fechaNacimiento;
	}
	
	public String getTelCelular(){
		return telCelular;
	}
	
	public void setTelCelular(String telCelular){
		this.telCelular = telCelular;
	}
	
	public String getCorreoElectronico(){
		return correoElectronico;
	}
	
	public void setCorreoElectronico(String correoElectronico){
		this.correoElectronico = correoElectronico;
	}
}
