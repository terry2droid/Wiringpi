package com.khadas.wiringpi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import src.com.khadas.wiringpi.R;

public class MainActivity extends Activity implements OnCheckedChangeListener{
	
	private final static String TAG = "Khadas";
	private final static boolean DEBUG = false;
	
	
	private final static int GPIOH5_INDEX  = 0;
	private final static int GPIOH6_INDEX  = 1;
	private final static int GPIOH7_INDEX  = 2;
	private final static int GPIOH8_INDEX  = 3;
	private final static int GPIOH9_INDEX  = 4;
	private final static int GPIOAO6_INDEX = 5;
	
	private final static int GPIOH5_PORT  = 176;
	private final static int GPIOH6_PORT  = 177;
	private final static int GPIOH7_PORT  = 178;
	private final static int GPIOH8_PORT  = 179;
	private final static int GPIOH9_PORT  = 180;
	private final static int GPIOAO6_PORT = 151;
	
	private final static int[] ALL_GPIO_PORTS = {
			GPIOH5_PORT,
			GPIOH6_PORT,
			GPIOH7_PORT,
			GPIOH8_PORT,
			GPIOH9_PORT,
			GPIOAO6_PORT,
	};
		
	//DEFAULT_DIRECTION : 0--Input  1---Output
	private int DIRECTION_INPUT = 0;
	private int DIRECTION_OUTPUT = 1;
	private int DEFAULT_DIRECTION = DIRECTION_OUTPUT;
	
	//DEFAULT_LEVEL :  0---low  1---high
	private int LEVEL_LOW = 0;
	private int LEVEL_HIGH = 1;
	private int DEFAULT_LEVEL = LEVEL_HIGH;
	

	RadioGroup  Rdg_GPIOH5_Direction;
	RadioGroup  Rdg_GPIOH5_Level;
	RadioButton Rdb_GPIOH5_Low;
	RadioButton Rdb_GPIOH5_High;
	RadioGroup  Rdg_GPIOH6_Direction;
	RadioGroup  Rdg_GPIOH6_Level;
	RadioButton Rdb_GPIOH6_Low;
	RadioButton Rdb_GPIOH6_High;
	RadioGroup  Rdg_GPIOH7_Direction;
	RadioGroup  Rdg_GPIOH7_Level;
	RadioButton Rdb_GPIOH7_Low;
	RadioButton Rdb_GPIOH7_High;
	RadioGroup  Rdg_GPIOH8_Direction;
	RadioGroup  Rdg_GPIOH8_Level;
	RadioButton Rdb_GPIOH8_Low;
	RadioButton Rdb_GPIOH8_High;
	RadioGroup  Rdg_GPIOH9_Direction;
	RadioGroup  Rdg_GPIOH9_Level;
	RadioButton Rdb_GPIOH9_Low;
	RadioButton Rdb_GPIOH9_High;
	RadioGroup  Rdg_GPIOAO6_Direction;
	RadioGroup  Rdg_GPIOAO6_Level;
	RadioButton Rdb_GPIOAO6_Low;
	RadioButton Rdb_GPIOAO6_High;

	
	LinearLayout Layout_GPIOH5_Input;
	LinearLayout Layout_GPIOH5_Output;
	LinearLayout Layout_GPIOH6_Input;
	LinearLayout Layout_GPIOH6_Output;
	LinearLayout Layout_GPIOH7_Input;
	LinearLayout Layout_GPIOH7_Output;
	LinearLayout Layout_GPIOH8_Input;
	LinearLayout Layout_GPIOH8_Output;
	LinearLayout Layout_GPIOH9_Input;
	LinearLayout Layout_GPIOH9_Output;
	LinearLayout Layout_GPIOAO6_Input;
	LinearLayout Layout_GPIOAO6_Output;
	
	
	TextView Txv_GPIOH5_State;
	TextView Txv_GPIOH6_State;
	TextView Txv_GPIOH7_State;
	TextView Txv_GPIOH8_State;
	TextView Txv_GPIOH9_State;
	TextView Txv_GPIOAO6_State;
	
	private List<GPIO_Attr> mList = new ArrayList<GPIO_Attr>();
	
