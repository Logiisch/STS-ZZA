package cc.mtz.sts.zza.data;

public class Bahnhof {
   private String name;
   private String gleiseRegex;
   private String endeRegex;

   public Bahnhof(String name, String gleiseRegex, String endeRegex) {
      this.name = name;
      this.gleiseRegex = gleiseRegex;
      this.endeRegex = endeRegex;
   }

   public String getEndeRegex() {
      return this.endeRegex;
   }

   public void setEndeRegex(String endeRegex) {
      this.endeRegex = endeRegex;
   }

   public String getGleiseRegex() {
      return this.gleiseRegex;
   }

   public void setGleiseRegex(String gleiseRegex) {
      this.gleiseRegex = gleiseRegex;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
