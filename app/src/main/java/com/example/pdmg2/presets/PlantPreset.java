package com.example.pdmg2.presets;

import java.io.Serializable;

import static java.lang.String.valueOf;

/**
 * Classe das plantas
 */
public class PlantPreset implements Serializable {
    private int num;
    private int maxTemp;
    private int minTemp;
    private int maxHum;
    private int minHum;
    private int maxLum;
    private int minLum;
    private String name;
    private boolean sw;

    /**
     * Construtor
     * @param num numero da planta
     * @param maxTemp temperatura máxima
     * @param minTemp temperatura mínima
     * @param maxHum humidade máxima
     * @param minHum humidade mínima
     * @param maxLum luminosidade máxima
     * @param minLum luminosidade mínima
     * @param name nome da planta
     * @param sw estado do switch da listview
     */
    public PlantPreset(int num, int maxTemp, int minTemp, int maxHum, int minHum, int maxLum, int minLum, String name, boolean sw) {
        this.num = num;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.maxHum = maxHum;
        this.minHum = minHum;
        this.maxLum = maxLum;
        this.minLum = minLum;
        this.name = name;
        this.sw = sw;
    }

    /**
     * Método para juntar os dados para ser enviados ao dispositivo
     * @return String com os dados separados por vírgulas
     */
    public String getBleData2Tx(){
        return (maxTemp + "," + minTemp + "," + maxHum + "," + minHum + "," + maxLum + "," + minLum);
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public int getMaxHum() {
        return maxHum;
    }

    public void setMaxHum(int maxHum) {
        this.maxHum = maxHum;
    }

    public int getMinHum() {
        return minHum;
    }

    public void setMinHum(int minHum) {
        this.minHum = minHum;
    }

    public int getMaxLum() {
        return maxLum;
    }

    public void setMaxLum(int maxLum) {
        this.maxLum = maxLum;
    }

    public int getMinLum() {
        return minLum;
    }

    public void setMinLum(int minLum) {
        this.minLum = minLum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return //name + ":" +
                "  Temperature: Cº " + maxTemp +
                        " - " + minTemp +
                        "\n  Humidity: %RH " + maxHum +
                        " - " + minHum +
                        "\n  Luminosity lux " + maxLum +
                        " - " + minLum;
    }


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean getSw() {
        return sw;
    }

    public void setSw(boolean sw) {
        this.sw = sw;
    }
}
