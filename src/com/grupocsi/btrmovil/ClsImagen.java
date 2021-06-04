package com.grupocsi.btrmovil;

import android.graphics.Bitmap;

public class ClsImagen {
	private boolean enviado;
	private Bitmap imagen;
	private String imgBase64;
	
	public ClsImagen(){
		enviado = false;
		imagen = null;
		imgBase64 = "";
	}

	boolean isEnviado() {
		return enviado;
	}

	void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	Bitmap getImagen() {
		return imagen;
	}

	void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}
	
	String getImgBase64(){
		return imgBase64;
	}
	
	void setImgBase64(String imgBase64){
		this.imgBase64 = imgBase64;
	}

}