	private Context mContext;
	private Process mProcess;
	private UpdateLevelHander mUpdateLevelHander;
	private ReadLevelThread  mReadLevelThread;
	private int LOOP_TIME = 200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		
		Rdg_GPIOH5_Direction  = (RadioGroup)   findViewById(R.id.rdg_gpioh5_direction);
		Rdg_GPIOH5_Level      = (RadioGroup)   findViewById(R.id.rdg_gpioh5_level);
		Rdb_GPIOH5_Low        = (RadioButton)  findViewById(R.id.rdb_gpioh5_low);
		Rdb_GPIOH5_High       = (RadioButton)  findViewById(R.id.rdb_gpioh5_high);
		
		Layout_GPIOH5_Input   = (LinearLayout) findViewById(R.id.input_gpioh5);
		Layout_GPIOH5_Output  = (LinearLayout) findViewById(R.id.output_gpioh5);
		
		Txv_GPIOH5_State      = (TextView)     findViewById(R.id.txv_gpioh5_state);
		
		Rdg_GPIOH5_Direction.setOnCheckedChangeListener(this);
		Rdg_GPIOH5_Level.setOnCheckedChangeListener(this);
		
		Rdg_GPIOH6_Direction  = (RadioGroup)   findViewById(R.id.rdg_gpioh6_direction);
		Rdg_GPIOH6_Level      = (RadioGroup)   findViewById(R.id.rdg_gpioh6_level);
		Rdb_GPIOH6_Low        = (RadioButton)  findViewById(R.id.rdb_gpioh6_low);
		Rdb_GPIOH6_High       = (RadioButton)  findViewById(R.id.rdb_gpioh6_high);
		
		Layout_GPIOH6_Input   = (LinearLayout) findViewById(R.id.input_gpioh6);
		Layout_GPIOH6_Output  = (LinearLayout) findViewById(R.id.output_gpioh6);
		
		Txv_GPIOH6_State      = (TextView)     findViewById(R.id.txv_gpioh6_state);
		
		Rdg_GPIOH6_Direction.setOnCheckedChangeListener(this);
		Rdg_GPIOH6_Level.setOnCheckedChangeListener(this);
		
		Rdg_GPIOH7_Direction  = (RadioGroup)   findViewById(R.id.rdg_gpioh7_direction);
		Rdg_GPIOH7_Level      = (RadioGroup)   findViewById(R.id.rdg_gpioh7_level);
		Rdb_GPIOH7_Low        = (RadioButton)  findViewById(R.id.rdb_gpioh7_low);
		Rdb_GPIOH7_High       = (RadioButton)  findViewById(R.id.rdb_gpioh7_high);
		
		Layout_GPIOH7_Input   = (LinearLayout) findViewById(R.id.input_gpioh7);
		Layout_GPIOH7_Output  = (LinearLayout) findViewById(R.id.output_gpioh7);
		
		Txv_GPIOH7_State      = (TextView)     findViewById(R.id.txv_gpioh7_state);
		
		Rdg_GPIOH7_Direction.setOnCheckedChangeListener(this);
		Rdg_GPIOH7_Level.setOnCheckedChangeListener(this);
		
		Rdg_GPIOH8_Direction  = (RadioGroup)   findViewById(R.id.rdg_gpioh8_direction);
		Rdg_GPIOH8_Level      = (RadioGroup)   findViewById(R.id.rdg_gpioh8_level);
		Rdb_GPIOH8_Low        = (RadioButton)  findViewById(R.id.rdb_gpioh8_low);
		Rdb_GPIOH8_High       = (RadioButton)  findViewById(R.id.rdb_gpioh8_high);
		
		Layout_GPIOH8_Input   = (LinearLayout) findViewById(R.id.input_gpioh8);
		Layout_GPIOH8_Output  = (LinearLayout) findViewById(R.id.output_gpioh8);
		
		Txv_GPIOH8_State      = (TextView)     findViewById(R.id.txv_gpioh8_state);
		
		Rdg_GPIOH8_Direction.setOnCheckedChangeListener(this);
		Rdg_GPIOH8_Level.setOnCheckedChangeListener(this);
		
