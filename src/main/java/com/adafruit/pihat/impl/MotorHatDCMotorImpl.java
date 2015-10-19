package com.adafruit.pihat.impl;

import com.adafruit.pihat.DCMotor;
import com.pi4j.io.i2c.I2CDevice;

public class MotorHatDCMotorImpl implements DCMotor {
  I2CDevice device = null;

  int pwm = 0;
  int in1 = 0;
  int in2 = 0;

  public MotorHatDCMotorImpl(I2CDevice d, int i) {
    device = d;
    switch(i){
      case 0:
        pwm = 8;
        in2 = 9;
        in1 = 10;
        break;
      case 1:
        pwm = 13;
        in2 = 12;
        in1 = 11;
        break;
      case 2:
        pwm = 2;
        in2 = 3;
        in1 = 4;
        break;
      case 3:
        pwm = 7;
        in2 = 6;
        in1 = 5;
        break;
    }
  };

  public void forward(){

  };

  public void reverse(){

  };

  public void release(){

  };

  public void setSpeed(int speed){

  };
}
