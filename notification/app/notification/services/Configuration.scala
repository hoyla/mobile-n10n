package notification.services

import conf.NotificationConfiguration

class Configuration extends NotificationConfiguration("notification") {
  lazy val hubEndpoint = getConfigString("azure.hub.endpoint")
  lazy val hubSharedAccessKeyName = getConfigString("azure.hub.sharedAccessKeyName")
  lazy val hubSharedAccessKey = getConfigString("azure.hub.sharedAccessKey")
  lazy val apiKey = conf.getStringProperty("notifications.api.secretKey")
  lazy val mapiItemEndpoint = conf.getStringProperty("mapi.items.endpoint", "http://mobile-apps.guardianapis.com/items")
}