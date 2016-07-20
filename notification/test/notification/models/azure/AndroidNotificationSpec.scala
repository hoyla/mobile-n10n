package notification.models.azure

import java.net.URI
import java.util.UUID

import models.GoalType.Penalty
import models.{GITContent, Topic}
import models.TopicTypes.{Breaking, FootballMatch, FootballTeam, TagSeries}
import notification.models.android.{BreakingNewsNotification, ContentNotification, GoalAlertNotification, Notification}
import notification.services.azure.PlatformUriTypes
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json

class AndroidNotificationSpec extends Specification {

  "A breaking news" should {
    "serialize to map" in new BreakingNewsScope {
      notification.payload shouldEqual expected
    }
  }

  "A content notification" should {
    "serialize to map" in new ContentNotificationScope {
      notification.payload shouldEqual expected
    }
  }

  "A goal alert notification" should {
    "serialize to map" in new GoalAlertNotificationScope {
      notification.payload shouldEqual expected
    }
  }

  trait NotificationScope extends Scope {
    def notification: Notification
    def expected: Map[String, String]
  }

  trait BreakingNewsScope extends NotificationScope {
    val notification = BreakingNewsNotification(
      uniqueIdentifier = UUID.fromString("30aac5f5-34bb-4a88-8b69-97f995a4907b"),
      title = "The Guardian",
      message = "Mali hotel attack: UN counts 27 bodies as hostage situation ends",
      thumbnailUrl = Some(new URI("http://media.guim.co.uk/09951387fda453719fe1fee3e5dcea4efa05e4fa/0_181_3596_2160/140.jpg")),
      link = new URI("http://mobile-apps.guardianapis.com/items/world/live/2015/nov/20/mali-hotel-attack-gunmen-take-hostages-in-bamako-live-updates"),
      uriType = PlatformUriTypes.Item,
      uri = "http://mobile-apps.guardianapis.com/items/world/live/2015/nov/20/mali-hotel-attack-gunmen-take-hostages-in-bamako-live-updates-uri",
      imageUrl = Some(new URI("https://mobile.guardianapis.com/img/media/a5fb401022d09b2f624a0cc0484c563fd1b6ad93/" +
        "0_308_4607_2764/master/4607.jpg/6ad3110822bdb2d1d7e8034bcef5dccf?width=800&height=-&quality=85")),
      topics = Set(Topic(Breaking, "uk")),
      debug = true,
      section = None,
      edition = None,
      keyword = None,
      editions = Set.empty,
      ticker = ""
    )

    val expected = Map(
      "topics" -> "breaking/uk",
      "uniqueIdentifier" -> "30aac5f5-34bb-4a88-8b69-97f995a4907b",
      "editions" -> "",
      "uri" -> "http://mobile-apps.guardianapis.com/items/world/live/2015/nov/20/mali-hotel-attack-gunmen-take-hostages-in-bamako-live-updates-uri",
      "debug" -> "true",
      "uriType" -> "item",
      "notificationType" -> "BreakingNews",
      "link" -> "http://mobile-apps.guardianapis.com/items/world/live/2015/nov/20/mali-hotel-attack-gunmen-take-hostages-in-bamako-live-updates",
      "message" -> "Mali hotel attack: UN counts 27 bodies as hostage situation ends",
      "title" -> "The Guardian",
      "type" -> "custom",
      "ticker" -> "",
      "imageUrl" -> "https://mobile.guardianapis.com/img/media/a5fb401022d09b2f624a0cc0484c563fd1b6ad93/0_308_4607_2764/master/4607.jpg/6ad3110822bdb2d1d7e8034bcef5dccf?width=800&height=-&quality=85",
      "thumbnailUrl" -> "http://media.guim.co.uk/09951387fda453719fe1fee3e5dcea4efa05e4fa/0_181_3596_2160/140.jpg"
    )
  }

  trait ContentNotificationScope extends NotificationScope {
    val notification = ContentNotification(
      uniqueIdentifier = UUID.fromString("c8bd6aaa-072f-4593-a38b-322f3ecd6bd3"),
      title = "Follow",
      message = "Which countries are doing the most to stop dangerous global warming?",
      thumbnailUrl = Some(new URI("http://media.guim.co.uk/a07334e4ed5d13d3ecf4c1ac21145f7f4a099f18/127_0_3372_2023/140.jpg")),
      uri = new URI("test-uri"),
      uriType = PlatformUriTypes.Item,
      ticker = "",
      link = new URI("http://mobile-apps.guardianapis.com/items/environment/ng-interactive/2015/oct/16/" +
        "which-countries-are-doing-the-most-to-stop-dangerous-global-warming"),
      topics = Set(Topic(TagSeries, "environment/series/keep-it-in-the-ground")),
      debug = true
    )

    val expected = Map(
      "topics" -> "tag-series/environment/series/keep-it-in-the-ground",
      "uniqueIdentifier" -> "c8bd6aaa-072f-4593-a38b-322f3ecd6bd3",
      "uri" -> "test-uri",
      "debug" -> "true",
      "uriType" -> "item",
      "link" -> "http://mobile-apps.guardianapis.com/items/environment/ng-interactive/2015/oct/16/which-countries-are-doing-the-most-to-stop-dangerous-global-warming",
      "message" -> "Which countries are doing the most to stop dangerous global warming?",
      "title" -> "Follow",
      "type" -> "custom",
      "ticker" -> "",
      "thumbnailUrl" -> "http://media.guim.co.uk/a07334e4ed5d13d3ecf4c1ac21145f7f4a099f18/127_0_3372_2023/140.jpg"
    )
  }

  trait GoalAlertNotificationScope extends NotificationScope {
    val notification = GoalAlertNotification(
      uniqueIdentifier = UUID.fromString("3e0bc788-a27c-4864-bb71-77a80aadcce4"),
      AWAY_TEAM_NAME = "Watford",
      AWAY_TEAM_SCORE = 1,
      HOME_TEAM_NAME = "Leicester",
      HOME_TEAM_SCORE = 2,
      SCORING_TEAM_NAME = "Watford",
      SCORER_NAME = "Deeney",
      GOAL_MINS = 75,
      OTHER_TEAM_NAME = "Leicester",
      matchId = "3833380",
      mapiUrl = new URI("http://football.mobile-apps.guardianapis.com/match-info/3833380"),
      uri = new URI("http://football.mobile-apps.guardianapis.com/match-info/3833380"),
      uriType = PlatformUriTypes.FootballMatch,
      debug = true
    )

    val expected =  Map(
      "AWAY_TEAM_SCORE" -> "1",
      "uniqueIdentifier" -> "3e0bc788-a27c-4864-bb71-77a80aadcce4",
      "GOAL_MINS" -> "75",
      "HOME_TEAM_SCORE" -> "2",
      "uri" -> "http://football.mobile-apps.guardianapis.com/match-info/3833380",
      "OTHER_TEAM_NAME" -> "Leicester",
      "SCORING_TEAM_NAME" -> "Watford",
      "AWAY_TEAM_NAME" -> "Watford",
      "debug" -> "true",
      "uriType" -> "football-match",
      "SCORER_NAME" -> "Deeney",
      "type" -> "goalAlert",
      "HOME_TEAM_NAME" -> "Leicester",
      "mapiUrl" -> "http://football.mobile-apps.guardianapis.com/match-info/3833380",
      "matchId" -> "3833380"
    )
  }

}