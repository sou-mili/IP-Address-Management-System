# IP Address Management System (IPAM)

## Overview

A comprehensive REST API for IPv4 address and subnet management built with **Spring Boot 3.5.13** and **Java 21**. Automates IP allocation, subnet creation, utilization tracking, and provides conflict detection with full device metadata support.

**Key Features:**
- Automated subnet management with CIDR validation
- Individual, sequential, and bulk IP allocation
- Real-time utilization metrics and device tracking
- Duplicate prevention and range validation
- Transactional consistency with concurrent safety

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.13 |
| Database | SQLite |
| Build | Maven 3.x |
| ORM | Hibernate |

## Data Models

### Subnet
```json
{
  "id": 1,
  "cidr": "192.168.1.0/24",
  "networkAddress": "192.168.1.0",
  "broadcastAddress": "192.168.1.255",
  "firstIp": "192.168.1.1",
  "lastIp": "192.168.1.254",
  "totalIps": 256,
  "description": "Office Network"
}
```

### IP Address
```json
{
  "id": 42,
  "ip": "192.168.1.100",
  "allocated": true,
  "hostname": "workstation-01",
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "deviceType": "Laptop",
  "owner": "John Doe",
  "subnet": { "id": 1 }
}
```

## Core Features

**Subnet Management:**
- CIDR validation and automatic network calculations
- Update descriptions, view utilization metrics
- Prevent deletion of subnets with allocated IPs
- Pagination support for large datasets

**IP Allocation:**
- Allocate specific IPs with duplicate prevention
- Auto-allocate next available IP
- Bulk allocate multiple IPs in one operation
- Release IPs back to available pool

**Validation & Safety:**
- Duplicate prevention, range validation, CIDR enforcement
- Referential integrity checks
- Transactional consistency

## Setup & Installation

### Prerequisites
- Java Development Kit (JDK) 21+
- Maven 3.6.0+
- Git

### Quick Start
```bash
# Clone and navigate to project
git clone <repository-url>
cd ipam

# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application starts on `http://localhost:8080` and creates `ipam.db` automatically.


## API Endpoints

### Subnet Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/subnet` | Create subnet with CIDR |
| GET | `/subnet` | Get all subnets |
| GET | `/subnet/page?page=0&size=10` | Get subnets with pagination |
| GET | `/subnet/{id}` | Get subnet by ID |
| PUT | `/subnet/{id}` | Update subnet description |
| GET | `/subnet/utilization/{id}` | Get subnet utilization |
| DELETE | `/subnet/{id}` | Delete subnet (only if empty) |

**Create Subnet:**
```bash
POST /subnet
Content-Type: application/json

{
  "cidr": "192.168.1.0/24",
  "description": "Office Network"
}
```

**Response:** 201 Created with subnet object

---

### IP Address Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/ip/allocate-specific` | Allocate specific IP |
| POST | `/ip/allocate-next/{subnetId}` | Allocate next available IP |
| POST | `/ip/bulk/{subnetId}/{count}` | Allocate multiple IPs |
| GET | `/ip` | Get all IP addresses |
| GET | `/ip/subnet/{subnetId}` | Get IPs in subnet |
| PUT | `/ip/release/{id}` | Release IP address |

**Allocate Specific IP:**
```bash
POST /ip/allocate-specific
Content-Type: application/json

{
  "subnetId": 1,
  "ip": "192.168.1.100",
  "hostname": "workstation-01",
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "deviceType": "Laptop",
  "owner": "John Doe"
}
```

**Response:** 200 OK with IP object

**Allocate Next Available:**
```bash
POST /ip/allocate-next/1
```
Auto-selects next free IP from subnet.

**Bulk Allocate:**
```bash
POST /ip/bulk/1/50
```
Allocates 50 consecutive IPs from subnet 1.

**Release IP:**
```bash
PUT /ip/release/42
```
Deallocates IP and returns it to available pool.

### HTTP Status Codes
- `200 OK` - Success
- `201 Created` - Resource created
- `400 Bad Request` - Invalid format or malformed request
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate IP, subnet has allocated IPs
- `500 Internal Server Error` - No available IPs, server error

## Implementation Details

**CIDR Calculation:** Uses bit masking - Network Address (IP AND mask), Broadcast Address (Network OR ~mask), First IP (Network + 1), Last IP (Broadcast - 1), Total IPs (2^(32 - prefix)).

**IP Allocation:** Sequential search with duplicate prevention, range validation, and database transactions for concurrent safety.

**Data Persistence:** SQLite database (`ipam.db`), Hibernate ORM with auto schema creation, SQL logging available.

