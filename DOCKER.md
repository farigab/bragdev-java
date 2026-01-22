# Docker instructions for Bragdoc

Build the image locally:

```bash
docker build -t bragdoc:local .
```

Run the container (passing environment variables):

```bash
docker run --rm -p 8080:8080 -e GEMINI_API_KEY=your_key \
  bragdoc:local
```

Using docker-compose:

```bash
GEMINI_API_KEY=your_key docker-compose up --build
```

Notes:
- The app listens on port `8080` by default (configured in `src/main/resources/application.properties`).
- Secrets should be provided via environment variables or a secrets manager in production.
