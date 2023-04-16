#!/bin/bash
mvn clean && mvn package -Dmaven.test.skip=true && docker compose down && docker compose build && docker compose -f docker-compose-prod.yml up --force-recreate -d
