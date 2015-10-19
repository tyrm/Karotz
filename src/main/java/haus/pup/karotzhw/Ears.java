package haus.pup.karotzhw;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.PinPullResistance;

public class Ears {
  GpioPinDigitalOutput rEarR = null;
  GpioPinDigitalOutput rEarL = null;
  GpioPinDigitalOutput lEarR = null;
  GpioPinDigitalOutput lEarL = null;

  GpioPinDigitalInput headBtn = null;

  public Ears(GpioController gpio){
    rEarR = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);
    rEarL = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyLED", PinState.LOW);
    lEarR = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "MyLED", PinState.LOW);
    lEarL = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyLED", PinState.LOW);

    headBtn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);

  }

  // set shutdown state for this pin
  //pin.setShutdownOptions(true, PinState.LOW);
}
