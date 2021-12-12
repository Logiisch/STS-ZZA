package cc.mtz.sts.zza;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import js.java.stspluginlib.PluginClient;
import js.java.stspluginlib.PluginClient.ZugDetails;
import js.java.stspluginlib.PluginClient.ZugFahrplanZeile;

public class ConfigClient extends PluginClient {
   private Set<String> bahnsteige = new HashSet<>();

   public ConfigClient(String name, String author, String version, String text) {
      super(name, author, version, text);
   }

   public Set<String> getBahnsteige() {
      return this.bahnsteige;
   }

   protected void connected() {
      this.request_bahnsteigliste();
   }

   protected void closed() {
   }

   protected void response_anlageninfo(int aid, String name, String build) {
   }

   protected void response_bahnsteigliste(HashMap<String, HashSet<String>> bl) {
      this.bahnsteige = bl.keySet();
      synchronized(this) {
         this.notify();
      }
   }

   protected void response_zugliste(HashMap<Integer, String> zl) {
   }

   protected void response_zugdetails(int zid, ZugDetails details) {
   }

   protected void response_zugfahrplan(int zid, LinkedList<ZugFahrplanZeile> plan) {
   }
}
