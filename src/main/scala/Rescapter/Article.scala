package Rescapter

import scala.io.Source


class Article() {
  var author: String = ""
  var eleAuthor: String = ""
  var url: String = ""
  var date: String = ""
  var eleHtml: String = "" 
  var eleText: String = ""
  var title: String = ""
  var perex: String = ""
  var section: String = ""
  //val imageUrls: List[String] = List()

  def createHtml(): String = {
    val possibleTmplFilePaths : List[String] =
      "src/main/resources/article-template.html" ::
        "article-template.html" ::
        "../../src/main/resources/article-template.html" ::
        Nil
    val t = Source.fromFile(Rescapter.getConfIfExists(possibleTmplFilePaths, "article-template.html")).mkString
    val a = new Article()
    a.fillToMatch()
    "xxxTOCTITLExxx".r.replaceAllIn(
    a.title.r.replaceAllIn(
      a.perex.r.replaceAllIn(
        a.section.r.replaceAllIn(
          a.url.r.replaceAllIn(
            a.eleText.r.replaceAllIn(
              a.date.r.replaceAllIn(
                a.eleAuthor.r.replaceAllIn(t, if (eleAuthor != null) eleAuthor else ""),
                if (date != null) date else ""),
              if (eleText != null) eleText else ""),
            if (url != null) url else ""),
          if (section != null) section else ""),
        if (perex!= null) perex else ""),
      if (title != null) title else ""),
      title.hashCode.toString)
  }

  def createTOCEntry() : String = {
    "<div class=\"TOCEntry\"><a href=\"#" + title.hashCode.toString + "\">" + section + ": " + title + "</a></div>"
  }

  def createNextEntry() : String = {
    "<div class=\"nextEntry\"><a href=\"#" + title.hashCode.toString + "\">" + "Další: " + title + "</a></div>"
  }


  def fillToMatch() : Article = {
    eleAuthor = "xxxELEAUTHORxxx"
    section = "xxxSECTIONxxx"
    title = "xxxTITLExxx"
    url = "xxxURLxxx"
    date = "xxxDATExxx"
    perex = "xxxPEREXxxx"
    eleText = "xxxELETEXTxxx"
    this
  }
  
