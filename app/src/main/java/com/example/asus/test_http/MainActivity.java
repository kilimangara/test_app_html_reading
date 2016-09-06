package com.example.asus.test_http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements OnThreadEndedListener {
    SearchView edUrl;
    TextView edMultiLine;
    RequestThread thread;
    Toolbar toolbar;
    OnThreadEndedListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listener = this;
        setUI();
    }

    private void setUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }
        edUrl = (SearchView) findViewById(R.id.search_bar);
        edMultiLine = (TextView) findViewById(R.id.multiline);
        edMultiLine.setMovementMethod(new ScrollingMovementMethod());
        edUrl.setQueryHint("Please enter URL");
        edUrl.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                thread = new RequestThread(query);
                Log.d("mylogs", "trying to search for "+query);
                Thread newThread= new Thread(thread);
                newThread.start();
                thread.getOutPut();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("mylogs","text is changong");
                return false;
            }
        });

    }

    @Override
    public void onThreadEnded(String newstr) {
        edMultiLine.setText(newstr);
    }

    private class RequestThread implements Runnable{
        String output;
        String url;
        public RequestThread(String URL){
            this.url = URL;
        }
        public String getOutPut(){
            return output;
        }
        public void setOutPut(String newString){
            output=newString;
        }
        @Override
        public void run() {
            URL urll;
            StringBuilder result ;
            try{
                urll=new URL(url);
                HttpURLConnection conn=null;
                result=new StringBuilder();
                conn=(HttpURLConnection)urll.openConnection();
                conn.setRequestProperty("Content-Type","//text plain");
                conn.setRequestProperty("Connection", "close");
                conn.setRequestMethod("GET");
                conn.setReadTimeout(250);
                conn.setUseCaches(false);
                conn.setConnectTimeout(250);
                conn.connect();
                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                try{
                    String line;
                    while((line=in.readLine())!=null){
                        result.append(line);
                        result.append("\n");
                    }
                } finally{
                    in.close();
                    conn.disconnect();
                    setOutPut(result.toString());
                    Log.d("mylogs", "thread ended" + getOutPut());
                    listener.onThreadEnded(getOutPut());
                }
            }
            catch(Exception e){
                Log.d("mytags", e.getMessage());
            }

        }

    }
}
