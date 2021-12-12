package cc.mtz.sts.zza.listener;

import cc.mtz.sts.zza.data.Bahnhof;
import cc.mtz.sts.zza.data.DataCache;

public interface StsListener {
   void bahnsteigSetup(String var1, Bahnhof var2);

   void naechsterZug(String var1, Bahnhof var2, int var3, DataCache var4);

   void keinZug(String var1);

   void zugEinfahrt(String var1, Bahnhof var2, int var3, DataCache var4);

   void zugHalt(String var1, Bahnhof var2, int var3, DataCache var4);

   void zugAbfahrt(String var1, Bahnhof var2, int var3, DataCache var4);

   void idleAction(DataCache var1);
}
