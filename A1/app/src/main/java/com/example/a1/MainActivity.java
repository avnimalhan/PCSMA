package com.example.a1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 600;
    private ArrayList<String>arr_x = new ArrayList<String>();
    private ArrayList<String>arr_y = new ArrayList<String>();
    private ArrayList<String>arr_z = new ArrayList<String>();
    private ArrayList<String>arr_time = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      /*  Button b1 = (Button)findViewById(R.id.start);
        Button b2 = (Button)findViewById(R.id.stop);
        Button b3 = (Button)findViewById(R.id.send);*/

    }

    public void caseStart(View view) {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        TextView t1 = (TextView)findViewById(R.id.x_axis);
        TextView t2 = (TextView)findViewById(R.id.y_axis);
        TextView t3 = (TextView)findViewById(R.id.z_axis);

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = dateFormat.format(new Date());

          //  if ((curTime - lastUpdate) > 100) {
            //    long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
/*
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }

                last_x = x;
                last_y = y;
                last_z = z;*/
                t1.setText(Float.toString(x));
                t2.setText(Float.toString(y));
                t3.setText(Float.toString(z));
                arr_x.add(Float.toString(x));
                arr_y.add(Float.toString(y));
                arr_z.add(Float.toString(z));
                arr_time.add(timestamp);
            }
        //}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void caseStop(View view) {
        senSensorManager.unregisterListener(this);

        try {
            String csv = Environment.getExternalStorageDirectory() + "/PCSMA_A1.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            for(int i=0; i<arr_x.size(); i++) {
                String[] arr = ((arr_x.get(i)) + "#" + arr_y.get(i) + "#" + arr_z.get(i) + "#" + arr_time.get(i)).split("#");
                writer.writeNext(arr);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void caseSend(View view) {
        MyClientTask myclient = new MyClientTask();
        myclient.execute();
    }
    class MyClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = null;
            // DataOutputStream output = null;



            DataOutputStream dos = null;
            try {
                socket= new Socket("192.168.57.32",8080);
                File myFile = new File(Environment.getExternalStorageDirectory(), "PCSMA_A1.csv");
                dos = new DataOutputStream(socket.getOutputStream());

                FileInputStream fis = new FileInputStream(myFile);
                byte[] buffer = new byte[4096];

                while (fis.read(buffer) > 0) {
                    dos.write(buffer);
                }

                fis.close();
                dos.close();

                socket.close();


        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        }
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), "Accelerometer values sent to the server", Toast.LENGTH_SHORT).show();
        }
    }



}