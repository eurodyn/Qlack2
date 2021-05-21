pipeline {
    agent any
    options { 
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10')) 
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install -Dmaven.repo.local=/root/.m2/qlack2/repository'
                sh 'mvn jacoco:report -Dmaven.repo.local=/root/.m2/qlack2/repository'
            }
        }
        stage('Depencencies Check') {
            steps {
                sh 'mvn org.owasp:dependency-check-maven:aggregate -Dmaven.repo.local=/root/.m2/qlack2/repository'
            }
        }    
        stage('Sonar Analysis') {
            steps {
                withSonarQubeEnv('sonar'){
                    sh 'mvn sonar:sonar -Dsonar.projectName=QLACK2 -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_KEY_QLACK2} -Dmaven.repo.local=/root/.m2/qlack2/repository'
                }
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
                        "project": "0cbafbeb-f23f-41c3-ac88-5126f8bfcd06",
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