package util

import ddp_sequencial.{GMLDDPReader, XMLDDPReader}

import scala.xml.XML

/**
  * Created by jferreira on 4/8/17.
  */
class WrongDDPException(message:String) extends Exception(message)

object FileProcessor {

  def process(content:String) = {
    try {
      val reader = XML.loadString(content) match {
        case s @ <DataDistributionPlan>{contents @ _*}</DataDistributionPlan> => new XMLDDPReader(s)
        case s @ <MultiSurface>{contents @ _*}</MultiSurface> => new GMLDDPReader(s)
        case _ =>  {
          throw new WrongDDPException("Not a DDP-XML or GML file");
        };
      }
      reader.polygons()
    } catch {
      case e:Exception => throw new RuntimeException("Not a XML file")
    }

  }


}
