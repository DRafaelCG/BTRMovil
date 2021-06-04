package com.grupocsi.btrmovil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class SplashScreen extends Activity {
	protected boolean active = true;
	protected int splashTime = 1000;
	int REQUEST_CODE;
	/**
	 * @author Rafael Castrejón
	 * Pantalla de inicio splash
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_splash_screen);
		if (REQUEST_CODE == 1) {
			this.finish();
		}
		Thread splashThread = new Thread(){
				@Override
				public void run(){
					try {
						int waited = 0;
						while (active && (waited < splashTime)) {
							sleep(100);
							if (active) {
								waited +=100;
							}
						}	
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						openApp();
					}
				}
		};
		splashThread.start();
	}
	
	private void openApp(){
		Intent mInicio = new Intent(SplashScreen.this, Inicio.class);
		startActivity(mInicio);
		finish();
	}
}
