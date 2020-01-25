package com.example.ilovezappos;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{

    TextView tv1;
    LineChart lineChart;
    ArrayList<Entry> data = new ArrayList<Entry>();
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = findViewById(R.id.tv1);

        Retrofit rf = new Retrofit.Builder()
                .baseUrl("https://www.bitstamp.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BitstampTransactionApi bitstampTransactionApi = rf.create(BitstampTransactionApi.class);
        Call<List<Transaction>> transactionCall = bitstampTransactionApi.getTransactions();

        transactionCall.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if(!response.isSuccessful())
                {
                    Toast.makeText(getBaseContext(),"Error:"+response.code(),Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {

                    List<Transaction> transactions = response.body();
                    ArrayList<Long> temp = new ArrayList<>();
                    for(Transaction transaction : transactions)
                    {
                        if(!temp.contains(Long.parseLong(transaction.date)))
                        {
                            temp.add(Long.parseLong(transaction.date));
                            data.add(new Entry(Long.parseLong(transaction.date),Float.parseFloat(transaction.price)));
                            java.util.Date time =new java.util.Date((Long.parseLong(transaction.date))*1000);
                            //System.out.println(time);
                        }
                        Collections.sort(data,Transaction.timeComparator);

                        //Entry entry = new Entry(Long.parseLong(transaction.date),Float.parseFloat(transaction.price));

                    }
                    java.util.Date starttime=new java.util.Date((long)data.get(0).getX()*1000);
                    java.util.Date endtime=new java.util.Date((long)data.get(data.size()-1).getX()*1000);
                    System.out.println(starttime);
                    System.out.println(endtime);
                    lineChart = findViewById(R.id.graph);

                    LineDataSet lineDataSet = new LineDataSet(data,"BTC/USD");
                    lineDataSet.setColors(Color.parseColor("#FF0000"));
                    lineDataSet.setDrawFilled(true);
                    lineDataSet.setFillColor(Color.parseColor("#ffb3b3"));
                    lineDataSet.setCircleColor(Color.parseColor("#ff0000"));
                    lineDataSet.setLineWidth(2);

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet);
                    LineData lineData = new LineData(dataSets);
                    lineChart.setData(lineData);
                    lineChart.getXAxis().setLabelRotationAngle(45);
                    lineChart.getXAxis().setGranularity(5f);
                    lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    lineChart.invalidate();
                }


            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });


        BitstampTickerApi bitstampTickerApi = rf.create(BitstampTickerApi.class);
        Call<Price> tickerCall = bitstampTickerApi.getPrice();
        tickerCall.enqueue(new Callback<Price>() {
            @Override
            public void onResponse(Call<Price> call, Response<Price> response) {
                if(!response.isSuccessful())
                {
                    Log.i(TAG, "onResponse: error");
                    return;
                }
                else
                {
                    tv1.setText("1 BTC = "+response.body().getLast()+" USD");
                }
            }

            @Override
            public void onFailure(Call<Price> call, Throwable t) {

            }
        });


    }

}
