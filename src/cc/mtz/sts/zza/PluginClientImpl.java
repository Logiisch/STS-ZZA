package cc.mtz.sts.zza;

import cc.mtz.sts.zza.data.Bahnhof;
import cc.mtz.sts.zza.data.DataCache;
import cc.mtz.sts.zza.data.ZugBelegung;
import cc.mtz.sts.zza.listener.StsListener;
import cc.mtz.sts.zza.sound.SoundPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import js.java.stspluginlib.PluginClient;
import js.java.stspluginlib.PluginClient.ZugDetails;
import js.java.stspluginlib.PluginClient.ZugFahrplanZeile;

public class PluginClientImpl extends PluginClient {
   private final StellwerkFile config;
   private final List<StsListener> listener;
   private boolean ready = true;
   private final DataCache currentCache;
   private DataCache lastRunCache;
   private Set<Integer> zuegeZuVerarbeiten;
   private final Map<String, Set<String>> einfahrtGemeldet = new HashMap<>();
   private final Map<String, Set<String>> abfahrtGemeldet = new HashMap<>();
   private final Map<String, Set<String>> haltGemeldet = new HashMap<>();
   private final Map<Integer, String> einfahrtsGleise = new HashMap<>();

   public PluginClientImpl(String name, String author, String version, String text, StellwerkFile config, List<StsListener> listener) {
      super(name, author, version, text);
      this.config = config;
      this.listener = listener;
      this.currentCache = new DataCache();
      SoundPlayer.getInstance().addReplaces(config.getSoundReplaces());
   }

   public DataCache getLastRunCache() {
      return new DataCache(this.lastRunCache);
   }

   protected void connected() {
      this.request_bahnsteigliste();
   }

   protected void closed() {
      System.exit(0);
   }

   protected void response_anlageninfo(int aid, String name, String build) {
   }

   protected void response_bahnsteigliste(HashMap<String, HashSet<String>> bl) {
      List<String> keys = new ArrayList<>(bl.keySet());
      Collections.sort(keys);

      label36:
      for(int bhf = 0; bhf < this.config.getBahnhoefe().size(); ++bhf) {
         Iterator<String> var4 = keys.iterator();

         while(true) {
            String bahnsteig;
            do {
               if (!var4.hasNext()) {
                  continue label36;
               }

               bahnsteig = var4.next();
            } while(!Pattern.matches(this.config.getBahnhoefe().get(bhf).getGleiseRegex(), bahnsteig));

            for (StsListener callback : this.listener) {
               callback.bahnsteigSetup(bahnsteig, this.config.getBahnhoefe().get(bhf));
            }
         }
      }

      if (this.ready) {
         this.ready = false;
         this.currentCache.clear();
         this.request_zugliste();
      }

   }

   protected void response_zugliste(HashMap<Integer, String> zl) {
      this.zuegeZuVerarbeiten = new HashSet<>(zl.keySet());

      for (int zid : zl.keySet()) {
         this.request_zugdetails(zid);
      }

   }

   protected void response_zugdetails(int zid, ZugDetails details) {
      this.currentCache.getDetailCache().put(zid, details);
      this.request_zugfahrplan(zid);
      this.register_ereignis(zid, "ankunft");
      this.register_ereignis(zid, "abfahrt");
   }

