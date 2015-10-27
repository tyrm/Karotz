package com.adafruit.pihat;

import com.adafruit.PCA9685;
import com.adafruit.pihat.impl.MotorHatDCMotorImpl;
import com.pi4j.io.i2c.I2CBus;

import java.io.IOException;

public class MotorHat {
  PCA9685 device = null;

  public MotorHat(I2CBus bus, int address) throws IOException, InterruptedException {
    device = new PCA9685(bus, address);
  }

  public DCMotor getDCMotor(int i) {
    return new MotorHatDCMotorImpl(device, i);
  }
}
