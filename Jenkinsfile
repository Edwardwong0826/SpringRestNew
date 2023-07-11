pipeline {
    agent any
    tools{
        maven 'maven_3_8_1'
    }

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

        stage('Login'){
            steps{
                script{
                    //kubernetesDeploy (configs: 'service.yaml', 'deployment.yaml')
                    sh "docker login -u exrvoexmoon -p destiny61376554"
                }
            }
        }

        stage('Push'){
            steps{
                script{
                    //kubernetesDeploy (configs: 'service.yaml', 'deployment.yaml')
                    sh "docker push exrvoexmoon/spring-rest-new-0.0.1 "
                }
            }
        }

//         stage('Push custom image to docker hub'){
//             steps{
//                 script{
//
//                    withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
//                    sh 'docker login -u exrvoexmoon -p ${dockerhubpwd}'
//
//                     }
//
//                    sh 'exrvoexmoon/spring-rest-new-0.0.1'
//                 }
//             }
//         }

        stage('Deploy to Kubernetes'){
            steps{
                script{
                    //kubernetesDeploy (configs: 'service.yaml', 'deployment.yaml')
                    sh "kubectl cluster-info"
                }
            }
        }
    }
}