pipeline {
    agent {
        kubernetes {
            yaml '''
              apiVersion: v1
              kind: Pod
              metadata:
                name: qlack2
                namespace: jenkins
              spec:
                affinity:
                  podAntiAffinity:
                    preferredDuringSchedulingIgnoredDuringExecution:
                    - weight: 50
                      podAffinityTerm:
                        labelSelector:
                          matchExpressions:
                          - key: jenkins/jenkins-jenkins-agent
                            operator: In
                            values:
                            - "true"
                        topologyKey: kubernetes.io/hostname
                securityContext:
                    runAsUser: 0
                    runAsGroup: 0
                containers:
                - name: qlack2-builder
                  image: eddevopsd2/ubuntu-dind:dind-mvn3.6.3-jdk8-npm6.14.13
                  volumeMounts:
                  - name: maven
                    mountPath: /root/.m2/
                    subPath: qlack2
                  - name: docker
                    mountPath: /root/.docker/config.json
                    subPath: config.json
                    readOnly: true
                  tty: true
                  securityContext:
                    privileged: true
                    runAsUser: 0
                    fsGroup: 0
                imagePullSecrets:
                - name: regcred
                volumes:
                - name: maven
                  persistentVolumeClaim:
                    claimName: maven-nfs-pvc
                - name: docker
                  persistentVolumeClaim:
                    claimName: docker-nfs-pvc
            '''
            workspaceVolume persistentVolumeClaimWorkspaceVolume(claimName: 'workspace-nfs-pvc', readOnly: false)
        }
    }
    options { 
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 3, unit: 'HOURS')
    }
    stages {
        stage ('Dockerd') {
            steps {
                container (name: 'qlack2-builder'){
                    sh 'dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock &> dockerd-logfile &'
                }
            }
        }
        stage('Build') {
            steps {
                container (name: 'qlack2-builder'){
                    sh '''
                        export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
                        mvn clean install -DskipTests
                    '''
                    sh 'mvn jacoco:report'
                }
            }
        }
        stage('Sonar Analysis') {
            steps {
                container (name: 'qlack2-builder'){
                    withSonarQubeEnv('sonar'){
                        sh 'update-alternatives --set java /usr/lib/jvm/java-11-openjdk-amd64/bin/java'
                        sh 'mvn sonar:sonar -Dsonar.projectName=QLACK2 -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.token=${SONAR_GLOBAL_KEY} -Dsonar.working.directory="/tmp"'
                    }
                }
            }
        }
        stage('Produce bom.xml'){
            steps{
                container (name: 'qlack2-builder'){
                    sh 'mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom'
                }
            }
        }
        stage('Dependency-Track Analysis'){
            steps{
                container (name: 'qlack2-builder'){
                    sh '''
                        cat > payload.json <<__HERE__
                        {
                            "project": "a42f438d-b743-49f5-b2ac-9f7b52c30241",
                            "bom": "$(cat target/bom.xml |base64 -w 0 -)"
                        }
                        __HERE__
                    '''

                    sh '''
                        curl -X "PUT" ${DEPENDENCY_TRACK_URL} -H 'Content-Type: application/json' -H 'X-API-Key: '${DEPENDENCY_TRACK_API_KEY} -d @payload.json
                    '''
                }
            }
        }
    }
    post {
        changed {
            emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT',
                        to: 'qlack@eurodyn.com'
        }
    }
}
