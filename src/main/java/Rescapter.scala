import java.io.{PrintWriter, File}
import com.typesafe.config.ConfigFactory
import org.openqa.selenium.{WebElement, By}
import scala.collection.JavaConverters._
import scala.io.Source
import java.util.Date
import java.text.SimpleDateFormat

object Rescapter {

  val conf = ConfigFactory.parseFile(new File("D:\\projects\\rescapter\\src\\main\\resources\\rescapter.conf"))
  val loginName = conf.getString("respekt-login.user")
  val loginPasswd = conf.getString("respekt-login.password")
  val urlCurrentIssue = "http://respekt.ihned.cz/aktualni-cislo" 
  val pathDownloads = System.getProperty("user.home") 
  val xAx = "xxxARTICLExxx"
  
  val outputFilePath="D:\\projects\\rescapter\\target\\Respekt_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm").format((new Date)) + ".htm"
  
  def main(args: Array[String]): Unit ={
    downloadCurrentIssue()
  }

  def downloadArticlesFromTOC(TOCUrl: String): Unit ={
    val htmlDriver = new HtmlLoginDriver()
    htmlDriver.get(TOCUrl)
    htmlDriver.login(loginName, loginPasswd)
    htmlDriver.get(TOCUrl)
    logInfo("Beggining the application.")
    logInfo("Issue has " + htmlDriver.findElements(By.cssSelector("#main > div.col12 > div > * > h2 > a")).size() + " articles. Downloading ...")
    val articleHtmls : List[String] = htmlDriver.findElements(By.cssSelector("#main > div.col12 > div > * > h2 > a"))
      .asScala.toList
      .map( (x : WebElement) => x.getAttribute("href") )
      .map(x => htmlDriver.getArticle(x))
      .map(x => x.createHtml())
    logInfo("Created htmls from articles")
    val issueHtml = makeIssueFromArticles(articleHtmls)
    val writer = new PrintWriter(new File(outputFilePath))
    writer.write(issueHtml)
    writer.close()
    logInfo("Created html at "+ outputFilePath +".")
  }

  def makeIssueFromArticles(articleHtmls: List[String]) : String = {
    val t = Source.fromFile("D:\\projects\\rescapter\\src\\main\\resources\\issue-template.html").mkString
    val all = (t::articleHtmls).reduce((x,y) => {xAx.r replaceFirstIn(x, y+"\n" + xAx)})
    xAx.r replaceFirstIn(all, "")
  }

  def downloadIssue(year: Int, yearsIssue: Int): Unit = {
    val url = "http://respekt.ihned.cz/?p=RA000A&archive[edition_number]=" + yearsIssue + "&archive[year]=" + year
    downloadArticlesFromTOC(url)
  }

  def downloadCurrentIssue(): Unit = {
    downloadArticlesFromTOC(urlCurrentIssue)
  }
  
  def logInfo(msg : String): Unit ={
    println(new Date + " INFO: " + msg)
  }

  def logError(msg : String): Unit ={
    println(new Date + " ERROR: " + msg)
  }
}