  def fillAsBibi() : Article = {
    author = "Fareed Zakaria"
    eleAuthor = "<div class=\"author-image\">\n\t\t\t\t<div class=\"img\"><img src=\"http://img.ihned.cz/attachment.php/550/37970550/aistv35CDFGJKMNjk6Qceghqrxy0STwV/Zakaria_118.jpg\" width=\"115\" height=\"118\" alt=\"Fareed Zakaria\" title=\"Fareed Zakaria\" /></div>\n\t\t\t\t<div class=\"author\"><ul><li><a href=\"http://respekt.ihned.cz/?m=authors&person[id]=16183350&article[aut_id]=16183350\" class=\"page-color\"><span>Fareed Zakaria</span></a></li></ul></div>\n\t\t\t</div>"
    url = "view-source:http://respekt.ihned.cz/?p=R00000_d&article%5Bid%5D=63642850" 
    date = "8. 3. 2015 &nbsp; | aktualizace: 8. 3. 2015 &nbsp;11:50"
    perex = "Izraelský premiér Netanjahu se ve věci íránského jaderného programu chová jako věčně nedospělý Petr Pan"
    eleText = "<p>Vystoupení izraelského premiéra Benjamina Netanjahua před Kongresem Spojených států bylo výmluvné, dojemné a&nbsp;inteligentně pojmenovalo problémy spojené s&nbsp;případnou dohodou o&nbsp;íránském jaderném programu. Když ale Netanjahu začal popisovat alternativní řešení, ocitl se v&nbsp;zemi králů, princezen a&nbsp;víl. Načrtnutý scénář byl naprosto odtržený od&nbsp;reality a&nbsp;Kongres, který odměňoval bouřlivým potleskem každý premiérův nesplnitelný požadavek, se k&nbsp;jeho snění přidal.</p>\n<div class=\"reklama\"><div id=\"a-wallpaper-1-wrapper\">\n\t<div id=\"a-wallpaper-1-label\" class=\"a-title\">reklama</div>\n\t<div id=\"a-wallpaper-1\" class=\"a-content\">\n\t\t<script type=\"text/javascript\">\n\t\t\t_sashec_queue.push(['position','a-wallpaper-1',{size:'wallpaper', pos:'1', async: false}]);\n\t\t</script>\n\t</div>\n</div></div>\n<!-- znacka_konec_textu_zdarma -->\n<p>Netanjahu prohlásil, že Washington by měl odmítnout dohodu, o&nbsp;níž právě jedná, a&nbsp;žádat, aby Teherán demontoval téměř celý svůj jaderný program a&nbsp;zavázal se k&nbsp;tomu, že už nikdy žádný neobnoví. Ve&nbsp;světě podle Bibiho v&nbsp;takovém případě Číňané, Rusové a&nbsp;Evropané zajásají, přitvrdí sankce a&nbsp;vystupňují svůj tlak, což nakonec přivede Írán ke&nbsp;kapitulaci. &bdquo;Sny se plní, stačí, když si je přejeme dostatečně silně,&ldquo; říkával pohádkový hrdina, věčně nedospělý Petr Pan.</p>\n<p class=\"detail-mezititulek\">Poučení z&nbsp;minulosti</p>\n<p>Zkušenost nám ale naznačuje, že ve&nbsp;skutečnosti by situace vypadala jinak. V&nbsp;letech 2003&ndash;2005, když v&nbsp;Íránu vládl jiný pragmatický prezident Muhammad Chátamí, jednal Írán se třemi významnými členy Evropské unie o&nbsp;dohodě, podle níž měl být íránský program omezen a&nbsp;pod dohledem inspektorů. Hlavním tehdejším vyjednavačem byl Hasan Rúhání, dnešní íránský prezident.</p>\n<p>Írán tehdy navrhoval, že zásadně omezí počet svých centrifug, palivo bude obohacovat pouze hluboko pod úroveň nutnou k&nbsp;výrobě zbraní a&nbsp;existující obohacený uran promění v&nbsp;palivové tyče (jež není možné využít k&nbsp;vojenským účelům). Peter Jenkins, britský zástupce v&nbsp;Mezinárodní agentuře pro atomovou energii, později pro Inter Press Service prohlásil, že &bdquo;návrh na&nbsp;všechny zapůsobil&ldquo;. Rozhovory se nicméně zhroutily, protože je Bushova vláda, jejíž zájmy na&nbsp;jednáních zastupovali právě Britové, vetovala. Bylo prý jisté, vysvětloval Jenkins, že pokud Západ Íránce &bdquo;vyděsí&ldquo;, &bdquo;vzdají se&ldquo;.</p>\n<p>Jaký byl výsledek? Vrátil se Írán k&nbsp;jednacímu stolu a&nbsp;kapituloval? Nikoli, země sankce přestála a&nbsp;bez ohledu na&nbsp;inspekce svůj program masivně rozšířila. Počet íránských centrifug se z&nbsp;tehdejších 164 zvýšil na&nbsp;19&nbsp;tisíc, Írán nahromadil více než osm tun obohaceného uranového plynu a&nbsp;urychlil výstavbu těžkovodního reaktoru v&nbsp;Aráku, který je možné využít k&nbsp;výrobě plutonia pro vojenské účely.</p>\n<p>Graham Allison z&nbsp;Harvardovy univerzity, jeden z&nbsp;předních amerických odborníků na&nbsp;jaderné otázky, upozornil, že &bdquo;lpění na&nbsp;maximalistických požadavcích a&nbsp;odmítnutí potenciálních dohod, z&nbsp;nichž první by omezila počet íránských centrifug na&nbsp;164, způsobilo, že Írán, který byl od&nbsp;jaderné bomby vzdálen deset let, se k&nbsp;ní přiblížil na&nbsp;několik měsíců&ldquo;.</p>\n<p class=\"detail-mezititulek\">Otázky pro Bibiho</p>\n<p>Pokud se dnešní dohoda zhroutí, bude se podobný scénář nejspíše opakovat. Írán opět rozšíří svůj jaderný program. Pokud budou jiné světové mocnosti přesvědčeny, že íránský návrh byl seriózní a&nbsp;Američané a&nbsp;Izraelci ho potopili, budou s&nbsp;posilováním sankcí váhat &ndash; a&nbsp;všechny sankce začnou být časem tak jako tak děravé. Netanjahu se obává, že podle nynější dohody bude moci Írán za&nbsp;deset let svůj program obnovit. Bez dohody bude ale Írán za&nbsp;deset let pravděpodobně provozovat 50&nbsp;tisíc centrifug, bude disponovat obrovským množstvím vysoce obohaceného uranu, novými zařízeními a&nbsp;tisíci zkušených jaderných vědců a&nbsp;techniků, vedle plně funkčního reaktoru schopného produkovat plutonium. Co bude Bibi dělat potom?</p>\n<p>Teorie, že Írán vystavený neustálému tlaku nakonec ustoupí, ignoruje základní fakta. Írán je hrdá, nacionalisticky naladěná země. Přežil 36 let západních sankcí bez ohledu na&nbsp;to, zda byly ceny ropy právě vysoké nebo nízké. Překonal osmiletou válku s&nbsp;Irákem, v&nbsp;níž ztratil odhadem půl milionu mužů. Jaderný program je v&nbsp;Íránu populární, dokonce i&nbsp;mezi vůdci prodemokratického hnutí.</p>\n<img title=\"\" src=\"http://img.ihned.cz/attachment.php/90/55298090/RTBo4Hq7DOgrwU6WEe0cKxL3jVzbJGv9/Netanjahu.jpg\" alt=\"\" width=\"150\" height=\"116\" align=\"left\" />\n<p>Jak upozornil zmíněný Graham Allison, Írán již má kapacitu dokončit program jaderných zbraní, získal ji v&nbsp;roce 2008, když zvládl technologii výroby vlastních centrifug a&nbsp;obohacování uranu. Přesto Írán zatím jadernou zbraň nemá. Netanjahu již 25 let nepřetržitě tvrdí, že Írán stojí na&nbsp;prahu výroby nukleárních zbraní. V&nbsp;roce 1996 &ndash; tedy před 19 lety &ndash; kvůli tomu oslovil americký Kongres v&nbsp;podstatě s&nbsp;úplně stejnou argumentací, s&nbsp;jakou vystoupil nyní. Posledních deset let pravidelně tvrdí, že Írán dělí od&nbsp;bomby jeden rok.</p>\n<p class=\"detail-mezititulek\">Pragmatičtí ajatolláhové</p>\n<p>Proč jsou tedy Bibiho předpovědi již 25 let mylné? Částečně za&nbsp;to může západní a&nbsp;izraelská sabotáž, která íránský program přibrzdila. Tajné služby si ale nemohou připsat zásluhy za&nbsp;větší zdržení než několika let. Důležitější je pravděpodobně skutečnost, že si Írán uvědomuje, že kdyby skutečně bombu sestrojil, bude čelit zásadním mezinárodním důsledkům. Jinými slovy, ajatolláhové si správně spočítali, že se jim dokončení programu nevyplatí. Klíčem k&nbsp;jakékoli dohodě s&nbsp;Íránem je udržet náklady na&nbsp;dokončení programu co nejvyšší a&nbsp;zisky co nejnižší. To je nejrealističtější cesta k&nbsp;tomu, aby se Írán nestal jadernou mocností. Nikoli snění Petra Pana.&nbsp;</p>\n<p align=\"right\"><em>Autor je americký </em><br /><em> politolog a&nbsp;publicista.</em></p>\n<p align=\"right\"><em>&copy; 2015, Washington Post </em><br /><em> Writers Group</em></p>"
    title = "Bibiho snění"
    section = "Komentáře"
    this
  }
  
  override def toString : String = {
    "title: " + title + "\n" +
      "author: " + author + "\n" +
      "eleAuthor: " + eleAuthor + "\n" +
      "date: " + date + "\n" +
      "url: " + url + "\n" +
      "section: " + section + "\n" +
      "perex: " + perex + "\n" +
      "eleText: " + eleText + "\n"
  } 
}
