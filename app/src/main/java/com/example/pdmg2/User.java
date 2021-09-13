package com.example.pdmg2;

import java.util.Calendar;
import java.util.List;

/**
 * Class User
 *
 * É uma classe para gerir o utilizador e sincronizar com a Firebase.
 */
public class User {

    public String name, email;
    public int last_log_day;
    public int last_log_month;
    public int last_log_year;
    Dia4 dia4;
    Dia3 dia3;
    Dia2 dia2;
    Dia1 dia1;
    Dia0 dia0;

    /**
     * Construtor da classe User
     */
    public User(){

    }

    /**
     * Esquema de Utilizador
     *
     * @param name - nome do utilizador
     * @param email - email do utilizador
     * @param last_log_day - ultimo dia que fez log na aplicação
     * @param last_log_month - ultimo mês que fez log na aplicação
     * @param last_log_year - ultimo ano que fez log na aplicação
     * @param dia4 - registos de hà 4 logs atrás
     * @param dia3 - registos de hà 3 logs atrás
     * @param dia2 - registos de hà 4 logs atrás
     * @param dia1 - registos de hà 1 log atrás
     * @param dia0 - registos atuais
     */
    public User(String name, String email,int last_log_day,int last_log_month,int last_log_year, Dia4 dia4, Dia3 dia3, Dia2 dia2, Dia1 dia1, Dia0 dia0){
        this.name = name;
        this.email = email;
        this.last_log_day = last_log_day;
        this.last_log_month = last_log_month;
        this.last_log_year = last_log_year;
        this.dia4 = dia4;
        this.dia3 = dia3;
        this.dia2 = dia2;
        this.dia1 = dia1;
        this.dia0 = dia0;
    }

    /**
     * Método para aceder à classe Dia4
     * @return dia4
     */
    public Dia4 getDia4() {
        return dia4;
    }

    /**
     * Método para aceder à classe Dia3
     * @return dia3
     */
    public Dia3 getDia3() {
        return dia3;
    }

    /**
     * Método para aceder à classe Dia2
     * @return dia2
     */
    public Dia2 getDia2() {
        return dia2;
    }

    /**
     * Método para aceder à classe Dia1
     * @return dia1
     */
    public Dia1 getDia1() {
        return dia1;
    }

    /**
     * Método para aceder à classe Dia0
     * @return dia0
     */
    public Dia0 getDia0() {
        return dia0;
    }

    /**
     * Classe Dia4
     *
     * Guarda os registos de há 4 logs atrás
     */
    public static class Dia4{
        public int temp_max;
        public int temp_min;
        public int hum_max;
        public int hum_min;

        /**
         * Construtor da classe Dia4
         */
        public Dia4(){
        }
        /**
         * Esquema da classe Dia4
         * @param temp_max - temperatura máxima
         * @param temp_min - temperatura minima
         * @param hum_max - humidade máxima
         * @param hum_min - humidade minima
         */
        public Dia4(int temp_max,int temp_min,int hum_max, int hum_min){
            this.temp_max =temp_max;
            this.temp_min =temp_min;
            this.hum_max =hum_max;
            this.hum_min =hum_min;
        }
    }

    /**
     * Classe Dia3
     *
     * Guarda os registos de há 3 logs atrás
     */
    public static class Dia3{
        public int temp_max;
        public int temp_min;
        public int hum_max;
        public int hum_min;

        public Dia3(){

        }
        /**
         * Esquema da classe Dia3
         * @param temp_max - temperatura máxima
         * @param temp_min - temperatura minima
         * @param hum_max - humidade máxima
         * @param hum_min - humidade minima
         */
        public Dia3(int temp_max,int temp_min,int hum_max, int hum_min){
            this.temp_max =temp_max;
            this.temp_min =temp_min;
            this.hum_max =hum_max;
            this.hum_min =hum_min;
        }
    }

    /**
     * Classe Dia2
     *
     * Guarda os registos de há 2 logs atrás
     */
    public static class Dia2{
        public int temp_max;
        public int temp_min;
        public int hum_max;
        public int hum_min;

        public Dia2(){

        }
        /**
         * Esquema da classe Dia2
         * @param temp_max - temperatura máxima
         * @param temp_min - temperatura minima
         * @param hum_max - humidade máxima
         * @param hum_min - humidade minima
         */
        public Dia2(int temp_max,int temp_min,int hum_max, int hum_min){
            this.temp_max =temp_max;
            this.temp_min =temp_min;
            this.hum_max =hum_max;
            this.hum_min =hum_min;
        }
    }

    /**
     * Classe Dia1
     *
     * Guarda os registos de há 1 log atrás
     */
    public static class Dia1{
        public int temp_max;
        public int temp_min;
        public int hum_max;
        public int hum_min;


        /**
         * Construtor da classe Dia1
         */
        public Dia1(){

        }
        /**
         * Esquema da classe Dia1
         * @param temp_max - temperatura máxima
         * @param temp_min - temperatura minima
         * @param hum_max - humidade máxima
         * @param hum_min - humidade minima
         */
        public Dia1(int temp_max,int temp_min,int hum_max, int hum_min){
            this.temp_max =temp_max;
            this.temp_min =temp_min;
            this.hum_max =hum_max;
            this.hum_min =hum_min;
        }
    }

    /**
     * Classe Dia0
     *
     * Guarda os registos atuais
     */
    public static class Dia0{
        public int temp_max;
        public int temp_min;
        public int hum_max;
        public int hum_min;

        /**
         * Construtor da classe Dia0
         */
        public Dia0(){
        }
        /**
         * Esquema da classe Dia0
         * @param temp_max - temperatura máxima
         * @param temp_min - temperatura minima
         * @param hum_max - humidade máxima
         * @param hum_min - humidade minima
         */
        public Dia0(int temp_max,int temp_min,int hum_max, int hum_min){
            this.temp_max =temp_max;
            this.temp_min =temp_min;
            this.hum_max =hum_max;
            this.hum_min =hum_min;
        }
    }
}
