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
        stage('Depencencies Check') {
            steps {
                sh 'mvn org.owasp:dependency-check-maven:aggregate'
            }
        }    
        stage('Sonar Analysis') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.projectName=QLACK2 -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_KEY_QLACK2}'
            }
        }
    }
    post {
        always {
            sh 'sh /var/lib/jenkins/docker-cleanup-test-containers.sh TEST-qlack'
            sh 'sh /var/lib/jenkins/kill-karaf-by-grep.sh "workspace/Qlack2-*"'
        }
        changed {
            emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT',
                        to: 'qlack@eurodyn.com'
        }
    }
}