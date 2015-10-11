package Rescapter

import java.io.{File, FileOutputStream, OutputStreamWriter}
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, GregorianCalendar}

import com.typesafe.config.ConfigFactory
import org.openqa.selenium.{By, WebElement}

import scala.collection.JavaConverters._
import scala.io.Source

object Rescapter {

  val conf = ConfigFactory.parseFile(new File("D:\\projects\\rescapter\\src\\main\\resources\\rescapter.conf"))
  val loginName = conf.getString("respekt-login.user")
  val loginPasswd = conf.getString("respekt-login.password")
  val urlCurrentIssue = "http://respekt.cz/tydenik/2015/32" 
  val pathDownloads = System.getProperty("user.home") 
  val xAx = "xxxARTICLExxx"
  val xTx = "xxxTOCxxx"
  val xIx = "xxxIIxxx"
  val xYx = "xxxYYYYxxx"
  
  val outputNoPictureFilePath="D:\\projects\\rescapter\\out\\Respekt_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date) + "-noPic.html"
  val outputPictureFilePath="D:\\projects\\rescapter\\out\\Respekt_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date) + "-Pic.html"
  
  def main(args: Array[String]): Unit = {
    
    /*val parser = new OptionParser[Config]("rescapter") {
      head("rescapter")
      opt[Int]('i', "issue") action { (x, c) =>
        c.copy(issue = x) } text("issue: optional issue's number")
      opt[Int]('y', "year") action { (x, c) =>
        c.copy(issue = x) } text("year: optional")
      opt[File]('o', "out") required() valueName("<file>") action { (x, c) =>
        c.copy(out = x) } text("out: required output file path")
        
      }

    parser.parse(args, Config()) match {
      case Some(config) =>
      // do stuff

      case None =>
      // arguments are bad, error message will have been displayed
    }*/ 
    
    val (issueNum, issueYear) = getCurrentIssueNumAndYear()
    val namedIssueHtml = nameIssue(downloadCurrentIssue(), issueNum, issueYear)
    val tagsToRemove1 = List("SCRIPT", "I class=\"image", "DIV class=\"ad view-banner\"")
    val cleanedIssueHtml = (namedIssueHtml :: tagsToRemove1).reduce((x,y) => {removeTags(x,y)})
    saveIssueHtml(cleanedIssueHtml, outputPictureFilePath)
    val tagsToRemove2 = List("SOURCE ","SVG ")
    val noPicIssueHtml = (cleanedIssueHtml :: tagsToRemove2).reduce((x,y) => {removeTags(x,y)})
    saveIssueHtml(noPicIssueHtml,outputNoPictureFilePath)
  }

  def downloadArticlesFromTOC(TOCUrl: String): String = {
    logInfo("Beggining the application.")
    val htmlDriver = new HtmlLoginDriver()
    val loginUrl = "http://www.respekt.cz/uzivatel/prihlaseni"
    htmlDriver.get(loginUrl)
    htmlDriver.login(loginName, loginPasswd)
    logInfo("Login completed")
    htmlDriver.get(TOCUrl)

    val articleAHrefs = htmlDriver.findElements(By.className("issuedetail-categorized-item"))
    articleAHrefs.addAll(htmlDriver.findElements(By.className("issuedetail-highlighted-item")))

    logInfo("Issue has " + articleAHrefs.size() + " articles. Downloading ...")
    val articles = articleAHrefs
      .asScala.toList
      .map((x: WebElement) => x.getAttribute("href"))
      .map(x => htmlDriver.getArticle(x))
    val articleTOCEntries = articles.map(a => a.makeTOCEntry())
    val articleHtmls = articles.map(x => x.createHtml())
    logInfo("Created htmls from articles")
    val issueHtml = makeTOC(articleTOCEntries, makeIssueFromArticles(articleHtmls))
    issueHtml
  }
   
  def saveIssueHtml(issueHtml : String, path : String) : Unit = { 
    val out = new FileOutputStream(new File(path))
    val writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)
    writer.write(issueHtml)
    writer.close()
    logInfo("Created html at "+ path +".")
  }

  def makeIssueFromArticles(articleHtmls: List[String]) : String = {
    val t = Source.fromFile("D:\\projects\\rescapter\\src\\main\\resources\\issue-template.html").mkString
    val all = (t::articleHtmls).reduce((x,y) => {xAx.r replaceFirstIn(x, y+"\n" + xAx)})
    xAx.r replaceFirstIn(all, "")
  }

  def makeTOC(articleTOCEntries: List[String], content : String) : String = {
    val all = (content::articleTOCEntries).reduce((x,y) => {xTx.r replaceFirstIn(x, y+"\n" + xTx)})
    xTx.r replaceFirstIn(all, "")
  }
  
  def nameIssue(content : String, issueNum : Int, issueYear : Int) : String = {
    xYx.r replaceFirstIn(
      xIx.r replaceFirstIn(
        content, 
        issueNum.toString), 
      issueYear.toString)
  }


  def downloadIssue(issYear: Int, issNum: Int): String  = {
    val url = "http://respekt.cz/tydenik/" + issYear + "/" + issNum
    downloadArticlesFromTOC(url)
  }

  def downloadCurrentIssue(): String = {
    val (issueNum, issueYear) = getCurrentIssueNumAndYear()
    downloadIssue(issueYear, issueNum)
  }
  
  def getCurrentIssueNumAndYear() : (Int, Int) = {
    val cal = new GregorianCalendar()
    cal.add(Calendar.DAY_OF_YEAR, 3)
    cal.get(Calendar.YEAR)
    cal.get(Calendar.WEEK_OF_YEAR)
    (cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR))
  }
  
  def removeTags(content : String, tagName : String) : String = {
    //val regex = "<" + tagName + "[a-z,A-Z,á-ž,Á-Ž,0-9, ,\\-,_,=,\",',/,\\(,\\),\\.,:,%]*>([\\s\\S]*?)</" + tagName.split(" ")(0) + ">"
    val regex = "<" + tagName + "([\\s\\S]*?)</" + tagName.split(" ")(0) + ">"
    regex.r replaceAllIn(content, "")
  }
  
  def logInfo(msg : String): Unit ={
    log(msg, "INFO")
  }

  def logError(msg : String): Unit ={
    log(msg, "ERROR")
  }

  def log(msg : String, msgType: String): Unit ={
    println(""+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((new Date)) + "\t" + msgType + ": " + msg)
  }


//  case class Config(issue: Int = -1, year: Int = -1, out: File = new File("."))
  
}
