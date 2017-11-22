@echo off

vagrant up --provision

vagrant ssh -c "sudo docker-compose up"