		Rdg_GPIOH9_Direction  = (RadioGroup)   findViewById(R.id.rdg_gpioh9_direction);
		Rdg_GPIOH9_Level      = (RadioGroup)   findViewById(R.id.rdg_gpioh9_level);
		Rdb_GPIOH9_Low        = (RadioButton)  findViewById(R.id.rdb_gpioh9_low);
		Rdb_GPIOH9_High       = (RadioButton)  findViewById(R.id.rdb_gpioh9_high);
		
		Layout_GPIOH9_Input   = (LinearLayout) findViewById(R.id.input_gpioh9);
		Layout_GPIOH9_Output  = (LinearLayout) findViewById(R.id.output_gpioh9);
		
		Txv_GPIOH9_State      = (TextView)     findViewById(R.id.txv_gpioh9_state);
		
		Rdg_GPIOH9_Direction.setOnCheckedChangeListener(this);
		Rdg_GPIOH9_Level.setOnCheckedChangeListener(this);
		
		Rdg_GPIOAO6_Direction  = (RadioGroup)   findViewById(R.id.rdg_gpioao6_direction);
		Rdg_GPIOAO6_Level      = (RadioGroup)   findViewById(R.id.rdg_gpioao6_level);
		Rdb_GPIOAO6_Low        = (RadioButton)  findViewById(R.id.rdb_gpioao6_low);
		Rdb_GPIOAO6_High       = (RadioButton)  findViewById(R.id.rdb_gpioao6_high);
		
		Layout_GPIOAO6_Input   = (LinearLayout) findViewById(R.id.input_gpioao6);
		Layout_GPIOAO6_Output  = (LinearLayout) findViewById(R.id.output_gpioao6);
		
		Txv_GPIOAO6_State      = (TextView)     findViewById(R.id.txv_gpioao6_state);
		
