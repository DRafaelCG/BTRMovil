package com.grupocsi.btrmovil;

import java.util.ArrayList;
import java.util.Arrays;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grupocsi.btrmovil.PullToRefreshListView.OnRefreshListener;

@SuppressLint("NewApi")
public class ConsultarExpedientes extends Activity{
	ClsEjecutivo ejecutivo = Login.getEjecutivo();
	GridvAdapter mAdapter = null;
	ArrayList<String> listTitles;
	ArrayList<String> listNombres;
	ArrayList<String> listIndicaciones;
	ArrayList<Integer> listNoExp;
	ArrayList<Integer> listEstInt;
	ArrayList<Integer> listEstCliente;
	private PullToRefreshListView gvExp;
	private boolean[] clickable;
	int idActualiza = 0;
	TextView tvSinExpedientes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_consultar_expedientes);
		tvSinExpedientes = (TextView)findViewById(R.id.tvSinExpedientes);
		listTitles = new ArrayList<String>();
		listEstInt = new ArrayList<Integer>();
		listEstCliente = new ArrayList<Integer>();
		listNombres = new ArrayList<String>();
		listIndicaciones = new ArrayList<String>();
		listNoExp = new ArrayList<Integer>(); 
		idActualiza = 0;
		new ObtenExpedientesTask().execute(String.valueOf(ejecutivo.getIdUsuario()));
		gvExp = (PullToRefreshListView)findViewById(R.id.gVlistExp);
		gvExp.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				idActualiza = 1;
				gvExp.postDelayed(new Runnable() {
					@Override
					public void run() {
						new ObtenExpedientesTask().execute(String.valueOf(ejecutivo.getIdUsuario()));
						gvExp.onRefreshComplete();
					}
				}, 2000);
			}
		});
	}
	
	private class ObtenExpedientesTask extends AsyncTask<String, Void, ArrayList<String>>{
		@Override
		protected void onPreExecute(){
		}
		
		@Override
		protected ArrayList<String> doInBackground(String...args){
			listarExpedientes lExpedientes = new listarExpedientes();
			lExpedientes.idUsuario = args[0];
			lExpedientes.connection();
			return listTitles;
		}
		
		
		@Override
		protected void onPostExecute(ArrayList<String> listTiList){
			if (listTitles != null) {
				mAdapter = null;
				mAdapter = new GridvAdapter();
				gvExp.setAdapter(null);
				gvExp.setAdapter(mAdapter);
				clickable = new boolean[listTiList.size()];
				Arrays.fill(clickable, Boolean.FALSE);
				tvSinExpedientes.setVisibility(View.INVISIBLE);
				tvSinExpedientes.setWidth(0);
				tvSinExpedientes.setHeight(0);
			}
		}
	}	
	
	public class listarExpedientes{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/ConsultarExpedientes";
	    private static final String METHOD_NAME = "ConsultarExpedientes";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	    /********btrsantander.grupocsi.com/ws/btrservice.asmx*************/
	    String idUsuario;
	    
	    public listarExpedientes(){
	    }
	    
	    public void connection(){
	    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("pIdUsuario", idUsuario.toString());
	    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = true;
	    	envelope.setOutputSoapObject(request);
	    	try {
	    		httpTransport.debug = true;
	    		httpTransport.call(SOAP_ACTION, envelope);
	    		SoapObject sResult = (SoapObject)envelope.getResponse();
	    		if (sResult != null) {
					listIndicaciones = new ArrayList<String>();
					listNoExp = new ArrayList<Integer>();
					listTitles = new ArrayList<String>();
					listNombres = new ArrayList<String>();
					listEstInt = new ArrayList<Integer>();
					listEstCliente = new ArrayList<Integer>();
					for (int i = 0; i < sResult.getPropertyCount(); i++) {
						SoapObject im = (SoapObject)sResult.getProperty(i);
						listNoExp.add(Integer.parseInt(im.getProperty(1).toString()));
						listTitles.add(im.getProperty(1).toString());
						listNombres.add(im.getProperty(5).toString());
						SoapObject sResultEstatusInt = (SoapObject)im.getProperty("EstatusInterno");
						listEstInt.add(Integer.parseInt(sResultEstatusInt.getProperty(0).toString()));
						SoapObject sResultEstatusCli = (SoapObject)im.getProperty("EstatusCliente");
						listEstCliente.add(Integer.parseInt(sResultEstatusCli.getProperty(0).toString()));
						SoapObject sResultInconsistencias = (SoapObject)im.getProperty("Inconsistencias");
						if (sResultInconsistencias != null) {
							if (sResultInconsistencias.getPropertyCount() == 0 ) {
								listIndicaciones.add("");
							}else {
								String incon = "";
								for (int j = 0; j < sResultInconsistencias.getPropertyCount(); j++) {
									incon = incon + sResultInconsistencias.getProperty(j).toString().replace("[", "").replace("]", "").replace(",", "") + "\n";
								}
								listIndicaciones.add(incon);
							}
						}
					}
	    		}
	    	}catch (Exception e) {
	    		Log.d("Error llenado adaptador -- ", e.toString());
	    	}
	    }
	}	

	public class GridvAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		
		public GridvAdapter(){
			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}		

		@Override
		public int getCount() {
			return listTitles.size();
		}

		@Override
		public Object getItem(int position) {
			return listTitles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder view;
			if (convertView == null) {
				view = new ViewHolder();
				convertView = mInflater.inflate(R.layout.rows, null);
				view.tVDocumentacion = (TextView)convertView.findViewById(R.id.tVDocumentacion);
				view.tvFolio = (TextView)convertView.findViewById(R.id.folio);
				view.tvNombre = (TextView)convertView.findViewById(R.id.tVNomCliente);
				view.tvIndicaciones = (TextView)convertView.findViewById(R.id.tVIndica);
				view.tvRevisionSEL = (TextView)convertView.findViewById(R.id.tVRevisado);
				view.tvSantander = (TextView)convertView.findViewById(R.id.tVSantander);
				convertView.setTag(view);
			}else {
				view = (ViewHolder)convertView.getTag();
			}
			view.tvFolio.setText(listTitles.get(position));
			view.tvNombre.setText(listNombres.get(position));
			view.tvIndicaciones.setText(listIndicaciones.get(position));
			switch (listEstInt.get(position)) {
			case 0:
				view.tvRevisionSEL.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_reject25x25, 0, 0);
				view.tVDocumentacion.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_reject25x25, 0, 0);
				clickable[position] = true;
				break;
			case 1:
				view.tvRevisionSEL.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_ok25x25, 0, 0);
				view.tVDocumentacion.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_ok25x25, 0, 0);
				clickable[position] = false;
				break;
			case 2:
				view.tvRevisionSEL.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_wait25x25_b, 0, 0);
				view.tVDocumentacion.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_ok25x25, 0, 0);
				clickable[position] = false;
				break;

			default:
				break;
			}
			switch (listEstCliente.get(position)) {
			case 0:
				view.tvSantander.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_reject25x25, 0, 0);
				break;
			case 1:
				view.tvSantander.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_ok25x25, 0, 0);
				break;
			case 2:
				view.tvSantander.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_wait25x25_b, 0, 0);
				break;
			}
			view.tVDocumentacion.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (clickable[position]) {
						startCargaDocumentos(listNoExp.get(position), String.valueOf(listTitles.get(position)), 
								String.valueOf(listIndicaciones.get(position)));
					}
				}
			});
			view.id = position;
			return convertView;
		}
	}
	
	class ViewHolder{
		public TextView tVDocumentacion;
		public TextView idexpediente;
		public TextView tvFolio;
		public TextView tvNombre;
		public TextView tvIndicaciones;
		public TextView tvRevisionSEL;
		public TextView tvSantander;
		int id;
	}	
	
	public void startCargaDocumentos(int idExp, String Folio, String Incon){
		Intent intCargaDoctos = new Intent(ConsultarExpedientes.this, Formulario.class);
		intCargaDoctos.putExtra("idRequest", 2);
		intCargaDoctos.putExtra("idExp", idExp);
		intCargaDoctos.putExtra("Folio", Folio);
		intCargaDoctos.putExtra("Incon", Incon);
		startActivity(intCargaDoctos);
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
			listTitles = null;
			listEstInt = null;
			listEstCliente = null;
			listNombres = null;
			listIndicaciones = null;
			listNoExp = null;
			clickable = null;
			Intent intRegresaMenu = new Intent(ConsultarExpedientes.this, Menu.class);
			startActivity(intRegresaMenu);
			finish();	
		}
		return true;
	}
}
