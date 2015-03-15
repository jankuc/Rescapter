
import java.io._
import java.net.URL
import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils
import org.openqa.selenium.{JavascriptExecutor, By, WebDriver, WebElement}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.typesafe.config.ConfigFactory

val conf = ConfigFactory.parseFile(new File("D:\\projects\\rescapter\\src\\main\\resources\\rescapter.conf"))
val loginName = conf.getString("respekt-login.user")
val loginPwd = conf.getString("respekt-login.password")
val link = "http://respekt.ihned.cz/aktualni-cislo"
val linkBibi = "http://respekt.ihned.cz/?p=R00000_d&article%5Bid%5D=63642850"
val url: URL = new URL(link)
val htmlDriver = new HtmlUnitDriver()
htmlDriver.get(link)
val loginbox = htmlDriver.findElement(By.id("login-box"))
loginbox.click()
val loginFormName = htmlDriver.findElement(By.id("l-nm"))
loginFormName.sendKeys(loginName)
val loginFormPwd = htmlDriver.findElement(By.id("l-pw-p"))
loginFormPwd.sendKeys(loginPwd)
loginFormPwd.submit()
htmlDriver.get(linkBibi)
val inStream : InputStream = IOUtils.toInputStream(htmlDriver.getPageSource(), StandardCharsets.UTF_8)
htmlDriver.setJavascriptEnabled(true)
val textElement = htmlDriver.findElement(By.id("text"))

val textSource = htmlDriver.executeScript("return arguments[0].innerHTML;", textElement).asInstanceOf[String]
//val jsExecutor : JavascriptExecutor = htmlDriver.asInstanceOf[JavascriptExecutor]
//val textSourceJS = jsExecutor.executeScript("return arguments[0].innerHTML;", textElement).asInstanceOf[String]


