package com.grupocsi.btrmovil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.grupocsi.btrmovil.cropImages.CropImage;

@SuppressLint("NewApi")
public class Identificacion extends Activity {
	ClsDataClass dataclass = ClsDataClass.getInstancia();
	ClsDocumento documento;
	int regresa = 0;
	boolean eliminar = false;
	Button btnAgregar;
	Button btnEliminar;
	int idImagen = 0;
	private static final int CAMERA_PIC_REQUEST = 1337;
	private boolean[] seleccionados;
	ImageView img;
	Bitmap foto;	
	GridView gridImagenes;
	ImageAdapter images = null;
	int idDocumento;
	int captureLastId = 0;
	private Uri output;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static File mediaFile;
	File mediaStorageDir;
	private static final int CROP_FROM_CAMERA = 2;
	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.activity_identificacion);
			images = new ImageAdapter();
			Bundle b = getIntent().getExtras();
			if (b != null) {
				idDocumento = b.getInt("IdDocumento");
				for (int i = 0; i < dataclass.documentos.size(); i++) {
					if (dataclass.documentos.get(i).getIdDocumento() == idDocumento) {
						documento = dataclass.documentos.get(i);
						setTitle(documento.getNombreDocumento());
						break;
					}
				}
				seleccionados = new boolean[documento.getImagenes().size()];
				idImagen = seleccionados.length;
				for (int i = 0; i < documento.getImagenes().size(); i++) {
					images.AddImage(documento.getImagenes().get(i).getImagen());
					seleccionados[i] = false;
				}
				
				btnAgregar = (Button)findViewById(R.id.btnAgregar);
				btnAgregar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startCaptura();
					}
				});
				gridImagenes = (GridView)findViewById(R.id.gVImagenes);
				gridImagenes.setAdapter(images);
				if (documento.getImagenes().size() == 0) {
					startCaptura();
				}
			}			
		} catch (Exception e) {
			Log.d("Error cargardo fotos", e.getMessage());
		}

	}
	
	public void startCaptura(){
		captureLastId = getLastImageId();
		Intent intCapturar = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intCapturar.putExtra(MediaStore.EXTRA_OUTPUT, CAMERA_PIC_REQUEST);
		output = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		intCapturar.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intCapturar, CAMERA_PIC_REQUEST);
	}
	
	private int getLastImageId(){
		final String[] imageColumns = { MediaStore.Images.Media._ID };
	    final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
	    final String imageWhere = null;
	    final String[] imageArguments = null;
	    Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, imageWhere, imageArguments, imageOrderBy);
	    if(imageCursor.moveToFirst()){
	        int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
	        imageCursor.close();
	        return id;
	    }else{
	        return 0;
	    }
	}
	
	private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}
	
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type){
		File mediaStorageDir = new File(""+Environment.getExternalStoragePublicDirectory("/SELMovil/"));
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("SELMovil", "No se pudo crear directorio");
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + "/selmovil_" + timeStamp + ".jpg");
		}else {
			return null;
		}
		return mediaFile;
	}
	
	/***Metodos para no perder la uri*********************************************************/
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		if (output != null) {
			savedInstanceState.putParcelable("output", output);
		}
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		Uri output2 = (Uri)savedInstanceState.getParcelable("output");
		if (output2 != null) {
			output = output2;
		}
	}
	/************************************************************/
	
	public String convertBase64(Drawable img){
		Bitmap bmSrc = null;
		if (img != null) {
			bmSrc = ((BitmapDrawable)img).getBitmap();	
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmSrc.compress(CompressFormat.JPEG, 90, stream);
		byte[] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeBytes(byte_arr);
		return image_str;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == RESULT_OK) {
				if (requestCode == CAMERA_PIC_REQUEST) {
					if (output != null) {
						doCrop();
					}
				}
				if (requestCode == CROP_FROM_CAMERA) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						String path = data.getStringExtra(CropImage.IMAGE_PATH);
						foto = null;
						//foto = BitmapFactory.decodeFile(output.getPath());
						foto = BitmapFactory.decodeFile(path);
						if (foto != null) {
							//Bitmap copia = Bitmap.createScaledBitmap(foto, 600, 800, false);
							idImagen++;
							seleccionados = new boolean[idImagen];
							//images.AddImage(copia);
							images.AddImage(foto);
							ClsImagen imagen = new ClsImagen();
							//imagen.setImagen(copia);
							imagen.setImagen(foto);
							documento.getImagenes().add(imagen);
							gridImagenes.setAdapter(images);
							images.notifyDataSetChanged();
							
						}
					}
				}
			}else {
				eliminaImagenTemp(output);
				eliminatempImage();
				System.gc();
			}
		} catch (Exception e) {
			Log.d("Activity result --", e.toString());
			eliminaImagenTemp(output);
			eliminatempImage();
			System.gc();
		}
	}
	
	private void doCrop() {
		try {
			Intent intent = new Intent(this, CropImage.class);
			intent.putExtra(CropImage.IMAGE_PATH, output.getPath());
			intent.putExtra(CropImage.SCALE, false);
			intent.putExtra(CropImage.OUTPUT_X, 400);
			intent.putExtra(CropImage.OUTPUT_Y, 600);
			intent.putExtra(CropImage.ASPECT_X, 0);
			intent.putExtra(CropImage.ASPECT_Y, 1);
			startActivityForResult(intent, CROP_FROM_CAMERA);
			System.gc();
		} catch (Exception e) {
			Log.d("Error haciendo crop.-", e.getMessage());
		}	
	}
	
	
	@SuppressWarnings("unused")
	private void eliminatempImage(){
		final String[] imageColumns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.SIZE, MediaStore.Images.Media._ID };
		final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
		final String imageWhere = MediaStore.Images.Media._ID+">?";
		final String[] imageArguments = { Integer.toString(Identificacion.this.captureLastId) };
		Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, imageWhere, imageArguments, imageOrderBy); 
		if(imageCursor.getCount()>=1){
		    while(imageCursor.moveToNext()){
		        int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
		        String path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
		        Long takenTimeStamp = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
		        Long size = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
		        ContentResolver cr = getContentResolver();
		        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{ Long.toString(id) } );
		        break;
		    }               
		}
		imageCursor.close();		
	}
	
	/*public void onBackPressed(){
		eliminaImagenTemp(output);
		eliminatempImage();
		finish();
	}*/
	
	private void eliminaImagenTemp(Uri outUri){
		File borrar = null;
		try {
			borrar = new File(outUri.getPath().replace(outUri.getLastPathSegment(), ""));
			if (borrar.isDirectory()) {
				for (File archivo : borrar.listFiles()) {
					archivo.delete();
				}
			}
		} catch (Exception e) {
			Log.d("Error borrando registro img", e.toString());
		} 
	}
	
	public class ImageAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private Vector<Bitmap> imagenes = new Vector<Bitmap>();
		public ImageAdapter(){
			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void AddImage(Bitmap b){
			imagenes.add(b);
		}
		
		@Override
		public int getCount(){
			return imagenes.size();
		}
		
		@Override
		public Object getItem(int position){
			return imagenes.get(position);
		}
		
		public Bitmap getBitmap(int position){
			return imagenes.get(position);
		}
		
		@Override
		public long getItemId(int position){
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.imagenes, null);
				holder.img = (ImageView)convertView.findViewById(R.id.iVimg1);
				holder.btnEliminar = (Button)convertView.findViewById(R.id.btnEliminar);
				holder.vDivision = (View)convertView.findViewById(R.id.vDivision);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.img.setId(position);
			holder.btnEliminar.setId(position);
			holder.img.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					ImageView imgv = (ImageView)v;
					int id = imgv.getId();
					if (seleccionados[id]) {
						eliminar = true;
						seleccionados[id] = true;
						if (holder.btnEliminar.getVisibility() == View.VISIBLE) {
							holder.btnEliminar.setVisibility(View.INVISIBLE);
						}else {
							holder.btnEliminar.setVisibility(View.VISIBLE);
						}
					}else {
						eliminar = false;
						seleccionados[id] = false;
						if (holder.btnEliminar.getVisibility() == View.INVISIBLE) {
							holder.btnEliminar.setVisibility(View.VISIBLE);	
						}else {
							holder.btnEliminar.setVisibility(View.INVISIBLE);
						}
					}
					return eliminar;
				}
			});
			holder.btnEliminar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int id = v.getId();
					if (!seleccionados[id]) {
						seleccionados[id] = false;
						imagenes.remove(id);
						documento.getImagenes().remove(id);
						eliminar();
					}	
				}
			});
			holder.img.setImageBitmap(imagenes.get(position));
			holder.img.setSelected(seleccionados[position]);
			holder.id = position;
			return convertView;
		}
	}

	class ViewHolder{
		ImageView img;
		Button btnEliminar;
		int id;
		View vDivision;
	}
	
	public void eliminar(){
		eliminar = false;
		gridImagenes.setAdapter(images);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		try {
			System.gc();
			trimCache(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void trimCache(Context context){
		try {
			File dir = context.getExternalCacheDir();
			File dir2 = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
			if (dir2 != null && dir2.isDirectory()) {
				deleteDir(dir2);
			}
		} catch (Exception e) {
			Log.d("Error borrando cacheDir.-", e.getMessage());
		}
	}
	
	public static boolean deleteDir(File dir){
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}	
		}
		return dir.delete();
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
		}else if (keycode == KeyEvent.KEYCODE_BACK) {
			eliminaImagenTemp(output);
			eliminatempImage();
			finish();
		}
		return true;
	}
}
