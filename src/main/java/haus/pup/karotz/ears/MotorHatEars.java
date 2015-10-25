package haus.pup.karotz.ears;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.i2c.I2CBus;

public class MotorHatEars {
  GpioPinDigitalOutput rEarR = null;
  GpioPinDigitalOutput rEarL = null;
  GpioPinDigitalOutput lEarR = null;
  GpioPinDigitalOutput lEarL = null;

  GpioPinDigitalInput headBtn = null;

  public MotorHatEars(I2CBus bus) {

  }

  // set shutdown state for this pin
  //pin.setShutdownOptions(true, PinState.LOW);
}
