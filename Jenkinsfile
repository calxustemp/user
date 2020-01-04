pipeline {
    agent none
    environment {
        APPLICATION = 'user'
    }
    stages {
        stage('Version') {
            agent {
                docker {
                    image 'alpine/git'
                }
            }
            steps {
                sh 'git tag 0.${BUILD_ID}.0 -a Version 0.${BUILD_ID}.0'
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
                sh 'docker login -u "$env:DOCKER_USERNAME" -p "$env:DOCKER_PASSWORD"'
                sh 'docker push docker.io/gtadam89/${APPLICATION}:${BUILD_ID}'
                sh 'docker tag docker.io/gtadam89/${APPLICATION}:${BUILD_ID} docker.io/gtadam89/${APPLICATION}:latest'
                sh 'docker push docker.io/gtadam89/${APPLICATION}:latest'
            }
        }
        stage('Deploy') {
            agent {
                docker {
                    image 'gotechnies/alpine-ssh'
                }
            }
            steps {
                sh 'cat $env:SSH_PRIVATEKEY'
            }
        }
    }
}