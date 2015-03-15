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
      println("INFO: Login completed")
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
  
  def getArticle(articleUrl : String): Article = {
    val currentUrl = getCurrentUrl()
    get(articleUrl)
    val art = new Article()
    art.url = articleUrl
    art.eleHtml = getPageSource()
    art.eleText = getInnerHtmlOfElement(findElement(By.id("text")))
    art.eleAuthor = getInnerHtmlOfElement(findElement(By.cssSelector("#heading > div.l > div.author-image")))
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

