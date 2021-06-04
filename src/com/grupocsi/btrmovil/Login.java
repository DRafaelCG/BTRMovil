package com.grupocsi.btrmovil;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
@SuppressLint("NewApi")
public class Login extends Activity {
	public static ClsEjecutivo ejecutivo = new ClsEjecutivo();
	public static ClsEjecutivo getEjecutivo(){
		return ejecutivo;
	}
	@SuppressWarnings("static-access")
	public void setEjecutivo(ClsEjecutivo ejecutivo){
		this.ejecutivo = ejecutivo;
	}
	ClsEjecutivo[] listaEjecutivo;
	RelativeLayout rLayoutLogin;
	EditText etUsuario;
	EditText etPwd;
	Button btnLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			setContentView(R.layout.activity_login);
			etUsuario = (EditText)findViewById(R.id.edusuario);
			etPwd = (EditText)findViewById(R.id.edpassword);
			btnLogin = (Button)findViewById(R.id.btnLogin);
			if (!verifConexion(this)) {
				Toast.makeText(getBaseContext(), "Comprueba tu conexion...", Toast.LENGTH_LONG).show();
				finish();
			}else {
				btnLogin.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if ((etUsuario.getText().toString().equals(null)) || (etUsuario.getText().toString().equals("")) ) {
							new AlertDialog.Builder(Login.this)
							.setTitle("¡Mensaje!")
							.setMessage("Debe colocar usuario")
							.setCancelable(true)
							.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							}).show();
						}else {
							if ((etPwd.getText().toString().equals(null)) || (etPwd.getText().toString().equals("")) ) {
								new AlertDialog.Builder(Login.this)
								.setTitle("¡Mensaje!")
								.setMessage("Debe colocar contraseña")
								.setCancelable(true)
								.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								}).show();
							}else {
								new validateCredentialTask().execute(etUsuario.getText().toString(), etPwd.getText().toString());
							}
						}
					}
				});
			}			
		} catch (Exception e) {
			Log.d("Create -- login", e.toString());
		}
	}

	public static boolean verifConexion(Context ctx){
		boolean pConectado = false;
		ConnectivityManager conx = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = conx.getAllNetworkInfo();
		//Comprueba wi-fi y gprs
		for (int i = 0; i < 2; i++) {
			if (netInfo[i].getState() == NetworkInfo.State.CONNECTED) {
				pConectado = true;
			}
		}
		return pConectado;			
	}
	
	private class validateCredentialTask extends AsyncTask<String, Void, ClsEjecutivo>{
		private final ProgressDialog dialogo = new ProgressDialog(Login.this);
		@Override
		protected void onPreExecute(){
			dialogo.setMessage("Validando...");
			dialogo.setIcon(R.drawable.ic_launcher);
			dialogo.show();
			dialogo.setCanceledOnTouchOutside(false);
		}
		
		@Override
		protected ClsEjecutivo doInBackground(String... args){
			AuthenticateUser mAuth = new AuthenticateUser();
			mAuth.mUser = args[0];
			mAuth.mPwd =  args[1];
			mAuth.connection(mAuth.mUser, mAuth.mPwd);
			return ejecutivo;
		}
		
		@Override
		protected void onPostExecute(ClsEjecutivo ejecutivo){
			if (dialogo.isShowing()) {
				postValidaUsuario();
				dialogo.dismiss();
			}
		}
	}	
	
	public void postValidaUsuario(){
		if (ejecutivo.getpNombre() == null || ejecutivo.getIdUsuario() == 0) {
			new AlertDialog.Builder(Login.this)
			.setTitle("Datos incorrectos")
			.setMessage("Usuario o contraseña incorrecto")
			.setCancelable(true)
			.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).show();
		}else {
			new AlertDialog.Builder(Login.this)
			.setTitle("Bienvenido")
			.setMessage(ejecutivo.getpNombre().toString())
			.setCancelable(true)
			.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					iniciarSes();
				}
			}).show();
		}
	}
	
	public void iniciarSes(){
		try {
			if (ejecutivo.getClass() != null) {
				Intent intMenu = new Intent(Login.this, Menu.class);
				startActivity(intMenu);
				etPwd.setText(null);
				finish();
			}
		} catch (Exception e) {
			Log.d("Error clsUser vacio -- ", e.toString());
		}catch (Throwable e) {
			Log.d("Error finalize() -- ", e.toString());
		}
	}
	
	public class AuthenticateUser{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/ValidarCredenciales";
	    private static final String METHOD_NAME = "ValidarCredenciales";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	    public String mUser;
	    public String mPwd;
	    public AuthenticateUser(){
	    }
	    public void connection(String mUser, String mPwrd){
	    	try {
		    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		    	request.addProperty("pUserName", mUser);
		    	request.addProperty("pPassword", mPwrd);
		    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
		    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		    	envelope.dotNet = true;
		    	envelope.setOutputSoapObject(request);
		    	try {
					httpTransport.debug = true;
					httpTransport.call(SOAP_ACTION, envelope);
					final SoapObject sResult = (SoapObject)envelope.getResponse();
					if (sResult != null) {
						listaEjecutivo = new ClsEjecutivo[1];
						for (int i = 0; i < listaEjecutivo.length; i++) {
							ejecutivo.setIdUsuario(Integer.parseInt(sResult.getProperty(0).toString()));
							ejecutivo.setIdBroker(Integer.parseInt(sResult.getProperty(1).toString()));
							ejecutivo.setNombreDeUsuario(sResult.getProperty(2).toString());
							ejecutivo.setpNombre(sResult.getProperty(3).toString());
							ejecutivo.setsNombre(sResult.getProperty(4).toString());
							ejecutivo.setApPaterno(sResult.getProperty(5).toString());
							ejecutivo.setApMaterno(sResult.getProperty(6).toString());
							ejecutivo.setFechaAlta(String.valueOf(sResult.getProperty(8).toString()));
							ejecutivo.setEmail(String.valueOf(sResult.getProperty(7).toString()));						
							listaEjecutivo[i] = ejecutivo;
						}
					}else {
						ejecutivo.setpNombre(null);
						ejecutivo.setIdUsuario(0);
					}
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}catch (XmlPullParserException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}				
			} catch (Exception e) {
				Log.d("Login - validando usuario", e.toString());
			}
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
			Intent mInicio = new Intent(Login.this, Inicio.class);
			startActivity(mInicio);
			finish();
		}
		return true;
	}
}