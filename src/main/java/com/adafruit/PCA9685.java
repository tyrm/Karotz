package com.adafruit;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.impl.I2CDeviceImpl;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adafruit PCA9685 16-Channel PWM Servo Driver
 *
 * Java Implementation of Adafruit's Adafruit_PWM_Servo_Driver.py
 * https://github.com/adafruit/Adafruit-Motor-HAT-Python-Library/blob/master/Adafruit_MotorHAT/Adafruit_PWM_Servo_Driver.py
 */
public class PCA9685 extends I2CDeviceImpl {
  Logger logger;

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

  public PCA9685(I2CBus b, int a) throws IOException, InterruptedException {
    super(b, a); // Init i2c device
    logger = LoggerFactory.getLogger("PCA9685." + a);

    logger.debug("Reseting PCA9685 MODE1 (without SLEEP) and MODE2");
    setAllPWM(0, 0);

    byte[] buffer = new byte[1];
    buffer[0] = OUTDRV;
    write(buffer, MODE2, 1);
    buffer[0] = ALLCALL;
    write(buffer, MODE1, 1);
    TimeUnit.MILLISECONDS.sleep(5);          // wait for oscillator

    read(buffer, MODE1, 1);
    buffer[0] = (byte) (buffer[0] & ~SLEEP); // wake up (reset sleep)
    write(buffer, MODE1, 1);
    TimeUnit.MILLISECONDS.sleep(5);          // wait for oscillator
  }

  /**
   * Sends a software reset (SWRST) command to all the servo drivers on the bus
   * @throws IOException
   */
  public void softwareReset() throws IOException {
    write(0, SWRST);
    logger.info("Sending reset");
  }

  /**
   * Sets the PWM frequency
   * @param freq
   * @throws IOException
   * @throws InterruptedException
   */

  public void setPWMFreq(double freq) throws IOException, InterruptedException {
    double prescaleval = 25000000.0;// 25MHz
    prescaleval       /= 4096.0;    // 12-bit
    prescaleval       /= freq;
    prescaleval       -= 1.0;
    logger.debug("Setting PWM frequency to " + freq + " Hz");
    logger.debug("Estimated pre-scale: " + prescaleval);
    double prescale = Math.floor(prescaleval + 0.5);
    logger.debug("Final pre-scale: " + prescale);

    byte[] oldmode = new byte[1];
    read(oldmode, MODE1, 1);
    byte[] newmode = new byte[1];
    newmode[0] =  (byte) ((oldmode[0] & (byte) 0x7F) | (byte) 0x10); // Sleep
    write(newmode, MODE1, 1);                                        // Go to Sleep
    byte[] flooredPrescale = new byte[1];
    flooredPrescale[0] = (byte) Math.floor(prescale);
    write(flooredPrescale, PRESCALE, 1);
    read(oldmode, MODE1, 1);

    TimeUnit.MILLISECONDS.sleep(5);

    byte[] newoldmode = new byte[1];
    newoldmode[0] = (byte) (oldmode[0] | (byte) 0x80);
    read(newoldmode, MODE1, 1);
  }

  /**
   * Sets a single PWM channel
   * @param channel PWM channel
   * @param on PWM on time
   * @param off PWM off time
   */
  public void setPWM(int channel, int on, int off) throws IOException {
    byte[] buffer = new byte[4];
    buffer[0] = (byte) (on & (byte) 0xFF);
    buffer[1] = (byte) (on >> 8);
    buffer[2] = (byte) (off & (byte) 0xFF);
    buffer[3] = (byte) (off >> 8);

    byte register = (byte) (LED0 + 4 * channel);

    write(buffer, register, 4);
    logger.info("write ["+asHex(register)+"]["+asHex(buffer)+"]");
  }

  /**
   * Sets a all PWM channels
   * @param on PWM on time
   * @param off PWM off time
   */
  public void setAllPWM(int on, int off) throws IOException {
    byte[] buffer = new byte[4];
    buffer[1] = (byte) (on & (byte) 0xFF);
    buffer[2] = (byte) (on >> 8);
    buffer[3] = (byte) (off & (byte) 0xFF);
    buffer[4] = (byte) (off >> 8);

    write(buffer, ALL_LED, 4);
    logger.info("write ["+asHex(ALL_LED)+"]["+asHex(buffer)+"]");
  }

  private String asHex(byte b){
    return String.format("%02x", b & 0xff);
  }
  private String asHex(byte[] bs){
    StringBuilder sb = new StringBuilder();
    for (byte b : bs) {
      sb.append(String.format("%02x", b & 0xff));
    }
    return sb.toString();
  }

}
