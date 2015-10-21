package com.adafruit.pihat.impl;

import com.adafruit.PCA9685;
import com.adafruit.pihat.DCMotor;

import java.io.IOException;

public class MotorHatDCMotorImpl implements DCMotor {
  PCA9685 device = null;

  int pwm = 0;
  int in1 = 0;
  int in2 = 0;

  public MotorHatDCMotorImpl(PCA9685 _device, int num) {
    device = _device;
    switch (num) {
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
  }

  ;

  public void forward() throws IOException {
    setPin(in1, true);
    setPin(in2, false);
  }

  ;

  public void reverse() throws IOException {
    setPin(in1, false);
    setPin(in2, true);
  }

  ;

  public void release() throws IOException {
    setPin(in1, false);
    setPin(in2, false);
  }

  ;

  public void setSpeed(int speed) throws IOException {
    if (speed < 0) {
      speed = 0;
    }
    if (speed > 255) {
      speed = 255;
    }

    device.setPWM(pwm, 0, speed * 16);
  }

  ;

  private void setPin(int pin, boolean value) throws IOException {
    if (value) {
      device.setPWM(pin, 4096, 0);
    } else {
      device.setPWM(pin, 0, 4096);
    }
  }
}
