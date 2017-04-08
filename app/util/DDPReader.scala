package ddp_sequencial

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader

import scala.xml.{Elem}

/**
  * Created by jferreira on 06/04/17.
  */

class LRITGeometry(val wkt: String) {
  lazy val jtsPolygon: Geometry = {
    (new WKTReader()).read(wkt)
  }
  def contains(other: LRITGeometry): Boolean = jtsPolygon.contains(other.jtsPolygon)
  override def toString = wkt
}

class DDPPolygon(val areaId: String, val wkt: String) {
  override def toString = areaId + " " + wkt
  override def equals(other: Any) = other.isInstanceOf[DDPPolygon] && areaId == other.asInstanceOf[DDPPolygon].areaId
}

class StandingOrder(val area: LRITGeometry, val ownerId: String, val areaId: String)



abstract class DDPReader(xml:Elem) {

  def polygons():List[DDPPolygon]

}
