package com.example.pdmg2.presets;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.pdmg2.R;

import java.util.ArrayList;

/**
 * ListViewAdapter para poder editar os conteudos de cada elemento da listview,
 * Aqui são atribuidas as diferentes imagens a cada item
 */
public class ListViewAdapter extends BaseAdapter {

    private ArrayList<PlantPreset> plants;
    private Context context;
    public ListViewAdapter(ArrayList<PlantPreset> plants, Context context) {
        this.plants = plants;
        this.context = context;

    }

    @Override
    public int getCount() {
        return plants.size();
    }
    @Override
    public Object getItem(int position) {
        return plants.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_layout, parent, false);
        }
        // get current item to be displayed
        PlantPreset currentItem = (PlantPreset) getItem(position);
        // get the TextView for item number and item name
        TextView textViewItemNumber =
                convertView.findViewById(R.id.textViewItemNum);
        TextView textViewItemName =
                convertView.findViewById(R.id.textViewItemName);
        ImageView imageView = convertView.findViewById(R.id.imageViewItemPhoto);
        //sets the text for item number and item name from the current item object

        textViewItemNumber.setText(currentItem.toString());
        textViewItemName.setText(currentItem.getName());

        Switch sw = convertView.findViewById(R.id.sw_preset);
        sw.setChecked(currentItem.getSw());

        if (currentItem.getNum() == 2){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.carrots));
        } else if (currentItem.getNum() == 3){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.potatoes));
        }else if (currentItem.getNum() == 4){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.lettuce));
        }else if (currentItem.getNum() == 5){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.pepper));
        }else if (currentItem.getNum() == 6){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.radish));
        }else if (currentItem.getNum() == 7){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.broccoli));
        }else if (currentItem.getNum() == 8){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.beetroot));
        }else if (currentItem.getNum() == 9){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mush));
        }else if (currentItem.getNum() == 10){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tomato));
        }else
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_foreground));
        // returns the view for the current row
        return convertView;
    }

}
