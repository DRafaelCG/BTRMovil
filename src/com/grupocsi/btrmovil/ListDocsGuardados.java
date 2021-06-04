package com.grupocsi.btrmovil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.grupocsi.btrmovil.PullToRefreshListView.OnRefreshListener;
import com.grupocsi.btrmovil.db.DatabaseHelper;
import com.grupocsi.btrmovil.model.DocumentosItems;

@SuppressLint("NewApi")
public class ListDocsGuardados extends Activity {
	ClsDataClass dataclass = ClsDataClass.getInstancia();
	ClsDocumento documento;
	ClsEjecutivo ejecutivo = Login.getEjecutivo();
	ClsDatosCliente dCliente;
	PullToRefreshListView listGuardados;
	TramiteAdapter adapter = null;
	DatabaseHelper helper;
	List<DocumentosItems> list;
	List<DocumentosItems> listElementos;
	private boolean[] checados;
	CheckBox chBSelTodo;
	boolean seleccionados = false;
	Button btnEnviarSeleccionados;
	public ArrayList<Integer> paraEnvio;
	TextView tvSinRegistrosGuardados;
	int idExpPendEnviado = 0;
	int idRegBase = 0;
	ProgressDialog pgDiaglogEnviaImgs;
	int idRegParaBorrar = 0;
	int cerrado = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_docs_guardados);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		listGuardados = (PullToRefreshListView)findViewById(R.id.gVlistExpGuardados);
		chBSelTodo = (CheckBox)findViewById(R.id.chBSelTodo);
		btnEnviarSeleccionados = (Button)findViewById(R.id.btnEnviarSeleccionados);
		tvSinRegistrosGuardados = (TextView)findViewById(R.id.tvSinRegistrosGuardados);
		helper = new DatabaseHelper(getApplicationContext());
		setDataToAdapter();
		paraEnvio = new ArrayList<Integer>();
		listGuardados.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				listGuardados.postDelayed(new Runnable() {
					@Override
					public void run() {
						setDataToAdapter();
						listGuardados.onRefreshComplete();
					}
				}, 2000);
			}
		});
		chBSelTodo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (list != null) {
					if (seleccionados) {
						for (int i = 0; i < checados.length; i++) {
							checados[i] = false;
							seleccionados = false;
						}
						if (paraEnvio.size() > 0) {
							paraEnvio.clear();
						}
					}else {
						for (int i = 0; i < checados.length; i++) {
							checados[i] = true;
							seleccionados = true;
						}
						if (paraEnvio.size() == 0) {
							for (int j = 0; j < list.size(); j++) {
								if (list.get(j).getIdReg() > 0) {
									paraEnvio.add(list.get(j).getIdReg());
								}
							}
						}else {
							paraEnvio.clear();
							for (int j = 0; j < list.size(); j++) {
								if (list.get(j).getIdReg() > 0) {
									paraEnvio.add(list.get(j).getIdReg());
								}
							}
						}
					}
					adapter.notifyDataSetChanged();
					listGuardados.setAdapter(adapter);
				}
			}
		});
		btnEnviarSeleccionados.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unused")
			@Override
			public void onClick(View v) {
				if (paraEnvio.isEmpty()) {
					new AlertDialog.Builder(ListDocsGuardados.this).setTitle("¡ Mensaje !")
					.setMessage("Debe seleccionar algún registro para enviar")
					.setCancelable(true)
					.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
				}else {
					for (int i = 0; i < paraEnvio.size(); i++) {
						idExpPendEnviado = paraEnvio.get(i);
						if (idExpPendEnviado == paraEnvio.get(i)) {
							paraEnvio.remove(i);
							preparaDocumentosParaEnvio(idExpPendEnviado);	
						}
						break;
					}
				}
			}
		});
		
	}
	
	private void preparaDocumentosParaEnvio(int idExpPend){
		limpiaDocumentos();
		idRegParaBorrar = idExpPend;
		list = new ArrayList<DocumentosItems>();
		list = helper.getDataWithIdRegGroupByIdDoc(idExpPend); 
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				dCliente = new ClsDatosCliente();
				listElementos = new ArrayList<DocumentosItems>();
				listElementos = helper.getDataWithIdRegAndIdDoc(list.get(i).getIdReg(), list.get(i).getIdDocumento());
				//documento = dataclass.documentos.get(i);
				documento = dataclass.documentos.get(list.get(i).getIdDocumento() - 1);
				ClsImagen img = new ClsImagen();
				for (int j = 0; j < listElementos.size(); j++) {
					idRegBase = listElementos.get(j).getId();
					documento.setIdDocumento(listElementos.get(j).getIdDocumento());
					documento.setNombreDocumento(listElementos.get(j).getDocNombre());
					dCliente.setPNombre(listElementos.get(0).getNombre1().toString());
					if (listElementos.get(0).getNombre2() != null) {
						dCliente.setSNombre(listElementos.get(0).getNombre2().toString());
					}else {
						dCliente.setSNombre("");
					}
					dCliente.setApPaterno(list.get(0).getApPaterno().toString());
					dCliente.setApMaterno(list.get(0).getApMaterno().toString());
					dCliente.setFechaNacimiento(list.get(0).getFechaNacimiento().toString());
					dCliente.setRFC(list.get(0).getRFC().toString());
					dCliente.setNumCelular(list.get(0).getTelCelular().toString());
					dCliente.setCorreoElectronico(list.get(0).getCorreoElectronico().toString());
					dCliente.setIdProducto(list.get(0).getIdProducto());
					dCliente.setProducto(list.get(0).getProdDescripcion().toString());
					img.setImgBase64(list.get(j).getImagen().toString());
					img.setEnviado(false);
					documento.getImagenes().add(img);
				}
			}
			new CreaExpedienteTask().execute();	
		}
		
	}
	
	public class CreaExpedienteTask extends AsyncTask<Void, Void, Void>{
		ProgressDialog pDialog = new ProgressDialog(ListDocsGuardados.this);
		@Override
		protected void onPreExecute(){
			pDialog.setMessage("Creando expediente...");
			pDialog.setCancelable(false);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params){
			CreaExpediente cExp = new CreaExpediente();
			cExp.connection();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v){
			if (dataclass.expediente != null) {
				if (dataclass.expediente.getIdExpediente() != 0) {
					pDialog.setMessage("Expediente creado");
					pDialog.dismiss();
					crearEnvio();
				}	
			}
		}
	}
	
	public class CreaExpediente{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/CrearExpediente";
	    private static final String METHOD_NAME = "CrearExpediente";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
		
		public CreaExpediente(){
		}
		
		public void connection(){
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("pIdUsuario", ejecutivo.getIdUsuario());
	    	request.addProperty("pPrimerNombre", dCliente.getPNombre().toString());
	    	request.addProperty("pSegundoNombre", dCliente.getSNombre().toString());
	    	request.addProperty("pApellidoPaterno", dCliente.getApPaterno().toString());
	    	request.addProperty("pApellidoMaterno", dCliente.getApMaterno().toString());
	    	request.addProperty("pRFC", dCliente.getRFC().toString());
	    	request.addProperty("pIdProducto", dCliente.getIdProducto());
	    	//request.addProperty("pFechaNacimiento", dCliente.getFechaNacimiento().toString());
	    	request.addProperty("pDiaFechaNacimiento", dCliente.getFechaNacimiento().substring(0,2).toString());
	    	request.addProperty("pMesFechaNacimiento", dCliente.getFechaNacimiento().substring(3,5).toString());
	    	request.addProperty("pAñoFechaNacimiento", dCliente.getFechaNacimiento().substring(6,10).toString());
	    	request.addProperty("pCelular", dCliente.getNumCelular().toString());
	    	request.addProperty("pCorreoElectronico", dCliente.getCorreoElectronico().toString());
	    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = true;
	    	envelope.setOutputSoapObject(request);
	    	try {
	    		httpTransport.debug = true;
				httpTransport.call(SOAP_ACTION, envelope);
				final SoapObject sResult = (SoapObject)envelope.getResponse();
				if (sResult != null) {
					ClsExpediente expediente = new ClsExpediente();
					expediente.setIdExpediente(Integer.parseInt(sResult.getProperty(0).toString()));
					expediente.setFolio(sResult.getProperty(1).toString());
					dataclass.expediente = expediente;
				}
			} catch (Exception e) {
				Log.d("Error creando expediente.- ", e.getMessage());
			}
		}
	}
	
	public void crearEnvio(){
		if (dataclass.expediente.getIdExpediente() != 0) {
			new CreaEnvioTask().execute();
		}
	}
	
	public class CreaEnvioTask extends AsyncTask<Void, Void, Void>{
		private final ProgressDialog pDialog = new ProgressDialog(ListDocsGuardados.this);
		@Override
		protected void onPreExecute(){
			pDialog.setMessage("Creando paquete de envio");
			pDialog.setCancelable(false);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();
		}
		@Override
		protected Void doInBackground(Void... params){
			CrearEnvio cEnvio = new CrearEnvio();
			cEnvio.connection();
			return null;
		}
		@Override
		protected void onPostExecute(Void v){
			pDialog.dismiss();
			anexaI();
		}
	}
	
	public class CrearEnvio{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/CrearEnvio";
	    private static final String METHOD_NAME = "CrearEnvio";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
		public CrearEnvio(){
			
		}
		
		public void connection(){
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("pIdUsuario", ejecutivo.getIdUsuario());
	    	request.addProperty("pIdExpediente", dataclass.expediente.getIdExpediente());
	    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = true;
	    	envelope.setOutputSoapObject(request);
	    	try {
	    		httpTransport.debug = true;
				httpTransport.call(SOAP_ACTION, envelope);
				final SoapObject sResult = (SoapObject)envelope.getResponse();
				if (sResult != null) {
					ClsEnvio envio = new ClsEnvio();
					envio.setIdEnvio(Integer.parseInt(sResult.getProperty(0).toString()));
					envio.setIdExpediente(Integer.parseInt(sResult.getProperty(1).toString()));
					dataclass.envio = envio;
				}
			} catch (Exception e) {
				Log.d("Error creando envio.- ", e.getMessage());
			}
		}
	}
	
	public void anexaI(){
		if (dataclass.envio.getIdEnvio() != 0) {
			new AnexarImagenTask().execute(0,0);
		}
	}
	
	private class AnexarImagenTask extends AsyncTask<Integer, Integer, Integer>{
		int indiceDocumento;
		int indiceImagen;
		int idImagen;
		ClsDocumento documento;
		@Override
		protected void onPreExecute(){
			pgDiaglogEnviaImgs = new ProgressDialog(ListDocsGuardados.this);
			pgDiaglogEnviaImgs.setProgressStyle(1);
			pgDiaglogEnviaImgs.setCancelable(false);
			pgDiaglogEnviaImgs.setCanceledOnTouchOutside(false);
			pgDiaglogEnviaImgs.setTitle("Enviando documentos...");
			pgDiaglogEnviaImgs.setMessage("Progreso...");
			pgDiaglogEnviaImgs.setProgress(0);
			pgDiaglogEnviaImgs.setMax(100);
			pgDiaglogEnviaImgs.show();
		}
		@Override
		protected Integer doInBackground(Integer...integers){
			indiceDocumento = integers[0];
			indiceImagen = integers[1];
			if (dataclass.documentos.size() > indiceDocumento) {
				if (dataclass.documentos.get(indiceDocumento).getImagenes().size() > 0) {
					documento = dataclass.documentos.get(indiceDocumento);
				}else {
					indiceDocumento++;
					if (dataclass.documentos.get(indiceDocumento).getImagenes().size() > 0) {
						documento = dataclass.documentos.get(indiceDocumento);
					}else {
						indiceDocumento++;
						if (dataclass.documentos.get(indiceDocumento).getImagenes().size() > 0) {
							documento = dataclass.documentos.get(indiceDocumento);
						}else {
							indiceDocumento++;
							if (dataclass.documentos.get(indiceDocumento).getImagenes().size() > 0) {
								documento = dataclass.documentos.get(indiceDocumento);
							}
						}
					}
				}
				if (documento.getImagenes().size() > indiceImagen) {
					if (!documento.getImagenes().get(indiceImagen).isEnviado()) {
						anexarImagenes aImagenes = new anexarImagenes();
						for (int j = 0; j < dataclass.documentos.size(); j++) {
							aImagenes.indiceDocumento = j;
							indiceDocumento = j;
							for (int i = 0; i < documento.getImagenes().size(); i++) {
								aImagenes.indiceImagen = i;
								indiceImagen = i;
								idImagen = aImagenes.connection(documento.getImagenes().get(indiceImagen).getImgBase64().toString());
								if (idImagen != 0) {
									documento.getImagenes().get(indiceImagen).setEnviado(true);
									for (int k = 0; k < 250; k+=5) {
										try {
											Thread.sleep(100);
										} catch (InterruptedException e) {
											Log.d("Error enviando imagenes", e.getMessage());
										}
										publishProgress(Math.round(100*k/250f), (indiceDocumento + 1),  (indiceImagen + 1));
									}
									indiceImagen++;
								}else {
									Toast.makeText(ListDocsGuardados.this, "Imagen no enviada", Toast.LENGTH_LONG)
									.show();
								}
							}
							indiceImagen = 0;
							indiceDocumento++;
							if (indiceDocumento < dataclass.documentos.size()) {
								documento = dataclass.documentos.get(indiceDocumento);
							}
						}
					}else{
						new AnexarImagenTask().execute(indiceDocumento, indiceImagen + 1);
					}
				}else {
					new AnexarImagenTask().execute(indiceDocumento + 1, 0);
				}
			}else {
				new CerrarEnvioTask().execute();
			}
			return 2;
		}
		@Override
		protected void onProgressUpdate(Integer...valores){
			int p = valores[0];
			pgDiaglogEnviaImgs.setMessage("Documento " + String.valueOf(valores[1]) + ", imagen " + String.valueOf(valores[2]));
			pgDiaglogEnviaImgs.setProgress(p);
		}
		@Override
		protected void onPostExecute(Integer bytes){
			if (pgDiaglogEnviaImgs.isShowing()) {
				pgDiaglogEnviaImgs.dismiss();
				cerrarElEnvio();
			}
		}
	}

	public class anexarImagenes{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/AnexarImagenEnvio";
	    private static final String METHOD_NAME = "AnexarImagenEnvio";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	    int indiceDocumento;
	    int indiceImagen;
	    int regreso;
		public anexarImagenes(){
		}
		public Integer connection(String imgBase64){
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("pIdExpediente", dataclass.expediente.getIdExpediente());
	    	request.addProperty("pIdEnvio", dataclass.envio.getIdEnvio());
	    	request.addProperty("pIdTipoImagen", dataclass.documentos.get(indiceDocumento).getIdDocumento());
	    	request.addProperty("pIdFuente", 2);
	    	request.addProperty("pImageFileBase64", dataclass.documentos.get(indiceDocumento).getImagenes().get(indiceImagen).getImgBase64().toString());
	    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = true;
	    	envelope.setOutputSoapObject(request);
	    	try {
	    		httpTransport.debug = true;
				httpTransport.call(SOAP_ACTION, envelope);
				SoapPrimitive sResult = (SoapPrimitive)envelope.getResponse();
				if (sResult != null) {
					regreso = Integer.parseInt(sResult.toString());
				}else {
					regreso = 0;
				}
			} catch (Exception e) {
				Log.d("Error ws anexar imagen", e.getMessage());
			}
			return regreso;
		}
	}
	
	public void cerrarElEnvio(){
		new CerrarEnvioTask().execute();
	}
	
	public class CerrarEnvioTask extends AsyncTask<Void, Void, Integer>{
		@Override
		protected void onPreExecute(){
		}
		
		@Override
		protected Integer doInBackground(Void... params){
			CerrarEnvio cEnvio = new CerrarEnvio();
			cEnvio.connection();
			return cerrado;
		}
		@Override
		protected void onPostExecute(Integer cerrado){
			if (cerrado == 1) {
				pgDiaglogEnviaImgs.dismiss();
				muestraMensajeEnvioCerrado();	
			}
		}
	}
	
	public class CerrarEnvio{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/CerrarEnvio";
	    private static final String METHOD_NAME = "CerrarEnvio";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
		public CerrarEnvio(){
		}
		public Integer connection(){
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("pIdExpediente", dataclass.expediente.getIdExpediente());
	    	request.addProperty("pIdEnvio", dataclass.envio.getIdEnvio());
	    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = true;
	    	envelope.setOutputSoapObject(request);
	    	try {
	    		httpTransport.debug = true;
				httpTransport.call(SOAP_ACTION, envelope);
				SoapPrimitive sResult = (SoapPrimitive)envelope.getResponse();
				if (sResult != null) {
					cerrado =  Integer.parseInt(sResult.toString());
				}else {
					cerrado = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return cerrado;
		}
	}
	
	public void muestraMensajeEnvioCerrado(){
		helper.deleteByIdRegPend(idRegParaBorrar);
		enviaRegistros();
	}
	
	@SuppressWarnings("unused")
	public void enviaRegistros(){
		if (paraEnvio.size() > 0) {
			for (int i = 0; i < paraEnvio.size(); i++) {
				idExpPendEnviado = paraEnvio.get(i);
				if (idExpPendEnviado == paraEnvio.get(i)) {
					paraEnvio.remove(i);
					preparaDocumentosParaEnvio(idExpPendEnviado);
				}
				break;
			}
		}else {
			setDataToAdapter();
		}
	}
	
	public class TramiteAdapter extends ArrayAdapter<DocumentosItems>{
		private Context mContext;
		private int row;
		private List<DocumentosItems> list;
		
		public TramiteAdapter(Context context, int tVidReg, List<DocumentosItems> list){
			super(context, tVidReg, list);
			this.mContext = context;
			this.row = tVidReg;
			this.list = list;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(row, null);
				holder = new ViewHolder();
				holder.tvNombreCompleto = (TextView)view.findViewById(R.id.tVNombreCompleto);
				holder.tvRFCCompleto = (TextView)view.findViewById(R.id.tVRFCCompleto);
				holder.tvprodSeleccionado = (TextView)view.findViewById(R.id.tVProductoSeleccionado);
				holder.chBItemSeleccionado = (CheckBox)view.findViewById(R.id.chBitem);
				holder.tvNoIds = (TextView)view.findViewById(R.id.tVNoIds);
				view.setTag(holder);
			}else {
				holder = (ViewHolder)view.getTag();
			}
			if ((list == null)|| ((position + 1) > list.size())) {
				return view;
			}
			DocumentosItems obj = list.get(position);
			if (holder.tvNombreCompleto != null && obj != null && obj.getNombre1() != null
					&& obj.getApPaterno() != null) {
				String nomCompleto = obj.getNombre1() + " " + obj.getNombre2() + " " + 
						obj.getApPaterno() + " " + obj.getApMaterno();
				holder.tvNombreCompleto.setText(nomCompleto.toString().replaceAll("null ", ""));
			}
			if (holder.tvRFCCompleto != null && obj.getRFC() != null) {
				holder.tvRFCCompleto.setText(obj.getRFC().toString());
			}
			if (holder.tvprodSeleccionado != null && obj.getProdDescripcion() != null) {
				holder.tvprodSeleccionado.setText(obj.getProdDescripcion().toString());
			}
			holder.chBItemSeleccionado.setId(position);
			for (int j = 0; j < list.size(); j++) {
				
			}
			if (listElementos != null) {
				holder.tvNoIds.setText(String.valueOf(listElementos.size()));
			}
			if (seleccionados) {
				holder.chBItemSeleccionado.setChecked(true);
			}else {
				holder.chBItemSeleccionado.setChecked(false);
			}
			holder.chBItemSeleccionado.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cb = (CheckBox)v;
					int id = cb.getId();
					if (checados[id]) {
						cb.setChecked(false);
						checados[id] = false;
						for (int i = 0; i < paraEnvio.size(); i++) {
							if (paraEnvio.get(i) == list.get(id).getIdReg()) {
								paraEnvio.remove(i);
								break;
							}
						}
					}else {
						cb.setChecked(true);
						checados[id] = true;
						paraEnvio.add(list.get(id).getIdReg());
					}
				}
			});
			holder.chBItemSeleccionado.setChecked(checados[position]);
			holder.id = position;
			return view;
		}
		
		public class ViewHolder{
			int id;
			public TextView tvNombreCompleto;
			public TextView tvRFCCompleto; 
			public TextView tvprodSeleccionado;
			public CheckBox chBItemSeleccionado;
			public TextView tvNoIds;
		}
		
	}
	
	private void setDataToAdapter(){
		limpiaDocumentos();
		list = helper.getData();
		if (list != null) {
			tvSinRegistrosGuardados.setWidth(0);
			tvSinRegistrosGuardados.setHeight(0);
			tvSinRegistrosGuardados.setVisibility(View.INVISIBLE);
			listElementos = helper.getDataWithidReg(list.get(0).getIdReg());
			checados = new boolean[list.size()];
			Arrays.fill(checados, false);
			adapter = new TramiteAdapter(this, R.layout.rows_guardados, list);
			listGuardados.setAdapter(adapter);
		}else {
			adapter = null;
			listGuardados.setAdapter(adapter);
			chBSelTodo.setChecked(false);
		}
	}
	
	public void limpiaDocumentos(){
		for (int i = 0; i < dataclass.documentos.size(); i++) {
			documento = dataclass.documentos.get(i);
			documento = null;
			if (dataclass.documentos.get(i).getImagenes().size() > 0) {
				dataclass.documentos.get(i).getImagenes().clear();
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
		}if (keycode == KeyEvent.KEYCODE_BACK) {
			limpiaDocumentos();
			Intent intRegresaMenu = new Intent(ListDocsGuardados.this, Menu.class);
			startActivity(intRegresaMenu);
			finish();	
		}
		return true;
	}
}
