# Storyteller

Minimal Spring Boot backend that proxies requests to OpenAI's `gpt-4o-mini` model so you can experiment with interactive storytelling logic before building a UI.

## Prerequisites

- JDK 17+ (tested with Temurin/OpenJDK 23)
- Maven 3.9+
- An OpenAI API key with access to `gpt-4o-mini`

## Configuration

Provide your key via environment variable (recommended):

```bash
export OPENAI_API_KEY=sk-your-key
```

Alternatively, edit `src/main/resources/application.yml` and set `openai.api-key`, but avoid committing secrets.

You can customize the base URL or default model by changing the `openai.*` properties.

## Run the service

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

- Health check: `GET /api/story/health`
- Story generation: `POST /api/story`

Example request:

```bash
curl -X POST http://localhost:8080/api/story \
  -H 'Content-Type: application/json' \
  -d '{"prompt": "You wake up in a floating city above the clouds. What happens next?"}'
```

Example response:

```json
{
  "story": "You wake with a start as the airship tilts..."
}
```

## Next steps

- Wire this endpoint into a frontend or chat UI
- Persist conversation state per user if you need longer stories
- Add retries, streaming responses, or cost controls before going to production
