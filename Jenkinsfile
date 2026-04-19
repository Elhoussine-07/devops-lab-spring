pipeline {
    agent any

    environment {
        DOCKER_BUILDKIT = '0'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📦 Récupération du code depuis GitHub...'
                checkout scm
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Construction de l\'image Docker Spring Boot...'
                dir('demo') {
                    sh 'docker build -t spring-api:latest .'
                }
            }
        }

        stage('Test d\'Intégration') {
            steps {
                echo '🔗 Test d\'intégration avec Docker Compose...'
                dir('demo') {
                    sh 'cp .env.mysql.example .env.mysql'
                    sh 'cp .env.api.example .env.api'
                    sh 'docker-compose up -d --build --scale api=3'
                    sh 'sleep 15'
                    sh '''
                        echo "=== Test Load Balancing (depuis le réseau Docker) ==="
                        for i in 1 2 3 4 5 6; do
                            docker exec nginx_lb curl -s http://localhost/hello
                            echo ""
                        done
                    '''
                    sh 'docker-compose down'
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Déploiement final...'
                dir('demo') {
                    sh 'cp .env.mysql.example .env.mysql'
                    sh 'cp .env.api.example .env.api'
                    sh 'docker-compose down || true'
                    sh 'docker-compose up -d --scale api=3'
                    sh 'echo "✅ Application déployée"'
                    sh 'echo "Pour tester depuis l\'hôte: curl http://localhost:8888/hello"'
                }
                echo '✅ Déploiement terminé'
            }
        }
    }

    post {
        always {
            echo '🧹 Nettoyage...'
            dir('demo') {
                sh 'docker-compose down || true'
            }
        }
        success {
            echo '🎉 Pipeline terminée avec succès !'
        }
        failure {
            echo '❌ La pipeline a échoué.'
        }
    }
}