pipeline {
    agent any
    options { 
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10')) 
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
                sh 'mvn jacoco:report'
            }
        }
        stage('Sonar Analysis') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.projectName=QLACK2 -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_KEY_QLACK2} -Dmaven.repo.local=/root/.m2/qlack2/repository'
            }
        }
        stage('Produce bom.xml'){
            steps{
                sh 'mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom -Dmaven.repo.local=/root/.m2/qlack2/repository'
            }
        }
        stage('Dependency-Track Analysis'){
            steps{
                sh '''
                    cat > payload.json <<__HERE__
                    {
                        "project": "bf9332c3-a693-4324-8c7e-09fbe65f1751",
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
    post {
        always {
            sh 'sh /var/lib/jenkins/scripts/docker-cleanup-test-containers.sh TEST-qlack'
            sh 'sh /var/lib/jenkins/scripts/kill-karaf-by-grep.sh "../workspace/Qlack2*"'
        }
        changed {
            emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT',
                        to: 'qlack@eurodyn.com'
        }
    }
}