package cc.mtz.sts.zza.sound;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

public class SoundPlayer extends Thread {
   private final Queue<PlayEntry> toPlay = new PriorityBlockingQueue();
   private Map<String, String> replaces = new LinkedHashMap();
   private MaryInterface mary;
   private static SoundPlayer instance = new SoundPlayer();

   public static SoundPlayer getInstance() {
      return instance;
   }

   private SoundPlayer() {
      this.replaces.put("0([0-9]):", "$1:");
      this.replaces.put(":0([0-9])", ":$1");
      this.replaces.put("([0-9]*):([0-9]*) Uhr", "$1 Uhr $2");
      this.replaces.put("Uhr 0([^0-9]*)", "Uhr$1");
      this.replaces.put("THA", "Thalys");
      this.replaces.put("Hbf", "Hauptbahnhof");
      this.replaces.put("HB", "Hauptbahnhof");
      this.replaces.put("Bad Bf", "Badischer Bahnhof");
      this.replaces.put("Bf", "Bahnhof");

      try {
         this.mary = new LocalMaryInterface();
         this.mary.setVoice("bits1-hsmm");
         this.mary.setLocale(Locale.GERMAN);
      } catch (MaryConfigurationException var2) {
         Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, "Error initializing MaryTTS", var2);
      }

   }

   public void addText(Priority priority, String text) {
      Entry replace;
      for(Iterator var3 = this.replaces.entrySet().iterator(); var3.hasNext(); text = text.replaceAll((String)replace.getKey(), (String)replace.getValue())) {
         replace = (Entry)var3.next();
      }

      synchronized(this.toPlay) {
         this.toPlay.add(new PlayEntry(priority, text));
      }
   }

   public void addReplaces(Map<String, String> replaces) {
      this.replaces.putAll(replaces);
   }

   public boolean isIdle() {
      synchronized(this.toPlay) {
         return this.toPlay.isEmpty();
      }
   }

   public int getQueueLength() {
      synchronized(this.toPlay) {
         return this.toPlay.size();
      }
   }

   public void run() {
      while(true) {
         AudioInputStream inStream = null;

         try {
            while(this.toPlay.isEmpty()) {
               try {
                  synchronized(this) {
                     this.wait();
                  }
               } catch (InterruptedException var31) {
                  Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, (String)null, var31);
               }
            }

            String playText;
            synchronized(this.toPlay) {
               playText = ((PlayEntry)this.toPlay.poll()).getText();
            }

            inStream = this.mary.generateAudio(playText);
            AudioFormat sourceFormat = inStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(), sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(), false);
            AudioInputStream gongStream = AudioSystem.getAudioInputStream(new File("gong.wav"));
            AudioFormat gongFormat = gongStream.getFormat();
            AudioFormat gongTargetFormat = new AudioFormat(Encoding.PCM_SIGNED, gongFormat.getSampleRate(), 16, gongFormat.getChannels(), gongFormat.getChannels() * 2, gongFormat.getSampleRate(), false);
            SourceDataLine gongLine = AudioSystem.getSourceDataLine(gongTargetFormat);
            gongLine.open();
            gongLine.start();
            byte[] buffer = new byte[128000];
            boolean var10 = true;

            int bytesfilled;
            do {
               bytesfilled = gongStream.read(buffer);
               if (bytesfilled > -1) {
                  gongLine.write(buffer, 0, bytesfilled);
               }
            } while(bytesfilled > -1);

            gongLine.drain();
            gongLine.stop();
            gongLine.close();
            gongStream.close();
            SourceDataLine line = AudioSystem.getSourceDataLine(targetFormat);
            line.open();
            line.start();

            do {
               bytesfilled = inStream.read(buffer);
               if (bytesfilled > -1) {
                  line.write(buffer, 0, bytesfilled);
               }
            } while(bytesfilled > -1);

            line.drain();
            line.stop();
            line.close();
            inStream.close();
         } catch (LineUnavailableException var32) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, (String)null, var32);
         } catch (UnsupportedAudioFileException var33) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, (String)null, var33);
         } catch (IOException var34) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, (String)null, var34);
         } catch (SynthesisException var35) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, (String)null, var35);
         } finally {
            try {
               if (inStream != null) {
                  inStream.close();
               }
            } catch (IOException var28) {
               Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, (String)null, var28);
            }

         }
      }
   }

   private static class PlayEntry implements Comparable<PlayEntry> {
      private String text;
      private Priority priority;

      public PlayEntry(Priority priority, String text) {
         this.priority = priority;
         this.text = text;
      }

      public int compareTo(PlayEntry o) {
         return this.priority.compareTo(o.priority);
      }

      public String getText() {
         return this.text;
      }
   }

   public static enum Priority implements Comparable<Priority> {
      HIGH,
      MEDIUM,
      LOW;
   }
}
