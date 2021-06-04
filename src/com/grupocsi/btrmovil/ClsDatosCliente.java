package com.grupocsi.btrmovil;

public class ClsDatosCliente {
	private String pNombre;
	private String sNombre;
	private String apPaterno;
	private String apMaterno;
	private String RFC;
	private int idProducto;
	private String producto;
	private String fechaNacimiento;
	private String numCelular;
	private String correoElectronico;
	
	private String imgCadenaBase64;
	
	public ClsDatosCliente(){
		pNombre = "";
		sNombre = "";
		apPaterno = "";
		apMaterno = "";
		RFC = "";
		idProducto = 0;
		producto = "";
		imgCadenaBase64 = "";
		fechaNacimiento = "";
		numCelular = "";
		correoElectronico = "";
	}
	
	public String getPNombre() {
		return pNombre;
	}
	public void setPNombre(String pNombre) {
		this.pNombre = pNombre;
	}
	public String getSNombre() {
		return sNombre;
	}
	public void setSNombre(String sNombre) {
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
	public String getRFC() {
		return RFC;
	}
	public void setRFC(String rFC) {
		RFC = rFC;
	}
	public int getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}
	public String getProducto() {
		return producto;
	}
	public void setProducto(String producto) {
		this.producto = producto;
	}
	public String getImgCadenaBase64() {
		return imgCadenaBase64;
	}
	public void setImgCadenaBase64(String imgCadenaBase64) {
		this.imgCadenaBase64 = imgCadenaBase64;
	}
	public String getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public String getNumCelular() {
		return numCelular;
	}
	public void setNumCelular(String numCelular) {
		this.numCelular = numCelular;
	}
	public String getCorreoElectronico() {
		return correoElectronico;
	}
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}
}