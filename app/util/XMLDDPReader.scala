package ddp_sequencial

import scala.xml.{Elem, Node}

class XMLDDPReader(xml:Elem) extends DDPReader(xml) {
  
  def extractContactPoints:List[(String,String)] = {
    val cg = for (p<-xml \ "ContractingGovernment")
      yield p match {
        case s @ <ContractingGovernment>{c @ _*}</ContractingGovernment> => ((s \ "Name").text,(s \ "NationalPointsOfContact").mkString("\n")) 
      }
    cg.toList
  }

  def extract(typeOfPolygon:String):List[DDPPolygon] = {
    
    def producePolygon(xml:Node) = {
	    xml match {
	      case s @ <Polygon>{contents @ _*}</Polygon> => {
	        val posElem = s \ "PosList"
	        val areaIdElem = s \ "@areaID"
	        val polygon = GeoUtil.polygonFromPosList(posElem.text)
	        new DDPPolygon(areaIdElem.text,polygon.toText())
	      }
	    }
    }
    
    val handled = for (polygon <- xml \ "ContractingGovernment" \ typeOfPolygon \ "Polygon" ) yield producePolygon(polygon)
    val handledTerritory = for (polygon <- xml \ "Territory" \ typeOfPolygon \ "Polygon" ) yield producePolygon(polygon)
    (handled ++ handledTerritory).toList    
  }  
  
  val internalWaters:List[DDPPolygon] = extract("InternalWaters")
  val territorialSea:List[DDPPolygon] = extract("TerritorialSea")
  val custom:List[DDPPolygon] = extract("CustomCoastalAreas")
  val seaward1000nm :List[DDPPolygon]= extract("SeawardAreaOf1000NM")
  override def polygons:List[DDPPolygon] = internalWaters ::: territorialSea  ::: custom  ::: seaward1000nm
  
}