pipeline {
    agent any

    environment {
        COUPON_SERVER_1 = 'nhnacademy@133.186.150.78'
        COUPON_SERVER_DOMAIN = 'buzz-book.store'
        DEPLOY_PATH_1 = '/home/nhnacademy'
        REPO_URL = 'https://github.com/nhnacademy-be6-AA/buzz-coupon.git'
        ARTIFACT_NAME = 'coupon-0.0.1-SNAPSHOT.jar'
        JAVA_OPTS = '-XX:+EnableDynamicAgentLoading -XX:+UseParallelGC'

    }

    tools {
        jdk 'jdk-21'
        maven 'maven-3.9.7'
    }

    stages {
        stage('Checkout') {
            steps {
                git(
                    url: REPO_URL,
                    branch: 'main',
                    credentialsId: 'aa-ssh'
                )
            }
        }

        stage('Build') {
            steps {
                withEnv(["JAVA_OPTS=${env.JAVA_OPTS}"]) {
                    sh 'mvn clean package -DskipTests=true'
                }
            }
        }

        stage('Add SSH Key to Known Hosts') {
            steps {
                script {
                    def remoteHost1 = '133.186.150.78'
                    sh """
                        mkdir -p ~/.ssh || true
                        ssh-keyscan -H ${remoteHost1} >> ~/.ssh/known_hosts || (echo "ssh-keyscan failed" && exit 1)
                    """
                }
            }
        }

        stage('Deploy to Front Server 1') {
            steps {
                script {
                    deployDockerContainer(env.COUPON_SERVER_1, env.DEPLOY_PATH_1, 8080)
                    showLogs(env.COUPON_SERVER_1, env.DEPLOY_PATH_1)
                }
            }
        }

        stage('Verification') {
            steps {
                verifyDeployment(env.COUPON_SERVER_DOMAIN, 8080)
            }
        }
    }

    post {
        success {
            echo 'Deployment succeeded!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}

def deployDockerContainer(server, deployPath, port) {
    withCredentials([sshUserPrivateKey(credentialsId: 'aa-ssh', keyFileVariable: 'PEM_FILE')]) {
        sh """
        scp -o StrictHostKeyChecking=no -i \$PEM_FILE target/${env.ARTIFACT_NAME} ${server}:${deployPath}
        ssh -o StrictHostKeyChecking=no -i \$PEM_FILE ${server} 'nohup java -jar ${deployPath}/${env.ARTIFACT_NAME} --server.port=${port} ${env.JAVA_OPTS} > ${deployPath}/app.log 2>&1 &'
        'echo 123456789a | docker login -u qmzo552@gmail.com --password-stdin'
        docker build -t parkheejun2/book-coupon-api:latest .
        docker push parkheejun2/book-coupon-api:latest
        docker pull parkheejun2/book-coupon-api:latest
        docker stop coupon_api || true
        docker rm coupon_api || true
        docker run -d --name coupon_api --network api_network  -p 8091:8091 parkheejun2/book-coupon-api:latest
        """
    }
}

def showLogs(server, deployPath) {
    withCredentials([sshUserPrivateKey(credentialsId: 'aa-ssh', keyFileVariable: 'PEM_FILE')]) {
        sh """
        ssh -o StrictHostKeyChecking=no -i \$PEM_FILE ${server} 'tail -n 100 ${deployPath}/app.log'
        """
    }
}

def verifyDeployment(server, port) {
    sh """
    curl -s --head http://${server}:${port} | head -n 1
    """
}
