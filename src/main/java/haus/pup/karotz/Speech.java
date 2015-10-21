package haus.pup.karotz;

public interface Speech {

  /**
   * Check if IvonaSpeech files is in cache
   * @param v Voice
   * @param t Text
   * @return true is speech file is cached
   */
  boolean inCache(String v, String t);

  /**
   * say text using default voice.
   * @param t text
   */
  void say(String t);

  /**
   * Say text using voice.
   * @param v voice
   * @param t text
   */
  void say(String v, String t);

  /**
   * Stage speech file in cache for text using voice. If file exists it will be overwritten
   * @param v voice
   * @param t text
   */
  void stageVoice(String v, String t);

  /**
   * Stage speech file in cache for text using the default voice. If file exists it will be overwritten
   * @param t text
   */
  void stageVoice(String t);

  /**
   * Set default voice
   * @param v voice
   */
  void setDefaultVoice(String v);

}
