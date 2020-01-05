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
                sh 'mvn versions:set -DnewVersion=0.${BUILD_ID}.0'
                sh 'mvn clean install -Duser.home=/var/maven'
                stash includes: 'target/', name: 'build-artifact'
            }
        }
        stage('Docker Build') {
            agent any
            steps {
                unstash 'build-artifact'
                sh 'docker build --tag docker.io/gtadam89/${APPLICATION}:0.${BUILD_ID}.0 .'
                sh 'docker login -u "${DOCKER_USERNAME}" -p "${DOCKER_PASSWORD}"'
                sh 'docker push docker.io/gtadam89/${APPLICATION}:0.${BUILD_ID}.0'
                sh 'docker tag docker.io/gtadam89/${APPLICATION}:0.${BUILD_ID}.0 docker.io/gtadam89/${APPLICATION}:latest'
                sh 'docker push docker.io/gtadam89/${APPLICATION}:latest'
            }
        }
        stage('Deploy') {
            agent {
                docker {
                    image 'williamyeh/ansible:ubuntu16.04'
                    args '-v /root/.ssh:/root/.ssh -e HOME=/root'
                }
            }
            steps {
                sh 'ansible-playbook --user=${SSH_USERNAME} -i "${DEPLOY_HOST}," deploy/playbook.yml'
            }
        }
    }
}