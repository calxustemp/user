pipeline {
    agent none
    environment {
        APPLICATION = 'user'
    }
    triggers {
        pollSCM '* * * * *'
    }
    stages {
        stage('Version') {
            agent any
            steps {
                sh 'git tag 0.${BUILD_ID}.0'
                sh 'git remote set-url origin git@github.com:calxus/user.git'
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
                sh 'mvn clean install -Duser.home=/var/maven'
            }
        }
        stage('Docker Build') {
            agent any
            steps {
                sh 'docker build --tag docker.io/gtadam89/${APPLICATION}:${BUILD_ID}'
                sh 'docker login -u "${DOCKER_USERNAME}" -p "${DOCKER_PASSWORD}"'
                sh 'docker push docker.io/gtadam89/${APPLICATION}:${BUILD_ID}'
                sh 'docker tag docker.io/gtadam89/${APPLICATION}:${BUILD_ID} docker.io/gtadam89/${APPLICATION}:latest'
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