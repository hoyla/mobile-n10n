package batches

import javax.inject.Singleton

import conf.NotificationConfiguration
import gu.msnotifications.NotificationHubConnection

@Singleton
class BackupConfiguration extends NotificationConfiguration("backup") {
  lazy val storageConnectionString = getConfigString("azure.storage.connectionString")
  lazy val containerName = getConfigString("azure.storage.containerName")
  lazy val directoryName = getConfigString("azure.storage.directoryName")
  lazy val backupRetention = getConfigInt("backup.retentionInDays")
  lazy val notificationHubConnection = NotificationHubConnection(
    getConfigString("azure.hub.endpoint"),
    getConfigString("azure.hub.sharedAccessKeyName"),
    getConfigString("azure.hub.sharedAccessKey"))
}
