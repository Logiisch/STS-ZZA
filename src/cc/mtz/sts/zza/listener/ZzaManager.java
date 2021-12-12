package cc.mtz.sts.zza.listener;

import cc.mtz.sts.zza.Main;
import cc.mtz.sts.zza.Rewriter;
import cc.mtz.sts.zza.Util;
import cc.mtz.sts.zza.data.Bahnhof;
import cc.mtz.sts.zza.data.DataCache;
import cc.mtz.sts.zza.data.RewrittenDetails;
import cc.mtz.sts.zza.ui.Zza;
import cc.mtz.sts.zza.ui.ZzaSmall;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import js.java.stspluginlib.PluginClient.ZugDetails;
import js.java.stspluginlib.PluginClient.ZugFahrplanZeile;

public class ZzaManager implements StsListener {
   private int aNum = 0;
   private Bahnhof lastBhf;
   private final Map<String, Zza> windows = new HashMap<>();
   private final boolean minutengenau;

   public ZzaManager(boolean minutengenau) {
      this.minutengenau = minutengenau;
   }

   public void bahnsteigSetup(String bahnsteig, Bahnhof bahnhof) {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] gs = ge.getScreenDevices();
      DisplayMode dm = gs[gs.length - 1].getDisplayMode();
      Zza zza = new ZzaSmall();
      zza.setVisible(true);
      int windowsPerColumn = dm.getHeight() / zza.getHeight();
      if (bahnhof != this.lastBhf) {
         this.lastBhf = bahnhof;
         if (this.aNum > 0) {
            this.aNum += windowsPerColumn - this.aNum % windowsPerColumn;
         }
      }

      int column = this.aNum / windowsPerColumn;
      int row = this.aNum % windowsPerColumn;
      zza.setBounds(dm.getWidth() - (column + 1) * zza.getWidth(), row * zza.getHeight(), zza.getWidth(), zza.getHeight());
      ++this.aNum;
      zza.setGleis(bahnsteig.replaceFirst("[^0-9]*(.*)$", "$1"));
      zza.setTitle(bahnsteig);
      zza.update("", "", "", "", "");
      this.windows.put(bahnsteig, zza);
   }

   public void naechsterZug(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
      RewrittenDetails details = Rewriter.getInstance().rewrite(cache.getDetailCache().get(zid), bahnhof);
      List<ZugFahrplanZeile> plan = cache.getFahrplaene().get(zid);
      int startIndex = 0;

      for(int i = 0; i < plan.size(); ++i) {
         if (plan.get(i).gleis.equals(gleis)) {
            startIndex = i + 1;
            break;
         }
      }

      if (!plan.get(startIndex - 1).flags.hasFlag('D') && !Pattern.matches(Main.CONFIG.getIgnorePattern(), cache.getDetailCache().get(zid).name)) {
         if (!Pattern.matches(bahnhof.getEndeRegex(), cache.getDetailCache().get(zid).nach) && !Pattern.matches(bahnhof.getEndeRegex(), details.nach)) {
            String vias = "";
            if (details.rewritten) {
               vias = details.vias;
            } else {
               for(int i = startIndex; i < startIndex + 3; ++i) {
                  String viaBhf = null;
                  if (plan.size() > i) {
                     vias = vias + " - " + plan.get(i).gleis;
                  }

               }

               if (vias.length() > 0) {
                  vias = vias.substring(3);
               }
            }

            StringBuilder infoString = new StringBuilder();
            if (details.verspaetung >= 1) {
               infoString.append("+++ ");
               infoString.append(Util.verspaetungsText(details.verspaetung, this.minutengenau, "etwa "));
               infoString.append(" später");
               infoString.append(" +++");
            }

            this.windows.get(gleis).update(details.name, details.nach, plan.get(startIndex - 1).getFormattedAb(), vias, infoString.toString());
         } else {
            StringBuilder infoString = new StringBuilder("+++ Bitte nicht einsteigen");
            if (details.verspaetung >= 1) {
               infoString.append(" +++ ");
               infoString.append(Util.verspaetungsText(details.verspaetung, this.minutengenau, "etwa "));
               infoString.append(" später");
            }

            infoString.append(" +++");
            this.windows.get(gleis).update(details.name, "Von " + details.von, plan.get(startIndex - 1).getFormattedAn(), "Zug endet hier.", infoString.toString());
         }
      } else {
         this.windows.get(gleis).update("", "Achtung Zugdurchfahrt!", "", "", "");
      }

   }

   public void zugEinfahrt(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
   }

   public void idleAction(DataCache cache) {
   }

   public void zugHalt(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
   }

   public void zugAbfahrt(String gleis, Bahnhof bahnhof, int zid, DataCache cache) {
   }

   public void keinZug(String gleis) {
      this.windows.get(gleis).update("", "", "", "", "");
   }
}
