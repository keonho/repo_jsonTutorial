package com.ecstasy.jsontutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
//	private static final String strURL = "http://10.0.2.2:8080/JSONServer/JSONServer.jsp";	//TBD
	private static final String strURL = "http://10.0.2.2:8080/JsonTutorialServer/TutorialPage.jsp";
	private EditText etColumn, etValue;
	private Button btnSend;
	private TextView tvRecvData;	
	private String strColumn, strValue, strResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        etColumn = (EditText)findViewById(R.id.et_column);
        etValue = (EditText)findViewById(R.id.et_value);
        btnSend = (Button)findViewById(R.id.btn_sendData);
        tvRecvData = (TextView)findViewById(R.id.tv_recvData);
        
        etColumn.setText("guest_name");
        etValue.setText("humbahumba");
        
        btnSend.setOnClickListener(this);
        strResult = "empty";
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_sendData:
			strColumn = etColumn.getText().toString();
			strValue = etValue.getText().toString();
//			String result = sendByHttp(sMessage);
			if(strColumn.length() ==0 ||strValue.length()==0)
			{
				Toast.makeText(getApplicationContext(), "Pls input value", Toast.LENGTH_LONG).show();
			}
			String sMessage = strColumn+":"+strValue;
			new NetworkTask().execute(sMessage);			
			break;

		default:
			break;
		}		
	}
	
	private String sendByHttp(String msg)
	{
		if(msg == null)
		{
			msg="";
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		try{
			HttpPost post = new HttpPost(strURL+"?msg="+msg);
			
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);
			
			HttpResponse response = client.execute(post);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
			
			String line = null;
			String result="";
			
			while((line = bufReader.readLine())!=null)
			{
				result += line;
			}		
			return result;
		}catch(Exception e)
		{
			e.printStackTrace();
			client.getConnectionManager().shutdown();
			return "";
		}
	}
	
	public String[][] jsonParserList(String pRecvServerPage)
	{
		Log.i("서버에서 받은 전체 내용: ", pRecvServerPage);
		
		try{
			JSONObject json = new JSONObject(pRecvServerPage);
			JSONArray jArr = json.getJSONArray("List");
			
			String[] jsonName={"guestName", "message"};
			String[][] parsedData = new String[jArr.length()][jsonName.length];
			
			for(int i=0; i<jArr.length(); i++)
			{
				json = jArr.getJSONObject(i);
				if(json != null)
				{
					for(int j=0; j<jsonName.length; j++)
					{
						Log.d("AKH",jsonName[j]);
						parsedData[i][j] = json.getString(jsonName[j]);
					}
				}
				
			}
			
			// 분해 된 데이터를 확인하기 위한 부분
			for(int i=0; i<parsedData.length; i++){
				Log.i("JSON을 분석한 데이터 "+i+" : ", parsedData[i][0]);
				Log.i("JSON을 분석한 데이터 "+i+" : ", parsedData[i][1]);
//				//Log.i("JSON을 분석한 데이터 "+i+" : ", parsedData[i][2]);
			}
			return parsedData;
		}catch(JSONException e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	 private class NetworkTask extends AsyncTask<String, Void, String>
	{
			String str;

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				Log.d("AKH", "in bg, "+params[0]);
				strResult = sendByHttp(params[0]);
				return strResult;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				Log.d("[AKH]", "onPostExecute");
				super.onPostExecute(result);
				String[][] parsedData = jsonParserList(strResult);
				//Log.d("[AKH]", ""+ strResult);
				String temp="";
				for(int i = 0;i<parsedData.length;i++)
				{
					for(int j=0;j<parsedData[0].length;j++)
					{
						temp += (parsedData[i][j])+" ";
					}
					temp+="\n";
				}
				tvRecvData.setText(temp);
				
			}		
	}
}
