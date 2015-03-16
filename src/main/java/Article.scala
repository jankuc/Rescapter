
class Article() {
  var author: String = ""
  var eleAuthor: String = ""
  var url: String = ""
  var date: String = ""
  var eleHtml: String = "" 
  var eleText: String = ""
  var title: String = ""
  var perex: String = ""
  var section: String = ""
  //val imageUrls: List[String] = List()
  
  def createHtml(): File = {
    
  }
  
  override def toString : String = {
    "title: " + title + "\n" +
      "author: " + author + "\n" +
      "date: " + date + "\n" +
      "url: " + url + "\n" +
      "section: " + section + "\n" +
      "perex: " + perex + "\n" +
      "eleText: " + eleText + "\n"
  } 
}
