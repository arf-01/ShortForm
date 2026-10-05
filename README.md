# ShortForm

ShortForm is a URL shortener with a Spring Boot API, an Nginx frontend, MySQL
for persistent data, and Redis for caching.

Live site: [http://20.198.121.219](http://20.198.121.219)

## Architecture

```text
Browser -> Nginx frontend -> Spring Boot backend -> MySQL
                                               \-> Redis
```

The application services are published to GitHub Container Registry (GHCR):

- `ghcr.io/arf-01/shortform-frontend`
- `ghcr.io/arf-01/shortform-backend`

MySQL and Redis use their official Docker Hub images.

## Requirements

- Docker Engine
- Docker Compose v2
- Git

## Configuration

Create the environment file from the template:

```bash
cp .env.example .env
```

Set strong, unique values for:

```env
MYSQL_USER=urluser
MYSQL_PASSWORD=your-application-password
MYSQL_ROOT_PASSWORD=your-root-password
```

Keep `.env` private and never commit it.

## Run with Docker Compose

The Compose file uses prebuilt images, so log in to GHCR if the packages are
private:

```bash
echo "$GHCR_TOKEN" | docker login ghcr.io -u arf-01 --password-stdin
```

Pull the images and start the application:

```bash
docker compose pull
docker compose up -d
```

Open `http://localhost` or the host's public IP. Check service status and logs
with:

```bash
docker compose ps
docker compose logs --tail=100
```

The frontend is exposed on port `80`. The backend, MySQL, and Redis are only
available inside the Compose network. MySQL data is stored in the persistent
`mysql-data` volume.

## GitHub Actions

The workflow at `.github/workflows/publish-images.yml` runs on pushes to
`main` and can also be started manually. It:

1. Builds the backend image with its multi-stage Dockerfile.
2. Builds the frontend image.
3. Pushes both images to GHCR.
4. Adds `latest` and commit-specific SHA tags.

The VM does not need Java, Gradle, Node.js, or a compiler. It only needs Docker,
Compose, the Compose file, and the production `.env` file.

## Deploy an update

After a successful GitHub Actions run:

```bash
cd /opt/shortform
git pull origin main
docker compose pull
docker compose up -d
docker compose ps
```

## Database initialization

On a fresh database, initialize the URL sequence once after the application has
created the `sequence_allocator` table:

```bash
docker compose exec mysql mysql -u root -p urlshortener
```

Then run:

```sql
INSERT INTO sequence_allocator (sequence_name, next_id)
VALUES ('url_seq', 1)
ON DUPLICATE KEY UPDATE sequence_name = sequence_name;
```

## Local backend development

The backend can also be built and tested directly:

```bash
cd backend
./gradlew clean build
```

On Windows, use:

```powershell
.\gradlew.bat clean build
```
