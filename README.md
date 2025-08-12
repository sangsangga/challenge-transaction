# e-Banking Portal Transaction API

A reusable REST API microservice for e-Banking portals that provides paginated transaction history with real-time currency conversion.

## 🎯 Features

- **Kafka Integration**: Consumes transaction events and stores them in PostgreSQL
- **Paginated API**: Month-based transaction listing with configurable page sizes
- **Currency Conversion**: Real-time FX conversion with page-level credit/debit totals
- **JWT Security**: OAuth2/OIDC authentication with customer isolation
- **Cloud Ready**: Docker containerization with Kubernetes deployment support
- **Observability**: Structured logging, health checks, and OpenAPI documentation

## 🏗️ Architecture

This service implements a layered architecture with clear separation of concerns:

- **Web Layer**: REST controllers with OpenAPI documentation
- **Service Layer**: Business logic for transaction processing and FX conversion
- **Integration Layer**: Kafka consumers and external API clients
- **Data Layer**: JPA repositories with optimized queries
- **Security Layer**: JWT authentication and authorization

For detailed architecture documentation, see [ARCHITECTURE.md](./ARCHITECTURE.md).

## 🛠️ Technology Stack

- **Java 17** with Spring Boot 3.x
- **Spring Data JPA** with PostgreSQL and Flyway migrations
- **Spring Kafka** for message consumption
- **Spring Security OAuth2** for JWT authentication
- **Spring WebFlux** for reactive HTTP clients
- **Docker** and **Kubernetes** for deployment
- **Testcontainers** for integration testing

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker (optional)
- PostgreSQL (for local development)
- Kafka (for local development)

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd challenge-transaction
```

2. **Start dependencies with Docker Compose**
```bash
docker compose up -d db kafka
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

4. **Access the API**
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

### Testing
```bash
# Run all tests
./mvnw clean test

# Run specific test
./mvnw -Dtest=TransactionConsumerTest test
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `txn` |
| `DB_USER` | Database username | `postgres` |
| `DB_PASS` | Database password | `postgres` |
| `KAFKA_BOOTSTRAP` | Kafka bootstrap servers | `localhost:9092` |
| `BASE_CURRENCY` | Base currency for conversions | `IDR` |
| `FX_API_URL` | FX rate service URL | `https://api.exchangerate.host` |
| `FX_API_KEY` | FX API key (optional) | - |
| `JWK_SET_URI` | JWT validation endpoint | - |

### Security Configuration

For JWT authentication, configure one of:
- `JWK_SET_URI`: Direct JWK endpoint
- `ISSUER_URI`: OAuth2 issuer URI (auto-discovers JWK endpoint)

Example:
```bash
export JWK_SET_URI=https://your-auth-provider/.well-known/jwks.json
```

## 📡 API Documentation

### Endpoints

#### `GET /api/v1/transactions`
Returns paginated transaction history for the authenticated customer.

**Parameters:**
- `monthKey`: First day of month (e.g., `2024-01-01`)
- `page`: Page number (0-based)
- `size`: Page size (default: 20)

**Authentication:** Bearer JWT token required

**Response:**
```json
{
  "transactions": [
    {
      "id": "uuid",
      "accountIban": "CH93...",
      "currency": "IDR",
      "amount": 1550000.00,
      "valueDate": "2024-01-15",
      "description": "Payment received",
      "transactionType": "CREDIT"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "currency": "IDR",
  "totalCredit": 2500000.00,
  "totalDebit": 1200000.00
}
```

### OpenAPI Documentation
- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI Spec**: `/v3/api-docs`

## 🔄 Kafka Integration

### Transaction Events

**Topic:** `transactions`  
**Key:** Transaction UUID  
**Value Format:**

```json
{
  "id": "00000000-0000-0000-0000-000000000001",
  "customerId": "P-1",
  "accountIban": "CH93-0000-0000-0000-0001",
  "currencyAmount": "CHF 100.50",
  "valueDate": "2024-01-15",
  "description": "Online payment"
}
```

### Supported Currency Amount Formats
- Credit: `"USD 100.50"`
- Debit: `"USD -100.50"`, `"USD (100.50)"`, `"USD 100.50-"`

## 🐳 Deployment

### Docker

1. **Build image**
```bash
docker build -t txn-svc:latest .
```

2. **Run with Docker**
```bash
docker run --rm -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e KAFKA_BOOTSTRAP=host.docker.internal:9092 \
  txn-svc:latest
```

### Docker Compose

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f app

# Stop services
docker compose down
```

### Kubernetes

1. **Update image in deployment**
```yaml
# k8s/deployment.yaml
spec:
  template:
    spec:
      containers:
      - name: app
        image: your-registry/txn-svc:1.0.0
```

2. **Deploy to cluster**
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

3. **Verify deployment**
```bash
kubectl get pods -l app=txn-svc
kubectl logs -f deployment/txn-svc
```

## 📊 Monitoring & Observability

### Health Checks
- **Liveness**: `/actuator/health/liveness`
- **Readiness**: `/actuator/health/readiness`
- **General Health**: `/actuator/health`

### Logging
Structured JSON logging with configurable levels:
```yaml
logging:
  level:
    com.transaction.challenge: INFO
    com.transaction.challenge.kafka: DEBUG
```

### Metrics
Spring Boot Actuator metrics available at `/actuator/metrics`

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/transaction/challenge/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── kafka/           # Kafka consumers
│   │   ├── model/           # JPA entities and events
│   │   ├── repository/      # Data access layer
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.yml   # Application configuration
│       └── db/migration/    # Flyway database migrations
└── test/                    # Test classes
```

## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Run `./mvnw clean test` to verify
5. Submit a pull request

### Code Standards
- Java 17+ features encouraged
- Spring Boot conventions
- Comprehensive test coverage
- Structured logging
- OpenAPI documentation

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
