package haus.pup.karotzhw;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.ivona.services.tts.IvonaSpeechCloudClient;
import com.ivona.services.tts.model.CreateSpeechRequest;
import com.ivona.services.tts.model.CreateSpeechResult;
import com.ivona.services.tts.model.Input;
import com.ivona.services.tts.model.Voice;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Speech {
  static IvonaSpeechCloudClient speechCloud = new IvonaSpeechCloudClient(
          new ClasspathPropertiesFileCredentialsProvider("IvonaCredentials.properties"));
  static Logger logger = LoggerFactory.getLogger("karotzhw.Speech");

  private Boolean speaking = false;
  private Queue phrases = new LinkedList();

  String defaultVoice = "Emma";
  String cacheDir = "tts";

  public Speech() {
    init();
  }

  public Speech(String d) {
    cacheDir = d;
    init();
  }

  private void init() {
    logger.info("Initializing Speech");
    speechCloud.setEndpoint("https://tts.eu-west-1.ivonacloud.com");

    for(int i=0; i<16; i++){
      for(int j=0; j<16; j++){
        String testDir = cacheDir + "/" + String.format("%01x", i & 0xf) + "/" + String.format("%01x", j & 0xf);
        File fCache = new File(testDir);

        if (!fCache.isDirectory()) {
          if (fCache.mkdirs()) {
            logger.info(testDir + " Created");
          }
          else {
            logger.error(testDir + " could not be Created");
          }
        }
      }
    }

    say("Speech Initialized");
  }

  /**
   * Set default voice
   * @param v voice
   */
  public void setDefaultVoice(String v){
    defaultVoice = v;
  }

  /**
   * say text using default voice.
   * @param t text
   */
  public void say(String t) {
    say(defaultVoice, t);
  }

  /**
   * Say text using voice.
   * @param v voice
   * @param t text
   */
  public void say(String v, String t){
    getFile(v, t);
    playFile(v, t);
  }

  /**
   * Check if Speech files is in cache
   * @param v Voice
   * @param t Text
   * @return true is speech file is cached
   */
  public boolean inCache(String v, String t) {
    String filename = getCacheFilename(v, t);

    return new File(filename).exists();
  }

  /**
   * Play file from cache
   * @param v Voice
   * @param t Text
   */
  private void playFile(String v, String t) {
    String filename = cacheDir + "/" + getCacheFilename(v, t);
    logger.debug("Queueing speech: (" + v + ") " + t);

    phrases.add(filename);

    startPlayer();
  }

  private void startPlayer() {

    new Thread() {
      public void run() {
        if (!speaking) {
          speaking = true;
          while (phrases.peek() != null) {
            Object firstElement = phrases.poll();

            String filename = firstElement.toString();

            logger.info("playing: " + filename);
            try {
              FileInputStream fis     = new FileInputStream(new File(filename).getCanonicalPath());
              BufferedInputStream bis = new BufferedInputStream(fis);
              final Player player = new Player(bis);

              try { player.play(); }
              catch (Exception e) { System.out.println(e); }
            }
            catch (Exception e) {
              System.out.println("Problem playing file " + filename);
              System.out.println(e);
            }
          }

          speaking = false;
        }
      }
    }.start();
  }

  /**
   * Wrapper around Ivona Example code to retrieve a file
   * @param v voice
   * @param t text to speak
   */
  private void getFile(String v, String t) {
    File outputFile = new File(cacheDir + "/" + getCacheFilename(v, t));

    if (!outputFile.exists()) {
      logger.info("Retrieving speech file: (" + v + ") " + t);
      Logger ivonaLog = LoggerFactory.getLogger("karotzhw.Speech.ivona");

      CreateSpeechRequest createSpeechRequest = new CreateSpeechRequest();
      Input input = new Input();
      Voice voice = new Voice();

      voice.setName(v);
      input.setData(t);

      createSpeechRequest.setInput(input);
      createSpeechRequest.setVoice(voice);
      InputStream in = null;
      FileOutputStream outputStream = null;

      try {

        CreateSpeechResult createSpeechResult = speechCloud.createSpeech(createSpeechRequest);


        ivonaLog.debug("\nSuccess sending request:");
        ivonaLog.debug(" content type:\t" + createSpeechResult.getContentType());
        ivonaLog.debug(" request id:\t" + createSpeechResult.getTtsRequestId());
        ivonaLog.debug(" request chars:\t" + createSpeechResult.getTtsRequestCharacters());
        ivonaLog.debug(" request units:\t" + createSpeechResult.getTtsRequestUnits());

        ivonaLog.debug("\nStarting to retrieve audio stream:");

        in = createSpeechResult.getBody();
        outputStream = new FileOutputStream(outputFile);

        byte[] buffer = new byte[2 * 1024];
        int readBytes;

        while ((readBytes = in.read(buffer)) > 0) {
          outputStream.write(buffer, 0, readBytes);
        }

        ivonaLog.info("\nFile saved: " + outputFile.getPath());

      } catch (FileNotFoundException e) {
        ivonaLog.error("File Not Found exception Occurred. See Trace.");
        e.printStackTrace();
      } catch (IOException e) {
        ivonaLog.error("IO exception Occurred. See Trace.");
        e.printStackTrace();
      } finally {
        try {
          if (in != null) {
            in.close();
          }
          if (outputStream != null) {
            outputStream.close();
          }
          logger.info("Speech file saved: " + outputFile.getPath());
        } catch (IOException e) {
          ivonaLog.error("IO exception Occurred. See Trace.");
          e.printStackTrace();
        }
      }
    } else {
      logger.debug("Speech file exists in cache: (" + v + ") " + t);
    }
  }

  /**
   * Build folder path and filename based on MD5 of voice and Text
   * @param v Voice
   * @param t Text
   * @return Cache Filepath
   */
  protected String getCacheFilename(String v, String t) {
    String hashString = "Ivona" + v + t;

    String hash = getMD5(hashString.toUpperCase());

    String folder1 = hash.substring(0,1);
    String folder2 = hash.substring(1,2);
    String filename = hash.substring(2);

    return folder1 + "/" + folder2 + "/" + filename + ".mp3";
  }

  protected String getMD5(String s) {
    // Safely Create MD5 Message Digest
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      logger.error("No Such Algorithm exception Occurred. See Trace.");
      e.printStackTrace();
    }

    md.update(s.getBytes());
    byte[] digest = md.digest();
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) {
      sb.append(String.format("%02x", b & 0xff));
    }

    return sb.toString();
  }
}
