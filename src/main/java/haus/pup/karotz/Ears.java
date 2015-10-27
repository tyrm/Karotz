package haus.pup.karotz;

import java.io.IOException;

public interface Ears {

  /**
   * Move ear 'ear' to home in the fastest direction.
   *
   * @param ear
   */
  void home(Ear ear) throws IOException, InterruptedException;

  /**
   * Move ear 'ear' forward 'step' number of steps.
   *
   * @param ear
   * @param steps
   */
  void moveForward(Ear ear, int steps);

  /**
   * Move ear 'ear' backward 'step' number of steps.
   *
   * @param ear
   * @param steps
   */
  void moveReverse(Ear ear, int steps);

  /**
   * Move ear 'ear' forward to 'position'.
   *
   * @param ear
   * @param position 0-16
   */
  void moveTo(Ear ear, int position);

  /**
   * Move ear 'ear' forward to 'position'.
   *
   * @param ear
   * @param position 0-16
   */
  void moveForwardTo(Ear ear, int position);

  /**
   * Move ear 'ear' backwards to 'position'.
   *
   * @param ear
   * @param position 0-16
   */
  void moveReverseTo(Ear ear, int position);

  /**
   * getPosition of right ear.
   *
   * @return position 0-16
   */
  int getRightPosition();

  /**
   * getPosition of left ear.
   *
   * @return position 0-16
   */
  int getLeftPosition();
}
