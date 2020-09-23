package cn.liusiqian.jnidemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.liusiqian.jnidemo.utils.PrimeCalcUtils;

public class MainActivity extends AppCompatActivity {

  private static final int CALC_PRIME_TARGET = 10000000;

  private TextView tvLoadLib, tvCallHello;
  private TextView tvCalcJava, tvCalcNative;

  private boolean alreadyLoadLibrary;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initWidgets();
  }

  private void initWidgets() {
    tvLoadLib = findViewById(R.id.txt_load_library);
    tvCallHello = findViewById(R.id.txt_call_hello);
    tvCalcJava = findViewById(R.id.txt_calc_prime_java);
    tvCalcNative = findViewById(R.id.txt_calc_prime_native);

    tvLoadLib.setOnClickListener(ocl);
    tvCallHello.setOnClickListener(ocl);
    tvCalcJava.setOnClickListener(ocl);
    tvCalcNative.setOnClickListener(ocl);
  }

  private View.OnClickListener ocl = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      if (view.getId() == R.id.txt_load_library) {
        loadLibrary();
      } else if (view.getId() == R.id.txt_call_hello) {
        callHelloWorld();
      } else if (view.getId() == R.id.txt_calc_prime_java) {
        callComputePrimeJava();
      } else if (view.getId() == R.id.txt_calc_prime_native) {
        callComputePrimeNative();
      }
    }
  };

  public native String getHelloStr();

  public native int countPrimeNative(int target);

  private void loadLibrary() {
    if (!alreadyLoadLibrary) {
      System.loadLibrary("native");
      showToastShort("Library Loaded");
      alreadyLoadLibrary = true;
    }
  }

  private void callHelloWorld() {
    if (alreadyLoadLibrary) {
      showToastShort(getHelloStr());
    } else {
      showToastShort("native Library not Loaded!");
    }
  }

  private void callComputePrimeJava() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        long start = SystemClock.elapsedRealtime();
        int result = PrimeCalcUtils.countPrimeJava(CALC_PRIME_TARGET);
        log("Java calc result: There are " + result + " prime numbers below " +
            CALC_PRIME_TARGET + " time elapsed: " + (SystemClock.elapsedRealtime() - start) + "ms");
      }
    }).start();
  }

  private void callComputePrimeNative() {
    if (alreadyLoadLibrary) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          long start = SystemClock.elapsedRealtime();
          int result = countPrimeNative(CALC_PRIME_TARGET);
          log("Native calc result: There are " + result + " prime numbers below " +
              CALC_PRIME_TARGET + " time elapsed: " + (SystemClock.elapsedRealtime() - start) + "ms");
        }
      }).start();
    } else {
      showToastShort("native Library not Loaded!");
    }
  }

  private void showToastShort(CharSequence charSequence) {
    Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
  }

  private void log(String message) {
    Log.i("JniDemo", message);
  }
}
