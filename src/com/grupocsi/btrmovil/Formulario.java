package com.grupocsi.btrmovil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.grupocsi.btrmovil.db.DatabaseHelper;
import com.grupocsi.btrmovil.model.DocumentosItems;

@SuppressLint({ "DefaultLocale", "SimpleDateFormat", "NewApi"})
public class Formulario extends Activity{
	ClsDataClass dataclass = ClsDataClass.getInstancia();
	ClsEjecutivo ejecutivo = Login.getEjecutivo();
	ClsDocumento documento;
	ClsCalcularRFC calculaRFC;
	Spinner sProductos;
	ArrayAdapter<String> nombreProducto;
	ArrayList<String> prodName;
	Button btnIdent;
	Button btnCompDom;
	Button btnCompIng;
	Button btnSolicitud;
	String tipoDocto = "";
	EditText eTpNombre;
	EditText eTsNombre;
	EditText eTapPaterno;
	EditText eTapMaterno;
	EditText eTRfc;
	Button btnEnviar;
	int idProducto = 0;
	String prodDescripcion = "";
	ArrayList<ClsProducto> productos;
	AnexarImagenTask anexarImagen;
	int idRequest = 0;
	RelativeLayout rLayoutDatosGenerales;
	RelativeLayout rLayoutIncidencias;
	ViewGroup LLayoutFormulario;
	RelativeLayout rLayoutAnexar;
	TextView tVIncid;
	int idExp;
	int numeroImagenes = 0;
	ProgressDialog pgDiaglogEnviaImgs;
	/**Guardado local**************************/
	int idRegPend = 0;
	List<DocumentosItems> listPendientes;
	DatabaseHelper helper;
	Button btnGuardaLocal;
	
