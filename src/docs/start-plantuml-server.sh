#!/bin/bash
#
# ready to serve plantuml diagrams at http://localhost:8080/
# see https://github.com/plantuml/plantuml-server for more information.
#
docker run -d --name plantuml -p 8080:8080 plantuml/plantuml-server:jetty
