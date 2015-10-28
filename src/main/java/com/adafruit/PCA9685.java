package com.adafruit;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adafruit PCA9685 16-Channel PWM Servo Driver
 * <p>
 * Java Implementation of Adafruit's Adafruit_PWM_Servo_Driver.py
 * https://github.com/adafruit/Adafruit-Motor-HAT-Python-Library/blob/master/Adafruit_MotorHAT/Adafruit_PWM_Servo_Driver.py
 */
public class PCA9685 {
  Logger logger;
  I2CDevice device;

  // Registers/etc
  static final byte MODE1 = (byte) 0x00;
  static final byte MODE2 = (byte) 0x01;
  //static final byte SUBADR1 = (byte) 0x02;
  //static final byte SUBADR2 = (byte) 0x03;
  //static final byte SUBADR3 = (byte) 0x04;
  static final byte PRESCALE = (byte) 0xfe;
  static final byte LED0 = (byte) 0x06;
  static final byte ALL_LED = (byte) 0xfa;

  // Bits
  //static final byte RESTART = (byte) 0x80;
  static final byte SLEEP = (byte) 0x10;
  static final byte ALLCALL = (byte) 0x01;
  //static final byte INVRT = (byte) 0x10;
  static final byte OUTDRV = (byte) 0x04;
  static final byte SWRST = (byte) 0x06;

  public PCA9685(I2CBus bus, int address) throws InterruptedException, IOException {
    device = bus.getDevice(address);
    logger = LoggerFactory.getLogger("i2c.PCA9685." + address);


    byte[] oldmode = new byte[1];

    try {
      int r = device.read(MODE1, oldmode, 0, oldmode.length);
      logger.debug("Starting Mode: " + oldmode[0]);
    } catch (IOException e) {
      logger.error("failed to read [" + asHex(MODE1) + "]", e);
    }

    logger.info("Reseting PCA9685 MODE1 (without SLEEP) and MODE2");
    setAllPWM(0, 0);

    try {
      device.write(MODE2, OUTDRV);
      logger.debug("write [" + asHex(MODE2) + "][" + asHex(OUTDRV) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex(MODE2) + "][" + asHex(OUTDRV) + "]", e);
    }

    try {
      device.write(MODE1, ALLCALL);
      logger.debug("write [" + asHex(MODE1) + "][" + asHex(ALLCALL) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex(MODE1) + "][" + asHex(ALLCALL) + "]", e);
    }
    TimeUnit.MILLISECONDS.sleep(50); // wait for oscillator


    byte[] buffer = new byte[1];
    try {
      int r = device.read(MODE1, buffer, 0, buffer.length);
      logger.debug("read [" + asHex(MODE1) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex(MODE1) + "][" + asHex(buffer) + "]", e);
    }
    buffer[0] = (byte) (buffer[0] & ~SLEEP); // wake up (reset sleep)
    try {
      device.write(MODE1, buffer[0]);
      logger.debug("write [" + asHex(MODE1) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex(MODE1) + "][" + asHex(buffer) + "]", e);
    }
    TimeUnit.MILLISECONDS.sleep(50); // wait for oscillator
  }

  /**
   * Sends a software reset (SWRST) command to all the servo drivers on the bus
   *
   * @throws IOException
   */
  public void softwareReset() throws IOException {
    device.write(0, SWRST);
    logger.info("Sending reset");
  }

  /**
   * Sets the PWM frequency
   *
   * @param freq
   * @throws IOException
   * @throws InterruptedException
   */

  public void setPWMFreq(double freq) throws IOException, InterruptedException {
    double prescaleval = 25000000.0; // 25MHz
    prescaleval /= 4096.0; // 12-bit
    prescaleval /= freq;
    prescaleval -= 1.0;
    logger.debug("Setting PWM frequency to " + freq + " Hz");
    logger.debug("Estimated pre-scale: " + prescaleval);
    double prescale = Math.floor(prescaleval + 0.5);
    logger.debug("Final pre-scale: " + prescale);

    byte[] oldmode = new byte[1];
    device.read(oldmode, MODE1, 1);

    byte[] newmode = new byte[1];
    newmode[0] = (byte) ((oldmode[0] & (byte) 0x7F) | (byte) 0x10); // Sleep
    device.write(newmode, MODE1, 1); // Go to Sleep
    byte[] flooredPrescale = new byte[1];
    flooredPrescale[0] = (byte) Math.floor(prescale);
    device.write(flooredPrescale, PRESCALE, 1);
    device.read(oldmode, MODE1, 1);

    TimeUnit.MILLISECONDS.sleep(50);

    byte[] newoldmode = new byte[1];
    newoldmode[0] = (byte) (oldmode[0] | (byte) 0x80);
    device.read(newoldmode, MODE1, 1);
  }

  /**
   * Sets a single PWM channel
   *
   * @param channel PWM channel
   * @param on      PWM on time
   * @param off     PWM off time
   */
  public void setPWM(int channel, int on, int off) {
    logger.debug("setPWM " + on + ", " + off);

    byte register = (byte) (LED0 + 4 * channel);

    byte buffer = (byte) (on & (byte) 0xFF);
    try {
      device.write(register, buffer);
      logger.debug("write [" + asHex(register) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex(register) + "][" + asHex(buffer) + "]", e);
    }

    buffer = (byte) (on >> 8);
    try {
      device.write(register+1, buffer);
      logger.debug("write [" + asHex((byte) (register + 1)) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex((byte) (register + 1)) + "][" + asHex(buffer) + "]", e);
    }

    buffer = (byte) (off & (byte) 0xFF);
    try {
      device.write(register+2, buffer);
      logger.debug("write [" + asHex((byte) (register + 2)) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex((byte) (register + 2)) + "][" + asHex(buffer) + "]", e);
    }

    buffer = (byte) (off >> 8);
    try {
      device.write(register+3, buffer);
      logger.debug("write [" + asHex((byte) (register + 3)) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex((byte) (register + 3)) + "][" + asHex(buffer) + "]", e);
    }

  }

  /**
   * Sets a all PWM channels
   *
   * @param on  PWM on time
   * @param off PWM off time
   */
  public void setAllPWM(int on, int off) {
    logger.debug("setAllPWM " + on + ", " + off);

    byte register = (byte) (ALL_LED);

    byte buffer = (byte) (on & (byte) 0xFF);
    try {
      device.write(register, buffer);
      logger.debug("write [" + asHex(register) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex(register) + "][" + asHex(buffer) + "]", e);
    }

    buffer = (byte) (on >> 8);
    try {
      device.write(register+1, buffer);
      logger.debug("write [" + asHex((byte) (register + 1)) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("failed to write [" + asHex((byte) (register + 1)) + "][" + asHex(buffer) + "]", e);
    }

    buffer = (byte) (off & (byte) 0xFF);
    try {
      device.write(register + 2, buffer);
      logger.debug("write [" + asHex((byte) (register + 2)) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex((byte) (register + 2)) + "][" + asHex(buffer) + "]", e);
    }

    buffer = (byte) (off >> 8);
    try {
      device.write(register+3, buffer);
      logger.debug("write [" + asHex((byte) (register + 3)) + "][" + asHex(buffer) + "]");
    } catch (IOException e) {
      logger.error("failed to write [" + asHex((byte) (register + 3)) + "][" + asHex(buffer) + "]", e);
    }
  }

  private String asHex(byte b) {
    return String.format("%02x", b & 0xff);
  }

  private String asHex(byte[] bs) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bs) {
      sb.append(String.format("%02x", b & 0xff));
    }

    return sb.toString();
  }

}
