package Rescapter

import java.io.{File, FileNotFoundException, FileOutputStream, OutputStreamWriter}
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, GregorianCalendar}

import com.typesafe.config.ConfigFactory
import org.openqa.selenium.{By, WebElement}

import scala.collection.JavaConverters._
import scala.io.Source
import scala.language.postfixOps
import scala.sys.process._

import mail._

object Rescapter {

  def getConfIfExists(paths: List[String], fileName: String = ""): File = {
    paths match {
      case Nil => throw new FileNotFoundException(fileName + " not found.")
      case path :: pathsTail => {
        val fileFromPath = new File(path)
        if (fileFromPath.exists()) {
          fileFromPath
        } else {
          getConfIfExists(pathsTail, fileName)
        }
      }
    }
  }

  val possibleConfFilePaths: List[String] =
    "src/main/resources/rescapter.conf" ::
      "rescapter.conf" ::
      "../../src/main/resources/rescapter.conf" ::
      Nil

  val conf = ConfigFactory.parseFile(getConfIfExists(possibleConfFilePaths, "rescapter.conf"))

  val loginName = conf.getString("respekt-login.user")
  val loginPasswd = conf.getString("respekt-login.password")
  val pathToPandocExe = conf.getString("system-vars.path_to_pandoc_exe")
  val pathToKindlegenExe = conf.getString("system-vars.path_to_kindlegen_exe")

  val emailSmtpHostName = conf.getString("email-setup.host_name")
  val emailSmtpPort = conf.getString("email-setup.smtp_port")
  val emailAddress = conf.getString("email-setup.email")
  val emailPassword = conf.getString("email-setup.password")
  val emailFromName = conf.getString("email-setup.from_name")
  val emailBccHtml = conf.getString("email-setup.bcc_list_html")
  val emailBccEpub = conf.getString("email-setup.bcc_list_epub")
  val emailToMobi = conf.getString("email-setup.to_list_mobi")

  val averageLineThreshold = 1200

  val urlCurrentIssue = "http://respekt.cz/tydenik/2015/32"
  val pathDownloads = System.getProperty("user.home")
  val xAx = "xxxARTICLExxx"
  val xTx = "xxxTOCxxx"
  val xIx = "xxxIIxxx"
  val xYx = "xxxYYYYxxx"
  val xNx = "xxxNEXTxxx"

