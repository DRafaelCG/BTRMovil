package com.grupocsi.btrmovil;

import com.grupocsi.btrmovil.R;

public class ClsBackground {
	static private ClsBackground instancia;
	int idStatus;
	int idBackground;
	int idBackOther;
	private ClsBackground(){
	}
	
	static public ClsBackground getInstancia(){
		if (instancia == null){
			instancia = new ClsBackground();
			switch (instancia.idStatus) {
			case 0:
				instancia.idBackground = R.drawable.docs01;
				instancia.idBackOther =  R.drawable.rechazado00;
				break;
			case 1:
				instancia.idBackground = R.drawable.docs02;
				instancia.idBackOther =  R.drawable.espera02;
			case 2:
				instancia.idBackground = R.drawable.docs03;
				instancia.idBackOther =  R.drawable.aprobado01;
			}
		}
		return instancia;
	}
	
}
