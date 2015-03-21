import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{WebElement, By}

class HtmlLoginDriver() extends HtmlUnitDriver {
  
  def login(loginName : String, loginPasswd : String): Unit = {
    val loginbox = findElement(By.id("login-box"))
    loginbox.click()
    val loginFormName = findElement(By.id("l-nm"))
    loginFormName.sendKeys(loginName)
    val loginFormPwd = findElement(By.id("l-pw-p"))
    loginFormPwd.sendKeys(loginPasswd)
    try {
      loginFormPwd.submit()
      Rescapter.logInfo("Login completed")
    } catch {
      case ex: NoSuchElementException => 
        println("ERROR: Login Failed")
    }  
  }
  
  def getInnerHtmlOfElement(element : WebElement): String = {
    setJavascriptEnabled(true)
    val result = executeScript("return arguments[0].innerHTML", element).asInstanceOf[String]
    setJavascriptEnabled(false)
    result
  }
  
  def getInHtmlOfByElement(by: By) : String = {
    // TODO
    "asdf"
  }
  
  def getArticle(articleUrl : String): Article = {
    val currentUrl = getCurrentUrl()
    get(articleUrl)
    val art = new Article()
    art.url = articleUrl
    art.eleHtml = getPageSource()
    art.eleText = getInnerHtmlOfElement(findElement(By.id("text")))
    
    art.eleAuthor = getInnerHtmlOfElement(findElement(By.cssSelector("#heading > div.l > div.author-image")))
    val w = "width=[0-9]+".r.findFirstIn(art.eleAuthor) match { 
      case Some(w) => "[0-9]+".r.findFirstIn(w).get.toInt
      case None => 118
    }
    val h = "height=[0-9]+".r.findFirstIn(art.eleAuthor) match {
      case Some(h) => "[0-9]+".r.findFirstIn(h).get.toInt
      case None => 118
    }
    art.eleAuthor = "width=[0-9]+".r.replaceFirstIn(art.eleAuthor, "width=" + 118*w/h)
    art.eleAuthor = "height=[0-9]+".r.replaceFirstIn(art.eleAuthor, "height=118")
    art.eleAuthor = "UL><LI".r.replaceAllIn(art.eleAuthor, "center")
    art.eleAuthor = "LI></UL".r.replaceAllIn(art.eleAuthor, "center")
    
    art.author = getInnerHtmlOfElement(findElement(By.cssSelector("#heading > div.l > div.author-image > div.author > ul > li > a > span")))
    art.date = getInnerHtmlOfElement(findElement(By.className("date")))
    art.title = getInnerHtmlOfElement(findElement(By.cssSelector("#heading > div.l > h1 > a")))
    art.perex = getInnerHtmlOfElement(findElement(By.id("perex")))
    art.section = getInnerHtmlOfElement(findElement(By.cssSelector(" #heading > div.l > a > span ")))
    get(currentUrl)
    art
  }
  
  
  def getImageUrlsForArticle(articleUrl : String): List[String] = {
    val imageUrls : List[String] = List()
    // TODO implement this. save urls of these images to List[String] to Article.  
    imageUrls
  }
  
}

