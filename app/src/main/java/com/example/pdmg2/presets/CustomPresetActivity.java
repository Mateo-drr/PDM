package com.example.pdmg2.presets;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdmg2.R;

import java.io.Serializable;

/**
 * Activiy para editar os limites maximos e minimos da temperatura, humidade e luminosidade,
 * devolve os dados ao PresetActivity para atualizar a listview
 */
public class CustomPresetActivity extends AppCompatActivity implements Serializable {

    private TextView txttempmax;
    private TextView txttempmin;
    private TextView txthummax;
    private TextView txthummin;
    private TextView txtlummax;
    private TextView txtlummin;

    /**
     * onCreate. Obtém os diferentes textviews
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_preset);
        txttempmax = findViewById(R.id.txtv_cp_maxtemp);
        txttempmin = findViewById(R.id.txtv_cp_mintemp);
        txthummax = findViewById(R.id.txtv_cp_maxhum);
        txthummin = findViewById(R.id.txtv_cp_minhum);
        txtlummax = findViewById(R.id.txtv_cp_maxlum);
        txtlummin = findViewById(R.id.txtv_cp_minlum);
    }

    /**
     * onClick do botão save, verifica se todos os campos foram preenchidos, e envia o novo objeto
     * 'plant' com os novos máximos e mínimos à PresetActivity
     * @param view
     */
    public void onClickCPSave(View view) {

        if(txttempmax.getText().toString().isEmpty() ||
                txttempmin.getText().toString().isEmpty() ||
                txthummax.getText().toString().isEmpty() ||
                txthummin.getText().toString().isEmpty() ||
                txtlummax.getText().toString().isEmpty() ||
                txtlummin.getText().toString().isEmpty())
        {
            Toast.makeText(this,getString(R.string.Fill_all), Toast.LENGTH_SHORT).show();
        }else{
            if(Integer.parseInt(txttempmax.getText().toString()) > Integer.parseInt(txttempmin.getText().toString()) &&
                    Integer.parseInt(txthummax.getText().toString()) > Integer.parseInt(txthummin.getText().toString()) &&
                    Integer.parseInt(txtlummax.getText().toString()) > Integer.parseInt(txtlummin.getText().toString()))
            {
                PlantPreset plant = new PlantPreset(1, Integer.parseInt(txttempmax.getText().toString()),
                Integer.parseInt(txttempmin.getText().toString()),
                Integer.parseInt(txthummax.getText().toString()),
                Integer.parseInt(txthummin.getText().toString()),
                Integer.parseInt(txtlummax.getText().toString()),
                Integer.parseInt(txtlummin.getText().toString()), getString(R.string.Custm_p), true);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("plant", plant);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }else
                Toast.makeText(this, getString(R.string.MaxMin_err), Toast.LENGTH_SHORT).show();
        }
    }
}