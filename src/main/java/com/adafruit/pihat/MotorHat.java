package com.adafruit.pihat;

import com.adafruit.pihat.impl.MotorHatDCMotorImpl;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

public class MotorHat {
  I2CDevice device = null;

  public MotorHat(I2CBus b, int a) throws IOException {
    device = b.getDevice(a);
  }

  public DCMotor getDCMotor(int i){
    return new MotorHatDCMotorImpl(device, i);
  }
}
