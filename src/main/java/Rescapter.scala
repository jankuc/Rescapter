import java.io.File
import com.typesafe.config.ConfigFactory
import org.openqa.selenium.{WebElement, By}
import scala.collection.JavaConverters._ 

object Rescapter {

  val conf = ConfigFactory.parseFile(new File("D:\\projects\\rescapter\\src\\main\\resources\\rescapter.conf"))
  val loginName = conf.getString("respekt-login.user")
  val loginPasswd = conf.getString("respekt-login.password")
  val urlCurrentIssue = "http://respekt.ihned.cz/aktualni-cislo" 
  val pathDownloads = System.getProperty("user.home") 
  
  def main(args: Array[String]): Unit ={
        
    def downloadArticlesFromTOC(TOCUrl: String): Unit ={
      val htmlDriver = new HtmlLoginDriver()
      htmlDriver.get(TOCUrl)
      htmlDriver.login(loginName, loginPasswd)
      val articleUrls : List[String] = htmlDriver.findElements(By.cssSelector("div.ow-enclose > div.ow > h2 > a"))
        .asScala.toList.map( (x : WebElement) => x.getAttribute("href") )
      //htmlDriver.setJavascriptEnabled(true)
      val articles : List[Article] = articleUrls.map(x => htmlDriver.getArticle(x))
    }

    def downloadIssue(year: Int, yearsIssue: Int): Unit = {
      val url = "http://respekt.ihned.cz/?p=RA000A&archive[edition_number]=" + yearsIssue + "&archive[year]=" + year
      downloadArticlesFromTOC(url)
    }

    def downloadCurrentIssue(): Unit = {
      downloadArticlesFromTOC(urlCurrentIssue)
    }
     
  }
}
