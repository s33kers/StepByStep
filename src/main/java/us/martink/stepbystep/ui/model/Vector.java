package us.martink.stepbystep.ui.model;

import java.util.StringJoiner;

/**
 * Created by tadas.
 */
public class Vector {

    private static final int q = 2;

    private String vectorText;
    private int[] vector;

    public String getVectorText() {
        return vectorText;
    }

    public void setVectorText(String vectorText) {
        this.vectorText = vectorText;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] vector) {
        this.vector = vector;
    }

    public int getVectorSize() {
        return vector.length;
    }

    /**
     * Vektoriaus vertimas į tekstinį pavidalą
     * @param vector vektorius
     * @param regex vektoriaus elementų atskiriamasis bruožas
     * @return vektorius teksto pavidale
     */
    public static String vectorToString(int[] vector, String regex) {
        StringJoiner stringJoiner = new StringJoiner(regex);
        for (int i : vector) {
            stringJoiner.add(Integer.toString(i));
        }
        return stringJoiner.toString();
    }

    /**
     * Teksto vertimas į vektorių
     * @param vectorText tekstas
     * @return vektorius
     */
    public static int[] textToVector(String vectorText) {
        vectorText = vectorText.replace(" ", "");
        int[] vector = new int[vectorText.length()];
        for (int i = 0; i < vectorText.length(); i++)
        {
            int value = vectorText.charAt(i) - '0';
            vector[i] = value;
        }
        return vector;
    }

    /**
     * Vektoriaus svoris
     * @param vector vektorius
     * @return vektoriaus svoris
     */
    public static int getWeight(int[] vector) {
        int weight = 0;
        for (int i : vector) {
            if (i == 1) {
                weight++;
            }
        }
        return weight;
    }

    /**
     * Vektorių sudėtis
     * @param vector1 pirmas vektorius
     * @param vector2 antras vektorius
     * @return sudėtas vektorius
     */
    public static int[] addVectors(int[] vector1, int[] vector2) {
        int[] result = new int[vector1.length];
        if (vector1.length == vector2.length) {
            for (int i = 0; i < vector1.length; i++) {
                result[i] = (vector1[i] + vector2[i]) % q;
            }
        }
        return result;
    }

    /**
     * Dešimtainis skaičius į vektorių. Pavertus į bitus, jei reikia pridedama priekyje tiek nulių, kad vektoriaus skaičių būtų size
     * @param number skaičius
     * @param size norimas vektoriaus ilgis
     * @return vektorius
     */
    public static int[] intoBinaryArray(int number, int size) {
        String variable = Integer.toBinaryString(number);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size - variable.length(); i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(variable);
        return Vector.textToVector(stringBuilder.toString());
    }

    /**
     * Vektoriaus bito keitimas į jo priešingą
     * @param vector vektorius
     * @param position bito pozicija vektoriuje
     * @return pakeistas vektorius
     */
    public static int[] changeVectorBit(int[] vector, int position){
        if(position < vector.length){
            vector[position] = (vector[position] + 1) % q;
        }
        return vector;
    }

}
