# Use an official Java 21 image as a base
FROM mcr.microsoft.com/devcontainers/java:21

# Install Gradle
RUN apt-get update && \
    apt-get install -y gradle && \
    rm -rf /var/lib/apt/lists/*

# Install additional dependencies if needed
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /workspace

# Set user to non-root for security
RUN useradd -ms /bin/bash devcontainer
USER devcontainer

# Default command
CMD ["/bin/bash"]
