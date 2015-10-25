package haus.pup;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import haus.pup.karotz.Ear;
import haus.pup.karotz.Ears;
import haus.pup.karotz.Speech;
import haus.pup.karotz.ears.MotorHatEars;
import haus.pup.karotz.speech.IvonaSpeech;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Karotz {
  public static void main(String args[]) throws IOException, InterruptedException {
    // Init Logging
    Logger logger = LoggerFactory.getLogger("Karotz");

    // Enable spoken debugging
    Boolean vocalDebug = false;

    if (Objects.equals(System.getProperty("vocaldebug"), "true")) {
      vocalDebug = true;
    }
    logger.info("Vocal Debugger: " + vocalDebug.toString());

    // Init DB
    String dbURI = "jdbc:h2:" + new File("karotz").getAbsolutePath();
    logger.info("Connecting to DB at :" + dbURI);

    Connection db = null;
    try {
      db = DriverManager.getConnection(dbURI);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (db != null) {
      logger.info("Connected to " + dbURI);
    } else {
      logger.error("Failed to make connection!");
    }

    // Init IvonaSpeech
    Speech speech = new IvonaSpeech();
    if (vocalDebug) {
      speech.say("Speech Initialized");
    }

    // Init RasPi Hardware
    GpioController gpio = GpioFactory.getInstance();

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

    ears.home(Ear.BOTH);

    i2cbus.close();
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
