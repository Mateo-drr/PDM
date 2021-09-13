package com.example.pdmg2.presets;

import android.content.Context;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe para controlar os dados do arraylist das plantas
 */
public class GestorPlantas implements Serializable {
    private ArrayList<PlantPreset> plants = new ArrayList<>();

    /**
     * Cria os presets das plantas
     */
    public void initPessoas() {
        plants.add(new PlantPreset(1, 50, 5, 90, 10, 200, 1, "Default preset", true));
        plants.add(new PlantPreset(2, 24, 13, 50, 10, 10, 1, "Carrots", false));
        plants.add(new PlantPreset(3, 13, 7, 50, 10, 10, 1, "Potatoes", false));
        plants.add(new PlantPreset(4, 18, 16, 50, 10, 10, 1, "Lettuce", false));
        plants.add(new PlantPreset(5, 27, 21, 50, 10, 10, 1, "Bell Peppers", false));
        plants.add(new PlantPreset(6, 18, 16, 50, 10, 10, 1, "Radishes", false));
        plants.add(new PlantPreset(7, 21, 4, 50, 10, 10, 1, "Broccoli", false));
        plants.add(new PlantPreset(8, 21, 10, 50, 10, 10, 1, "Beetroots", false));
        plants.add(new PlantPreset(9, 16, 13, 50, 10, 10, 1, "Champignon", false));
        plants.add(new PlantPreset(10, 24, 13, 50, 10, 10, 1, "Tomatoes", false));
    }

    /**
     * substitui a planta default preset por os novos máximos e mínimos da CustomPresetActivity
     * @param p objeto PlantPreset com os novos valores
     */
    public void setcustom(PlantPreset p) {
        plants.set(0,p);
    }

    public ArrayList<PlantPreset> getListaPlantas() {
        return plants;
    }

    /**
     * Salva os dados dos presets no armazenamento interno
     * @param context
     */
    public void gravarFicheiro(Context context) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("plants.bin", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(plants);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            Toast.makeText(context, "Could not write people data to internal storage.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Lê os ficheiros
     * @param context
     */
    public void lerFicheiro(Context context) {
        try {
            FileInputStream fileInputStream =
                    context.openFileInput("plants.bin");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            plants = (ArrayList<PlantPreset>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Could not read people data from internal storage.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error reading people data from internal storage.", Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "Error reading people data from internal storage.", Toast.LENGTH_LONG).show();
        }
    }
}