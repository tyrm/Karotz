package haus.pup;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import haus.pup.karotzhw.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Karotz {
  public static void main(String args[]) {
    String dbURI = "jdbc:h2:" + new File("karotz").getAbsolutePath();
    Logger logger = LoggerFactory.getLogger("Karotz");

    // Conncet to DB
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

    //final GpioController gpio = GpioFactory.getInstance();

    Speech speech = new Speech();
    //Ears ears = new Ears(gpio);

    speech.say("Well I guess that makes you a couple of naughty stewardeses.");

  }
}
