pipeline {
    agent none
    environment {
        APPLICATION = 'user'
        APPLICATION_VERSION = '0.${BUILD_ID}.0'
    }
    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Version') {
            agent any
            steps {
                sh 'git tag ${APPLICATION_VERSION}'
                sh 'git remote set-url origin git@github.com:calxus/${APPLICATION}.git'
                sh 'git push origin --tags'
            }
        }
        stage('Maven Build') { 
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v /root/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2'
                }
            }
            steps {
                sh 'mvn versions:set -DnewVersion=${APPLICATION_VERSION}'
                sh 'mvn clean install -Duser.home=/var/maven'
            }
        }
        stage('Docker Build') {
            agent any
            steps {
                sh 'docker build --tag docker.io/gtadam89/${APPLICATION}:${APPLICATION_VERSION} .'
                sh 'docker login -u "${DOCKER_USERNAME}" -p "${DOCKER_PASSWORD}"'
                sh 'docker push docker.io/gtadam89/${APPLICATION}:${APPLICATION_VERSION}'
                sh 'docker tag docker.io/gtadam89/${APPLICATION}:${APPLICATION_VERSION} docker.io/gtadam89/${APPLICATION}:latest'
                sh 'docker push docker.io/gtadam89/${APPLICATION}:latest'
            }
        }
        stage('Deploy') {
            agent any
            steps {
                sh 'scp docker-compose.yml ${SSH_USERNAME}@${DEPLOY_HOST}:/home/gadam/user/docker-compose.yml'
                sh 'ssh ${SSH_USERNAME}@${DEPLOY_HOST} \'service ${APPLICATION} restart\''
            }
        }
    }
}