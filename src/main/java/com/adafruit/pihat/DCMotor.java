package com.adafruit.pihat;

public interface DCMotor {

  void forward(); // Move forward
  void reverse(); // Move backwards
  void release(); // Stop moving

  void setSpeed(int speed);

}
