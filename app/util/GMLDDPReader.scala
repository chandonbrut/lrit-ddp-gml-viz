package ddp_sequencial

import java.io.File

import scala.xml.{Elem, Node, XML}

class GMLDDPReader(xml:Elem) extends DDPReader(xml) {

  def producePolygon(xml:Node) = {
    xml match {
      case s @ <Polygon>{contents @ _*}</Polygon> => {
        val posElem = s \\ "posList"
        val areaIdElem = s \ "@{http://www.opengis.net/gml/3.2}id"
        val polygon = GeoUtil.polygonFromPosList(posElem.text)
        new DDPPolygon(areaIdElem.text,polygon.toText())
      }
    }
  }

  override def polygons:List[DDPPolygon] = {


    val polygons = for (polygon <- xml \ "surfaceMembers" \ "Polygon")
                    yield producePolygon(polygon)

    polygons.toList
  }
  
}