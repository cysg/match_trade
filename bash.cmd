call mvn kotlin:compile package -Dmaven.test.skip=true

@rem call copy mt-user\target\mt-user-0.0.1-SNAPSHOT.jar docker
@rem call copy mt-gateway\target\mt-gateway-0.0.1-SNAPSHOT.jar docker
@rem call copy mt-socket\target\mt-socket-0.0.1-SNAPSHOT.jar docker
@rem call copy mt-engine\target\mt-engine-0.0.1-SNAPSHOT.jar docker
@echo mt-engine
call scp mt-engine\target\mt-engine-0.0.1-SNAPSHOT.jar root@101.37.34.61:/usr/local/docker/runner/mt-docker-compose-project
@echo mt-gateway
call scp mt-gateway\target\mt-gateway-0.0.1-SNAPSHOT.jar root@101.37.34.61:/usr/local/docker/runner/mt-docker-compose-project
@echo mt-socket
call scp mt-socket\target\mt-socket-0.0.1-SNAPSHOT.jar root@101.37.34.61:/usr/local/docker/runner/mt-docker-compose-project
@echo mt-user
call scp mt-user\target\mt-user-0.0.1-SNAPSHOT.jar root@101.37.34.61:/usr/local/docker/runner/mt-docker-compose-project
