package backup

import java.io.File

import play.api.inject.guice.GuiceApplicationLoader
import play.api.{ApplicationLoader, Environment, Logger, Mode}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag
import scala.util.{Failure, Success}

object Boot {
  val logger = Logger(this.getClass)

  def runBatch[T <: Batch](rootPath: String)(implicit classTag: ClassTag[T]): Unit = {
    val env = Environment(new File(rootPath), this.getClass.getClassLoader, Mode.Prod)
    val ctx = ApplicationLoader.createContext(env)
    val app = new GuiceApplicationLoader().load(ctx)
    val className = classTag.runtimeClass.getName

    try {
      val backup = app.injector.instanceOf[T]

      logger.info(s"Starting $className")
      backup.execute().onComplete {
        case Success(_) =>
          logger.info(s"End of $className")
          app.stop()
        case Failure(error) =>
          logger.error(error.getMessage, error)
          app.stop()
      }
    } catch {
      case e: Exception =>
        logger.error(e.getMessage, e)
        app.stop()
    }
  }

  def main (args: Array[String]): Unit = runBatch[Backup](args.headOption.getOrElse("./backup"))
}
