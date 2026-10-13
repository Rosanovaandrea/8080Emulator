FROM ghcr.io/graalvm/jdk-community:25i4
LABEL authors="andrea"
WORKDIR /app
RUN

ENTRYPOINT ["top", "-b"]