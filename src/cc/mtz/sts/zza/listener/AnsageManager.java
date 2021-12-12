package cc.mtz.sts.zza.listener;

import cc.mtz.sts.zza.Main;
import cc.mtz.sts.zza.Rewriter;
import cc.mtz.sts.zza.StellwerkFile;
import cc.mtz.sts.zza.Util;
import cc.mtz.sts.zza.data.Bahnhof;
import cc.mtz.sts.zza.data.DataCache;
import cc.mtz.sts.zza.data.RewrittenDetails;
import cc.mtz.sts.zza.data.ZugBelegung;
import cc.mtz.sts.zza.sound.SoundPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import js.java.stspluginlib.PluginClient.ZugDetails;
import js.java.stspluginlib.PluginClient.ZugFahrplanZeile;

public class AnsageManager implements StsListener {
   private StellwerkFile config;
   private Map<String, Map<String, Long>> verspaetungsAnsageZeiten = new HashMap();
   private Map<String, Map<String, Long>> gleiswechselAnsageZeiten = new HashMap();
   private Map<Integer, Long> stehendAnsageZeiten = new HashMap();
   private Map<Integer, Long> haltZeiten = new HashMap();
   private Map<String, Set<String>> einfahrtGemeldet = new HashMap();
   private boolean einfahrten;
   private boolean anschluesse;
   private boolean verspaetungen;
   private boolean minutengenau;

   public AnsageManager(StellwerkFile config, boolean einfahrten, boolean anschluesse, boolean verspaetungen, boolean minutengenau) {
      this.config = config;
      this.einfahrten = einfahrten;
      this.anschluesse = anschluesse;
      this.verspaetungen = verspaetungen;
      this.minutengenau = minutengenau;
   }

   public void bahnsteigSetup(String bahnsteig, Bahnhof bahnhof) {
   }

   public void naechsterZug(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
   }

