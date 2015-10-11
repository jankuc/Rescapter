package Rescapter

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{By, WebElement}

class HtmlLoginDriver() extends HtmlUnitDriver {
  
  def login(loginName : String, loginPasswd : String): Unit = {
    val loginbox = findElement(By.id("frm-loginForm"))
    loginbox.click()
    val loginFormName = findElement(By.id("frm-loginForm-username"))
    loginFormName.sendKeys(loginName)
    val loginFormPwd = findElement(By.id("frm-loginForm-password"))
    loginFormPwd.sendKeys(loginPasswd)
    try {
      loginFormPwd.submit()
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
  
  def getMetaContentByName(name : String): String = {
    findElement(By.xpath("//meta[@name=\"" + name + "\"]")).getAttribute("content")
  }
  
  def getArticle(articleUrl : String): Article = {
    val currentUrl = getCurrentUrl()
    try {
      get(articleUrl)
    }
    catch {
      case e: Exception => 
    }
    val art = new Article()
    art.url = articleUrl
    //art.eleHtml = getPageSource()
    
    // AUTHOR
    
    try {
      art.eleAuthor = getMetaContentByName("author")
      if (art.eleAuthor == null) {
        try {
          art.eleAuthor = findElement(By.className("post-author-name")).getText
        } catch {
          case e: Exception =>
        }
      }
    } catch  { // TODO  <div class="authorship-names"> <span class="post-author-name">Jan Lukavec</span> </div>
      case e: Exception =>   
    }
    
    try {
      art.author = getMetaContentByName("author")
      if (art.author == null) {
        try {
          art.author = findElement(By.className("post-author-name")).getText
        } catch {
          case e: Exception => 
        }
      }
    } catch {
      case e: Exception =>
    }
    
    try {
      art.date = findElement(By.className("authorship-note")).getText
    } catch {
      case e: Exception =>
    }
    
    try {
      art.title = findElement(By.tagName("h1")).getText
    }
    catch {
      case e: Exception =>
    }
    
    try {
      art.perex =  getMetaContentByName("description")
    } catch {
      case e: Exception => 
    }
    
    try {
      art.section = findElement(By.className("post-topics")).getText
    }
    catch {
      case e: Exception =>
    }
    
    try {
      art.eleText = getInnerHtmlOfElement(findElement(By.id("postcontent")))
    } catch {
      case e: Exception =>
    }
    
    get(currentUrl)
    art
  }
  
  
  def getImageUrlsForArticle(articleUrl : String): List[String] = {
    val imageUrls : List[String] = List()
    // TODO implement this. save urls of these images to List[String] to Article.  
    imageUrls
  }
  
}

