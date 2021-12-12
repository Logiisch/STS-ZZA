package cc.mtz.sts.zza.data;

import js.java.stspluginlib.PluginClient.ZugDetails;

public class RewrittenDetails {
   public String gleis;
   public String nach;
   public String name;
   public String plangleis;
   public boolean sichtbar;
   public int verspaetung;
   public String von;
   public int zid;
   public String vias;
   public boolean rewritten;

   public RewrittenDetails(ZugDetails originalDetails) {
      this.gleis = originalDetails.gleis;
      this.nach = originalDetails.nach;
      this.name = originalDetails.name;
      this.plangleis = originalDetails.plangleis;
      this.sichtbar = originalDetails.sichtbar;
      this.verspaetung = originalDetails.verspaetung;
      this.von = originalDetails.von;
      this.zid = originalDetails.zid;
      this.vias = "";
      this.rewritten = false;
   }
}
