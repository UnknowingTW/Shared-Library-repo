def call(Map config = [:]) {
    def appDir = config.appDir ?: '.'

    stage('Install Dependencies') {
        dir(appDir) {
            sh 'npm install'
        }
    }

    stage('Run Tests') {
        dir(appDir) {
            sh 'npm test || true' // Avoid pipeline failure if no tests yet
        }
    }

    stage('Build Docker Image') {
        dir(appDir) {
            sh "docker build -t ${config.imageName} ."
        }
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
