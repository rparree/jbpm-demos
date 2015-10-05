# jBPMN/BPM Suite example

Some examples for the 6.x release mostly ported from 5.x release and demos from other BPMN engines (plus a few new ones)


## The demos

- **outcome**
- **signalling**
- **error-handler**
- **compensation**
- **webservice* (uses the `cxf-srping` service from [camel-demos](https://github.com/rparree/camel-demos))
- **human-tasks**
- **custom-workitems**
- **remoting** (uses the docker image below)

## Deploying to jbpm-console/business central

Make sure the parent pom is deployed:

```
$ mvn deploy -N -DrepositoryId=bc
```

## Docker Image

One or more demos might require the docker image: [docker.io/jboss/jbpm-workbench-showcase](https://hub.docker.com/r/jboss/jbpm-workbench-showcase/

To run:

```
$ docker run -d -P -p 8080:8080 -p 8001:8001 --name jbpm  docker.io/jboss/jbpm-workbench-showcase
```