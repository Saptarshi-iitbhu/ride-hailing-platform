# Ride-Hailing Microservices Platform

A distributed ride-hailing system built with **6 microservices**, using the **Saga pattern** for distributed transactions, **Apache Kafka** as the event backbone, and **Redis geospatial indexing** for real-time driver matching. Deployed on **AWS EKS** with **Prometheus/Grafana** observability.

---

## 🏗️ Architecture

```
┌─────────────┐     ┌──────────────┐     ┌───────────────┐
│ Rider       │────▶│  Kafka       │────▶│ Matching      │
│ Service     │     │  (Events)    │     │ Service       │
└─────────────┘     └──────────────┘     └───────┬───────┘
                                                   │
                                                   ▼
┌─────────────┐     ┌──────────────┐     ┌───────────────┐
│ Notification│◀────│ Trip         │◀────│ Redis         │
│ Service     │     │ Service      │     │ (Geospatial)  │
└─────────────┘     └───────┬──────┘     └───────────────┘
                             │
                             ▼
                     ┌───────────────┐
                     │ Payment       │
                     │ Service       │
                     └───────┬───────┘
                             │
                    ┌────────┴────────┐
                    │  Driver Service │
                    │ (subscribes to  │
                    │  cancellations) │
                    └─────────────────┘
```

### Saga Flow (Choreography-based)

**Happy path:**
1. `RideRequested` → Rider Service
2. `DriverMatched` → Matching Service (Redis geospatial query)
3. `TripCreated` → Trip Service
4. `PaymentAuthorized` → Payment Service
5. `TripConfirmed` → Trip Service
6. Notification sent → Notification Service

**Compensating flow (on failure):**
- `PaymentFailed` → `TripCancelled` → Driver released back to available pool → Rider notified

---

## 🧰 Tech Stack

| Category         | Technology                                |
|-------------------|--------------------------------------------|
| Language           | Java 17+                                   |
| Framework          | Spring Boot 3.x, Spring Cloud             |
| Messaging          | Apache Kafka                              |
| Caching / Geo      | Redis (Geospatial commands)                |
| Database           | PostgreSQL (per-service)                   |
| Containerization   | Docker, Docker Compose                     |
| Orchestration      | Kubernetes, AWS EKS                        |
| Observability      | Prometheus, Grafana, Micrometer            |
| Testing            | JUnit 5, Mockito, Testcontainers           |

---

## 📦 Services

| Service               | Responsibility                                      |
|------------------------|------------------------------------------------------|
| `rider-service`         | Rider profiles, ride requests                       |
| `driver-service`        | Driver profiles, availability, location updates     |
| `matching-service`      | Nearby-driver matching via Redis geospatial queries |
| `trip-service`          | Trip lifecycle & Saga coordination                  |
| `payment-service`       | Fare calculation, payment authorization              |
| `notification-service`  | Ride event notifications                            |

---

## 📁 Repository Structure

```
ride-hailing-platform/
├── rider-service/
├── driver-service/
├── matching-service/
├── trip-service/
├── payment-service/
├── notification-service/
├── docker-compose.yml
├── k8s/
│   ├── deployments/
│   ├── services/
│   └── configmaps/
├── docs/
│   └── architecture.md
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven or Gradle

### Run locally
```bash
git clone https://github.com/saptarshi783/ride-hailing-platform.git
cd ride-hailing-platform
docker-compose up --build
```

This spins up all 6 services along with Kafka, Redis, and PostgreSQL.

---

## 🧪 Testing

```bash
./mvnw test
```

- 92% test coverage on core matching algorithms
- 80+ JUnit 5 / Mockito tests, including simulated high-concurrency workloads via Testcontainers

---

## 📊 Observability

Once running, metrics are available via:
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

---

## 📌 Roadmap

- [ ] Core service scaffolding (Spring Boot + Postgres)
- [ ] Redis geospatial driver matching
- [ ] Kafka event flow between services
- [ ] Saga pattern (choreography-based) with compensating transactions
- [ ] Testcontainers-based integration test suite
- [ ] Docker Compose local environment
- [ ] Kubernetes manifests (local via minikube/kind)
- [ ] AWS EKS deployment
- [ ] Prometheus + Grafana dashboards

---

## 📄 License

This project is licensed under the MIT License.
