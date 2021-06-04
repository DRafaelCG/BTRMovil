package com.grupocsi.btrmovil;

import java.util.ArrayList;

public class ClsDataClass {
	static private ClsDataClass instancia;
	ClsExpediente expediente;
	ClsEnvio envio;
	ArrayList<ClsDocumento> documentos;

	private ClsDataClass(){
		
	}

	static public ClsDataClass getInstancia() {
		if (instancia == null) {
			instancia = new ClsDataClass();
			instancia.documentos = new ArrayList<ClsDocumento>();
			ClsDocumento documento1 = new ClsDocumento();
			documento1.setIdDocumento(1);
			documento1.setNombreDocumento("Solicitud");
			ClsDocumento documento2 = new ClsDocumento();
			documento2.setIdDocumento(2);
			documento2.setNombreDocumento("Identificación");
			ClsDocumento documento3 = new ClsDocumento();
			documento3.setIdDocumento(3);
			documento3.setNombreDocumento("Comprobante de domicilio");
			ClsDocumento documento4 = new ClsDocumento();
			documento4.setIdDocumento(4);
			documento4.setNombreDocumento("Comprobante de ingresos");
			instancia.documentos.add(documento1);
			instancia.documentos.add(documento2);
			instancia.documentos.add(documento3);
			instancia.documentos.add(documento4);
		}
		return instancia;
	}
	
}
