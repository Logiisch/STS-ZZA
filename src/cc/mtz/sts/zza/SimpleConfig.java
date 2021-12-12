package cc.mtz.sts.zza;

import cc.mtz.sts.zza.data.Bahnhof;
import java.util.Iterator;
import java.util.List;

public class SimpleConfig extends StellwerkFile {
   public SimpleConfig(String bahnhof, List<String> bahnsteige) {
      StringBuilder gleiseRegex = new StringBuilder("^(");

      for (String bahnsteig : bahnsteige) {
         gleiseRegex.append(bahnsteig);
         gleiseRegex.append("|");
      }

      gleiseRegex.deleteCharAt(gleiseRegex.length() - 1);
      gleiseRegex.append(")$");
      this.bahnhoefe.add(new Bahnhof(bahnhof, gleiseRegex.toString(), "(Gleis " + gleiseRegex.toString().substring(1, gleiseRegex.toString().length() - 1) + "|" + bahnhof + ")"));
   }
}
