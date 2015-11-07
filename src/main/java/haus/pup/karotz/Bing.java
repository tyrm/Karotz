package haus.pup.karotz;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.InputStream;

public class Bing {
  public void playSound()  {
    try
    {
      // get the sound file as a resource out of my jar file;
      // the sound file must be in the same directory as this class file.
      // the input stream portion of this recipe comes from a javaworld.com article.
      InputStream inputStream = getClass().getResourceAsStream("/S0000011.WAV");
      AudioStream audioStream = new AudioStream(inputStream);
      AudioPlayer.player.start(audioStream);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
