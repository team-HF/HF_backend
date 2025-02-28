pipeline {
    agent any

    stages {
        stage('Prepare secret file') {
            steps {
                withCredentials([file(credentialsId: 'application-secret', variable: 'prodCredentials')]) {
                    script {
                        sh 'sudo cp $prodCredentials ./src/main/resources/application-secret.yml'
                    }
                }
            }
        }
        

        stage('Build Jar') {
            steps {
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                sh './gradlew clean build -x test'
            }
        }

        stage('Dockerize') {
            steps {
                sh "sudo docker image build -t hf/backend:${env.BUILD_ID} ."
                sh "sudo docker tag hf/backend:${env.BUILD_ID} rudeh1253/hf-backend:${env.BUILD_ID}"
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([string(credentialsId: 'docker_hub_access_token', variable: 'dockerHubAccesstoken')]) {
                    sh "echo ${dockerHubAccesstoken} | sudo docker login --username rudeh1253 --password-stdin"
                    sh "sudo docker push rudeh1253/hf-backend:${env.BUILD_ID}"
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([string(credentialsId: 'node_ip', variable: 'nodeIp'),
                        string(credentialsId: 'docker_hub_access_token', variable: 'dockerHubAccessToken')]) {
                    sh "ssh ubuntu@${nodeIp} \"docker-compose down\" | true"
                    sh "scp docker-compose.yml ubuntu@${nodeIp}:~"
                    sh "scp nginx ubuntu@${nodeIp}:~"
                    sh "ssh ubuntu@${workerNodeIp} \"echo ${dockerHubAccessToken} | sudo docker login --username hansoo0614 --password-stdin\""
                    sh "ssh ubuntu@${workerNodeIp} \"sudo docker pull hansoo0614/metamong-backend:latest\""
                    sh "ssh ubuntu@${workerNodeIp} \"sudo docker-compose --profile blue --env-file ~/envs up -d\""
                }
            }
        }

        stage('Convert Blue or Green') {
            steps {
                echo "Convert traffic"
                echo "Stop old version container"
            }
        }

            stage('Clear') {
                steps {
                    echo "Clear old images"
                }
            }
        }
}