	/****************************/
	/***Fecha de nacimiento***************/
	EditText eTFechaNacimiento;
	private int year;
	private int month;
	private int day;
	EditText etCelular;
	EditText etCorreo;
	/******************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formulario);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (!verifConexion(this)) {
			Toast.makeText(getBaseContext(), "Comprueba tu conexion...", Toast.LENGTH_LONG).show();
			finish();
		}else {
			LLayoutFormulario = (ViewGroup)findViewById(R.id.LLayoutFormulario);
			rLayoutDatosGenerales = (RelativeLayout)findViewById(R.id.rLayoutDatosGenerales);
			rLayoutIncidencias = (RelativeLayout)findViewById(R.id.rLayoutIncidencias);
			rLayoutAnexar = (RelativeLayout)findViewById(R.id.rLayoutAnexar);
			btnGuardaLocal = (Button)findViewById(R.id.btnGuardarLocal);
			eTFechaNacimiento = (EditText)findViewById(R.id.eTFechaNacimiento);
			etCelular = (EditText)findViewById(R.id.eTTelCcelular);
			etCorreo = (EditText)findViewById(R.id.eTCorreo);
			calculaRFC = new ClsCalcularRFC();
			setFechaActual();
			Bundle b = getIntent().getExtras();
			if (b != null) {
				idRequest = b.getInt("idRequest");
				if (idRequest == 1) {
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					new ConnectionTask().execute();
					rLayoutDatosGenerales.setVisibility(View.VISIBLE);
					rLayoutIncidencias.setVisibility(View.INVISIBLE);
					rLayoutAnexar.setVisibility(View.INVISIBLE);
					View panel = LLayoutFormulario.findViewById(R.id.rLayoutIncidencias);
					View panel2 = LLayoutFormulario.findViewById(R.id.rLayoutAnexar);
					LLayoutFormulario.removeView(panel);
					LLayoutFormulario.removeView(panel2);
					sProductos = (Spinner)findViewById(R.id.sProductos);
					eTpNombre = (EditText)findViewById(R.id.eTpNombre);
					eTsNombre = (EditText)findViewById(R.id.eTsNombre);
					eTapPaterno = (EditText)findViewById(R.id.eTapPaterno);
					eTapMaterno = (EditText)findViewById(R.id.eTapMaterno);
					eTRfc = (EditText)findViewById(R.id.eTRfc);
				}else {
					setTitle("     " + "Folio: " +  String.valueOf(b.getString("Folio")));
					ClsExpediente expediente = new ClsExpediente();
					rLayoutIncidencias.setVisibility(View.VISIBLE);
					rLayoutAnexar.setVisibility(View.VISIBLE);
					rLayoutDatosGenerales.setVisibility(View.INVISIBLE);
					View panel = LLayoutFormulario.findViewById(R.id.rLayoutDatosGenerales);
					LLayoutFormulario.removeView(panel);
					tVIncid = (TextView)findViewById(R.id.tVIncid);
					tVIncid.setText("- " + b.getString("Incon").toString());
					expediente.setIdExpediente(b.getInt("idExp"));
					expediente.setFolio(b.getString("Folio"));
					dataclass.expediente = expediente;
					btnGuardaLocal.setVisibility(View.INVISIBLE);
				}
			}
			btnIdent = (Button)findViewById(R.id.btnIdentificacion);
			btnCompDom = (Button)findViewById(R.id.btnCompDom);
			btnCompIng = (Button)findViewById(R.id.btnCompIng);
			btnSolicitud = (Button)findViewById(R.id.btnSolicitud);
			btnEnviar = (Button)findViewById(R.id.btnEnviar);
			helper = new DatabaseHelper(getApplicationContext());
			btnSolicitud.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent cargaSolicitud = new Intent(Formulario.this, Identificacion.class);
					cargaSolicitud.putExtra("IdDocumento", 1);
					startActivity(cargaSolicitud);
				}
			});		
			btnIdent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent cargaIdentificacion = new Intent(Formulario.this, Identificacion.class);
					cargaIdentificacion.putExtra("IdDocumento", 2);
					startActivity(cargaIdentificacion);
				}
			});
			btnCompDom.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent cargaCompDom = new Intent(Formulario.this, Identificacion.class);
					cargaCompDom.putExtra("IdDocumento", 3);
					startActivity(cargaCompDom);				
				}
			});
			
			btnCompIng.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent cargaCompIng = new Intent(Formulario.this, Identificacion.class);
					cargaCompIng.putExtra("IdDocumento", 4);
					startActivity(cargaCompIng);
				}
			});
			
			btnEnviar.setOnClickListener(new OnClickListener() {
				@SuppressLint("DefaultLocale")
				@Override
				public void onClick(View v) {
					EnviarDocumentos();
				}
			});
		}
		
		btnGuardaLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				guardaLocal();
			}
		});
	}
	
	public void SeleccionaFecha(View v){
		switch (v.getId()) {
		case R.id.eTFechaNacimiento:
			creaDialogo();
			
			break;
		}
	}
	
	//@Override
	protected void creaDialogo(){
		DatePickerDialog dpd = new DatePickerDialog(Formulario.this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int selectedYear, int selectedMonthOfYear,
					int selectedDayOfMonth) {
				if (selectedMonthOfYear < 10) {
					if (selectedDayOfMonth < 10) {
						eTFechaNacimiento.setText(new StringBuilder() 
						.append("0").append(selectedDayOfMonth).append("/0").append(selectedMonthOfYear + 1).append("/").append(selectedYear).append(" "));
							
					}else {
						eTFechaNacimiento.setText(new StringBuilder() 
						.append(selectedDayOfMonth).append("/0").append(selectedMonthOfYear + 1).append("/").append(selectedYear).append(" "));
					}
				}else {
					if (selectedDayOfMonth < 10) {
						eTFechaNacimiento.setText(new StringBuilder() 
						.append("0").append(selectedDayOfMonth).append("/").append(selectedMonthOfYear + 1).append("/").append(selectedYear).append(" "));	
					}else {
						eTFechaNacimiento.setText(new StringBuilder() 
						.append(selectedDayOfMonth).append("/").append(selectedMonthOfYear + 1).append("/").append(selectedYear).append(" "));
					}
					/*eTRfc.setText(calculaRFC.calcularRFC(eTpNombre.getText().toString() + " " + eTsNombre.getText().toString(), 
							eTapPaterno.getText().toString(), eTapMaterno.getText().toString(), eTFechaNacimiento.getText().toString()));*/
				}
				eTRfc.setText(calculaRFC.calcularRFC(eTpNombre.getText().toString() + " " + eTsNombre.getText().toString(), 
						eTapPaterno.getText().toString(), eTapMaterno.getText().toString(), eTFechaNacimiento.getText().toString()));
			}
		}, year, month, day);
		dpd.setTitle("Seleccione \n fecha de nacimiento");
		dpd.show();
		
	}
	
	public void setFechaActual(){
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		if (month < 10) {
			if (day < 10) {
				eTFechaNacimiento.setText(new StringBuilder() 
				.append("0").append(day).append("/0").append(month + 1).append("/").append(year).append(" "));	
			}else {
				eTFechaNacimiento.setText(new StringBuilder() 
				.append(day).append("/0").append(month + 1).append("/").append(year).append(" "));
			}
		}else {
			if (day < 10) {
				eTFechaNacimiento.setText(new StringBuilder() 
				.append("0").append(day).append("/").append(month + 1).append("/").append(year).append(" "));
			}else {
				eTFechaNacimiento.setText(new StringBuilder() 
				.append(day).append("/").append(month + 1).append("/").append(year).append(" "));	
			}
			
		}
	}
	
	public void EnviarDocumentos(){
		numeroImagenes = 0;
		for (int i = 0; i < dataclass.documentos.size(); i++) {
			numeroImagenes = numeroImagenes + dataclass.documentos.get(i).getImagenes().size();
		}
		if (idRequest == 1) {
			if (!validarCamposVacioAndRegex(eTpNombre.getText().toString(), "nombre 1")) {
			}else {
				if (eTsNombre.getText().toString().matches("")) {
					if (!validarCamposVacioAndRegex(eTapPaterno.getText().toString(), "apellido paterno")) {
					}else {
						if (!validarCamposVacioAndRegex(eTapMaterno.getText().toString(), "apellido materno")) {
						}else {
							if (!validaCampoVacio(eTRfc.getText().toString(), "RFC")) {
							}else {
								if (eTRfc.getText().toString().length() != 10) {
									if (eTRfc.getText().toString().length() != 13) {
										new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
										.setMessage("Campo RFC no válido, tiene formato incorrecto")
										.setCancelable(true)
										.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();
											}
										}).show();
									}else {
										if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
										}else {
											if (!validaEdad(eTFechaNacimiento.getText().toString())) {
											}else {
												if (validaCelular(etCelular.getText().toString())) {
												}else {
													if (!validaCorreo(etCorreo.getText().toString())) {
													}else {
														for (int i = 0; i < productos.size(); i++) {
															if (productos.get(i).getDescripcion() == sProductos.getSelectedItem().toString()) {
																idProducto = productos.get(i).getIdProducto();
																prodDescripcion = productos.get(i).getDescripcion();
																break;
															}
														}
														if (idProducto == 0) {
															new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
															.setMessage("Debe seleccionar un producto")
															.setCancelable(true)
															.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	dialog.cancel();
																}
															}).show();
														}else {
															if (numeroImagenes == 0) {

																new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
																.setMessage("La solicitud no contiene documentación")
																.setCancelable(true)
																.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(DialogInterface dialog, int which) {
																		dialog.cancel();
																	}
																}).show();
															}else {
																for (int i = 0; i < dataclass.documentos.size(); i++) {
																	if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																		sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																		i++;
																		break;
																	}else {
																		i++;
																		if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																			sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																			i++;
																			break;
																		}else {
																			i++;
																			i++;
																			if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																				sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																				break;
																			}else {
																				if (dataclass.expediente == null) {
																					new CreaExpedienteTask().execute(String.valueOf(ejecutivo.getIdUsuario()), eTpNombre.getText().toString(), eTsNombre.getText().toString(), 
																							eTapPaterno.getText().toString(), eTapMaterno.getText().toString(), eTRfc.getText().toString(),
																							String.valueOf(idProducto), eTFechaNacimiento.getText().toString(), etCelular.getText().toString(), 
																							etCorreo.getText().toString());	
																				}else if (dataclass.envio == null) {
																					new CreaEnvioTask().execute();
																				}else {
																					new AnexarImagenTask().execute(0,0);
																				}	
																			}
																		}	
																	}
																}
															}
														}	
													}
												}
											}
										}
									}
								}else {
									if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
									}else {
										if (!validaEdad(eTFechaNacimiento.getText().toString())) {
										}else {
											if (validaCelular(etCelular.getText().toString())) {
											}else {
												if (!validaCorreo(etCorreo.getText().toString())) {
												}else {
													for (int i = 0; i < productos.size(); i++) {
														if (productos.get(i).getDescripcion() == sProductos.getSelectedItem().toString()) {
															idProducto = productos.get(i).getIdProducto();
															prodDescripcion = productos.get(i).getDescripcion();
															break;
														}
													}
													if (idProducto == 0) {
														new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
														.setMessage("Debe seleccionar un producto")
														.setCancelable(true)
														.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
															@Override
															public void onClick(DialogInterface dialog, int which) {
																dialog.cancel();
															}
														}).show();
													}else {
														if (numeroImagenes == 0) {

															new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
															.setMessage("La solicitud no contiene documentación")
															.setCancelable(true)
															.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	dialog.cancel();
																}
															}).show();
														}else {
															for (int i = 0; i < dataclass.documentos.size(); i++) {
																if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																	sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																	i++;
																	break;
																}else {
																	i++;
																	if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																		sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																		i++;
																		break;
																	}else {
																		i++;
																		i++;
																		if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																			sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																			break;
																		}else {
																			if (dataclass.expediente == null) {
																				new CreaExpedienteTask().execute(String.valueOf(ejecutivo.getIdUsuario()), eTpNombre.getText().toString(), eTsNombre.getText().toString(), 
																						eTapPaterno.getText().toString(), eTapMaterno.getText().toString(), eTRfc.getText().toString(),
																						String.valueOf(idProducto), eTFechaNacimiento.getText().toString(), etCelular.getText().toString(), 
																						etCorreo.getText().toString());	
																			}else if (dataclass.envio == null) {
																				new CreaEnvioTask().execute();
																			}else {
																				new AnexarImagenTask().execute(0,0);
																			}	
																		}
																	}	
																}
															}
														}
													}	
												}
											}
										}
									}
								}
							}							
						}	
					}
				}else {
					if (!validarCamposVacioAndRegex(eTsNombre.getText().toString(), "nombre 2")) {
					}else {
						if (!validarCamposVacioAndRegex(eTapPaterno.getText().toString(), "apellido paterno")) {
						}else {
							if (!validarCamposVacioAndRegex(eTapMaterno.getText().toString(), "apellido materno")) {
							}else {
								if (!validaCampoVacio(eTRfc.getText().toString(), "RFC")) {
								}else {
									if (eTRfc.getText().toString().length() != 10) {
										if (eTRfc.getText().toString().length() != 13) {
											new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
											.setMessage("Campo RFC no válido, tiene formato incorrecto")
											.setCancelable(true)
											.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.cancel();
												}
											}).show();
										}else {
											if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
											}else {
												if (!validaEdad(eTFechaNacimiento.getText().toString())) {
												}else {
													if (validaCelular(etCelular.getText().toString())) {
													}else {
														if (!validaCorreo(etCorreo.getText().toString())) {
														}else {
															for (int i = 0; i < productos.size(); i++) {
																if (productos.get(i).getDescripcion() == sProductos.getSelectedItem().toString()) {
																	idProducto = productos.get(i).getIdProducto();
																	prodDescripcion = productos.get(i).getDescripcion();
																	break;
																}
															}
															if (idProducto == 0) {
																new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
																.setMessage("Debe seleccionar un producto")
																.setCancelable(true)
																.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(DialogInterface dialog, int which) {
																		dialog.cancel();
																	}
																}).show();
															}else {
																if (numeroImagenes == 0) {

																	new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
																	.setMessage("La solicitud no contiene documentación")
																	.setCancelable(true)
																	.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																		@Override
																		public void onClick(DialogInterface dialog, int which) {
																			dialog.cancel();
																		}
																	}).show();
																}else {
																	for (int i = 0; i < dataclass.documentos.size(); i++) {
																		if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																			sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																			i++;
																			break;
																		}else {
																			i++;
																			if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																				sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																				i++;
																				break;
																			}else {
																				i++;
																				i++;
																				if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																					sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																					break;
																				}else {
																					if (dataclass.expediente == null) {
																						new CreaExpedienteTask().execute(String.valueOf(ejecutivo.getIdUsuario()), eTpNombre.getText().toString(), eTsNombre.getText().toString(), 
																								eTapPaterno.getText().toString(), eTapMaterno.getText().toString(), eTRfc.getText().toString(),
																								String.valueOf(idProducto), eTFechaNacimiento.getText().toString(), etCelular.getText().toString(), 
																								etCorreo.getText().toString());	
																					}else if (dataclass.envio == null) {
																						new CreaEnvioTask().execute();
																					}else {
																						new AnexarImagenTask().execute(0,0);
																					}	
																				}
																			}	
																		}
																	}
																}
															}	
														}
													}
												}
											}
										}
									}else {
										if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
										}else {
											if (!validaEdad(eTFechaNacimiento.getText().toString())) {
											}else {
												if (validaCelular(etCelular.getText().toString())) {
												}else {
													if (!validaCorreo(etCorreo.getText().toString())) {
													}else {
														for (int i = 0; i < productos.size(); i++) {
															if (productos.get(i).getDescripcion() == sProductos.getSelectedItem().toString()) {
																idProducto = productos.get(i).getIdProducto();
																prodDescripcion = productos.get(i).getDescripcion();
																break;
															}
														}
														if (idProducto == 0) {
															new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
															.setMessage("Debe seleccionar un producto")
															.setCancelable(true)
															.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	dialog.cancel();
																}
															}).show();
														}else {
															if (numeroImagenes == 0) {
																new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
																.setMessage("La solicitud no contiene documentación")
																.setCancelable(true)
																.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(DialogInterface dialog, int which) {
																		dialog.cancel();
																	}
																}).show();
															}else {
																for (int i = 0; i < dataclass.documentos.size(); i++) {
																	if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																		sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																		i++;
																		break;
																	}else {
																		i++;
																		if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																			sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																			i++;
																			break;
																		}else {
																			i++;
																			i++;
																			if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																				sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																				break;
																			}else {
																				if (dataclass.expediente == null) {
																					new CreaExpedienteTask().execute(String.valueOf(ejecutivo.getIdUsuario()), eTpNombre.getText().toString(), eTsNombre.getText().toString(), 
																							eTapPaterno.getText().toString(), eTapMaterno.getText().toString(), eTRfc.getText().toString(),
																							String.valueOf(idProducto), eTFechaNacimiento.getText().toString(), etCelular.getText().toString(), 
																							etCorreo.getText().toString());	
																				}else if (dataclass.envio == null) {
																					new CreaEnvioTask().execute();
																				}else {
																					new AnexarImagenTask().execute(0,0);
																				}	
																			}	
																		}	
																	}
																}
															}
														}	
													}
												}
											}
										}
									}
								}							
							}	
						}
					}
				}
			}	
		}else {//idRequest != 1
			if (numeroImagenes == 0) {
				new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
				.setMessage("La solicitud no contiene documentación")
				.setCancelable(true)
				.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
			}else {
				if (dataclass.expediente == null) {
					new CreaExpedienteTask().execute(String.valueOf(ejecutivo.getIdUsuario()), eTpNombre.getText().toString(), eTsNombre.getText().toString(), 
							eTapPaterno.getText().toString(), eTapMaterno.getText().toString(), eTRfc.getText().toString(),
							String.valueOf(idProducto), eTFechaNacimiento.getText().toString(), etCelular.getText().toString(), 
							etCorreo.getText().toString());
				}else if (dataclass.envio == null) {
					new CreaEnvioTask().execute();
				}else {
					new AnexarImagenTask().execute(0,0);
				}
			}
		}
	}
	
	public boolean validaCelular(String numeroCelular){
		boolean error = false;
		if (!numeroCelular.toString().matches("")) {
			if (numeroCelular.toString().toUpperCase().contains("A")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("B")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("	C")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("D")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("E")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("F")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("G")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("H")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("I")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("J")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("K")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("L")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("M")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("N")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("Ñ")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("P")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("Q")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("R")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("S")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("T")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("U")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("V")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("W")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("X")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("Y")) {
				error = true;
			}else if (numeroCelular.toString().toUpperCase().contains("Z")) {
				error = true;
			}
		}else {
			error = false;
		}
		if (error) {
			new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
			.setMessage("Campo Celular no válido, tiene formato incorrecto")
			.setCancelable(true)
			.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
			  dialog.cancel();
			  }
			}).show();
		}else {
			error = false;
		}
		return error;
	}
	
	public boolean validaCorreo(String correoElectronico){
		boolean valido = false;
		Pattern pattern;
		Matcher matcher;
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(correoElectronico);
		if (matcher.matches()) {
			valido = true;
		}
		if (!valido) {
			new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
			.setMessage("Campo Correo no válido, tiene formato incorrecto")
			.setCancelable(true)
			.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
			  dialog.cancel();
			  }
			}).show();
		}
		return valido;
	}
	
	public boolean validaEdad(String fechaNacimiento){
		boolean edadValida = false;
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Calendar c = Calendar.getInstance();
		fechaNacimiento = fechaNacimiento.toString().replaceAll("/", "-");
		try {
			Date date = format.parse(fechaNacimiento);
					Date date2 = format.parse(String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "-"
					+ String.valueOf(c.get(Calendar.MONTH)) + "-" + String.valueOf( c.get(Calendar.YEAR)));
			Calendar fNac = getCalendar(date);
			Calendar fActual = getCalendar(date2);
			int diff = fActual.get(Calendar.YEAR) - fNac.get(Calendar.YEAR);
			if (fNac.get(Calendar.MONTH) > fActual.get(Calendar.MONTH) || 
					fNac.get(Calendar.MONTH) == fActual.get(Calendar.MONTH) && fNac.get(Calendar.DATE) > fActual.get(Calendar.DATE)) {
				diff--;
			}
			if (diff > 90 || diff < 18) {
				new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
				.setMessage("Su edad actual es mayor a 90 o menor a 18 años")
				.setCancelable(true)
				.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialog, int which) {
				  dialog.cancel();
				  }
				}).show();
				edadValida = false;
			}else {
				edadValida = true;
			}
		} catch (Exception e) {
			Log.d("Error validando edad", e.getMessage());
		}
		return edadValida;
	}
	
	public static Calendar getCalendar(Date date){
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		return cal;
	}
	
	public void guardaLocal(){
		numeroImagenes = 0;
		for (int i = 0; i < dataclass.documentos.size(); i++) {
			numeroImagenes = numeroImagenes + dataclass.documentos.get(i).getImagenes().size();
		}
		if (!validarCamposVacioAndRegex(eTpNombre.getText().toString(), "nombre 1")) {
		}else {
			if (eTsNombre.getText().toString().matches("")) {
				if (!validarCamposVacioAndRegex(eTapPaterno.getText().toString(), "apellido paterno")) {
				}else {
					if (!validarCamposVacioAndRegex(eTapMaterno.getText().toString(), "apellido materno")) {
					}else {
						if (!validaCampoVacio(eTRfc.getText().toString(), "RFC")) {
						}else {
							if (eTRfc.getText().toString().length() != 10) {
								if (eTRfc.getText().toString().length() != 13) {
									new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
									.setMessage("Campo RFC no válido, tiene formato incorrecto")
									.setCancelable(true)
									.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which) {
									  dialog.cancel();
									  }
									}).show();
								}else {
									if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
									}else {
										if (!validaEdad(eTFechaNacimiento.getText().toString())) {
										}else {
											if (validaCelular(etCelular.getText().toString())) {
											}else {
												if (!validaCorreo(etCorreo.getText().toString())) {
												}else {
													for (int i = 0; i < productos.size(); i++) {
														if (productos.get(i).getDescripcion().toString().matches(sProductos.getSelectedItem().toString())) {
															idProducto = productos.get(i).getIdProducto();
															prodDescripcion = productos.get(i).getDescripcion();
															break;
														}
													}
													if (idProducto == 0) {
														new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
														  .setMessage("Debe seleccionar un producto")
														  .setCancelable(true)
														  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
														  @Override
														  public void onClick(DialogInterface dialog, int which) {
														  dialog.cancel();
														  }
														  }).show();
													}else {
														if (numeroImagenes == 0) {
															new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
															  .setMessage("La solicitud no contiene documentación")
															  .setCancelable(true)
															  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
															  @Override
															  public void onClick(DialogInterface dialog, int which) {
															  dialog.cancel();
															  }
															  }).show();
														}else {
															for (int i = 0; i < dataclass.documentos.size(); i++) {
																if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																	sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																	i++;
																	break;
																}else {
																	i++;
																	if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																		sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																		i++;
																		break;
																	}else {
																		i++;
																		i++;
																		if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																			sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																			break;
																		}else {
																			createNewTramiteItem();	
																		}
																	}
																}
															}
														}
													}	
												}
											}
										}
									}
								}
							}else {
								if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
								}else {
									if (!validaEdad(eTFechaNacimiento.getText().toString())) {
									}else {
										if (validaCelular(etCelular.getText().toString())) {
										}else {
											if (!validaCorreo(etCorreo.getText().toString())) {
											}else {
												for (int i = 0; i < productos.size(); i++) {
													if (productos.get(i).getDescripcion().toString().matches(sProductos.getSelectedItem().toString())) {
														idProducto = productos.get(i).getIdProducto();
														prodDescripcion = productos.get(i).getDescripcion();
														break;
													}
												}
												if (idProducto == 0) {
													new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
													  .setMessage("Debe seleccionar un producto")
													  .setCancelable(true)
													  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
													  @Override
													  public void onClick(DialogInterface dialog, int which) {
													  dialog.cancel();
													  }
													  }).show();
												}else {
													if (numeroImagenes == 0) {
														new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
														  .setMessage("La solicitud no contiene documentación")
														  .setCancelable(true)
														  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
														  @Override
														  public void onClick(DialogInterface dialog, int which) {
														  dialog.cancel();
														  }
														  }).show();
													}else {
														for (int i = 0; i < dataclass.documentos.size(); i++) {
															if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																i++;
																break;
															}else {
																i++;
																if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																	sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																	i++;
																	break;
																}else {
																	i++;
																	i++;
																	if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																		sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																		break;
																	}else {
																		createNewTramiteItem();	
																	}
																}
															}
														}
													}
												}	
											}
										}
									}
								}
							}
						}
					}
				}
			}else {//sNombre != ""
				if (!validarCamposVacioAndRegex(eTsNombre.getText().toString(), "nombre 2")) {
				}else {
					if (!validarCamposVacioAndRegex(eTapPaterno.getText().toString(), "apellido paterno")) {
					}else {
						if (!validarCamposVacioAndRegex(eTapMaterno.getText().toString(), "apellido materno")) {
						}else {
							if (!validaCampoVacio(eTRfc.getText().toString(), "RFC")) {
							}else {
								if (eTRfc.getText().toString().length() != 10) {
									if (eTRfc.getText().toString().length() != 13) {
										new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
										.setMessage("Campo RFC no válido, tiene formato incorrecto")
										.setCancelable(true)
										.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
										  @Override
										  public void onClick(DialogInterface dialog, int which) {
										  dialog.cancel();
										  }
										}).show();
									}else {
										if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
										}else {
											if (!validaEdad(eTFechaNacimiento.getText().toString())) {
											}else {
												if (validaCelular(etCelular.getText().toString())) {
												}else {
													if (!validaCorreo(etCorreo.getText().toString())) {
													}else {
														for (int i = 0; i < productos.size(); i++) {
															if (productos.get(i).getDescripcion().toString().matches(sProductos.getSelectedItem().toString())) {
																idProducto = productos.get(i).getIdProducto();
																prodDescripcion = productos.get(i).getDescripcion();
																break;
															}
														}
														if (idProducto == 0) {
															new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
															  .setMessage("Debe seleccionar un producto")
															  .setCancelable(true)
															  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
															  @Override
															  public void onClick(DialogInterface dialog, int which) {
															  dialog.cancel();
															  }
															  }).show();
														}else {
															if (numeroImagenes == 0) {
																new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
																  .setMessage("La solicitud no contiene documentación")
																  .setCancelable(true)
																  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
																  @Override
																  public void onClick(DialogInterface dialog, int which) {
																  dialog.cancel();
																  }
																  }).show();
															}else {
																for (int i = 0; i < dataclass.documentos.size(); i++) {
																	if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																		sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																		i++;
																		break;
																	}else {
																		i++;
																		if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																			sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																			i++;
																			break;
																		}else {
																			i++;
																			i++;
																			if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																				sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																				break;
																			}else {
																				if (dataclass.expediente == null) {
																					createNewTramiteItem();
																				}	
																			}
																		}
																	}
																}
															}
														}	
													} 
												}
											}
										}
									}
								}else {
									if (!validaRfc10(eTRfc.getText().toString(), "RFC")) {
									}else {
										if (!validaEdad(eTFechaNacimiento.getText().toString())) {
										}else {
											if (validaCelular(etCelular.getText().toString())) {
											}else {
												if (!validaCorreo(etCorreo.getText().toString())) {
												}else {
													for (int i = 0; i < productos.size(); i++) {
														if (productos.get(i).getDescripcion().toString().matches(sProductos.getSelectedItem().toString())) {
															idProducto = productos.get(i).getIdProducto();
															prodDescripcion = productos.get(i).getDescripcion();
															break;
														}
													}
													if (idProducto == 0) {
														new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
														  .setMessage("Debe seleccionar un producto")
														  .setCancelable(true)
														  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
														  @Override
														  public void onClick(DialogInterface dialog, int which) {
														  dialog.cancel();
														  }
														  }).show();
													}else {
														if (numeroImagenes == 0) {
															new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
															  .setMessage("La solicitud no contiene documentación")
															  .setCancelable(true)
															  .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
															  @Override
															  public void onClick(DialogInterface dialog, int which) {
															  dialog.cancel();
															  }
															  }).show();
														}else {
															for (int i = 0; i < dataclass.documentos.size(); i++) {
																if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																	sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																	i++;
																	break;
																}else {
																	i++;
																	if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																		sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																		i++;
																		break;
																	}else {
																		i++;
																		i++;
																		if (dataclass.documentos.get(i).getImagenes().size() == 0) {
																			sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
																			break;
																		}else {
																			createNewTramiteItem();	
																		}
																	}
																}
															}
														}
													}	
												}
											}
										}
									}	
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void createNewTramiteItem(){
		int indiceDoc = 0;
		int indiceImg = 0;
		int idReg = helper.getMaxIdReg();
		if (idReg == 0) {
			idReg = 1;
			if (dataclass.documentos.size() > indiceDoc) {
				if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
					documento = dataclass.documentos.get(indiceDoc);
				}else {
					indiceDoc++;
					if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
						documento = dataclass.documentos.get(indiceDoc);
					}else {
						indiceDoc++;
						if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
							documento = dataclass.documentos.get(indiceDoc);
						}else {
							indiceDoc++;
							if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
								documento = dataclass.documentos.get(indiceDoc);
							}
						}
					}
				}
				if (documento.getImagenes().size() > indiceImg) {
					Date fecha = new Date();
					SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					if (!documento.getImagenes().get(indiceImg).isEnviado()) {
						for (int i = 0; i < dataclass.documentos.size(); i++) {
							indiceDoc = i;
							for (int j = 0; j < documento.getImagenes().size(); j++) {
								indiceImg = j;
								DocumentosItems item = new DocumentosItems();
								item.setIdDocumento(documento.getIdDocumento());
								item.setDocNombre(documento.getNombreDocumento());
								item.setNombre1(eTpNombre.getText().toString());
								if (!eTsNombre.getText().toString().matches("")) {
									item.setNombre2(eTsNombre.getText().toString());
								}else {
									item.setNombre2("");
								}
								item.setApPaterno(eTapPaterno.getText().toString());
								item.setApMaterno(eTapMaterno.getText().toString());
								item.setFechaNacimiento(eTFechaNacimiento.getText().toString());
								item.setRFC(eTRfc.getText().toString());
								item.setTelCelular(etCelular.getText().toString());
								item.setCorreoElectronico(etCorreo.getText().toString());
								item.setIdProducto(idProducto);
								item.setProdDescripcion(prodDescripcion);
								item.setImagen(convertBase64(documento.getImagenes().get(j).getImagen()));
								item.setIdReg(idReg);
								item.setFecha(String.valueOf(formato.format(fecha)));
								helper.addRegistro(item);
								documento.getImagenes().get(indiceImg).setEnviado(true);
								indiceImg++;
							}
							indiceImg = 0;
							indiceDoc++;
							if (indiceDoc < dataclass.documentos.size()) {
								documento = dataclass.documentos.get(indiceDoc);
							}
						}
					}
				}
			}
		}else{
			idReg++;
			if (dataclass.documentos.size() > indiceDoc) {
				if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
					documento = dataclass.documentos.get(indiceDoc);
				}else {
					indiceDoc++;
					if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
						documento = dataclass.documentos.get(indiceDoc);
					}else {
						indiceDoc++;
						if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
							documento = dataclass.documentos.get(indiceDoc);
						}else {
							indiceDoc++;
							if (dataclass.documentos.get(indiceDoc).getImagenes().size() > 0) {
								documento = dataclass.documentos.get(indiceDoc);
							}
						}
					}
				}
				if (documento.getImagenes().size() > indiceImg) {
					Date fecha = new Date();
					SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					if (!documento.getImagenes().get(indiceImg).isEnviado()) {
						for (int i = 0; i < dataclass.documentos.size(); i++) {
							indiceDoc = i;
							for (int j = 0; j < documento.getImagenes().size(); j++) {
								indiceImg = j;
								DocumentosItems item = new DocumentosItems();
								item.setIdDocumento(documento.getIdDocumento());
								item.setDocNombre(documento.getNombreDocumento());
								item.setNombre1(eTpNombre.getText().toString());
								if (!eTsNombre.getText().toString().matches("")) {
									item.setNombre2(eTsNombre.getText().toString());
								}
								item.setApPaterno(eTapPaterno.getText().toString());
								item.setApMaterno(eTapMaterno.getText().toString());
								item.setFechaNacimiento(eTFechaNacimiento.getText().toString());
								item.setRFC(eTRfc.getText().toString());
								item.setTelCelular(etCelular.getText().toString());
								item.setCorreoElectronico(etCorreo.getText().toString());
								item.setIdProducto(idProducto);
								item.setProdDescripcion(prodDescripcion);
								item.setImagen(convertBase64(documento.getImagenes().get(j).getImagen()));
								item.setIdReg(idReg);
								item.setFecha(String.valueOf(formato.format(fecha)));
								helper.addRegistro(item);
								documento.getImagenes().get(indiceImg).setEnviado(true);
								indiceImg++;
							}
							indiceImg = 0;
							indiceDoc++;
							if (indiceDoc < dataclass.documentos.size()) {
								documento = dataclass.documentos.get(indiceDoc);
							}
						}
					}
				}
			}
		}
		documentosGuardados();
		
	}
	
	public void documentosGuardados(){
		new AlertDialog.Builder(Formulario.this).setTitle("Documentos guardados")
		.setMessage("Expediente guardado")
		.setCancelable(true)
		.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				regresaMenu();
			}
		}).show();	
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
	
	public Boolean validarCamposVacioAndRegex(String campo, String nombre){
		boolean valido = false;
		String caractInvalidos = "[\"@'#_-$%&/()?!,.1234567890]";
		if ((campo.toString().equals(null)) || (campo.toString().equals(""))) {
			valido = false;
			new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
			.setMessage("Falta ingresar " + nombre)
			.setCancelable(true)
			.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).show();
		}else {
			if (campo.length() < 2) {
				valido = false;
				new AlertDialog.Builder(Formulario.this).setTitle("Datos incorrectos")
			    .setMessage("Campo " + nombre + " no tiene la longitud mínima de 2 o contiene caracteres no válidos: "+ caractInvalidos)
			    .setCancelable(true)
			    .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
					    dialog.cancel();
				    }
			    }).show();
			}else {
				String regexvalido = "^[a-zA-Z][a-zA-Z ñÑÁáÉéÍíÓóÚúü]+$";
				if(!campo.toString().matches(regexvalido)) {
					new AlertDialog.Builder(Formulario.this).setTitle("Datos incorrectos")
					.setMessage("Campo " + nombre + " no tiene la longitud mínima de 2 o contiene caracteres no válidos: "+ caractInvalidos)
					.setCancelable(true)
					.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
					valido = false;
				}else {
					valido = true;
				}
			}
		}
		return valido;
	}
	
	public Boolean validaCampoVacio(String campo, String nombre){
		boolean valido = false;
		if ((campo.toString().equals(null)) || (campo.toString().equals(""))) {
			new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
			.setMessage("Falta ingresar " + nombre)
			.setCancelable(true)
			.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).show();
			valido = false;
		}else{
			valido = true;
		}
		return valido;
	}
	
	public Boolean validaRegexCampo(String campo, String nombre){
		boolean valida = false;
		String caractInvalidos = "[\"@'#_-$%&/()?!,.1234567890]";
		if (campo.length() < 2) {
			new AlertDialog.Builder(Formulario.this).setTitle("Datos incorrectos")
			.setMessage("Campo " + nombre + " no tiene la longitud mínima de 2 o contiene caracteres no válidos: "+ caractInvalidos)
			.setCancelable(true)
			.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).show();
			valida = false;
		}else{	
			String regexvalido = "^[a-zA-Z][a-zA-Z ñÑÁáÉéÍíÓóÚúü]+$";
			if(!campo.toString().matches(regexvalido)) {
				new AlertDialog.Builder(Formulario.this).setTitle("Datos incorrectos")
				.setMessage("Campo " + nombre + " no tiene la longitud mínima de 2 o contiene caracteres no válidos: "+ caractInvalidos)
				.setCancelable(true)
				.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
				valida = false;
			}else{
				valida = true;
			}
		}
		return valida;
	}
	
	public Boolean validaRfc10(String campo, String nombre){
		boolean rfcvalido = false;
		if (campo.length() == 10) {
			String regexRfc = "[a-zA-Z]{4}[0-9]{6}";
			if (!campo.toString().matches(regexRfc)) {
				new AlertDialog.Builder(Formulario.this).setTitle("Datos incorrectos")
				.setMessage("Campo " + nombre + " no válido, tiene formato incorrecto ó contiene caracteres no validos")
				.setCancelable(true)
				.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
				rfcvalido = false;
			}else {
				rfcvalido = true;
			}
		}else if (campo.length() == 13) {
			String regexRfcShc = "[a-zA-Z]{4}[0-9]{6}[a-zA-Z0-9]{3}";
			if (!campo.toString().matches(regexRfcShc)) {
				new AlertDialog.Builder(Formulario.this).setTitle("Datos incorrectos")
				.setMessage("Campo " + nombre + " no válido, tiene formato incorrecto ó contiene caracteres no validos")
				.setCancelable(true)
				.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
				rfcvalido = false;
			}else {
				rfcvalido = true;
			}
		}
		return rfcvalido;
	}
	
	public void sinImagenes(String docName){
		new AlertDialog.Builder(Formulario.this).setTitle("Datos incompletos")
		.setMessage("El documento " + docName + " no contiene imagenes.")
		.setCancelable(true)
		.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}
	
	private class AnexarImagenTask extends AsyncTask<Integer, Integer, Integer>{
		int indiceDocumento;
		int indiceImagen;
		int idImagen;
		@Override
		protected void onPreExecute(){
			pgDiaglogEnviaImgs = new ProgressDialog(Formulario.this);
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
						for (int j = indiceDocumento; j < dataclass.documentos.size(); j++) {
							aImagenes.indiceDocumento = j;
							indiceDocumento = j;
							for (int i = indiceImagen; i < documento.getImagenes().size(); i++) {
								aImagenes.indiceImagen = i;
								indiceImagen = i;
								idImagen = aImagenes.connection(documento.getImagenes().get(indiceImagen).getImagen());
								if (idImagen != 0) {
									documento.getImagenes().get(indiceImagen).setEnviado(true);
									for (int k = 0; k < 250; k +=5) {
										try {
											Thread.sleep(200);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										publishProgress(Math.round(100 * k/250f), (indiceDocumento + 1), (indiceImagen + 1));
									}
									indiceImagen++;
								}else {
									Toast.makeText(Formulario.this, "Envio no terminado", Toast.LENGTH_LONG).show();
								}
							}
							indiceImagen = 0;
							indiceDocumento++;
							if (indiceDocumento < dataclass.documentos.size()) {
								documento = dataclass.documentos.get(indiceDocumento);
							}
						}
					}else {
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
	
	public void cerrarElEnvio(){
		new CerrarEnvioTask().execute();
	}
	
	public class CerrarEnvioTask extends AsyncTask<Void, Void, Void>{
		int cerrado;
		@Override
		protected void onPreExecute(){
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			CerrarEnvio cEnvio = new CerrarEnvio();
			cerrado = cEnvio.connection();
			return null;
		}
		@Override
		protected void onPostExecute(Void v){
			if (cerrado == 1) {
				if (idRequest == 1) {
					AlertDialog.Builder terminado = new AlertDialog.Builder(Formulario.this);
					terminado.setTitle("¡ Envio finalizado !");
					TextView tvMsgTerminado = new TextView(Formulario.this);
					tvMsgTerminado.setTextSize(14);
					tvMsgTerminado.setText("Expediente enviado y procesado \n con folio : "
							+ dataclass.expediente.getFolio());
					tvMsgTerminado.setHeight(60);
					tvMsgTerminado.setScrollbarFadingEnabled(true);
					tvMsgTerminado.setGravity(Gravity.CENTER_HORIZONTAL);
					terminado.setView(tvMsgTerminado);
					terminado.setCancelable(false);
					terminado.setPositiveButton("       Ok      ", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							regresaMenu();
						}
					});
					
					terminado.show();	
				}else {
					regresaMenu();
				}
				
			}else {
				Toast.makeText(Formulario.this, "Envio no finalizado", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void regresaMenu(){
		for (int i = 0; i < dataclass.documentos.size(); i++) {
			for (int j = 0; j < dataclass.documentos.get(i).getImagenes().size(); j++) {
				while (dataclass.documentos.get(i).getImagenes().size() != 0) {
					dataclass.documentos.get(i).getImagenes().remove(j);	
				}
			}
		}
		dataclass.envio = null;
		dataclass.expediente = null;
		Intent intMenu = new Intent(Formulario.this, Menu.class);
		startActivity(intMenu);
		finish();
	}
	
	public class CerrarEnvio{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/CerrarEnvio";
	    private static final String METHOD_NAME = "CerrarEnvio";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	    int cerrado;
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
	
	public String convertBase64(Bitmap imagen){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		imagen.compress(CompressFormat.JPEG, 90, stream);
		byte[] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeBytes(byte_arr);
		return image_str;
	}
	
	public class anexarImagenes{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/AnexarImagenEnvio";
	    private static final String METHOD_NAME = "AnexarImagenEnvio";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	    int indiceDocumento;
	    int indiceImagen;
	    int regreso;
	    String convertImagen;
	
	    public anexarImagenes(){
	    }
	    
	    public Integer connection(Bitmap imagen){
	    	convertImagen = convertBase64(dataclass.documentos.get(indiceDocumento).getImagenes().get(indiceImagen).getImagen());
	    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("pIdExpediente", dataclass.expediente.getIdExpediente());
	    	request.addProperty("pIdEnvio", dataclass.envio.getIdEnvio());
	    	request.addProperty("pIdTipoImagen", dataclass.documentos.get(indiceDocumento).getIdDocumento());
	    	request.addProperty("pIdFuente", 2);
	    	request.addProperty("pImageFileBase64", convertImagen);
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
				e.printStackTrace();
			}
	    	return regreso;
	    }
	}
	
	private class CreaEnvioTask extends AsyncTask<Void, Void, Void>{
		private final ProgressDialog dialogo = new ProgressDialog(Formulario.this);
		@Override
		protected void onPreExecute(){
			dialogo.setMessage("Creando paquete de envio");
			dialogo.setCancelable(false);
			dialogo.setCanceledOnTouchOutside(false);
			dialogo.show();
		}
		@Override
		protected Void doInBackground(Void... params){
			creaEnvio cEnvio = new creaEnvio();
			cEnvio.connection();
			return null;
		}
		
		protected void onPostExecute(Void v){
			dialogo.dismiss();
			anexaI();
		}
	}
	
	public void anexaI(){
		if (idRequest == 1) {
			for (int i = 0; i < dataclass.documentos.size(); i++) {
				if (dataclass.documentos.get(i).getImagenes().size() == 0) {
					sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
					i++;
					break;
				}else {
					i++;
					if(dataclass.documentos.get(i).getImagenes().size() == 0) {
						sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
						i++;
						break;
					}else {
						i++;
						i++;
						if(dataclass.documentos.get(i).getImagenes().size() == 0) {
							sinImagenes(dataclass.documentos.get(i).getNombreDocumento().toString());
							break;
						}else {
							new AnexarImagenTask().execute(0,0);		
						}
					}
				}
			}	
		}else {
			new AnexarImagenTask().execute(0,0);
		}
	}
	
	
	public class creaEnvio{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/CrearEnvio";
	    private static final String METHOD_NAME = "CrearEnvio";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
		
	    public creaEnvio(){
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
				e.printStackTrace();
			}
	    }
	}
	
	private class CreaExpedienteTask extends AsyncTask<String, Void, Void>{
		@Override
		protected void onPreExecute(){
		}
		
		protected Void doInBackground(String...strings){
			creaExpediente cExp = new creaExpediente();
			cExp.idUsuario = strings[0];
			cExp.pNombre = strings[1];
			cExp.sNombre = strings[2];
			cExp.apPaterno = strings[3];
			cExp.apMaterno = strings[4];
			cExp.rfc = strings[5];
			cExp.producto = strings[6];
			cExp.fechaNacimiento = strings[7];
			cExp.numCelular = strings[8];
			cExp.correoElectronico = strings[9];
			cExp.connection();
			return null;
		}
	    
	    protected void onPostExecute(Void v){
	    	new CreaEnvioTask().execute();
	    }
	}
	
	public class creaExpediente{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/CrearExpediente";
	    private static final String METHOD_NAME = "CrearExpediente";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	    public String idUsuario;
	    public String pNombre;
	    public String sNombre;
	    public String apPaterno;
	    public String apMaterno;
	    public String rfc;
	    public String producto;
	    public String fechaNacimiento;
	    public String numCelular;
	    public String correoElectronico;
	    
	    public creaExpediente(){
	    }
	    
	    public void connection(){
	    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	request.addProperty("pIdUsuario", ejecutivo.getIdUsuario());
	    	request.addProperty("pPrimerNombre", pNombre);
	    	request.addProperty("pSegundoNombre", sNombre);
	    	request.addProperty("pApellidoPaterno", apPaterno);
	    	request.addProperty("pApellidoMaterno", apMaterno);
	    	request.addProperty("pRFC", rfc);
	    	request.addProperty("pIdProducto", Integer.parseInt(producto));
	    	request.addProperty("pDiaFechaNacimiento", fechaNacimiento.substring(0,2).toString());
	    	request.addProperty("pMesFechaNacimiento", fechaNacimiento.substring(3,5).toString());
	    	request.addProperty("pAñoFechaNacimiento", fechaNacimiento.substring(6,10).toString());
	    	//request.addProperty("pFechaNacimiento", fechaNacimiento );
	    	request.addProperty("pCelular", numCelular);
	    	request.addProperty("pCorreoElectronico", correoElectronico);
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
				e.printStackTrace();
			}
	    }
	    
	}
	
	@Override
	public void onResume(){
		super.onResume();
		for (int i = 0; i < dataclass.documentos.size(); i++) {
			if (dataclass.documentos.get(i).getImagenes().size() > 0) {
				if (i == 0) {
					btnSolicitud.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20_s2, 0, 0, 0);
				}
				if (i == 1) {
					btnIdent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20_s2, 0, 0, 0);
				}
				if (i == 2) {
					btnCompDom.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20_s2, 0, 0, 0);
				}
				if (i == 3) {
					btnCompIng.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20_s2, 0, 0, 0);
				}
			}else {
				if (i == 0) {
					btnSolicitud.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20, 0, 0, 0);
				}
				if (i == 1) {
					btnIdent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20, 0, 0, 0);
				}
				if (i == 2) {
					btnCompDom.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20, 0, 0, 0);
				}
				if (i == 3) {
					btnCompIng.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera20x20, 0, 0, 0);
				}
			}
		}
	}
	

	private class ConnectionTask extends AsyncTask<Void, Void, ArrayAdapter<String>>{
		@Override
		protected void onPreExecute(){
		}

		@Override
		protected ArrayAdapter<String> doInBackground(Void...params){
			listaProductos lProd = new listaProductos();
			lProd.connection();
			return nombreProducto;
		}
		
		protected void onPostExecute(ArrayAdapter<String> nombreProducto){
			if (nombreProducto != null) {
				nombreProducto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sProductos.setAdapter(nombreProducto);				
			}
		}
	}
	
	public class listaProductos{
		private static final String SOAP_ACTION = "http://demo.grupocsi.com/wsbtrsantander/ConsultarProductos";
	    private static final String METHOD_NAME = "ConsultarProductos";
	    private static final String NAMESPACE = "http://demo.grupocsi.com/wsbtrsantander/";
	    private String URL="http://btrsantander.grupocsi.com/ws/btrservice.asmx";
	
	    public listaProductos(){
	    }
	    @SuppressLint("InlinedApi")
	    public ArrayAdapter<String> connection(){
	    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	    	HttpTransportSE httpTransport = new HttpTransportSE(URL);
	    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	envelope.dotNet = true;
	    	envelope.setOutputSoapObject(request);
	    	try {
				httpTransport.debug = true;
				httpTransport.call(SOAP_ACTION, envelope);
				final SoapObject sResult = (SoapObject)envelope.getResponse();
				productos = new ArrayList<ClsProducto>();
				prodName = new ArrayList<String>();
				if (sResult != null) {
					ClsProducto producto = new ClsProducto();
					producto.setIdProducto(0);
					producto.setDescripcion("");
					prodName.add("");
					for (int i = 0; i < sResult.getPropertyCount(); i++) {
						SoapObject so = (SoapObject)sResult.getProperty(i);
						producto = new ClsProducto();
						producto.setIdProducto(Integer.parseInt(so.getProperty(0).toString()));
						producto.setDescripcion(so.getProperty(1).toString());
						productos.add(producto);
						prodName.add(so.getProperty(1).toString());
					}
					nombreProducto = new ArrayAdapter<String>(Formulario.this,
							android.R.layout.simple_selectable_list_item, prodName);
				}
			} catch (IOException ioe) {
				Log.d("IOException carga productos -- ", ioe.toString());
			}catch (Exception e) {
				Log.d("Exception carga productos -- ", e.toString());
			}
	    	return nombreProducto;
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
			if (idRequest == 1) {
				regresaMenu();	
			}else{
				Intent obtenerListado = new Intent(Formulario.this, ConsultarExpedientes.class);
				startActivity(obtenerListado);
			}
		}
		return true;
	}
}