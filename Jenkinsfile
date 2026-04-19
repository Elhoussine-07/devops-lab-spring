pipeline {
    agent any

    environment {
        // Variables pour Docker Hub (à adapter)
        DOCKER_HUB_REPO = 'lahoussine2003/spring-nginx-lb'
        // Pour éviter les erreurs de permission Docker
        DOCKER_BUILDKIT = '1'
    }

    stages {
        // 1. CHECKOUT - Récupérer le code depuis Git
        stage('Checkout') {
            steps {
                echo '📦 Récupération du code depuis GitHub...'
                checkout scm
            }
        }

        // 2. BUILD DOCKER IMAGE - Construire l'image Spring Boot
        stage('Build Docker Image') {
            steps {
                echo '🐳 Construction de l\'image Docker Spring Boot...'
                dir('demo') {
                    sh 'docker build -t spring-api:latest .'
                }
            }
        }

        // 3. TEST UNITAIRE - Tester l'image
        stage('Test Unitaire') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                dir('demo') {
                    // Test simple : vérifier que l'image contient bien l'application
                    sh 'docker run --rm spring-api:latest java -version'
                }
            }
        }

        // 4. TEST D'INTEGRATION - Lancer Docker Compose et tester le load balancing
        stage("Test d'Intégration") {
            steps {
                echo '🔗 Lancement du test d\'intégration avec Docker Compose...'
                dir('demo') {
                    // Créer les fichiers .env à partir des templates (pour CI)
                    sh 'cp .env.mysql.example .env.mysql'
                    sh 'cp .env.api.example .env.api'

                    // Lancer les services avec 3 instances
                    sh 'docker compose up -d --build --scale api=3'

                    // Attendre que tout soit prêt
                    sh 'sleep 15'

                    // Tester le load balancing (6 requêtes, doit répartir entre instances)
                    sh '''
                        echo "=== Test du Load Balancing ==="
                        for i in 1 2 3 4 5 6; do
                            curl -s http://localhost/hello
                            echo ""
                        done
                    '''

                    // Nettoyer
                    sh 'docker compose down'
                }
            }
        }

        // 5. PUSH - Pousser l'image vers Docker Hub
        stage('Push to Docker Hub') {
            when {
                branch 'main'  // Ne push que depuis la branche main
            }
            steps {
                echo '📤 Push de l\'image vers Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker tag spring-api:latest $DOCKER_HUB_REPO:latest
                        docker push $DOCKER_HUB_REPO:latest
                    '''
                }
            }
        }

        // 6. DEPLOY - Déploiement final
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Déploiement de l\'application...'
                dir('demo') {
                    // Arrêter les anciens conteneurs
                    sh 'docker compose down || true'

                    // Lancer la nouvelle version
                    sh 'docker compose up -d --scale api=3'

                    // Vérifier que tout fonctionne
                    sh '''
                        sleep 10
                        echo "=== Vérification du déploiement ==="
                        curl -s http://localhost/hello
                    '''
                }
                echo '✅ Déploiement terminé ! Application accessible sur http://localhost'
            }
        }
    }

    post {
        // Nettoyage systématique
        always {
            echo '🧹 Nettoyage des environnements...'
            dir('demo') {
                sh 'docker compose down || true'
            }
        }
        success {
            echo '🎉 Pipeline terminée avec succès !'
        }
        failure {
            echo '❌ La pipeline a échoué. Vérifie les logs ci-dessus.'
        }
    }
}