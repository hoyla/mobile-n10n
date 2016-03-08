package registration.services.topic

import javax.inject.Inject

import auditor.{AuditorWSClient, mkAuditorGroup}
import com.google.inject.ImplementedBy
import models.Topic
import play.api.Logger
import play.api.libs.ws.WSClient
import registration.services.Configuration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.\/
import scalaz.syntax.either._

@ImplementedBy(classOf[AuditorTopicValidator])
trait TopicValidator {
  def removeInvalid(topics: Set[Topic]): Future[TopicValidatorError \/ Set[Topic]]

  trait TopicValidatorError {
    def reason: String
  
    def topicsQueried: Set[Topic]
  }
}

final class AuditorTopicValidator(auditorClient: AuditorWSClient, configuration: Configuration)
  extends TopicValidator {
  private val logger = Logger(classOf[TopicValidator])

  @Inject def this(wsClient: WSClient, configuration: Configuration) = this(new AuditorWSClient(wsClient),
                                                                            configuration)

  override def removeInvalid(topics: Set[Topic]): Future[\/[AuditorClientError, Set[Topic]]] =
    mkAuditorGroup(configuration.auditorConfiguration)
      .queryEach { auditorClient.expiredTopics(_, topics) }
      .map { expired => (topics -- expired.flatten).right }
      .recover { case e => {
        logger.error("Failed fetching invalid topics from Auditor client:", e)
        AuditorClientError(topics).left
      }
      }
  
  case class AuditorClientError(topicsQueried: Set[Topic]) extends TopicValidatorError {
    override def reason: String = "Failed fetching invalid topics from Auditor client"
  }
}

