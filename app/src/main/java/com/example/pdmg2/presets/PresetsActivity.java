package com.example.pdmg2.presets;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pdmg2.R;

import java.io.Serializable;

/**
 * Activity para mostrar a listview dos diferentes presets
 */
public class PresetsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, Serializable {

    private GestorPlantas gp;
    private ListView lv;
    private ListViewAdapter adapter;
    private boolean sw;
    private String bletxdata;

    /**
     * Coloca os dados na listview, do armazenamento interno, se existirem
     * Coloca tambem os onClicklisteners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        if (savedInstanceState == null) {
            gp = new GestorPlantas();
            gp.lerFicheiro(this);
        } else {
            this.gp = (GestorPlantas)
                    savedInstanceState.getSerializable("estado");
        }
        if (gp.getListaPlantas().isEmpty()) {
            gp.initPessoas();
        }

        lv = findViewById(R.id.listview);
        adapter = new ListViewAdapter(gp.getListaPlantas(), this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    /**
     * Salva os dados dentro do ficheiro se a activity entra no estado onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("","onPause");
        gp.gravarFicheiro(this);
    }

    /**
     * Metodo que permite que se a seta do navigation menu é carregada o onBackPressed é chamado
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Quando volta a MainActivity é verificado qual dos items da listview tem o switch ativo,
     * e são obtidos os limites para enviar ao dispositivo
     */
    @Override
    public void onBackPressed() {
        for (int i = 0; i < gp.getListaPlantas().size(); i++) {
            if (gp.getListaPlantas().get(i).getSw()){
                bletxdata = gp.getListaPlantas().get(i).getBleData2Tx();
                break;
            }
        }

        Intent data = new Intent();
        data.putExtra("pre", bletxdata);
        setResult(Activity.RESULT_OK, data);
        Log.d("","back");
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("estado", gp);
    }

    /**
     * Método para receber o objeto da CustomActivityPreset e atualizar a listview
     * @param requestCode numero com que foi iniciada a activity, neste caso = 1
     * @param resultCode Código de succeso da operação = RESULT_OK = -1
     * @param returnIntent Intent utilizado na CustomPresetActivity para voltar a PresetActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("","gp.set");
                PlantPreset p = (PlantPreset) returnIntent.getSerializableExtra("plant");
                gp.setcustom(p);
                bletxdata = gp.getListaPlantas().get(0).getBleData2Tx();
                adapter.notifyDataSetChanged();
                //Toast.makeText(this, bletxdata, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * onClick dos itens da listview. So pode existir um switch ativado, entao se o utilizador tenta
     * ativar um segundo switch o switch anterior é desativado.
     * Se o primeiro item da listview está ativado e é carregado novamente um alert dialog é criado,
     * preguntando se o utilizador quer criar um preset personalizado. Se sim, é iniciada a CustomPresetActivity
     * se não, o click é ignorado.
     * @param parent
     * @param view
     * @param position item da lista
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //switch on and first item
        if(gp.getListaPlantas().get(position).getSw() && position == 0){
            //Criar um alert dialog
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle(getString(R.string.Custm_p_q));
            ad.setButton(Dialog.BUTTON_POSITIVE, "OK", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent aa = new Intent(PresetsActivity.this, CustomPresetActivity.class);
                    startActivityForResult(aa, 1);
                }
            });
            //Ao selecionar Cancel, nao faz nada
            ad.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", null,null);
            ad.show();
        }
        //keep default on if nothing else is selected
        if(gp.getListaPlantas().get(position).getSw() && position != 0){
            gp.getListaPlantas().get(position).setSw(false);
            gp.getListaPlantas().get(0).setSw(true);
            bletxdata = gp.getListaPlantas().get(0).getBleData2Tx();
        }else {
            gp.getListaPlantas().get(position).setSw(true);
            bletxdata = gp.getListaPlantas().get(position).getBleData2Tx();
            for (int i = 0; i < gp.getListaPlantas().size(); i++) {
                if (i != position) {
                    gp.getListaPlantas().get(i).setSw(false);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}