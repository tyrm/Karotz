package haus.pup;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import haus.pup.karotzhw.*;

public class Karotz {
  public static void main(String args[]) {

    //final GpioController gpio = GpioFactory.getInstance();

    Speech speech = new Speech();
    //Ears ears = new Ears(gpio);

    speech.say("Well I guess that makes you a couple of naughty stewardeses.");

  }
}
