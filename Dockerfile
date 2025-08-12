# Single-stage build using pre-built JAR
# Runtime stage with Amazon Corretto 21
FROM amazoncorretto:21-alpine

# Install runtime dependencies
RUN apk add --no-cache curl ca-certificates

# Create non-root user for security
RUN addgroup -S umcapp && adduser -S umcapp -G umcapp

# Set working directory
WORKDIR /app

# Copy built application JAR
COPY build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R umcapp:umcapp /app

# Switch to non-root user
USER umcapp

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Expose port (Contents microservice uses 8082)
EXPOSE 8082

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
