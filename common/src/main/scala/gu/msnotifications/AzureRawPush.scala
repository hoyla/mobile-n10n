package gu.msnotifications

import models.{UserId, Push, Topic}
import play.libs.Json

case class AzureRawPush(wnsType: String, body: String, topics: Option[Set[WNSTopic]]) {
  def tagQuery: Option[String] = topics.map { set =>
    set.map(_.uri).mkString("(", " && ", ")")
  }
}

object AzureRawPush {
  def fromPush(push: Push) = {
    val body = Json.stringify(Json.toJson(push.notification.payload))
    push.destination match {
      case Left(topic: Topic) => AzureRawPush("wns/raw", body, Some(Set(WNSTopic.fromTopic(topic))))
      case Right(user: UserId) => AzureRawPush("wns/raw", body, Some(Set(WNSTopic.fromUserId(user))))
    }
  }
}