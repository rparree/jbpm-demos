package demo.util

import org.drools.core.spi.ProcessContext
import org.jbpm.bpmn2.handler.ServiceTaskHandler
import org.kie.api.runtime.{KieRuntime, KieSession}
import org.kie.api.runtime.manager.{RuntimeManager, RuntimeManagerFactory, RuntimeEngine}
import org.kie.internal.runtime.manager.context.EmptyContext
import org.specs2.execute.{Result, AsResult}
import org.specs2.specification.ForEach


trait KieTestContext extends ForEach[RuntimeEngine]  with DefaultClasspathRuntimeEnvironment with H2 {

  def foreach[R : AsResult](f : RuntimeEngine => R) : Result = {
    val runtimeManager: RuntimeManager = RuntimeManagerFactory.Factory.get.newSingletonRuntimeManager(environment)
    val runtimeEngine: RuntimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get)
    val session: KieSession = runtimeEngine.getKieSession
    session.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler())


    try AsResult(f(runtimeEngine))
    finally {
      runtimeManager.disposeRuntimeEngine(runtimeEngine)
      runtimeManager.close()
    }

  }


}
