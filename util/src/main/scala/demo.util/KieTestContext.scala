package demo.util

import org.drools.core.spi.ProcessContext
import org.jbpm.bpmn2.handler.ServiceTaskHandler
import org.kie.api.runtime.{KieRuntime, KieSession}
import org.kie.api.runtime.manager.RuntimeEngine
import org.kie.internal.runtime.manager.context.EmptyContext
import org.specs2.execute.{Result, AsResult}
import org.specs2.specification.ForEach


trait KieTestContext extends ForEach[KieSession]  with DefaultClasspathRuntimeEnvironment with SingletonRuntimeManager with H2 {

  def foreach[R : AsResult](f : KieSession => R) : Result = {
    val runtimeEngine: RuntimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get)
    val session: KieSession = runtimeEngine.getKieSession
    session.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler())


    try AsResult(f(session))
    finally {
      runtimeManager.disposeRuntimeEngine(runtimeEngine)
      runtimeManager.close()
    }

  }


}
