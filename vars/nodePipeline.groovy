def call(Map config = [:]) {
    def appDir = config.appDir ?: '.'

    stage('Install Dependencies') {
        sh "cd ${appDir} && npm install"
    }

    stage('Run Tests') {
        sh "cd ${appDir} && npm test || true"
    }

    stage('Build Docker Image') {
        sh "cd ${appDir} && docker build -t ${config.imageName} ."
    }

    stage('Push to DockerHub') {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            sh """
                echo "$PASSWORD" | docker login -u "$USERNAME" --password-stdin
                docker push ${config.imageName}
            """
        }
    }
}
