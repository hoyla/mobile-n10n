package binders

import models._
import models.pagination.CursorSet
import org.joda.time.DateTime
import play.api.mvc.QueryStringBindable
import scala.language.implicitConversions
import PartialFunction.condOpt

package object querystringbinders {

  sealed trait RegistrationsSelector
  case class RegistrationsByUdidParams(udid: UniqueDeviceIdentifier) extends RegistrationsSelector
  case class RegistrationsByTopicParams(topic: Topic, cursor: Option[CursorSet]) extends RegistrationsSelector
  case class RegistrationsByDeviceToken(platform: Platform, deviceToken: String)extends RegistrationsSelector

  class Parsing[A](parse: String => Either[String, A], serialize: A => String, typeName: String)
    extends QueryStringBindable[A] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, A]] = params.get(key).flatMap(_.headOption).map { p =>
      parse(p).left.map(message => s"Cannot parse parameter $key as $typeName" + (if (message.nonEmpty) s": $message" else ""))
    }

    override def unbind(key: String, value: A): String = key + "=" + serialize(value)
  }

  implicit def qsbindableTopic: QueryStringBindable[Topic] = new Parsing[Topic](
    parse = Topic.fromString(_).toEither,
    serialize = _.toString,
    typeName = "Topic"
  )

  implicit def qsbindableUniqueDeviceIdentifier: QueryStringBindable[UniqueDeviceIdentifier] = new Parsing[UniqueDeviceIdentifier](
    parse = UniqueDeviceIdentifier.fromString(_).toRight("Invalid udid"),
    serialize = _.toString,
    typeName = "udid"
  )

  implicit def qsbindablePlatform: QueryStringBindable[Platform] = new Parsing[Platform](
    parse = Platform.fromString(_).toRight("Invalid platform"),
    serialize = _.toString,
    typeName = "Platform"
  )

  implicit def qsbindableDateTime: QueryStringBindable[DateTime] = new QueryStringBindable.Parsing[DateTime](
    parse = DateTime.parse,
    serialize = _.toString,
    error = (key: String, e: Exception) => s"Cannot parse parameter $key as DateTimeFloat: ${e.getMessage}"
  )

  implicit def qsbindableCursorSet: QueryStringBindable[CursorSet] = new Parsing[CursorSet](
    parse = (CursorSet.fromString _).andThen(Right.apply),
    serialize = _.encoded,
    typeName = "Cursor"
  )

  implicit def qsbindableNotificationType: QueryStringBindable[NotificationType] = new Parsing[NotificationType](
    parse = NotificationType.fromRep.get(_).map(Right(_)).getOrElse(Left("")),
    serialize = NotificationType.toRep,
    typeName = "NotificationType"
  )

  implicit def qsbindableRegistrationsSelector: QueryStringBindable[RegistrationsSelector] = new QueryStringBindable[RegistrationsSelector] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, RegistrationsSelector]] = {
      List(
        qsbindableRegistrationsByUdidParams,
        qsbindableRegistrationsByTopicParams,
        qsbindableRegistrationsByDeviceToken).view.map(_.bind(key, params)
      ).collectFirst { case Some(result) => result }
    }

    override def unbind(key: String, value: RegistrationsSelector): String = value match {
      case v: RegistrationsByUdidParams => qsbindableRegistrationsByUdidParams.unbind(key, v)
      case v: RegistrationsByTopicParams => qsbindableRegistrationsByTopicParams.unbind(key, v)
      case v: RegistrationsByDeviceToken => qsbindableRegistrationsByDeviceToken.unbind(key, v)
    }
  }

  implicit def qsbindableRegistrationsByUdidParams: QueryStringBindable[RegistrationsByUdidParams] = new QueryStringBindable[RegistrationsByUdidParams] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, RegistrationsByUdidParams]] =
      qsbindableUniqueDeviceIdentifier.bind("udid", params).map { _.right.map(RegistrationsByUdidParams.apply) }

    override def unbind(key: String, value: RegistrationsByUdidParams): String =
      qsbindableUniqueDeviceIdentifier.unbind("udid", value.udid)
  }

  implicit def qsbindableRegistrationsByTopicParams: QueryStringBindable[RegistrationsByTopicParams] = new QueryStringBindable[RegistrationsByTopicParams] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, RegistrationsByTopicParams]] = {
      qsbindableTopic.bind("topic", params) map { topicBound =>
        topicBound.right.flatMap { topic =>
          optionEitherSwap(qsbindableCursorSet.bind("cursor", params)).right.map { cursor =>
            RegistrationsByTopicParams(topic, cursor)
          }
        }
      }
    }

    override def unbind(key: String, value: RegistrationsByTopicParams): String =
      qsbindableTopic.unbind("topic", value.topic) +
        value.cursor.map(cursor => s"&${qsbindableCursorSet.unbind("cursor", cursor)}").getOrElse("")
  }

  implicit def qsbindableRegistrationsByDeviceToken: QueryStringBindable[RegistrationsByDeviceToken] = new QueryStringBindable[RegistrationsByDeviceToken] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, RegistrationsByDeviceToken]] = {
      val optEitherPlatform = qsbindablePlatform.bind("platform", params)
      val optEitherDeviceToken = QueryStringBindable.bindableString.bind("deviceToken", params)

      condOpt((optEitherPlatform, optEitherDeviceToken)) {
        case (Some(eitherPlatform), Some(eitherDeviceToken)) =>
          for {
            platform <- eitherPlatform.right
            deviceToken <- eitherDeviceToken.right
          } yield {
            RegistrationsByDeviceToken(platform, deviceToken)
          }

        case (Some(_), None) => Left("Missing parameter: deviceToken")
        case (None, Some(_)) => Left("Missing parameter: platform")
      }
    }

    override def unbind(key: String, value: RegistrationsByDeviceToken): String = {
      s"${qsbindablePlatform.unbind("topic", value.platform)}&${QueryStringBindable.bindableString.unbind("deviceToken", value.deviceToken)}"
    }
  }

  private def optionEitherSwap[A, B](value: Option[Either[A, B]]): Either[A, Option[B]] =
    value.map(_.right.map(Option.apply)).getOrElse(Right(None))
}
