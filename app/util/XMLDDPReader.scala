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

  /*
  override def polygons:List[DDPPolygon] = {

    val cg = for (p<-xml \ "ContractingGovernment")
      yield p match {
        case s @ <ContractingGovernment>{c @ _*}</ContractingGovernment> => {
          val valor = ((s \ "@lritID").text)
          valor
        }
      }


    val tr = for (p<-xml \ "Territory")
      yield p match {
        case s @ <Territory>{c @ _*}</Territory> => {
          val valor = ((s \ "@lritID").text)
          valor
        }
      }

    val lll = cg.toList ++ tr.toList


    def createBigOne(listOfPolygons: List[DDPPolygon]): DDPPolygon = {
      val ret = listOfPolygons match {
        case x :: Nil => new DDPPolygon("BigOne",x.wkt)
        case x :: xs => {
          val p1 = GeoUtil.geometryFromWKT(x.wkt)
          val p2 = GeoUtil.geometryFromWKT(createBigOne(xs).wkt)
          val p = new DDPPolygon("BigOne", p1.union(p2).toText)
          p
        }
      }
      ret
    }

    val polies = for (
      elem <- lll;
      gaca <- custom.filter(p => p.areaId.contains(elem))
    ) {
      val gaots = seaward1000nm.filter(p => p.areaId.contains(elem)).toList
      val gatss = territorialSea.filter(p => p.areaId.contains(elem)).toList
      val gaiws = internalWaters.filter(p => p.areaId.contains(elem)).toList

      val bigGaot = createBigOne(gatss)


      val poly = if (GeoUtil.geometryFromWKT(bigGaot.wkt).contains(GeoUtil.geometryFromWKT(gaca.wkt))) Some(gaca) else None
      poly match {
        case Some(x) => println("***" + x.areaId)
        case _ =>
      }
    }
  }
  */
  override def polygons:List[DDPPolygon] = internalWaters ::: territorialSea  ::: custom  ::: seaward1000nm


}