package demo.util

import org.jbpm.test.JBPMHelper
import org.kie.api.runtime.manager._


trait SRuntimeEnvironment {
  val environment: RuntimeEnvironment
}

trait DefaultClasspathRuntimeEnvironment extends SRuntimeEnvironment{

  val config  : KBaseSession

  lazy val builderFactory: RuntimeEnvironmentBuilderFactory = RuntimeEnvironmentBuilder.Factory.get
  lazy val builder = builderFactory.newClasspathKmoduleDefaultBuilder(config.kbase,config.ksession)
  lazy val environment = builder.get

}

case class KBaseSession(kbase : String, ksession : String)

sealed trait RuntimeManagerStrategy

trait SingletonRuntimeManager extends  RuntimeManagerStrategy{
  this : SRuntimeEnvironment  =>
  lazy val runtimeManager: RuntimeManager = RuntimeManagerFactory.Factory.get.newSingletonRuntimeManager(environment)
}

trait PreRequestRuntimeManager extends RuntimeManagerStrategy {
  this : SRuntimeEnvironment  =>
  lazy val runtimeManager: RuntimeManager = RuntimeManagerFactory.Factory.get.newPerProcessInstanceRuntimeManager(environment)
}

trait H2 {
  JBPMHelper.startH2Server
  JBPMHelper.setupDataSource

}