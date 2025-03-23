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
                sh "sudo docker image build -t rudeh1253/hf-backend:latest ."
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([string(credentialsId: 'docker_hub_access_token', variable: 'dockerHubAccesstoken')]) {
                    sh "echo ${dockerHubAccesstoken} | sudo docker login --username rudeh1253 --password-stdin"
                    sh "sudo docker push rudeh1253/hf-backend:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([string(credentialsId: 'workernode_url', variable: 'workernodeUrl'),
                        string(credentialsId: 'docker_hub_access_token', variable: 'dockerHubAccesstoken')]) {
                    sh "ssh ubuntu@${workernodeUrl} \"rm -rf ~/docker-compose.yml\""
                    sh "ssh ubuntu@${workernodeUrl} \"rm -rf ~/nginx\""
                    sh "scp docker-compose.yml ubuntu@${workernodeUrl}:~"
                    sh "scp -r nginx ubuntu@${workernodeUrl}:~"
                    sh "ssh ubuntu@${workernodeUrl} \"echo ${dockerHubAccesstoken} | sudo docker login --username rudeh1253 --password-stdin\""
                    sh "ssh ubuntu@${workernodeUrl} \"sudo docker pull rudeh1253/hf-backend:latest\""
                    sh "ssh ubuntu@${workernodeUrl} \"sudo docker-compose -f docker-compose.yml --profile blue --env-file envs down\""
                    sh "ssh ubuntu@${workernodeUrl} \"sudo docker-compose -f docker-compose.yml --profile blue --env-file envs up -d\""
                }
            }
        }

        stage('Health Check') {
            steps {
                echo "Health Check new deployment"
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
    post {
        success {
            slackSend (
                channel: '#jenkins-알림', 
                color: '#00FF00', 
                message: "빌드 SUCCESS: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]"
            )
        }
        failure {
            slackSend (
                channel: '#jenkins-알림', 
                color: '#FF0000', 
                message: "빌드 FAIL: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]"
            )
        }
    }
}