package models

import play.api.libs.json.Json.JsValueWrapper

case class Paginated[T](results: List[T], cursor: Option[String])

object Paginated {
  import play.api.libs.json._

  implicit def reads[T](implicit reader: Reads[List[T]]): Reads[Paginated[T]] = new Reads[Paginated[T]] {
    def reads(json: JsValue): JsResult[Paginated[T]] = for {
      results <- (json \ "results").validate[List[T]](reader)
      cursor <- (json \ "cursor").validateOpt[String]
    } yield Paginated(results, cursor)
  }

  implicit def writes[T](implicit writer: Writes[List[T]]): Writes[Paginated[T]] = new Writes[Paginated[T]] {
    def writes(paginated: Paginated[T]): JsValue = JsObject(List(
        Some("results" -> writer.writes(paginated.results)),
        paginated.cursor.map(cursor => "cursor" -> JsString(cursor))
      ).flatten)
  }
}
