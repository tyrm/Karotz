package haus.pup.karotz.ears;

import com.adafruit.pihat.DCMotor;
import com.adafruit.pihat.MotorHat;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.i2c.I2CBus;
import haus.pup.karotz.Ear;
import haus.pup.karotz.Ears;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MotorHatEars implements Ears {
  MotorHat motorHat = null;

  DCMotor rightEar = null;
  DCMotor leftEar = null;

  GpioPinDigitalInput rightEncoder = null;
  GpioPinDigitalInput leftEncoder = null;

  public MotorHatEars(I2CBus bus, int address, GpioPinDigitalInput _rightEncoder, GpioPinDigitalInput _leftEncoder) throws IOException, InterruptedException {
    motorHat = new MotorHat(bus, address);

    rightEar = motorHat.getDCMotor(0);
    leftEar = motorHat.getDCMotor(1);

    rightEar.setSpeed(255);
    leftEar.setSpeed(255);

    rightEncoder = _rightEncoder;
    leftEncoder = _leftEncoder;
  }

  public void home(Ear ear) throws IOException, InterruptedException {
    rightEar.forward();

    TimeUnit.SECONDS.sleep(5); // wait for oscillator

    //rightEar.release();

  }

  public void moveForward(Ear ear, int steps) {

  }

  public void moveReverse(Ear ear, int steps) {

  }

  public void moveTo(Ear ear, int position) {

  }

  public void moveForwardTo(Ear ear, int position) {

  }

  public void moveReverseTo(Ear ear, int position) {

  }

  public int getRightPosition() {
    return 0;
  }

  public int getLeftPosition() {
    return 0;
  }

  // set shutdown state for this pin
  //pin.setShutdownOptions(true, PinState.LOW);
}