  val dateTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date)

  val outDirPath = "out/"

  val outputNoPictureFilePath = outDirPath + "Respekt_" + dateTime + "-noPic.html"
  val outputPictureFilePath = outDirPath + "Respekt_" + dateTime + "-Pic.html"

  def main(args: Array[String]): Unit = {

    // Testing that /out/ folder exists, also kindlegen.exe and pandoc.exe
    if (!new File(outDirPath).exists()) {
      new File(outDirPath).mkdir()
    }
    {pathToPandocExe + " -v" !}
    {pathToKindlegenExe + " -releasenotes" !}

    val (issueNum, issueYear) = getCurrentIssueNumAndYear()
    val outputEpubFilePath = outDirPath + "Respekt_" + issueYear + "_" + issueNum + ".epub"
    val outputMobiFilePath = outputEpubFilePath.replaceFirst("(epub$)", "mobi")
    val namedIssueHtml = nameIssue(downloadCurrentIssue(), issueNum, issueYear)

    // Saves the issue with Pictures
    val tagsToRemove1 = List("SCRIPT", "I class=\"image", "DIV class=\"ad view-banner\"", "DIV class=ad-content")
    val cleanedIssueHtml = (namedIssueHtml :: tagsToRemove1).reduce((x, y) => {
      removeTags(x, y)
    })
    saveIssueHtml(cleanedIssueHtml, outputPictureFilePath)

    // Saves the issue without some of the Pictures
    val tagsToRemove2 = List("SOURCE ", "SVG ")
    val noPicIssueHtml = (cleanedIssueHtml :: tagsToRemove2).reduce((x, y) => {
      removeTags(x, y)
    })
    saveIssueHtml(noPicIssueHtml, outputNoPictureFilePath)

    // Creates epub and mobi editions
    val epubCreated = pathToPandocExe + " -t epub " + outputNoPictureFilePath +
      " --toc-depth=2 -o " + outputEpubFilePath !;
    val mobiCreated = pathToKindlegenExe + " " + outputEpubFilePath + " -verbose" !;

    // Check that the file has reasonably long lines (does not have articles cutoff in the middle due to failed login)
    val linesLengths = Source.fromFile(outputNoPictureFilePath, "UTF-8").getLines.toList.map(x => x.length).filter(l => l > 100)
    val averageLineLength = linesLengths.reduce((a,b)=>a+b)*1.0/linesLengths.length

    // Send Emails
    if (averageLineLength > averageLineThreshold) {
      sendEmail(outputPictureFilePath, "", emailBccHtml, "Respekt")
      logInfo("Email with HTML sent to: " + emailBccHtml)
    } else {
      throw new Exception("Html doesn't seem all right.")
    }
    if (epubCreated == 0) {
      sendEmail(outputEpubFilePath, "", emailBccEpub, "Respekt epub")
      logInfo("Email with EPUB sent to: " + emailBccEpub)
    }
    if (epubCreated == 0 & mobiCreated < 2) {
      sendEmail(outputMobiFilePath, emailToMobi, "", "")
      logInfo("Email with MOBI sent to: " + emailToMobi)
    }
  }


  def sendEmail(attachmentPath: String, to: String, bcc: String, subject: String) {
    send a new Mail(
      from = (emailAddress, emailFromName),
      to = to,
      bcc = bcc,
      subject = subject,
      message = "",
      attachment = new java.io.File(attachmentPath)
    )
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
    val articles: List[Article] = articleAHrefs
      .asScala.toList
      .map((x: WebElement) => x.getAttribute("href"))
      .map((y: String) => htmlDriver.getArticle(y))

    // list of sections in the correct order. Everything not specified will be added at the end of the list
    val sectionsList = List("Editorial", "Despekt", "Anketa", "Dopis z", "Lidé", "Dopisy", "Komentáře", "Téma", "Fokus", "Rozhovor", "Kultura", "Civilizace", "Místa, kde se potkáváme", "Od věci", "Jeden den v životě", "Komiks", "Minulý týden", "Připravujeme")

    // TODO: Warning: 'Kultura * Komiks' is twice in the filtering, Tema can be too, if it's e.g. Kultura -> make unique and/or enable wiser sectionList (tuples (stringItHastoContain,StringItCannotContain) )
    val knownSectionArticles = sectionsList.flatMap(sec => articles.filter(_.section.contains(sec)))
    val unknownSectionArticles = articles.filter(art => sectionsList.forall(sec => !art.section.contains(sec)))
    val sortedArticles = knownSectionArticles ::: unknownSectionArticles

    // TODO: Add H1 sections (titles) Komentáře, Téma, Fokus, Rozhovor, Kultura
    val articleHtmls = sortedArticles.map(x => x.createHtml())
    logInfo("Created htmls from articles")
    val articleNextLinks: List[String] = sortedArticles.map(x => x.createNextEntry())
    val issueHtml = makeNextLinks(articleNextLinks.tail, makeIssueFromArticles(articleHtmls))
    issueHtml
  }

  def saveIssueHtml(issueHtml: String, path: String): Unit = {
    val out = new FileOutputStream(new File(path))
    val writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)
    writer.write(issueHtml)
    writer.close()
    logInfo("Created html at " + path + ".")
  }

  def makeIssueFromArticles(articleHtmls: List[String]): String = {
    val possibleTmplFilePaths: List[String] =
      "src/main/resources/issue-template.html" ::
        "issue-template.html" ::
        "../../src/main/resources/issue-template.html" ::
        Nil
    val t = Source.fromFile(getConfIfExists(possibleTmplFilePaths, "issue-template.html")).mkString
    val all = (t :: articleHtmls).reduce((x, y) => {
      xAx.r replaceFirstIn(x, y + "\n" + xAx)
    })
    xAx.r replaceFirstIn(all, "")
  }

  def makeTOC(articleTOCEntries: List[String], content: String): String = {
    val all = (content :: articleTOCEntries).reduce((x, y) => {
      xTx.r replaceFirstIn(x, y + "\n" + xTx)
    })
    xTx.r replaceFirstIn(all, "")
  }

  def makeNextLinks(articleNextEntries: List[String], content: String): String = {
    val all = (content :: articleNextEntries).reduce((x, y) => {
      xNx.r replaceFirstIn(x, y)
    })
    xNx.r replaceFirstIn(all, "")
  }

  def nameIssue(content: String, issueNum: Int, issueYear: Int): String = {
    xYx.r replaceFirstIn(
      xIx.r replaceFirstIn(
        content,
        issueNum.toString),
      issueYear.toString)
  }


  def downloadIssue(issYear: Int, issNum: Int): String = {
    val url = "http://respekt.cz/tydenik/" + issYear + "/" + issNum
    downloadArticlesFromTOC(url)
  }

  def downloadCurrentIssue(): String = {
    val (issueNum, issueYear) = getCurrentIssueNumAndYear()
    downloadIssue(issueYear, issueNum)
  }

  def getCurrentIssueNumAndYear(): (Int, Int) = {
    val cal = new GregorianCalendar()
    cal.add(Calendar.DAY_OF_YEAR, 3)
    cal.get(Calendar.YEAR)
    cal.get(Calendar.WEEK_OF_YEAR)
    (cal.get(Calendar.WEEK_OF_YEAR), cal.get(Calendar.YEAR))
  }

  def removeTags(content: String, tagName: String): String = {
    val regex = "<" + tagName + "([\\s\\S]*?)</" + tagName.split(" ")(0) + ">"
    regex.r replaceAllIn(content, "")
  }

  def logInfo(msg: String): Unit = {
    log(msg, "INFO")
  }

  def logError(msg: String): Unit = {
    log(msg, "ERROR")
  }

  def log(msg: String, msgType: String): Unit = {
    println("" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\t" + msgType + ": " + msg)
  }
}
