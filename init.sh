#!/bin/bash
rm -rf app
docker rm $(docker ps -a -q)
docker-compose down -v
docker build -t builder_image -f Dockerfile_builder .
docker create -it --name builder builder_image bash
docker cp builder:/ISD_2st_LAB_BIT ./app
docker rm -f builder
docker build -t ansible_image -f Dockerfile_ansible .
docker build -t servers_image -f Dockerfile_servers .
docker-compose -p 'proj' up --force-recreate