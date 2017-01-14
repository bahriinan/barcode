package com.example.my_cse476_project;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    TextView scanText;
    Button scanButton;
    ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        scanText = (TextView)findViewById(R.id.scanText);
    }
    public void onPause() {
        super.onPause();
        releaseCamera();
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

    PreviewCallback previewCb = new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();
                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);
                int result = scanner.scanImage(barcode);
                if (result != 0) {
                    SymbolSet syms = scanner.getResults();
                    for (Symbol sym : syms) {
                        scanText.setText("Barkod Sonucu: " + sym.getData());
                        scanText.setTextColor(Color.parseColor("#00AF03"));
                        barcodeScanned = true;
                        releaseCamera();
                        Intent intent = new Intent();
                        intent.putExtra("barcodeValue", sym.getData().toString());
                        setResult(RESULT_OK, intent);
                    }
                }
            }
        };
    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };

}
