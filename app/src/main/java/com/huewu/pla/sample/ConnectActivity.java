package com.huewu.pla.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dodowaterfall.Helper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ConnectActivity extends Activity {
    StringBuilder sb=new StringBuilder();
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initView();
    }

    private void initView() {
       text=(TextView)findViewById(R.id.text);
        new myAsyncTask().execute();

    }


    class myAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            Connection conn=Jsoup.connect("http://www.jianshu.com/p/7f7f07716176");
            try {
                Document document=conn.get();
                Elements es=document.body().getElementsByClass("show-content");
                Elements es1=es.get(0).getElementsByTag("p");
                for(Element e:es1){
                    sb.append( e.text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            text.setText(sb.toString());
        }
    }
}
