pipeline {
    agent any


    environment{
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
    }

    stages{

        stage('Build Maven'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Edwardwong0826/SpringRestNew']]])
                sh 'mvn clean install'
            }
        }

        stage('Build docker image'){
            steps{
                script{
                    sh 'docker build -t exrvoexmoon/spring-rest-new-0.0.1 .'
                }
            }
        }

        stage('Push docker image'){
            steps{
                script{
                    sh "docker login -u exrvoexmoon -p destiny61376554"
                    //sh "docker push exrvoexmoon/spring-rest-new-0.0.1 "
                }
            }
        }

        stage('Deploy to Kubernetes'){
            steps{
                script{
                    sh "kubectl cluster-info"
                    sh 'kubectl create -f service.yaml'
                    sh 'kubectl create -f deployment.yaml'
                }
            }
        }

    }
}