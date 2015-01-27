package com.app.parsetest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ActionBarActivity {

    private TextView mainTv;
    private ListView mainLv;

    ArrayList<String> statList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    String TAG = "DEV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTv = (TextView) findViewById(R.id.main_tv);
        mainLv = (ListView) findViewById(R.id.main_lv);

        new NewThread().execute();

        adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.stat_name,statList);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String resultProcessor(String html){

        String resText;

        int start = html.indexOf("<div id=\"cnt\">");
        Log.d(TAG,"start index: " + start);

        html = html.substring(start);

        int firstBr = html.indexOf("<br>");
        Log.d(TAG,"first <br> position: " + firstBr);

        String mainTempBlock = html.substring(0,firstBr);
        String statsBlock = html.substring(firstBr);
        Log.d(TAG,mainTempBlock);

        int main_start = mainTempBlock.indexOf("<strong>");
        int main_end = mainTempBlock.indexOf("</strong>");

        String mainTemp = mainTempBlock.substring(main_start + 8, main_end);

        Log.d(TAG,statsBlock);

        int stat_end = statsBlock.indexOf("<img");
        Log.d(TAG,"stat block end: " + stat_end);
        statsBlock = statsBlock.substring(0,stat_end);
        Log.d(TAG,statsBlock);

        String[] temp_arr = statsBlock.split("<br>");

        for (int i = 0; i < temp_arr.length; i++){
            temp_arr[i] = temp_arr[i].replaceAll("<b>","").replaceAll("</b>","");
            if (temp_arr[i] != null && !temp_arr[i].trim().equals(""))
                statList.add(temp_arr[i]);
            Log.d(TAG,temp_arr[i]);
        }

        resText = mainTemp;

        return resText;
    }

    public class NewThread extends AsyncTask<String, Void, String>{

        String str;

        @Override
        protected String doInBackground(String... params) {

            Document doc;

            try {
                doc = Jsoup.connect("http://btlt.ru/info/temperature").get();
                str = doc.html();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mainTv.setText(resultProcessor(str));
            mainLv.setAdapter(adapter);
        }
    }
}
