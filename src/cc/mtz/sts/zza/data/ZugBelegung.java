package cc.mtz.sts.zza.data;

import java.util.Comparator;
import js.java.stspluginlib.PluginClient.ZugDetails;
import js.java.stspluginlib.PluginClient.ZugFahrplanZeile;

public class ZugBelegung {
   public ZugDetails zug;
   public ZugFahrplanZeile zeile;

   public ZugBelegung(ZugDetails zug, ZugFahrplanZeile zeile) {
      this.zug = zug;
      this.zeile = zeile;
   }

   public static class DelayComparator implements Comparator<ZugBelegung> {
      public int compare(ZugBelegung o1, ZugBelegung o2) {
         long o1time = o1.zeile.an;
         if (o1time == 0L) {
            o1time = o1.zeile.ab;
         }

         if (o1.zug != null) {
            o1time += (o1.zug.verspaetung * '\uea60');
         }

         long o2time = o2.zeile.an;
         if (o2time == 0L) {
            o2time = o2.zeile.ab;
         }

         if (o2.zug != null) {
            o2time += (o2.zug.verspaetung * '\uea60');
         }

         return (int)(o1time - o2time);
      }
   }
}
