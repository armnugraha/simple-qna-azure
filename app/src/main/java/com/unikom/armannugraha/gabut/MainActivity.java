package com.unikom.armannugraha.gabut;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button mButton;

    ListView listview;
    ListView listviewAnswer;
    Button Addbutton;
    EditText GetValue;
    String[] ListElements = new String[] {};
    String[] ListAnswers = new String[] {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listViewQuestion);
        GetValue = (EditText)findViewById(R.id.chatEditText);

        final List<String> ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (MainActivity.this, android.R.layout.simple_list_item_1, ListElementsArrayList);

        listview.setAdapter(adapter);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(GetValue.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Input Text Is Empty.. Please Enter Some Text", Toast.LENGTH_SHORT).show();
                } else {
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute();

                    ListElementsArrayList.add(GetValue.getText().toString());

                    adapter.notifyDataSetChanged();
                }

            }
        });

    }

    // Create GetText Metod
    public void GetText() throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;
        EditText chatText = (EditText)findViewById(R.id.chatEditText);

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://armanqna.azurewebsites.net/qnamaker/knowledgebases/de2d73ee-7b73-43dd-81a6-0555e4a5475f/generateAnswer");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "EndpointKey d7480eef-b004-4458-931a-e77caa1fa412");
            conn.setRequestProperty("Content-Type", "application/json");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("question", chatText.getText().toString());


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("karma", "json is " + jsonParam);

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }

//            listviewAnswer = (ListView) findViewById(R.id.listViewAnswers);
//
//            final List<String> ListViewAnswers = new ArrayList<String>(Arrays.asList(ListAnswers));
//
//            final ArrayAdapter<String> adapterAnswer = new ArrayAdapter<String>
//                    (MainActivity.this, android.R.layout.simple_list_item_1, ListViewAnswers);
//
//            listviewAnswer.setAdapter(adapterAnswer);
//
//            ListViewAnswers.add("haha");

            text = sb.toString();
//            get object pertama (answers)
            JSONObject obj = new JSONObject(text);

//            get array pada object answers
            JSONArray arr = new JSONArray(obj.getString("answers"));
            JSONObject jObj = arr.getJSONObject(0);

            Log.d("karma ", "response is " + jObj.getString("answer"));

            final String hasilJawaban = jObj.getString("answer");

            listviewAnswer = (ListView) findViewById(R.id.listViewAnswers);

            final List<String> ListViewAnswers = new ArrayList<String>(Arrays.asList(ListAnswers));

            final ArrayAdapter<String> adapterAnswer = new ArrayAdapter<String>
                    (MainActivity.this, android.R.layout.simple_list_item_1, ListViewAnswers);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    listviewAnswer.setAdapter(adapterAnswer);

                    ListViewAnswers.add(hasilJawaban);

                    adapterAnswer.notifyDataSetChanged();
                }
            });

        } catch (Exception ex) {
//            Log.d("karma", "exception at last " + ex);
            Toast.makeText(getApplicationContext(),"Excep" + ex,Toast.LENGTH_SHORT).show();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
        }


    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("karma", "called");
                GetText();
                Log.d("karma", "after called");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("karma", "Exception occurred " + e);
            }

            return null;
        }
    }

}