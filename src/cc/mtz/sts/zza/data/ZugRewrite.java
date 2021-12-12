package cc.mtz.sts.zza.data;

import java.util.List;
import java.util.regex.Pattern;
import js.java.stspluginlib.PluginClient.ZugDetails;

public class ZugRewrite {
   private String zug;
   private String simStart;
   private String simEnde;
   private String start;
   private String ende;
   private List<String> vias;

   public ZugRewrite() {
   }

   public ZugRewrite(String zug, String simStart, String simEnde, String start, String ende) {
      this.zug = zug;
      this.simStart = simStart;
      this.simEnde = simEnde;
      this.start = start;
      this.ende = ende;
   }

   public ZugRewrite(String zug, String simStart, String simEnde, String start, String ende, List<String> vias) {
      this.zug = zug;
      this.simStart = simStart;
      this.simEnde = simEnde;
      this.start = start;
      this.ende = ende;
      this.vias = vias;
   }

   public ZugRewrite(String zug, String simStart, String simEnde) {
      this.zug = zug;
      this.simStart = simStart;
      this.simEnde = simEnde;
   }

   public String getEnde() {
      return this.ende;
   }

   public void setEnde(String ende) {
      this.ende = ende;
   }

   public String getSimEnde() {
      return this.simEnde;
   }

   public void setSimEnde(String simEnde) {
      this.simEnde = simEnde;
   }

   public String getSimStart() {
      return this.simStart;
   }

   public void setSimStart(String simStart) {
      this.simStart = simStart;
   }

   public String getStart() {
      return this.start;
   }

   public void setStart(String start) {
      this.start = start;
   }

   public List<String> getVias() {
      return this.vias;
   }

   public void setVias(List<String> vias) {
      this.vias = vias;
   }

   public String getZug() {
      return this.zug;
   }

   public void setZug(String zug) {
      this.zug = zug;
   }

   public boolean matches(ZugDetails zug) {
      return Pattern.matches(this.zug, zug.name) && (Pattern.matches(this.simStart, zug.von) || zug.von == null) && (Pattern.matches(this.simEnde, zug.nach) || zug.nach == null);
   }

   public RewrittenDetails rewrite(ZugDetails zug) {
      RewrittenDetails rewritten = new RewrittenDetails(zug);
      if (this.start != null) {
         rewritten.von = this.start;
      }

      if (this.ende != null) {
         rewritten.nach = this.ende;
      }

      return rewritten;
   }
}
