package Rescapter

import java.io.{File, FileOutputStream, OutputStreamWriter}
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, GregorianCalendar}

import com.typesafe.config.ConfigFactory
import org.openqa.selenium.{By, WebElement}

import scala.collection.JavaConverters._
import scala.io.Source

import scala.sys.process._

object Rescapter {

  val conf = ConfigFactory.parseFile(new File("D:\\projects\\rescapter\\src\\main\\resources\\rescapter.conf"))
  val loginName = conf.getString("respekt-login.user")
  val loginPasswd = conf.getString("respekt-login.password")
  val pathToPandocExe = conf.getString("system-vars.path_to_pandoc_exe")
  val pathToKindlegenExe = conf.getString("system-vars.path_to_kindlegen_exe")
  
  val urlCurrentIssue = "http://respekt.cz/tydenik/2015/32" 
  val pathDownloads = System.getProperty("user.home") 
  val xAx = "xxxARTICLExxx"
  val xTx = "xxxTOCxxx"
  val xIx = "xxxIIxxx"
  val xYx = "xxxYYYYxxx"
  val xNx = "xxxNEXTxxx"
  
  val dateTime =  new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date)
  
  val outDirPath = "D:\\projects\\rescapter\\out\\"
  val outputNoPictureFilePath= outDirPath + "Respekt_" + dateTime + "-noPic.html"
  val outputPictureFilePath= outDirPath + "Respekt_" + dateTime + "-Pic.html"
  
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
    val outputEpubFilePath = outDirPath + "Respekt_" + issueYear + "_" + issueNum + ".epub"
    val namedIssueHtml = nameIssue(downloadCurrentIssue(), issueNum, issueYear)
    
    // Saves the issue with Pictures
    val tagsToRemove1 = List("SCRIPT", "I class=\"image", "DIV class=\"ad view-banner\"", "DIV class=ad-content")
    val cleanedIssueHtml = (namedIssueHtml :: tagsToRemove1).reduce((x,y) => {removeTags(x,y)})
    saveIssueHtml(cleanedIssueHtml, outputPictureFilePath)

    // Saves the issue without some of the Pictures
    val tagsToRemove2 = List("SOURCE ","SVG ")
    val noPicIssueHtml = (cleanedIssueHtml :: tagsToRemove2).reduce((x,y) => {removeTags(x,y)})
    saveIssueHtml(noPicIssueHtml,outputNoPictureFilePath)

    // Creates epub and mobi editions
    val epubCreated = pathToPandocExe + " -t epub " + outputNoPictureFilePath + 
      " --toc-depth=2 -o  " + outputEpubFilePath !
    val mobiCreated = pathToKindlegenExe + " " + outputEpubFilePath !
    
  }

  def downloadArticlesFromTOC(TOCUrl: String): String = {
    logInfo("Beggining the application.")
    val htmlDriver = new HtmlLoginDriver()
    val loginUrl = "http://www.respekt.cz/uzivatel/prihlaseni"
    htmlDriver.get(loginUrl)
    htmlDriver.login(loginName, loginPasswd)
    logInfo("Login completed")
    htmlDriver.get(TOCUrl)

    // get links to all articles in this issue
    val articleAHrefs = htmlDriver.findElements(By.className("issuedetail-highlighted-item"))
    articleAHrefs.addAll(htmlDriver.findElements(By.className("issuedetail-categorized-item")))
    
    // get all the article contents
    logInfo("Issue has " + articleAHrefs.size() + " articles. Downloading ...")
    val articles : List[Article] = articleAHrefs
      .asScala.toList
      .map((x: WebElement) => x.getAttribute("href"))
      .map((y: String) => htmlDriver.getArticle(y))
    
    // list of sections in the correct order. Everything not specified will be added at the end of the list
    val sectionsList = List("Editorial", "Despekt", "Anketa", "Dopis z", "Lidé", "Dopisy", "Komentáře", "Téma", "Fokus", "Rozhovor", "Kultura", "Civilizace", "Místa, kde se potkáváme", "Od věci", "Jeden den v životě", "Komiks", "Minulý týden", "Připravujeme")
    
    // TODO: Warning: 'Kultura * Komiks' is twice in the filtering, Tema can be too, if it's e.g. Kultura -> make unique and/or enable wiser sectionList (tuples (stringItHastoContain,StringItCannotContain) ) 
    val knownSectionArticles = sectionsList.map(sec => articles.filter(_.section.contains(sec))) flatten
    val unknownSectionArticles  = articles.filter(art => sectionsList.forall(sec => !art.section.contains(sec)))
    val sortedArticles = knownSectionArticles ::: unknownSectionArticles 
    
    // TODO: Add H1 sections (titles) Komentáře, Téma, Fokus, Rozhovor, Kultura
    val articleHtmls = sortedArticles.map(x => x.createHtml())
    logInfo("Created htmls from articles")
    val articleNextLinks : List[String] = sortedArticles.map(x => x.createNextEntry())
    val issueHtml = makeNextLinks(articleNextLinks.tail, makeIssueFromArticles(articleHtmls))
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

  def makeNextLinks(articleNextEntries: List[String], content : String) : String = {
    val all = (content::articleNextEntries).reduce((x,y) => {xNx.r replaceFirstIn(x, y)})
    xNx.r replaceFirstIn(all, "")
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
