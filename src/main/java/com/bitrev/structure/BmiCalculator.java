/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitrev.structure;

/**
 *
 * @author Ian Kipchirchir <potterke4@gmail.com>
 */
public class BmiCalculator {
    private double height; //In meters
    private double weight; //In kilograms
    private double result;
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setResult(double result) {
        this.result = result;
    }
    
}
