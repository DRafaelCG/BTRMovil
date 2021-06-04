package com.grupocsi.btrmovil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.grupocsi.btrmovil.flyoutmenu.view.FlyOutContainer;
@SuppressLint("NewApi")
public class Menu extends Activity implements OnTouchListener{
	ClsEjecutivo ejecutivo = Login.getEjecutivo();
	Button btnCargaSol;
	Button btnListado;
	int idRequest = 1;
	FlyOutContainer root;
	public float init_x;
	public float init_y;
	Button btnListadoBase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.root = (FlyOutContainer)this.getLayoutInflater().inflate(R.layout.activity_menu, null);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.setContentView(root);
		root.setOnTouchListener(this);
		btnCargaSol = (Button)findViewById(R.id.btnCargaSolicitud);
		btnListado = (Button)findViewById(R.id.btnListado);
		btnListadoBase = (Button)findViewById(R.id.btnListadoBase);
		btnCargaSol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent cargaSol = new Intent(Menu.this, Formulario.class);
				cargaSol.putExtra("idRequest", idRequest);
				startActivity(cargaSol);
			}
		});
		
		btnListado.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent obtenerListado = new Intent(Menu.this, ConsultarExpedientes.class);
				startActivity(obtenerListado);
			}
		});
		btnListadoBase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent cargaListadoBase = new Intent(Menu.this, ListDocsGuardados.class);
				startActivity(cargaListadoBase);
			}
		});
		
	}
	@Override
	public void onBackPressed(){
	}
	
	public void opcionMenu(View v){
		switch (v.getId()) {
		case R.id.btnInformacion:
			informacion();
			break;
		case R.id.btnCerrarSesion:
			cerrarSesion();
			break;
		case R.id.btnCerrarApp:
			cerrarApp();
			break;
		}
	}
	
	private void cerrarSesion(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Va a cerrar su sesión");
		builder.setMessage("¿Desea continuar?")
				.setCancelable(false)
				.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ejecutivo = null;
						Intent intCerrarSesion = new Intent(Menu.this, Inicio.class);
						intCerrarSesion.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intCerrarSesion);
						finish();
					}
				})
				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void cerrarApp(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Esta por finalizar la aplicación SELMóvil");
		builder.setMessage("¿Desea continuar?")
				.setCancelable(false)
				.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ejecutivo = null;
						finishAffinity();
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				})
				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void informacion(){
		Intent intInfo = new Intent(Menu.this, Informacion.class);
		startActivity(intInfo);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			init_x = event.getX();
			init_y = event.getY();
			return true;
		case MotionEvent.ACTION_UP:
			float distance = init_x - event.getX();
			float distancey = init_y - event.getY();
			if (distancey >= -100 && distancey <= 100) {
				if (distance > 0) {
					this.root.toggleMenu();
				}
				if (distance < 0) {
					this.root.toggleMenu();
				}
				if (distance == 0) {
				}
			}
		}
		return false;
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
		}else {
		}
		return true;
	}
}