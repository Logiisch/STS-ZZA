package cc.mtz.sts.zza;

import java.util.regex.Pattern;

public class Util {
   public static boolean isFernzug(String name) {
      return Pattern.matches(Main.CONFIG.getFernzugPattern(), name);
   }

   public static String ansagenName(String name) {
      return !isFernzug(name) ? name.replaceAll("^(.*) [0-9]+", "$1") : name;
   }

   public static String verspaetungsText(int minuten, boolean minutengenau, String prefix) {
      if (!minutengenau) {
         minuten = (minuten - 1) / 5 * 5;
      }

      if (minuten == 0) {
         return "wenige Minuten";
      } else {
         return minuten == 1 ? prefix + "1 Minute" : prefix + minuten + " Minuten";
      }
   }
}
