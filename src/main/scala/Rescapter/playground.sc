package Rescapter

import java.io._
import java.net.URL
import java.nio.charset.StandardCharsets
import java.sql.DriverManager
import java.util.{Calendar, GregorianCalendar}
import org.apache.commons.io.IOUtils
import org.openqa.selenium.{JavascriptExecutor, By, WebDriver, WebElement}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.io.Source
new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date)
/*
val conf = ConfigFactory.parseFile(new File("D:\\projects\\rescapter\\src\\main\\resources\\rescapter.conf"))
val loginName = conf.getString("respekt-login.user")
val loginPwd = conf.getString("respekt-login.password")
// TODO: find the right links for TOC and some non restricted article for testing
*/
//val htmlDriver = new HtmlLoginDriver()

//val rozhovorURL="http://www.respekt.cz/tydenik/2015/39/kontrolujte-co-jite"

//htmlDriver.get(rozhovorURL)
//htmlDriver.findElement(By.className("//div[@class='authorship-names']//*")).getText
//htmlDriver.findElement(By.className("//div[@class='authorship-names']")).getText
//htmlDriver.findElement(By.cssSelector("//div[@class='authorship-names']//a")).getText
//htmlDriver.findElement(By.className("authorship-names"))
//htmlDriver.findElement(By.className("authorship-names"))

//htmlDriver.findElement(By.xpath("//meta[@name=\"author\"]")).getAttribute("content")
//htmlDriver.findElement(By.xpath("//meta[@name=\"description\"]")).getAttribute("content")
//val art = htmlDriver.getArticle(rozhovorURL)
//art.toString
//val as = htmlDriver.getArticle(rozhovorURL)
//htmlDriver.get(link)
//htmlDriver.login(loginName, loginPwd)
//val articleUrls : List[String] = htmlDriver.findElement(By.cssSelector("div.ow-enclose > div.ow > h2 > a")).asScala.toList.map( (x : WebElement) => x.getAttribute("href") )
//val art2 = htmlDriver.getArticle(linkBibi)

val src = Source.fromFile("D:/projects/rescapter/target/classes/artEx-IS.xml").mkString
//val regex = "<script>([\\s\\S]*?)</script>"
val REGEX = "<SCRIPT>([\\s\\S]*?)</SCRIPT>"
//regex.r replaceAllIn(src,"")
REGEX.r replaceAllIn(src,"")

