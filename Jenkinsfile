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
                sh "sudo docker tag hf/backend:${env.BUILD_ID} rudeh1253/sample-hub:${env.BUILD_ID}"
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([string(credentialsId: 'docker_hub_access_token', variable: 'dockerHubAccesstoken')]) {
                    sh "echo ${dockerHubAccesstoken} | sudo docker login --username rudeh1253 --password-stdin"
                    sh "sudo docker push rudeh1253/sample-hub:${env.BUILD_ID}"
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([file(credentialsId: 'docker_shell_script', variable: 'deployShellFile')]) {
                    sh "sudo chown jenkins ${deployShellFile}"
                    sh "sh ${deployShellFile} ${env.BUILD_ID}"
                }
            }
        }
    }
}