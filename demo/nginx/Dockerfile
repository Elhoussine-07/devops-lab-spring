FROM nginx:alpine

# Supprimer toute configuration par défaut
RUN rm -f /etc/nginx/conf.d/default.conf

# Copier notre configuration principale
COPY nginx.conf /etc/nginx/nginx.conf

# Vérifier la syntaxe pendant la construction
RUN nginx -t
