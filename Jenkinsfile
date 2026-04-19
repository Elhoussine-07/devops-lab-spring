pipeline {
    agent any

    environment {
        // Désactiver BuildKit temporairement
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

        stage('Test Unitaire') {
            steps {
                echo '🧪 Test de l\'image Docker...'
                dir('demo') {
                    sh 'docker run --rm spring-api:latest java -version'
                }
            }
        }

        stage('Test d\'Intégration') {
            steps {
                echo '🔗 Test d\'intégration avec Docker Compose...'
                dir('demo') {
                    // Copier les fichiers .example en .env
                    sh 'cp .env.mysql.example .env.mysql'
                    sh 'cp .env.api.example .env.api'

                    // Lancer Docker Compose avec 3 instances
                    sh 'docker-compose up -d --build --scale api=3'
                    sh 'sleep 15'

                    // Tester le load balancing (6 requêtes)
                    sh '''
                        echo "=== Test Load Balancing ==="
                        for i in 1 2 3 4 5 6; do
                            curl -s http://localhost/hello
                            echo ""
                        done
                    '''

                    // Nettoyer
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
                    // Copier les fichiers .example en .env
                    sh 'cp .env.mysql.example .env.mysql'
                    sh 'cp .env.api.example .env.api'

                    // Arrêter les anciens conteneurs
                    sh 'docker-compose down || true'

                    // Lancer le déploiement avec 3 instances
                    sh 'docker-compose up -d --scale api=3'
                    sh 'sleep 5'

                    // Vérifier que l'application répond
                    sh 'curl -s http://localhost/hello'
                }
                echo '✅ Application déployée sur http://localhost'
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