## Workflows

### Setup Network
```bash
# Create subnet
curl -X POST http://localhost:8080/subnet \
  -H "Content-Type: application/json" \
  -d '{"cidr": "10.0.0.0/24", "description": "Production"}'

# Check utilization
curl http://localhost:8080/subnet/utilization/1
```

### Allocate IPs
```bash
# Allocate specific IP
curl -X POST http://localhost:8080/ip/allocate-specific \
  -H "Content-Type: application/json" \
  -d '{
    "subnetId": 1,
    "ip": "10.0.0.100",
    "hostname": "server-01",
    "deviceType": "Server"
  }'

# Bulk allocate
curl -X POST http://localhost:8080/ip/bulk/1/50

# View allocated IPs
curl http://localhost:8080/ip/subnet/1
```

### Manage IPs
```bash
# Release IP
curl -X PUT http://localhost:8080/ip/release/42

# Allocate next available
curl -X POST http://localhost:8080/ip/allocate-next/1
```

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Invalid CIDR format | Wrong format | Use `XXX.XXX.XXX.XXX/PREFIX` (0-32) |
| IP already allocated | IP in use | Use `allocate-next` or release IP first |
| IP not in subnet range | IP outside subnet | Verify IP is between `firstIp` and `lastIp` |
| Cannot delete subnet | IPs still allocated | Release all IPs before deletion |
| No available IPs | Subnet full | Expand subnet or release IPs |

## Database Schema

```sql
CREATE TABLE subnet (
  id BIGINT PRIMARY KEY,
  cidr VARCHAR(255) NOT NULL,
  network_address VARCHAR(255),
  broadcast_address VARCHAR(255),
  first_ip VARCHAR(255),
  last_ip VARCHAR(255),
  total_ips INT,
  description VARCHAR(255)
);

CREATE TABLE ip_address (
  id BIGINT PRIMARY KEY,
  ip VARCHAR(255) NOT NULL,
  allocated BOOLEAN DEFAULT FALSE,
  hostname VARCHAR(255),
  mac_address VARCHAR(255),
  device_type VARCHAR(255),
  owner VARCHAR(255),
  subnet_id BIGINT REFERENCES subnet(id)
);
```

## Configuration

Edit `application.properties` to customize:

```properties
# Database location
spring.datasource.url=jdbc:sqlite:/custom/path/ipam.db

# SQL logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Server port
server.port=9090

# Schema management
spring.jpa.hibernate.ddl-auto=update
```

## Project Structure

```
ipam/
├── src/main/java/com/ipam/
│   ├── IpamApplication.java              # Entry point
│   ├── controller/
│   │   ├── IPController.java             # IP endpoints
│   │   └── SubnetController.java         # Subnet endpoints
│   ├── service/
│   │   ├── IPService.java                # IP logic
│   │   └── SubnetService.java            # Subnet logic
│   ├── model/
│   │   ├── IPAddress.java
│   │   └── Subnet.java
│   ├── repository/
│   │   ├── IPRepository.java
│   │   └── SubnetRepository.java
│   ├── dto/
│   │   ├── IpRequestDTO.java
│   │   └── IpResponseDTO.java
│   ├── util/
│   │   └── IPUtils.java                  # CIDR utilities
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── ResourceNotFoundException.java
├── pom.xml
├── Readme.md
└── ipam-postman-collection.json
```

## Testing with Curl

```bash
# Create subnet
curl -X POST http://localhost:8080/subnet \
  -H "Content-Type: application/json" \
  -d '{"cidr": "172.16.0.0/16", "description": "Test"}'

# List subnets
curl http://localhost:8080/subnet

# Allocate IP
curl -X POST http://localhost:8080/ip/allocate-specific \
  -H "Content-Type: application/json" \
  -d '{"subnetId": 1, "ip": "172.16.0.10"}'

# View utilization
curl http://localhost:8080/subnet/utilization/1
```

## Postman Collection

Use the included `ipam-postman-collection.json`:
1. Open Postman
2. Click "Import" → "Upload Files"
3. Select the JSON file
4. All endpoints ready to test

## Contributing

1. Fork repository
2. Create feature branch: `git checkout -b feature/YourFeature`
3. Commit changes: `git commit -am 'Add YourFeature'`
4. Push: `git push origin feature/YourFeature`
5. Open Pull Request

## License

MIT License - see LICENSE file for details

## Support

For issues or questions:
- Check documentation above
- Review troubleshooting section
- Check Postman collection examples
- Review application logs

---

**Version:** 0.0.1-SNAPSHOT  
**Last Updated:** April 3, 2026