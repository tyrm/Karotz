package com.adafruit.pihat;

import java.io.IOException;

public interface DCMotor {

  void forward() throws IOException; // Move forward

  void reverse() throws IOException; // Move backwards

  void release() throws IOException; // Stop moving

  void setSpeed(int speed) throws IOException;

}