   protected void response_zugfahrplan(int zid, LinkedList<ZugFahrplanZeile> plan) {
      try {
         this.currentCache.getFahrplaene().put(zid, plan);
         this.zuegeZuVerarbeiten.remove(zid);

         for (ZugFahrplanZeile zeile : plan) {

            for (Bahnhof bahnhof : this.config.getBahnhoefe()) {
               if (Pattern.matches(bahnhof.getGleiseRegex(), zeile.gleis)) {
                  List<ZugBelegung> gleisBelegung = this.currentCache.getBelegung().get(zeile.gleis);
                  if (gleisBelegung == null) {
                     gleisBelegung = new ArrayList<>();
                  }

                  gleisBelegung.add(new ZugBelegung(this.currentCache.getDetailCache().get(zid), zeile));
                  this.currentCache.getBelegung().put(zeile.gleis, gleisBelegung);
               }
            }
         }

         if (this.zuegeZuVerarbeiten.isEmpty()) {
            this.lastRunCache = new DataCache(this.currentCache);
            boolean soundIdle = SoundPlayer.getInstance().isIdle();
            long simutime = this.getSimutime();
            Iterator var15 = this.currentCache.getBelegung().keySet().iterator();

            while(true) {
               boolean zugVorhanden;
               Iterator var10;

               String gleis;
               label79:
               do {
                  if (!var15.hasNext()) {
                     if (soundIdle) {
                        var15 = this.listener.iterator();

                        while(var15.hasNext()) {
                           StsListener callback = (StsListener)var15.next();
                           callback.idleAction(this.currentCache);
                        }
                     }

                     this.ready = true;
                     return;
                  }

                  gleis = (String)var15.next();
                  List<ZugBelegung> gleisBelegung = this.currentCache.getBelegung().get(gleis);
                  zugVorhanden = false;
                  if (gleisBelegung != null) {
                     gleisBelegung.sort(new ZugBelegung.DelayComparator());
                     var10 = gleisBelegung.iterator();

                     ZugBelegung zugBelegung;
                     do {
                        do {
                           do {
                              if (!var10.hasNext()) {
                                 continue label79;
                              }

                              zugBelegung = (ZugBelegung)var10.next();
                           } while(zugBelegung.zeile == null);
                        } while(zugBelegung.zug == null);
                     } while(zugBelegung.zeile.an + (long)(zugBelegung.zug.verspaetung * '\uea60') <= simutime && zugBelegung.zeile.ab + (long)(zugBelegung.zug.verspaetung * '\uea60') <= simutime);

                     zugVorhanden = true;
                     this.naechsterZug(zugBelegung.zug.zid, zugBelegung.zeile.gleis);
                  }
               } while(zugVorhanden);

               var10 = this.listener.iterator();

               while(var10.hasNext()) {
                  StsListener callback = (StsListener)var10.next();
                  callback.keinZug(gleis);
               }
            }
         }
      } catch (Exception var12) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, var12);
      }

   }

   protected void response_ereignis(int zid, String art, ZugDetails zugDetails) {
      this.currentCache.getDetailCache().put(zid, zugDetails);
      Bahnhof bahnhof;
      Iterator<StsListener> var5;
      StsListener callback;
      if (art.equals("ankunft")) {
         if (this.currentCache.getDetailCache().get(zid).sichtbar && this.currentCache.getDetailCache().get(zid).amgleis && this.isTrainStopping(zid, this.currentCache.getDetailCache().get(zid).gleis)) {
            bahnhof = this.getBahnhofByGleis(this.currentCache.getDetailCache().get(zid).gleis);
            if (bahnhof != null && (!this.haltGemeldet.containsKey(this.currentCache.getDetailCache().get(zid).gleis) || !this.haltGemeldet.get(this.currentCache.getDetailCache().get(zid).gleis).contains(this.currentCache.getDetailCache().get(zid).name))) {
               this.einfahrtsGleise.put(zid, this.currentCache.getDetailCache().get(zid).gleis);
               var5 = this.listener.iterator();

               while(var5.hasNext()) {
                  callback = var5.next();
                  if (!this.haltGemeldet.containsKey(this.currentCache.getDetailCache().get(zid).gleis)) {
                     this.haltGemeldet.put(this.currentCache.getDetailCache().get(zid).gleis, new HashSet());
                  }

                  this.haltGemeldet.get(this.currentCache.getDetailCache().get(zid).gleis).add(this.currentCache.getDetailCache().get(zid).name);
                  callback.zugHalt(this.currentCache.getDetailCache().get(zid).gleis, bahnhof, zid, this.currentCache);
               }
            }
         }
      } else if (art.equals("abfahrt") && this.einfahrtsGleise.get(zid) != null) {
         bahnhof = this.getBahnhofByGleis(this.einfahrtsGleise.get(zid));
         if (bahnhof != null && (!this.abfahrtGemeldet.containsKey(this.einfahrtsGleise.get(zid)) || !this.abfahrtGemeldet.get(this.einfahrtsGleise.get(zid)).contains(this.currentCache.getDetailCache().get(zid).name))) {
            var5 = this.listener.iterator();

            while(var5.hasNext()) {
               callback = var5.next();
               if (!this.abfahrtGemeldet.containsKey(this.einfahrtsGleise.get(zid))) {
                  this.abfahrtGemeldet.put(this.einfahrtsGleise.get(zid), new HashSet());
               }

               this.abfahrtGemeldet.get(this.einfahrtsGleise.get(zid)).add(this.currentCache.getDetailCache().get(zid).name);
               callback.zugAbfahrt(this.einfahrtsGleise.get(zid), bahnhof, zid, this.currentCache);
            }
         }
      }

   }

   public void naechsterZug(int zid, String gleis) {
      if (this.currentCache.getDetailCache().get(zid) != null) {
         Bahnhof bahnhof = null;

         for (Bahnhof bhf : this.config.getBahnhoefe()) {
            if (Pattern.matches(bhf.getGleiseRegex(), gleis)) {
               bahnhof = bhf;
               break;
            }
         }

         if (bahnhof != null) {
            Iterator<StsListener> var4 = this.listener.iterator();

            StsListener callback;
            while(var4.hasNext()) {
               callback = var4.next();
               callback.naechsterZug(gleis, bahnhof, zid, this.currentCache);
            }

            if (this.currentCache.getDetailCache().get(zid).sichtbar && this.currentCache.getDetailCache().get(zid).gleis.equals(gleis) && (!this.einfahrtGemeldet.containsKey(gleis) || !this.einfahrtGemeldet.get(gleis).contains(this.currentCache.getDetailCache().get(zid).name))) {
               var4 = this.listener.iterator();

               while(var4.hasNext()) {
                  callback = var4.next();
                  if (!this.einfahrtGemeldet.containsKey(gleis)) {
                     this.einfahrtGemeldet.put(gleis, new HashSet<>());
                  }

                  this.einfahrtGemeldet.get(gleis).add(this.currentCache.getDetailCache().get(zid).name);
                  callback.zugEinfahrt(gleis, bahnhof, zid, this.currentCache);
               }
            }

         }
      }
   }

   private Bahnhof getBahnhofByGleis(String gleis) {
      Iterator<Bahnhof> var2 = this.config.getBahnhoefe().iterator();

      Bahnhof bhf;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         bhf = var2.next();
      } while(!Pattern.matches(bhf.getGleiseRegex(), gleis));

      return bhf;
   }

   private boolean isTrainStopping(int zid, String gleis) {
      Iterator<ZugFahrplanZeile> var3 = (this.currentCache.getFahrplaene().get(zid)).iterator();
      if (!var3.hasNext()) {
         return false;
      } else {
         ZugFahrplanZeile zeile = var3.next();
         return zeile.gleis.equals(gleis) && !zeile.flags.hasFlag('D');
      }
   }

   public void haltGemeldet(int zid, String gleis) {
      if (this.haltGemeldet.containsKey(gleis)) {
         this.haltGemeldet.get(gleis).add(this.currentCache.getDetailCache().get(zid).name);
      }

   }

   public void requestZuege() {
      this.ready = false;
      this.currentCache.clear();
      this.request_zugliste();
   }

   public boolean isReady() {
      return this.ready;
   }
}
