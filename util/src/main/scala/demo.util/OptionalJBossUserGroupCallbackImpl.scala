package demo.util

import java.util.{Collections, Properties}

import com.typesafe.scalalogging.LazyLogging
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl

/**
 * todo
 */
class OptionalJBossUserGroupCallbackImpl(location: String) extends JBossUserGroupCallbackImpl(location) with LazyLogging {
  override def init(userGroups: Properties): Unit = {
    Option(userGroups) match {
      case Some(props) => super.init(props)
      case None => logger.warn(s"JBossUserGroupCallbackImpl not initialised (resource $location, not found)")
    }
  }
}
