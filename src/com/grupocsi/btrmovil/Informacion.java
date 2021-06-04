package com.grupocsi.btrmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Informacion extends Activity {
	ClsDatosInformacion inf = ClsDatosInformacion.getInstancia();
	TextView tVTitleInformacion;
	TextView tVInformacion;
	TextView tVTel1;
	TextView tVInformacion_b;
	TextView tVTel2;
	TextView tVInformacion_c;
	TextView tVInformacion2;
	TextView tvInfCorreo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacion);
		tVTitleInformacion = (TextView)findViewById(R.id.titleinformacion);
		tVInformacion = (TextView)findViewById(R.id.tVInformacion);
		tVTel1 = (TextView)findViewById(R.id.tVTel1);
		tVInformacion_b = (TextView)findViewById(R.id.tVInformacion_b);
		tVTel2 = (TextView)findViewById(R.id.tVTel2);
		tVInformacion_c = (TextView)findViewById(R.id.tVInformacion_c);
		tVInformacion2 = (TextView)findViewById(R.id.tVInformacion2);
		tvInfCorreo = (TextView)findViewById(R.id.tVInfCorreo);
		llenaInfo();
	}
	
	private void llenaInfo(){
		if (inf != null) {
			tVTitleInformacion.setText(inf.infTitulo.toString());
			tvInfCorreo.setText(inf.infContenido1.toString());
			tVTel1.setText(inf.infTelLada.toString());
			tVInformacion_b.setText(inf.infContenido2.toString());
			tVTel2.setText(inf.infTelLocal.toString());
			tVInformacion_c.setText(inf.infContenido3.toString());
			tVInformacion2.setText(inf.infContacto.toString());
			tvInfCorreo.setText(inf.infEmailContacto.toString());
		}
	}


	/**
	 * Si el usuario oprime la tecla menu de su telefono movil, 
	 * la aplicación termina por completo 
	 */
	@Override
	public boolean onKeyDown(int keycode, KeyEvent event){
		if (keycode == KeyEvent.KEYCODE_MENU) {
			 finishAffinity();
			 android.os.Process.killProcess(android.os.Process.myPid());
		}
		if (keycode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
}
