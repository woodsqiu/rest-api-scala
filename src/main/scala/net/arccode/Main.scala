package net.arccode

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.DebuggingDirectives
import com.typesafe.scalalogging.Logger
import net.arccode.foundation.module.{ConfigModule, MigrationModule}

import scala.util.{Failure, Success}

/**
  * 系统启动入口
  *
  * @author http://arccode.net
  * @since 2018-04-04
  */
object Main extends App with ConfigModule with MigrationModule with Routes {

  private val log = Logger("Main")

  migrate()
  val routesWithLog =
    DebuggingDirectives.logRequestResult("LogRequestResponse")(routes)

  val bindingFuture = Http().bindAndHandle(handler = routesWithLog,
                                           interface = httpInterface,
                                           port = httpPort)

  bindingFuture.onComplete {
    case Success(binding) ⇒ {
      log.info(s"启动成功: $binding, env=$env")
      log.info("日志级别: {}", system.settings.LogLevel)
    }
    case Failure(t) ⇒ {
      log.info("主线程错误: {}", t.getClass.getName)
      log.info("主线程错误: {}", t.getMessage)
      log.info("主线程错误: {}", t.getStackTrace.mkString("\n -|"))

      System.exit(-1)
    }
  }

}