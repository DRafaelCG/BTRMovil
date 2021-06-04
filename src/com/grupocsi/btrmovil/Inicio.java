package com.grupocsi.btrmovil;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("NewApi")
public class Inicio extends Activity {
	ClsDatosInformacion llenaInfo = ClsDatosInformacion.getInstancia();
	Button btnInf;
	Button btnInicio;
	KeyEvent event;
	
	/***
	 * @author Rafael Castrejón
	 * Pantalla de inicio 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("");
		setContentView(R.layout.activity_inicio);
		/**
		 * Inicia la actividad de información
		 */
		btnInf = (Button)findViewById(R.id.btnInforme);
		btnInf.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intInfo = new Intent(Inicio.this, Informacion.class);
				startActivity(intInfo);
			}
		});
		/**
		 * Inicia la actividad login 
		 * y finaliza la actividad actual
		 */
		btnInicio = (Button)findViewById(R.id.btnInicio);
		btnInicio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intLogin = new Intent(Inicio.this, Login.class);
				startActivity(intLogin);
				finish();
			}
		});
		if (llenaInfo.infTitulo.equals("")) {
			new llenaInfoTask().execute();
		}
		/*final Timer t = new Timer("");
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				t.cancel();
				
			}
		}, 12000);*/
		
		
	}
	
	public void onBackPressed(){
		Intent mSplashScreen = new Intent(Inicio.this, SplashScreen.class);
		startActivity(mSplashScreen);
		finish();
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
	
	private class llenaInfoTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute(){
		}
		@Override
		protected Void doInBackground(Void...strings){
			llenaInfo lI = new llenaInfo();
			lI.connection();
			return null;
		}
	    @Override
	    protected void onPostExecute(Void v){
	    }
	}
	
	public class llenaInfo{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/ConsultarInformacionSoporte";
	    private static final String METHOD_NAME = "ConsultarInformacionSoporte";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	    public llenaInfo(){
	    }
	    public void connection(){
	    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("pIdTipoDispositivo", 1);
	    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = true;
	    	envelope.setOutputSoapObject(request);
	    	try {
	    		httpTransport.debug = true;
				httpTransport.call(SOAP_ACTION, envelope);
				final SoapObject sResult = (SoapObject)envelope.getResponse();
				if (sResult != null) {
					llenaInfo.infTitulo = sResult.getPropertyAsString(0);
					llenaInfo.infContenido1 = sResult.getPropertyAsString(1);
					llenaInfo.infTelLada = sResult.getPropertyAsString(2);
					llenaInfo.infContenido2 = sResult.getPropertyAsString(3);
					llenaInfo.infTelLocal = sResult.getPropertyAsString(4);
					llenaInfo.infContenido3 = sResult.getPropertyAsString(5);
					llenaInfo.infContacto = sResult.getPropertyAsString(6);
					llenaInfo.infEmailContacto = sResult.getPropertyAsString(7);
				}
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}catch (XmlPullParserException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}

}
