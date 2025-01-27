FROM 202317bh063/user-service:v2

# Install curl
USER root

# Install curl
RUN apt-get update && \
    apt-get install --no-install-recommends -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*