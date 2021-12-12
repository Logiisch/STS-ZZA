package cc.mtz.sts.zza.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import js.java.stspluginlib.PluginClient.ZugDetails;
import js.java.stspluginlib.PluginClient.ZugFahrplanZeile;

public class DataCache {
   private Map<Integer, List<ZugFahrplanZeile>> fahrplaene = new HashMap<>();
   private Map<String, List<ZugBelegung>> belegung = new HashMap<>();
   private Map<Integer, ZugDetails> detailCache = new HashMap<>();

   public DataCache() {
   }

   public DataCache(DataCache toCopy) {
      this.fahrplaene = new HashMap<>(toCopy.fahrplaene);
      this.belegung = new HashMap<>(toCopy.belegung);
      this.detailCache = new HashMap<>(toCopy.detailCache);
   }

   public void clear() {
      this.fahrplaene.clear();
      this.belegung.clear();
      this.detailCache.clear();
   }

   public Map<String, List<ZugBelegung>> getBelegung() {
      return this.belegung;
   }

   public Map<Integer, ZugDetails> getDetailCache() {
      return this.detailCache;
   }

   public Map<Integer, List<ZugFahrplanZeile>> getFahrplaene() {
      return this.fahrplaene;
   }
}
