val l=1 :: 4 :: (1.to(4)).toList

l filter (_ < 3)
l filterNot (_ < 3)
//l.lastIndexOf(l find (_ < 3) get)
("a","b")._1

val map = scala.collection.immutable.Map("a0"->"one", "a1"->"two", "b2"->"three", "a3"-> "four", "c4" -> "five", "d5"->"six")

val withA = map.filterKeys(_ contains("a"))
val ac = List("a","c")
val list = map.keys.toList
val viaMap = ac map (x => list filter (y => (y.contains(x)))) flatten
var acs = for {ind <- ac
               elements = list.filter(_ contains ind)
} yield elements
acs flatten

list.filter(x => ac.forall(sec => !x.contains(sec)) )
