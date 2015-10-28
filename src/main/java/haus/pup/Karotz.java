package haus.pup;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import haus.pup.karotz.Ear;
import haus.pup.karotz.Ears;
import haus.pup.karotz.Speech;
import haus.pup.karotz.ears.MotorHatEars;
import haus.pup.karotz.speech.IvonaSpeech;

import java.io.IOException;
import java.util.Objects;

import org.bson.Document;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Karotz {
  public static void main(String args[]) throws IOException, InterruptedException {
    // Init Logging
    final Logger logger = LoggerFactory.getLogger("Karotz");

    // Enable spoken debugging
    Boolean vocalDebug = false;

    if (Objects.equals(System.getProperty("vocaldebug"), "true")) {
      vocalDebug = true;
    }
    logger.info("Vocal Debugger: " + vocalDebug.toString());

    // Init DB
    MongoClient mongoClient = new MongoClient();
    MongoDatabase db = mongoClient.getDatabase("karotz");

    MongoCollection<Document> coll =  db.getCollection("testCollection");

    // Init IvonaSpeech
    Speech speech = new IvonaSpeech();
    if (vocalDebug) {
      speech.say("Speech Initialized");
    }

    // Init RasPi Hardware
    final GpioController gpio = GpioFactory.getInstance();

    I2CBus i2cbus = null;
    try {
      i2cbus = I2CFactory.getInstance(1);
    } catch (IOException e) {
      speech.say("I could not open i squared c bus 1");
      logger.error("Could not open I2C bus 1");
      e.printStackTrace();
    }

    Ears ears = null;
    try {
       ears = new MotorHatEars(i2cbus, 0x60, null, null);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    greet(speech);


    // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
    final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);

    // create and register gpio pin listener
    myButton.addListener(new GpioPinListenerDigital() {
      @Override
      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        // display pin state on console
        logger.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
      }

    });

    ears.home(Ear.BOTH);

    for (;;) {
      Thread.sleep(500);
    }

  }

  private static void greet(Speech s) {
    DateTime dt = new DateTime();

    if (dt.getHourOfDay() < 12) {
      s.say("Good Morning");
    } else if (dt.getHourOfDay() > 11 && dt.getHourOfDay() < 17) {
      s.say("Good Afternoon");
    } else {
      s.say("Good Evening");
    }

    s.say("I am ready to work.");

  }
}