   public void zugEinfahrt(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
      if (!this.einfahrtGemeldet.containsKey(gleis) || !((Set)this.einfahrtGemeldet.get(gleis)).contains(((ZugDetails)cache.getDetailCache().get(zid)).name)) {
         if (!this.einfahrtGemeldet.containsKey(gleis)) {
            this.einfahrtGemeldet.put(gleis, new HashSet());
         }

         ((Set)this.einfahrtGemeldet.get(gleis)).add(((ZugDetails)cache.getDetailCache().get(zid)).name);
      }

      if (this.einfahrten) {
         RewrittenDetails details = Rewriter.getInstance().rewrite((ZugDetails)cache.getDetailCache().get(zid), bahnhof);
         List<ZugFahrplanZeile> plan = (List)cache.getFahrplaene().get(zid);
         int startIndex = 0;

         for(int i = 0; i < plan.size(); ++i) {
            if (((ZugFahrplanZeile)plan.get(i)).gleis.equals(gleis)) {
               startIndex = i + 1;
               break;
            }
         }

         if (!((ZugFahrplanZeile)plan.get(startIndex - 1)).flags.hasFlag('D') && !Pattern.matches(Main.CONFIG.getIgnorePattern(), ((ZugDetails)cache.getDetailCache().get(zid)).name)) {
            if (!Pattern.matches(bahnhof.getEndeRegex(), ((ZugDetails)cache.getDetailCache().get(zid)).nach) && !Pattern.matches(bahnhof.getEndeRegex(), details.nach)) {
               String vias = "";
               if (details.rewritten) {
                  vias = details.vias;
               } else {
                  for(int i = startIndex; i < startIndex + 3; ++i) {
                     String viaBhf = null;
                     if (viaBhf == null && plan.size() > i) {
                        vias = vias + " - " + ((ZugFahrplanZeile)plan.get(i)).gleis;
                     }

                     if (viaBhf != null && ((String)viaBhf).length() > 0) {
                        vias = vias + " - " + viaBhf;
                     }
                  }

                  if (vias.length() > 0) {
                     vias = vias.substring(3);
                  }
               }

               if (!Pattern.matches(bahnhof.getEndeRegex(), details.von) && !details.von.equals(details.nach)) {
                  SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("einfahrt-normal", details, (ZugFahrplanZeile)plan.get(startIndex - 1), vias));
               } else {
                  Main.client.haltGemeldet(zid, gleis);
                  if (((ZugDetails)cache.getDetailCache().get(zid)).amgleis) {
                     this.haltZeiten.put(zid, Main.client.getSimutime());
                  }

                  SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("einfahrt-bereitstellung", details, (ZugFahrplanZeile)plan.get(startIndex - 1), vias));
               }

               synchronized(SoundPlayer.getInstance()) {
                  SoundPlayer.getInstance().notify();
               }
            } else {
               SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("einfahrt-endend", details, (ZugFahrplanZeile)plan.get(startIndex - 1)));
               synchronized(SoundPlayer.getInstance()) {
                  SoundPlayer.getInstance().notify();
               }

               if (this.anschluesse) {
                  long anschlussZeit = (((ZugFahrplanZeile)plan.get(startIndex - 1)).an - Main.client.getSimutime()) / 1000L + (long)(details.verspaetung * 60);
                  if (anschlussZeit < 0L) {
                     anschlussZeit = 0L;
                  }
               }
            }
         } else {
            SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("durchfahrt", details));
            synchronized(SoundPlayer.getInstance()) {
               SoundPlayer.getInstance().notify();
            }
         }
      }

   }

   public void idleAction(DataCache cache) {
      if (this.verspaetungen) {
         Iterator var2 = cache.getBelegung().keySet().iterator();

         label230:
         while(true) {
            long simutime;
            String gleis;
            Bahnhof bahnhof;
            List planBelegung;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               gleis = (String)var2.next();
               bahnhof = null;
               Iterator var5 = this.config.getBahnhoefe().iterator();

               while(var5.hasNext()) {
                  Bahnhof bhf = (Bahnhof)var5.next();
                  if (Pattern.matches(bhf.getGleiseRegex(), gleis)) {
                     bahnhof = bhf;
                     break;
                  }
               }

               if (bahnhof == null) {
                  throw new RuntimeException("Bahnhof not found");
               }

               simutime = Main.client.getSimutime();
               planBelegung = (List)cache.getBelegung().get(gleis);
            } while(planBelegung == null);

            Collections.sort(planBelegung, new ZugBelegung.DelayComparator());
            Iterator var8 = planBelegung.iterator();

            while(true) {
               ZugBelegung zugBelegung;
               RewrittenDetails details;
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       do {
                                          do {
                                             do {
                                                do {
                                                   do {
                                                      if (!var8.hasNext()) {
                                                         continue label230;
                                                      }

                                                      zugBelegung = (ZugBelegung)var8.next();
                                                   } while(zugBelegung.zeile == null);
                                                } while(zugBelegung.zug == null);
                                             } while(Pattern.matches(Main.CONFIG.getIgnorePattern(), zugBelegung.zug.name));

                                             if (!zugBelegung.zeile.flags.hasFlag('D') && !zugBelegung.zeile.gleis.equals(zugBelegung.zeile.plan) && (zugBelegung.zeile.an + (long)(zugBelegung.zug.verspaetung * '\uea60') > simutime && (zugBelegung.zeile.an + (long)(zugBelegung.zug.verspaetung * '\uea60') - simutime) / 60000L <= 15L || zugBelegung.zeile.ab + (long)(zugBelegung.zug.verspaetung * '\uea60') > simutime && (zugBelegung.zeile.ab + (long)(zugBelegung.zug.verspaetung * '\uea60') - simutime) / 60000L <= 15L) && (!this.gleiswechselAnsageZeiten.containsKey(gleis) || !((Map)this.gleiswechselAnsageZeiten.get(gleis)).containsKey(zugBelegung.zug.name) || simutime - (Long)((Map)this.gleiswechselAnsageZeiten.get(gleis)).get(zugBelegung.zug.name) >= 300000L)) {
                                                details = Rewriter.getInstance().rewrite(zugBelegung.zug, bahnhof);
                                                if (!this.gleiswechselAnsageZeiten.containsKey(gleis)) {
                                                   this.gleiswechselAnsageZeiten.put(gleis, new HashMap());
                                                }

                                                ((Map)this.gleiswechselAnsageZeiten.get(gleis)).put(details.name, simutime);
                                                if (!Pattern.matches(bahnhof.getEndeRegex(), details.nach) && !details.nach.equals("")) {
                                                   if (zugBelegung.zug.verspaetung >= 5) {
                                                      SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("gleiswechsel-normal-verspaetet", details, zugBelegung.zeile));
                                                   } else {
                                                      SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("gleiswechsel-normal-normal", details, zugBelegung.zeile));
                                                   }
                                                } else if (zugBelegung.zug.verspaetung >= 5) {
                                                   SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("gleiswechsel-endend-verspaetet", details, zugBelegung.zeile));
                                                } else {
                                                   SoundPlayer.getInstance().addText(SoundPlayer.Priority.HIGH, Main.CONFIG.getAnsage("gleiswechsel-endend-normal", details, zugBelegung.zeile));
                                                }

                                                synchronized(SoundPlayer.getInstance()) {
                                                   SoundPlayer.getInstance().notify();
                                                }
                                             }

                                             if (!zugBelegung.zeile.flags.hasFlag('D') && (zugBelegung.zeile.an + (long)(zugBelegung.zug.verspaetung * '\uea60') > simutime && (zugBelegung.zeile.an - simutime) / 60000L <= 15L || zugBelegung.zeile.ab + (long)(zugBelegung.zug.verspaetung * '\uea60') > simutime && (zugBelegung.zeile.ab - simutime) / 60000L <= 15L) && zugBelegung.zug.verspaetung >= 5 && (!this.einfahrtGemeldet.containsKey(gleis) || !((Set)this.einfahrtGemeldet.get(gleis)).contains(zugBelegung.zug.name)) && (!this.verspaetungsAnsageZeiten.containsKey(gleis) || !((Map)this.verspaetungsAnsageZeiten.get(gleis)).containsKey(zugBelegung.zug.name) || simutime - (Long)((Map)this.verspaetungsAnsageZeiten.get(gleis)).get(zugBelegung.zug.name) >= 300000L)) {
                                                details = Rewriter.getInstance().rewrite(zugBelegung.zug, bahnhof);
                                                if (!this.verspaetungsAnsageZeiten.containsKey(gleis)) {
                                                   this.verspaetungsAnsageZeiten.put(gleis, new HashMap());
                                                }

                                                ((Map)this.verspaetungsAnsageZeiten.get(gleis)).put(details.name, simutime);
                                                details.gleis = gleis;
                                                if (!Pattern.matches(bahnhof.getEndeRegex(), details.nach) && !details.nach.equals("")) {
                                                   SoundPlayer.getInstance().addText(SoundPlayer.Priority.LOW, Main.CONFIG.getAnsage("verspaetung-normal", details, zugBelegung.zeile));
                                                } else {
                                                   SoundPlayer.getInstance().addText(SoundPlayer.Priority.LOW, Main.CONFIG.getAnsage("verspaetung-endend", details, zugBelegung.zeile));
                                                }

                                                synchronized(SoundPlayer.getInstance()) {
                                                   SoundPlayer.getInstance().notify();
                                                }
                                             }
                                          } while(zugBelegung.zeile.flags.hasFlag('D'));
                                       } while(!zugBelegung.zug.amgleis);
                                    } while(zugBelegung.zug.gleis == null);
                                 } while(!this.einfahrtGemeldet.containsKey(gleis));
                              } while(!((Set)this.einfahrtGemeldet.get(gleis)).contains(zugBelegung.zug.name));
                           } while(!this.haltZeiten.containsKey(zugBelegung.zug.zid));
                        } while(simutime - (Long)this.haltZeiten.get(zugBelegung.zug.zid) < 180000L);
                     } while(this.stehendAnsageZeiten.containsKey(zugBelegung.zug.zid) && simutime - (Long)this.stehendAnsageZeiten.get(zugBelegung.zug.zid) < 300000L);

                     details = Rewriter.getInstance().rewrite(zugBelegung.zug, bahnhof);
                  } while(!details.rewritten);
               } while(!Pattern.matches(bahnhof.getEndeRegex(), zugBelegung.zug.nach) && !Pattern.matches(bahnhof.getEndeRegex(), details.nach));

               SoundPlayer.getInstance().addText(SoundPlayer.Priority.LOW, Main.CONFIG.getAnsage("stehend", details, zugBelegung.zeile, details.vias));
               synchronized(SoundPlayer.getInstance()) {
                  SoundPlayer.getInstance().notify();
               }
            }
         }
      }
   }

   public void zugHalt(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
      if (this.anschluesse && !Pattern.matches(Main.CONFIG.getIgnorePattern(), ((ZugDetails)cache.getDetailCache().get(zid)).name)) {
         try {
            cache = Main.client.getLastRunCache();
            StringBuilder ansage = new StringBuilder();
            RewrittenDetails details = Rewriter.getInstance().rewrite((ZugDetails)cache.getDetailCache().get(zid), bahnhof);
            details.gleis = gleis;
            this.haltZeiten.put(zid, Main.client.getSimutime());
            if (details.nach != null && !Pattern.matches(bahnhof.getEndeRegex(), details.nach)) {
               ansage.append(Main.CONFIG.getAnsage("halt-normal", details, (ZugFahrplanZeile)null, (String)null, bahnhof.getName()));
            } else {
               ansage.append(Main.CONFIG.getAnsage("halt-endend", details, (ZugFahrplanZeile)null, (String)null, bahnhof.getName()));
            }

            List<ZugBelegung> anschlussZuege = this.getAnschlusszuege(cache, bahnhof, gleis);
            if (!anschlussZuege.isEmpty()) {
               ansage.append(" ");
               ansage.append(Main.CONFIG.getAnsage("anschluesse", details));
               Iterator var8 = anschlussZuege.iterator();

               while(var8.hasNext()) {
                  ZugBelegung planzug = (ZugBelegung)var8.next();
                  details = Rewriter.getInstance().rewrite((ZugDetails)cache.getDetailCache().get(planzug.zug.zid), bahnhof);
                  ansage.append(" ");
                  if (planzug.zug.verspaetung >= 5) {
                     ansage.append(Main.CONFIG.getAnsage("anschluss-verspaetet", details, planzug.zeile));
                  } else {
                     ansage.append(Main.CONFIG.getAnsage("anschluss-normal", details, planzug.zeile));
                  }
               }
            }

            SoundPlayer.getInstance().addText(SoundPlayer.Priority.MEDIUM, ansage.toString());
            synchronized(SoundPlayer.getInstance()) {
               SoundPlayer.getInstance().notify();
            }
         } catch (Exception var12) {
            Logger.getAnonymousLogger().log(Level.WARNING, "AnschlussAnsage", var12);
         }
      }

   }

   public void zugAbfahrt(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
      if (this.anschluesse && !Pattern.matches(Main.CONFIG.getIgnorePattern(), ((ZugDetails)cache.getDetailCache().get(zid)).name)) {
         try {
            cache = Main.client.getLastRunCache();
            if (this.haltZeiten.containsKey(zid)) {
               this.haltZeiten.remove(zid);
            }

            if (cache.getDetailCache().get(zid) != null && Util.isFernzug(((ZugDetails)cache.getDetailCache().get(zid)).name)) {
               RewrittenDetails details = Rewriter.getInstance().rewrite((ZugDetails)cache.getDetailCache().get(zid), bahnhof);
               if (details.nach == null && Pattern.matches(bahnhof.getEndeRegex(), details.nach)) {
                  return;
               }

               SoundPlayer.getInstance().addText(SoundPlayer.Priority.MEDIUM, Main.CONFIG.getAnsage("abfahrt", details));
               synchronized(SoundPlayer.getInstance()) {
                  SoundPlayer.getInstance().notify();
               }
            }
         } catch (Exception var9) {
            Logger.getAnonymousLogger().log(Level.WARNING, "AbfahrtAnsage", var9);
         }
      }

   }

   private List<ZugBelegung> getAnschlusszuege(DataCache cache, Bahnhof bahnhof, String gleis) {
      List<ZugBelegung> ret = new ArrayList();
      Iterator var5 = cache.getBelegung().keySet().iterator();

      label59:
      while(true) {
         String nachbargleis;
         do {
            do {
               if (!var5.hasNext()) {
                  Collections.sort((List)ret, new ZugBelegung.DelayComparator());
                  int maxAnschluesse = Math.max(1, 5 - SoundPlayer.getInstance().getQueueLength());
                  if (((List)ret).size() > maxAnschluesse) {
                     ret = ((List)ret).subList(0, maxAnschluesse - 1);
                  }

                  return (List)ret;
               }

               nachbargleis = (String)var5.next();
            } while(!Pattern.matches(bahnhof.getGleiseRegex(), nachbargleis));
         } while(nachbargleis.equals(gleis));

         List<ZugBelegung> zuege = (List)cache.getBelegung().get(nachbargleis);
         Collections.sort(zuege, new ZugBelegung.DelayComparator());
         Iterator var8 = zuege.iterator();

         while(true) {
            ZugBelegung planzug;
            do {
               do {
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label59;
                        }

                        planzug = (ZugBelegung)var8.next();
                     } while(Pattern.matches(Main.CONFIG.getIgnorePattern(), planzug.zug.name));
                  } while(planzug.zeile.flags.hasFlag('D'));
               } while(planzug.zeile.ab <= 0L);
            } while(planzug.zeile.ab > Main.client.getSimutime() + 900000L && planzug.zeile.ab + (long)(planzug.zug.verspaetung * '\uea60') > Main.client.getSimutime() + 900000L);

            if (!Pattern.matches(bahnhof.getEndeRegex(), planzug.zug.nach)) {
               ((List)ret).add(planzug);
            }
         }
      }
   }

   public void keinZug(String gleis) {
   }
}
