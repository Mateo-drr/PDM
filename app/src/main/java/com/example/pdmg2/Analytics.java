package com.example.pdmg2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static com.example.pdmg2.R.layout.fragment_analytics;

/**
 * Classe de Análise de Dados
 *
 * Esta classe mostra um gráfico de barras com o registo da temperatura maxima/minima e humidade maxima/minima, dos últimos 5 logs na aplicação.
 */

public class Analytics extends Fragment {

    /**
     * Construtor da Classe Analytics
     */
    public Analytics() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     *  Classe onCreateView
     *
     * Vai buscar os registos à Firebase e gera o gráfico de barras.
     * Passa a visualização do ecrã para o fragmento "fragment_analytics".
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(fragment_analytics, container, false);
        BarChart barChart = (BarChart) view.findViewById(R.id.bargraph);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID= user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        ArrayList<BarEntry> barEntries1 = new ArrayList <>();
        ArrayList<BarEntry> barEntries2 = new ArrayList <>();
        ArrayList<BarEntry> barEntries3 = new ArrayList <>();
        ArrayList<BarEntry> barEntries4 = new ArrayList <>();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {

                    barEntries4.add(new BarEntry(1,userProfile.dia4.hum_max));
                    barEntries4.add(new BarEntry(2,userProfile.dia3.hum_max));
                    barEntries4.add(new BarEntry(3,userProfile.dia2.hum_max));
                    barEntries4.add(new BarEntry(4,userProfile.dia1.hum_max));
                    barEntries4.add(new BarEntry(5,userProfile.dia0.hum_max));

                    barEntries3.add(new BarEntry(6,userProfile.dia4.hum_min));
                    barEntries3.add(new BarEntry(7,userProfile.dia3.hum_min));
                    barEntries3.add(new BarEntry(8,userProfile.dia2.hum_min));
                    barEntries3.add(new BarEntry(9,userProfile.dia1.hum_min));
                    barEntries3.add(new BarEntry(10,userProfile.dia0.hum_min));

                    barEntries2.add(new BarEntry(11,userProfile.dia4.temp_max));
                    barEntries2.add(new BarEntry(12,userProfile.dia3.temp_max));
                    barEntries2.add(new BarEntry(13,userProfile.dia2.temp_max));
                    barEntries2.add(new BarEntry(14,userProfile.dia1.temp_max));
                    barEntries2.add(new BarEntry(15,userProfile.dia0.temp_max));

                    barEntries1.add(new BarEntry(16,userProfile.dia4.temp_min));
                    barEntries1.add(new BarEntry(17,userProfile.dia3.temp_min));
                    barEntries1.add(new BarEntry(18,userProfile.dia2.temp_min));
                    barEntries1.add(new BarEntry(19,userProfile.dia1.temp_min));
                    barEntries1.add(new BarEntry(20,userProfile.dia0.temp_min));

                    BarDataSet barDataSet4 = new BarDataSet(barEntries4, "hum_max");
                    barDataSet4.setColors(ColorTemplate.rgb("3D9140"));
                    BarDataSet barDataSet3 = new BarDataSet(barEntries3, "hum_min");
                    barDataSet3.setColors(ColorTemplate.rgb("7BA37C"));
                    BarDataSet barDataSet2 = new BarDataSet(barEntries2, "temp_max");
                    barDataSet2.setColors(ColorTemplate.rgb("3F51B5"));
                    BarDataSet barDataSet1 = new BarDataSet(barEntries1, "temp_min");
                    barDataSet1.setColors(ColorTemplate.rgb("878CAC"));

                    String[] label = new String[]{" "," ", "4 Logs Ago"," "," "," ", "3 Logs Ago"," "," "," "," ","2 Logs Ago"," "," "," "," ", "1 Log Ago"," "," "," "," ", "Now"};

                    BarData data = new BarData(barDataSet4, barDataSet3, barDataSet2, barDataSet1);
                    barChart.setData(data);
                    data.setValueTextColor(Color.WHITE);
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(label));
                    barChart.getXAxis().setCenterAxisLabels(true);
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getXAxis().setGranularity(1);
                    barChart.getXAxis().setGranularityEnabled(true);
                    barChart.setDragEnabled(true);
                    barChart.getXAxis().setTextColor(Color.WHITE);

                    data.setBarWidth(1f);
                    float barSpace = 0.08f;
                    float groupSpace =0.44f;

                    barChart.getXAxis().setAxisMinimum(0);
                    barChart.getXAxis().setAxisMaximum(barChart.getBarData().getGroupWidth(groupSpace,barSpace)*5);
                    barChart.getAxisLeft().setAxisMinimum(0);
                    barChart.groupBars(0,groupSpace,barSpace);
                    barChart.invalidate();


                    barChart.getXAxis().setDrawGridLines(false);
                    barChart.setDrawGridBackground(false);
                    barChart.setFitBars(true);
                    barChart.getAxisLeft().setTextColor(Color.WHITE); // left y-axis
                    barChart.getAxisRight().setEnabled(false);
                    barChart.getDescription().setEnabled(false);
                    barChart.getLegend().setEnabled(true);
                    barChart.getLegend().setTextColor(Color.WHITE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("","ERRO");
            }
        });
        return view;
    }
}