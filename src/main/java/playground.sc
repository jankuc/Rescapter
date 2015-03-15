
import java.io._
import java.net.URL
import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils
import org.openqa.selenium.{JavascriptExecutor, By, WebDriver, WebElement}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

val conf = ConfigFactory.parseFile(new File("D:\\projects\\rescapter\\src\\main\\resources\\rescapter.conf"))

val loginName = conf.getString("respekt-login.user")
val loginPwd = conf.getString("respekt-login.password")
val link = "http://respekt.ihned.cz/aktualni-cislo"
val linkBibi = "http://respekt.ihned.cz/?p=R00000_d&article%5Bid%5D=63642850"

val htmlDriver = new HtmlLoginDriver()
htmlDriver.get(link)
htmlDriver.login(loginName, loginPwd)
//val articleUrls : List[String] = htmlDriver.findElements(By.cssSelector("div.ow-enclose > div.ow > h2 > a")).asScala.toList.map( (x : WebElement) => x.getAttribute("href") )

val art1 = new Article()
htmlDriver.get(linkBibi)
//val source = htmlDriver.getPageSource()

//val perex = htmlDriver.executeScript("return arguments[0].innerHTML", htmlDriver.findElement(By.id("perex"))).asInstanceOf[String]
val rubrika = htmlDriver.getInnerHtmlOfElement(htmlDriver.findElement(By.cssSelector(" #heading > div.l > a > span ")))


htmlDriver.getArticle(linkBibi)
/*
if (false) {
  htmlDriver.login(loginName, loginPwd)
  htmlDriver.get(linkBibi)
  val inStream: InputStream = IOUtils.toInputStream(htmlDriver.getPageSource(), StandardCharsets.UTF_8)
  htmlDriver.setJavascriptEnabled(true)
  val textElement = htmlDriver.findElement(By.id("text"))
  val textSource = htmlDriver.executeScript("return arguments[0].innerHTML", textElement).asInstanceOf[String]
}
*/
//val jsExecutor : JavascriptExecutor = htmlDriver.asInstanceOf[JavascriptExecutor]
//val textSourceJS = jsExecutor.executeScript("return arguments[0].innerHTML;", textElement).asInstanceOf[String]

