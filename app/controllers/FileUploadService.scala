package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.util.Timeout
import ddp_sequencial.DDPPolygon
import javax.inject.Inject
import play.api.data.Form
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, InjectedController}
import play.api.data.Forms.{mapping, text}
import util.{FileProcessor, WrongDDPException}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by jferreira on 4/8/17.
  */

case class UploadedXML(uploadedContent:String)

class FileUploadService @Inject() (implicit system:ActorSystem, materializer: Materializer) extends InjectedController {

  implicit val uploadedXMLReads = Json.reads[UploadedXML]
  implicit val uploadedXMLWrites = Json.writes[UploadedXML]


  val configForm = Form(
    mapping(
      "uploadedContent" ->text)(UploadedXML.apply)(UploadedXML.unapply)
  )

  implicit val timeout = Timeout(2 seconds)

  def showMain = Action {
    Ok(views.html.show())
  }

  def uploadXML = Action(parse.json) {
    request => {
      val uploadedXML = request.body.validate[UploadedXML]
      uploadedXML.fold(
        errors => BadRequest(Json.obj("error" -> JsError.toJson(errors))),
        uploadedXML => {
          try {
            val polygons = FileProcessor.process(uploadedXML.uploadedContent)
            Ok(Json.obj("status" -> ("ok"),  "polygons" -> assemblePolygonsJSON(polygons)))
          } catch {
            case e:WrongDDPException => BadRequest(Json.obj("status" -> "fail", "reason" -> e.getMessage))
            case e:Exception => BadRequest(Json.obj("status" -> "fail", "reason" -> e.getMessage))
          }
        }
      )
    }
  }

  private def assemblePolygonsJSON(polygons:List[DDPPolygon]) = {
    val result = for (polygon <- polygons) yield
      Json.obj("areaId" -> polygon.areaId,
               "wkt" -> polygon.wkt)

    result.toList
  }

}