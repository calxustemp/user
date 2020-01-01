pipeline {
    agent none
    stages {
        stage('Build') { 
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v $HOME/.m2:/root/.m2:z -u root'
                }
            }
            steps {
                sh 'mvn clean install' 
            }
        }
    }
}