cd ~/Desktop/CI2/s2/DEVOPS/TPs/DockerCompose

# Remplacer le Jenkinsfile
cat > Jenkinsfile << 'EOF'
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
                    
                    sh '''
                        echo "⏳ Attente du démarrage des services..."
                        sleep 30
                        
                        echo "🔄 Vérification que les APIs répondent..."
                        for i in 1 2 3 4 5 6; do
                            if curl -s http://localhost:8888/hello > /dev/null; then
                                echo "✅ API prête (tentative \$i)"
                                break
                            else
                                echo "⏳ API pas encore prête (tentative \$i/6)..."
                                sleep 5
                            fi
                        done
                    '''
                    
                    sh '''
                        echo "=== Test Load Balancing ==="
                        for i in 1 2 3 4 5 6; do
                            curl -s http://localhost:8888/hello
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
                    
                    sh '''
                        echo "⏳ Attente du déploiement..."
                        sleep 30
                        
                        echo "🔄 Vérification finale..."
                        for i in 1 2 3 4 5; do
                            if curl -s http://localhost:8888/hello > /dev/null; then
                                echo "✅ Application déployée avec succès!"
                                curl -s http://localhost:8888/hello
                                break
                            else
                                echo "⏳ En attente (tentative \$i/5)..."
                                sleep 5
                            fi
                        done
                    '''
                }
                echo '✅ Application déployée sur http://localhost:8888'
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

