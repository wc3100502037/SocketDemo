package com.example.mysockettest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    EditText ipAdress,portNet;
    Button connect,cutoff,sendBtn;
    EditText sendedText;
    TextView receiveText;
    private String TAG="MainActivity";
    Socket socket=null;
    private Thread socketThread,receiveThread;
    private BufferedReader mBufferedReader;
    private InputStream myInputStream;
    private PrintStream mPrintStream;
    private Handler mhandler;
    private static boolean recThreadFlag;
    private String rcvThreadText;
    private String rcvThreadInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipAdress=(EditText) findViewById(R.id.ipAdress);
        portNet=(EditText) findViewById(R.id.portNet);
        connect=(Button) findViewById(R.id.connect);
        cutoff=(Button) findViewById(R.id.cutoff);
        sendedText=(EditText) findViewById(R.id.sendedText);
        receiveText=(TextView) findViewById(R.id.receivedText);
        sendBtn=(Button) findViewById(R.id.sendBtn); 
        recThreadFlag=true;
        Log.i(TAG, "----onCreate()----");
 ///////////////////////////////////////////////////////////////////////////////////////////////////
 ////////////////////////////////////////////////////////////////////////////////////////////
        
        connect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
			 String ipadressString=ipAdress.getText().toString();
			 String portNetString=portNet.getText().toString();
			 recThreadFlag=true;
			 Toast.makeText(MainActivity.this, "before try"+" "+ipadressString,Toast.LENGTH_SHORT).show();	
			 if((socketThread.isAlive()==true)||(receiveThread.isAlive()==true))
			 {   
				 
				 recThreadFlag=false;
			 }
			 socketThread.start();
			 receiveThread.start(); 
		     Log.i(TAG, "----after try----");	
		     Toast.makeText(MainActivity.this, "after try"+" "+ipadressString,Toast.LENGTH_SHORT).show();
				
			}
		});
        
       sendBtn.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO 自动生成的方法存根
		Log.i(TAG, "----push sendbtn----");	
		String msg=sendedText.getText().toString();
		mPrintStream.print(msg);		
		mPrintStream.flush();
			
			
		}
	});
     
       
        cutoff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				try {myInputStream.close();
				     mPrintStream.close();
					socket.close();
					if(socketThread.isAlive()==true)
					socketThread.interrupt();
					if(receiveThread.isAlive()==true)
					 recThreadFlag=false;
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		});
        
        mhandler=new Handler(){
        public void handleMessage(Message msg)
        {   byte[] recData=new byte[20];
        	Log.i(TAG, "----mHandler----");
         	switch(msg.what)
         	{ 
         	case 1:recData=msg.getData().getByteArray("content");
         	Log.i(TAG, "----"+rcvThreadInt+""+"----");
         	break;
         	default: break;
         	
         	}
         	convertToString(recData);
         	String str=new String(recData);
            receiveText.setText(str);	
        }
      };

/////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
        
        socketThread=new Thread(new Runnable() {	
		@Override
		public void run() {
			// TODO 自动生成的方法存根
			//Socket socket=null;
			Log.i(TAG, "----socketThread run----");	
			String ipadressString=ipAdress.getText().toString();
			String portNetString=portNet.getText().toString();
		   try {
			Log.i(TAG, "using try  "+ipadressString+"//"+portNetString);
			//socket=new Socket(ipadressString,Integer.parseInt(portNetString));
			socket=new Socket(ipadressString,Integer.parseInt(portNetString));
			Log.i(TAG, "----after new sock  et----");
			mPrintStream=new PrintStream(socket.getOutputStream());
			Log.i(TAG, "----after new print++678writer----");
			if(socket.getInputStream()!=null){
				
			//mBufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			myInputStream=socket.getInputStream();
			}
			} catch (UnknownHostException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		}
	});
/////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////   
        
        receiveThread=new Thread(new Runnable() {
        	
			byte[] bytes=new byte[20];
			Bundle bundle=new Bundle();
			//String mess=null;
			int readMsg;
			//InputStreamReader inputReader=null;
			
			@Override
			public void run() {
				// TODO 自动生成的方法存根
			Log.i(TAG, "----receiveThread run----");
		    while(recThreadFlag)
			{ Message msg=mhandler.obtainMessage();	
			  msg.what=1;		
			  Log.i(TAG, "----receiveThread_while()----");
			  try {
				if(myInputStream!=null){
				Log.i(TAG, "----BufferReader.readLine()----");
				//inputReader=new InputStreamReader(myInputStream);
				
				if((readMsg=myInputStream.read(bytes))!=0){
					//readMsg = myInputStream.read(bytes);	
					Log.i("---the length of bytes is",String.valueOf(readMsg));
					Log.i(TAG, "----"+convertToString(bytes)+"----");
					Log.i(TAG, "----"+bytes[1]+"----");
					bundle.putByteArray("content",bytes);
					msg.setData(bundle);
					mhandler.sendMessage(msg);	
				} }}catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			                            }
			
			}	
	}
		
        });      
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
private byte[] convertToString(byte[] data)
{
 int length=data.length;
 for(int i=0;i<length;i++)
 {
	 data[i]=(byte) (data[i]-48);
 }
  return data;
}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
   
}
