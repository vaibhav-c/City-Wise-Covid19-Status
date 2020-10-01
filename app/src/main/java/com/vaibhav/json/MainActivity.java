package com.vaibhav.json;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button button;
    TextView active;
    TextView total;
    TextView deceased;
    TextView recovered;
    TextView yesterday;
    TextView deltainc;
    TextView deltadec;
    TextView deltadead;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        active = findViewById(R.id.active);
        total = findViewById(R.id.total);
        deceased = findViewById(R.id.deceased);
        recovered = findViewById(R.id.recovered);
        deltainc = findViewById(R.id.deltainc);
        deltadec = findViewById(R.id.deltadec);
        deltadead = findViewById(R.id.deltadead);
        yesterday = findViewById(R.id.yesterday);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String city, state;
                EditText stateName = (EditText)findViewById(R.id.statename);
                EditText cityName = (EditText)findViewById(R.id.cityname);
                state = stateName.getText().toString();
                city = cityName.getText().toString();
                SharedPreferences shrdState = getSharedPreferences("demo", MODE_PRIVATE);
                SharedPreferences shrdCity = getSharedPreferences("demo", MODE_PRIVATE);
                SharedPreferences.Editor edtState = shrdState.edit();
                SharedPreferences.Editor edtCity = shrdCity.edit();
                edtCity.putString("city", city);
                edtState.putString("state", state);
                edtCity.apply();
                edtState.apply();
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        "https://api.covid19india.org/state_district_wise.json", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            textView.setText("");
                            JSONObject jObject= response.getJSONObject(state);
                            JSONObject inner = jObject.getJSONObject("districtData");
                            JSONObject innermost = inner.getJSONObject(city);
                            JSONObject inners = innermost.getJSONObject("delta");
                            active.setText("Active cases: " + innermost.getString("active"));
                            total.setText("Confirmed: " + innermost.getString("confirmed"));
                            recovered.setText("Recovered: " + innermost.getString("recovered"));
                            deceased.setText("Deceased: " + innermost.getString("deceased"));
                            deltainc.setText("Cases: " + inners.getString("confirmed"));
                            deltadec.setText("Recovered: " + inners.getString("recovered"));
                            deltadead.setText("Deceased: " + inners.getString("deceased"));
                            yesterday.setText("Yesterday");
                        } catch (JSONException e){
                            e.printStackTrace();
                            textView.setText("Error. Enter again.");
                            active.setText("");
                            total.setText("");
                            recovered.setText("");
                            deceased.setText("");
                            deltainc.setText("");
                            deltadead.setText("");
                            deltadec.setText("");
                            yesterday.setText("");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Fine", "Everything went wrong");
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }
        });
        SharedPreferences shared = getSharedPreferences("demo", MODE_PRIVATE);
        String val = shared.getString("city", "");
        String val1 = shared.getString("state", "");
        EditText stateName = (EditText)findViewById(R.id.statename);
        EditText cityName = (EditText)findViewById(R.id.cityname);
        stateName.setText(val1);
        cityName.setText(val);
    }
}