		Rdg_GPIOAO6_Direction.setOnCheckedChangeListener(this);
		Rdg_GPIOAO6_Level.setOnCheckedChangeListener(this);
		
		
		try {
            mProcess = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		InitDefaultState();
	}
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mReadLevelThread.setStop(false);
		
	}




	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mReadLevelThread.setStop(true);
	}




	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unexportAllGPIO();
		
	}




	private void exportAllGPIO() {
		for (int port : ALL_GPIO_PORTS) {
			exportGPIO(port);
		}	
	}
	
	private void unexportAllGPIO() {
		for (int port : ALL_GPIO_PORTS) {
			unexportGPIO(port);
		}	
	}
	
	private void InitDefaultState(){
		exportAllGPIO();
		for (int port : ALL_GPIO_PORTS) {
			setDirection(port,DEFAULT_DIRECTION);
			setLevel(port,DEFAULT_LEVEL);
		}
		

		
		for(int i=0;i<ALL_GPIO_PORTS.length;i++){
			GPIO_Attr mGPIO_Attr = new GPIO_Attr(ALL_GPIO_PORTS[i], DEFAULT_DIRECTION);
			mList.add(mGPIO_Attr);
		}
		
		mUpdateLevelHander = new UpdateLevelHander();
		mReadLevelThread   = new ReadLevelThread();
		mReadLevelThread.setStop(false);
		mReadLevelThread.start();
			
	}
	
	private boolean exportGPIO(int port) {
		
		
		 try {
	            DataOutputStream os = new DataOutputStream(mProcess.getOutputStream());
	         
	            os.writeBytes("echo " + port + " > /sys/class/gpio/export\n");	          
	            os.flush();
	            Thread.sleep(100);
	        } catch (IOException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	            return false;
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        return true;

	}
	
   private  boolean unexportGPIO(int port) {
	   
        try {
            DataOutputStream os = new DataOutputStream(mProcess.getOutputStream());
            os.writeBytes("echo " + port + " > /sys/class/gpio/unexport\n");
            os.flush();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }
		
        return true;
    }
	private boolean setDirection(int port,int direction) {
		if (DEBUG) Log.d(TAG, "setDirection  port= "+port+" direction="+direction);
		
		 try {
	            DataOutputStream os = new DataOutputStream(mProcess.getOutputStream());
	    
	            if (direction==0)
	            os.writeBytes("echo in > /sys/class/gpio/gpio" + port + "/direction\n");
	            else 
	            os.writeBytes("echo out > /sys/class/gpio/gpio" + port + "/direction\n");
	            
	            os.flush();
	            Thread.sleep(100);
	        } catch (IOException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	            return false;
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        return true;
	}
	
	private boolean setLevel(int port,int level) {

		if (DEBUG) Log.d(TAG, "setLevel  port= "+port+" level="+level);
		
		 try {
	            DataOutputStream os = new DataOutputStream(mProcess.getOutputStream());
	    
	            if (level==0)
	            os.writeBytes("echo 0 > /sys/class/gpio/gpio" + port + "/value\n");
	            else 
	            os.writeBytes("echo 1 > /sys/class/gpio/gpio" + port + "/value\n");
	            
	            os.flush();
	            Thread.sleep(100);
	        } catch (IOException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	            return false;
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        return true;
	}
	
	private int getLevel(int port) {
	
	      try {
	            Runtime runtime = Runtime.getRuntime();
	            Process process = runtime.exec("cat " + "/sys/class/gpio/gpio" + port + "/value"); 
	            InputStream is = process.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line ;
	            while (null != (line = br.readLine())) {
	                return Integer.parseInt(line.trim());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            Log.w(TAG,e.getMessage());
	        }
	        return -1;
	        
	}
	
	
	class GPIO_Attr {
		int port;
		int direction;
		
		
	    public GPIO_Attr(int port, int direction){
	    	setport(port);
	    	setdirection(direction);  	
	    }
		private void setport(int port){
			this.port = port;
		}
		private int getport(){
			return this.port;
		}
		private void setdirection(int direction){
			this.direction = direction;
		}
		private int getdirection(){
			return this.direction;
		}
		
	}
	
	public class ReadLevelThread extends Thread {

		private int port;
		private boolean stop = false;
		private List<GPIO_Attr> list = new ArrayList<GPIO_Attr>();
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			while(true){
				if (!stop){
					for (int i=0;i<mList.size();i++){
						if (mList.get(i).direction == DIRECTION_INPUT) {
							Message msg = new Message();
							int level = getLevel(mList.get(i).port);
							msg.what = i;
							msg.arg1 = level;
							mUpdateLevelHander.sendMessage(msg);
						}
					}
					try {
						Thread.sleep(LOOP_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		public void setStop(boolean stop) {
			this.stop = stop;
		}
		
	}
	
	public class UpdateLevelHander extends Handler  {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (DEBUG) Log.d(TAG, "UpdateLevelHander what= "+msg.what+" arg1= "+msg.arg1);
			switch(msg.what) {
			case GPIOH5_INDEX:
				Txv_GPIOH5_State.setText(""+msg.arg1);
				break;
			case GPIOH6_INDEX:
				Txv_GPIOH6_State.setText(""+msg.arg1);
				break;
			case GPIOH7_INDEX:
				Txv_GPIOH7_State.setText(""+msg.arg1);
				break;
			case GPIOH8_INDEX:
				Txv_GPIOH8_State.setText(""+msg.arg1);
				break;
			case GPIOH9_INDEX:
				Txv_GPIOH9_State.setText(""+msg.arg1);
				break;
			case GPIOAO6_INDEX:
				Txv_GPIOAO6_State.setText(""+msg.arg1);
				break;
			}
		}
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		int id = group.getId();
		switch(id) {
		case R.id.rdg_gpioh5_direction:
			 switch(checkedId) {
			 case R.id.rdb_gpioh5_input:
				 Layout_GPIOH5_Output.setVisibility(View.GONE);
				 Layout_GPIOH5_Input.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH5_PORT, DIRECTION_INPUT);
				 mList.set(GPIOH5_INDEX, new GPIO_Attr(GPIOH5_PORT, DIRECTION_INPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 case R.id.rdb_gpioh5_output:
				 Layout_GPIOH5_Input.setVisibility(View.GONE);
				 Layout_GPIOH5_Output.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH5_PORT,DIRECTION_OUTPUT);
				 if (Rdb_GPIOH5_High.isChecked()){
					 setLevel(GPIOH5_PORT,LEVEL_HIGH);
				 }else if (Rdb_GPIOH5_Low.isChecked()) {
					 setLevel(GPIOH5_PORT,LEVEL_LOW);
				 }
				 mList.set(GPIOH5_INDEX, new GPIO_Attr(GPIOH5_PORT, DIRECTION_OUTPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 default:
				 break;
			 }
			break;
		case R.id.rdg_gpioh5_level:
			 switch(checkedId) {
			 case R.id.rdb_gpioh5_low:
				 setLevel(GPIOH5_PORT,LEVEL_LOW);
				 break;
			 case R.id.rdb_gpioh5_high:
				 setLevel(GPIOH5_PORT,LEVEL_HIGH);
				 break;
			 }
			break;
		case R.id.rdg_gpioh6_direction:
			 switch(checkedId) {
			 case R.id.rdb_gpioh6_input:
				 Layout_GPIOH6_Output.setVisibility(View.GONE);
				 Layout_GPIOH6_Input.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH6_PORT, DIRECTION_INPUT);
				 mList.set(GPIOH6_INDEX, new GPIO_Attr(GPIOH6_PORT, DIRECTION_INPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 case R.id.rdb_gpioh6_output:
				 Layout_GPIOH6_Input.setVisibility(View.GONE);
				 Layout_GPIOH6_Output.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH6_PORT,DIRECTION_OUTPUT);
				 if (Rdb_GPIOH6_High.isChecked()){
					 setLevel(GPIOH6_PORT,LEVEL_HIGH);
				 }else if (Rdb_GPIOH6_Low.isChecked()) {
					 setLevel(GPIOH6_PORT,LEVEL_LOW);
				 }
				 mList.set(GPIOH6_INDEX, new GPIO_Attr(GPIOH6_PORT, DIRECTION_OUTPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 default:
				 break;
			 }
			break;
		case R.id.rdg_gpioh6_level:
			 switch(checkedId) {
			 case R.id.rdb_gpioh6_low:
				 setLevel(GPIOH6_PORT,LEVEL_LOW);
				 break;
			 case R.id.rdb_gpioh6_high:
				 setLevel(GPIOH6_PORT,LEVEL_HIGH);
				 break;
			 }
			break;
		case R.id.rdg_gpioh7_direction:
			 switch(checkedId) {
			 case R.id.rdb_gpioh7_input:
				 Layout_GPIOH7_Output.setVisibility(View.GONE);
				 Layout_GPIOH7_Input.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH7_PORT, DIRECTION_INPUT);
				 mList.set(GPIOH7_INDEX, new GPIO_Attr(GPIOH7_PORT, DIRECTION_INPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 case R.id.rdb_gpioh7_output:
				 Layout_GPIOH7_Input.setVisibility(View.GONE);
				 Layout_GPIOH7_Output.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH7_PORT,DIRECTION_OUTPUT);
				 if (Rdb_GPIOH7_High.isChecked()){
					 setLevel(GPIOH7_PORT,LEVEL_HIGH);
				 }else if (Rdb_GPIOH7_Low.isChecked()) {
					 setLevel(GPIOH7_PORT,LEVEL_LOW);
				 }
				 mList.set(GPIOH7_INDEX, new GPIO_Attr(GPIOH7_PORT, DIRECTION_OUTPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 default:
				 break;
			 }
			break;
		case R.id.rdg_gpioh7_level:
			 switch(checkedId) {
			 case R.id.rdb_gpioh7_low:
				 setLevel(GPIOH7_PORT,LEVEL_LOW);
				 break;
			 case R.id.rdb_gpioh7_high:
				 setLevel(GPIOH7_PORT,LEVEL_HIGH);
				 break;
			 }
			break;
		case R.id.rdg_gpioh8_direction:
			 switch(checkedId) {
			 case R.id.rdb_gpioh8_input:
				 Layout_GPIOH8_Output.setVisibility(View.GONE);
				 Layout_GPIOH8_Input.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH8_PORT, DIRECTION_INPUT);
				 mList.set(GPIOH8_INDEX, new GPIO_Attr(GPIOH8_PORT, DIRECTION_INPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 case R.id.rdb_gpioh8_output:
				 Layout_GPIOH8_Input.setVisibility(View.GONE);
				 Layout_GPIOH8_Output.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH8_PORT,DIRECTION_OUTPUT);
				 if (Rdb_GPIOH8_High.isChecked()){
					 setLevel(GPIOH8_PORT,LEVEL_HIGH);
				 }else if (Rdb_GPIOH8_Low.isChecked()) {
					 setLevel(GPIOH8_PORT,LEVEL_LOW);
				 }
				 mList.set(GPIOH8_INDEX, new GPIO_Attr(GPIOH8_PORT, DIRECTION_OUTPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 default:
				 break;
			 }
			break;
		case R.id.rdg_gpioh8_level:
			 switch(checkedId) {
			 case R.id.rdb_gpioh8_low:
				 setLevel(GPIOH8_PORT,LEVEL_LOW);
				 break;
			 case R.id.rdb_gpioh8_high:
				 setLevel(GPIOH8_PORT,LEVEL_HIGH);
				 break;
			 }
			break;
		case R.id.rdg_gpioh9_direction:
			 switch(checkedId) {
			 case R.id.rdb_gpioh9_input:
				 Layout_GPIOH9_Output.setVisibility(View.GONE);
				 Layout_GPIOH9_Input.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH9_PORT, DIRECTION_INPUT);
				 mList.set(GPIOH9_INDEX, new GPIO_Attr(GPIOH9_PORT, DIRECTION_INPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 case R.id.rdb_gpioh9_output:
				 Layout_GPIOH9_Input.setVisibility(View.GONE);
				 Layout_GPIOH9_Output.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOH9_PORT,DIRECTION_OUTPUT);
				 if (Rdb_GPIOH5_High.isChecked()){
					 setLevel(GPIOH9_PORT,LEVEL_HIGH);
				 }else if (Rdb_GPIOH9_Low.isChecked()) {
					 setLevel(GPIOH9_PORT,LEVEL_LOW);
				 }
				 mList.set(GPIOH9_INDEX, new GPIO_Attr(GPIOH9_PORT, DIRECTION_OUTPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 default:
				 break;
			 }
			break;
		case R.id.rdg_gpioh9_level:
			 switch(checkedId) {
			 case R.id.rdb_gpioh9_low:
				 setLevel(GPIOH9_PORT,LEVEL_LOW);
				 break;
			 case R.id.rdb_gpioh9_high:
				 setLevel(GPIOH9_PORT,LEVEL_HIGH);
				 break;
			 }
			break;
		case R.id.rdg_gpioao6_direction:
			 switch(checkedId) {
			 case R.id.rdb_gpioao6_input:
				 Layout_GPIOAO6_Output.setVisibility(View.GONE);
				 Layout_GPIOAO6_Input.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOAO6_PORT, DIRECTION_INPUT);
				 mList.set(GPIOAO6_INDEX, new GPIO_Attr(GPIOAO6_PORT, DIRECTION_INPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 case R.id.rdb_gpioao6_output:
				 Layout_GPIOAO6_Input.setVisibility(View.GONE);
				 Layout_GPIOAO6_Output.setVisibility(View.VISIBLE);
				 mReadLevelThread.setStop(true);
				 setDirection(GPIOAO6_PORT,DIRECTION_OUTPUT);
				 if (Rdb_GPIOAO6_High.isChecked()){
					 setLevel(GPIOAO6_PORT,LEVEL_HIGH);
				 }else if (Rdb_GPIOAO6_Low.isChecked()) {
					 setLevel(GPIOAO6_PORT,LEVEL_LOW);
				 }
				 mList.set(GPIOAO6_INDEX, new GPIO_Attr(GPIOAO6_PORT, DIRECTION_OUTPUT));
				 mReadLevelThread.setStop(false);
				 break;
			 default:
				 break;
			 }
			break;
		case R.id.rdg_gpioao6_level:
			 switch(checkedId) {
			 case R.id.rdb_gpioao6_low:
				 setLevel(GPIOAO6_PORT,LEVEL_LOW);
				 break;
			 case R.id.rdb_gpioao6_high:
				 setLevel(GPIOAO6_PORT,LEVEL_HIGH);
				 break;
			 }
			break;
		default:
			break;
		}
	}

}
