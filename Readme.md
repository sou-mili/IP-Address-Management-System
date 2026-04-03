# IP Address Management System (IPAM)

## Overview

A comprehensive REST API for IPv4 address and subnet management built with **Spring Boot 3.5.13** and **Java 21**. Automates IP allocation, subnet creation, utilization tracking, and provides conflict detection with full device metadata support for enterprise-grade network administration.

**Core Capabilities:**
- Automated subnet management with CIDR notation and validation
- Individual, sequential, and bulk IP allocation with intelligent detection
- Real-time utilization metrics and comprehensive device metadata tracking
- Duplicate prevention, range validation, and referential integrity checks
- Transactional consistency with safeguards against concurrent allocation conflicts

## Tech Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Language | Java 21 | Core runtime environment |
| Framework | Spring Boot 3.5.13 | REST API and application framework |
| Persistence | Spring Data JPA | ORM and database abstraction |
| Database | SQLite | Lightweight file-based datastore |
| Build Tool | Maven 3.x | Dependency management and compilation |
| API Tool | Postman | API testing and documentation |
| IDE | VSCode | Development environment |

## Data Models

### Subnet Entity
Represents a logical IP subnet with CIDR notation and calculated network properties.

```json
{
  "id": 1,
  "cidr": "192.168.1.0/24",
  "networkAddress": "192.168.1.0",
  "broadcastAddress": "192.168.1.255",
  "firstIp": "192.168.1.1",
  "lastIp": "192.168.1.254",
  "totalIps": 256,
  "description": "Office Network Segment"
}
```

**Properties:**
- `cidr`: IPv4 CIDR notation (e.g., 192.168.1.0/24)
- `networkAddress`: Network address (auto-calculated)
- `broadcastAddress`: Broadcast address (auto-calculated)
- `firstIp`: First usable IP (auto-calculated)
- `lastIp`: Last usable IP (auto-calculated)
- `totalIps`: Total IPs in subnet (2^(32-prefix))
- `description`: Administrative metadata

### IP Address Entity
Represents an individual IPv4 address with device metadata.

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

**Properties:**
- `ip`: IPv4 address in string format
- `allocated`: Boolean allocation status (true/false)
- `hostname`: Device hostname for identification
- `macAddress`: 48-bit MAC address
- `deviceType`: Device classification (Laptop, Server, Printer, etc.)
- `owner`: Device owner or assigned user
- `subnet`: Foreign key to parent Subnet

## Core Features

**Subnet Management:**
- CIDR validation with automatic network calculations
- Create, read, update, and delete operations
- Real-time utilization metrics (total, allocated, free)
- Pagination support for large subnet collections
- Safe deletion prevention when IPs allocated

**IP Allocation Strategies:**
- **Specific Allocation:** Request exact IP with duplicate prevention
- **Sequential Allocation:** Auto-select next available IP
- **Bulk Operations:** Allocate multiple IPs in single transaction
- **Device Metadata:** Store hostname, MAC, type, owner per IP
- **Deallocation:** Release IPs back to available pool instantly

**Validation & Safety:**
- Duplicate prevention with existence checks before allocation
- CIDR format enforcement and range validation
- Referential integrity for subnet-IP relationships
- Transactional consistency for concurrent operations
- Automatic state management with allocation flags

## Setup & Installation

### Prerequisites
- Java Development Kit (JDK) 21 or higher
- Apache Maven 3.6.0 or higher
- Git for repository cloning
- SQLite (included with application)

### Installation Steps
```bash
# 1. Clone repository
git clone <repository-url>
cd ipam

# 2. Build project with Maven
mvn clean install

# 3. Run Spring Boot application
mvn spring-boot:run
```

**Expected Output:**
```
Started IpamApplication in X.XXX seconds
SQLite database 'ipam.db' initialized at project root
Available at: http://localhost:8080
```

### Verify Installation
```bash
curl http://localhost:8080/subnet
# Returns: []
```

## API Endpoints

### Subnet Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/subnet` | Create new subnet with CIDR |
| GET | `/subnet` | Retrieve all subnets |
| GET | `/subnet/{id}` | Get subnet by ID |
| PUT | `/subnet/{id}` | Update subnet metadata |
| DELETE | `/subnet/{id}` | Delete subnet (if empty) |
| GET | `/subnet/utilization/{id}` | Get usage statistics |
| GET | `/subnet/page` | Paginated subnet retrieval |

**Create Subnet Example:**
```bash
POST /subnet
Content-Type: application/json

{
  "cidr": "192.168.1.0/24",
  "description": "Office Network"
}
```
Response: `201 Created` with subnet object

### IP Address Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/ip/allocate-specific` | Allocate specific IP |
| POST | `/ip/allocate-next/{subnetId}` | Auto-allocate next available |
| POST | `/ip/bulk/{subnetId}/{count}` | Allocate multiple IPs |
| GET | `/ip` | Get all IP addresses |
| GET | `/ip/subnet/{subnetId}` | Get IPs within subnet |
| PUT | `/ip/release/{id}` | Release/deallocate IP |

**Allocate Specific IP Example:**
```bash
POST /ip/allocate-specific
{
  "subnetId": 1,
  "ip": "192.168.1.100",
  "hostname": "workstation-01",
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "deviceType": "Laptop",
  "owner": "John Doe"
}
```
Response: `200 OK` with IP object

**Bulk Allocation:**
```bash
POST /ip/bulk/1/50
```
Allocates 50 consecutive IPs from subnet 1

**Release IP:**
```bash
PUT /ip/release/42
```
Deallocates IP and returns to available pool

### HTTP Status Codes
- `200 OK`: Successful operation
- `201 Created`: Resource successfully created
- `400 Bad Request`: Invalid format or parameters
- `404 Not Found`: Resource not found
- `409 Conflict`: Duplicate IP or subnet has allocated IPs
- `500 Internal Server Error`: Server error or no available IPs

## Postman Integration

### Setup Postman
1. Open Postman application
2. Click "File" → "Import"
3. Select `ipam-postman-collection.json`
4. Create new environment or use default

### Environment Variables
```
base_url: http://localhost:8080
```

### Pre-Configured Collections
**Subnet Requests:**
- Create Subnet - POST /subnet
- Get All Subnets - GET /subnet
- Get Subnet by ID - GET /subnet/{id}
- Update Subnet - PUT /subnet/{id}
- Check Utilization - GET /subnet/utilization/{id}
- Delete Subnet - DELETE /subnet/{id}

**IP Requests:**
- Allocate Specific - POST /ip/allocate-specific
- Allocate Next - POST /ip/allocate-next/{subnetId}
- Bulk Allocate - POST /ip/bulk/{subnetId}/{count}
- Get All IPs - GET /ip
- Get IPs by Subnet - GET /ip/subnet/{subnetId}
- Release IP - PUT /ip/release/{id}

### Testing Workflow
1. Send POST /subnet to create network
2. Copy returned subnet ID
3. Use ID for IP allocation requests
4. Monitor with GET /subnet/utilization/{id}
5. Release IPs with PUT /ip/release/{id}

## Implementation Details

**CIDR Calculation Algorithm:**
- Network Address: IP AND subnet_mask
- Broadcast: Network OR inverted_mask
- First IP: Network + 1
- Last IP: Broadcast - 1
- Total IPs: 2^(32 - prefix_length)

**IP Allocation:**
- Sequential scanning from first to last usable IP
- Duplicate prevention with existence validation
- Range validation ensuring IP within subnet bounds
- Atomic database transactions for concurrency safety

**Data Persistence:**
- SQLite database (`ipam.db`) in project root
- Spring Data JPA for ORM mapping
- Automatic schema creation via Hibernate DDL
- SQL logging available in application.properties

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

Edit `application.properties` in `src/main/resources/`:

```properties
# Database
spring.datasource.url=jdbc:sqlite:ipam.db

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Logging
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG

# Server
server.port=8080
server.servlet.context-path=/
```

## VSCode Development Setup

### Recommended Extensions
- Extension Pack for Java (Microsoft)
- Spring Boot Extension Pack (Pivotal)
- REST Client (Huachao Mao)
- Thunder Client (Ranga Vadhineni)

### Run Configuration
Create `.vscode/launch.json`:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Spring Boot App",
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "cwd": "${workspaceFolder}",
      "mainClass": "com.ipam.IpamApplication",
      "projectName": "ipam",
      "preLaunchTask": "maven: clean",
      "presentation": {
        "options": {
          "statline": "compact"
        }
      },
      "args": "",
      "console": "integratedTerminal"
    }
  ]
}
```

### Project Structure
```
ipam/
├── src/main/java/com/ipam/
│   ├── IpamApplication.java
│   ├── controller/ (REST endpoints)
│   ├── service/ (Business logic)
│   ├── model/ (JPA entities)
│   ├── repository/ (Data access)
│   ├── dto/ (Transfer objects)
│   ├── util/ (CIDR utilities)
│   ├── config/ (Security, Swagger)
│   └── exception/ (Error handling)
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── ipam-postman-collection.json
```

## Common Workflows

**Network Setup:**
```bash
curl -X POST http://localhost:8080/subnet \
  -H "Content-Type: application/json" \
  -d '{"cidr":"10.0.0.0/24","description":"Production"}'
curl http://localhost:8080/subnet/utilization/1
```

**IP Allocation:**
```bash
curl -X POST http://localhost:8080/ip/allocate-specific \
  -H "Content-Type: application/json" \
  -d '{"subnetId":1,"ip":"10.0.0.50","hostname":"server-01"}'
curl -X POST http://localhost:8080/ip/bulk/1/100
```

**IP Management:**
```bash
curl http://localhost:8080/ip/subnet/1
curl -X PUT http://localhost:8080/ip/release/42
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Invalid CIDR format" | Use XXX.XXX.XXX.XXX/PREFIX (0-32) |
| "IP already allocated" | Use allocate-next or release first |
| "IP not in subnet range" | Verify IP between firstIp-lastIp |
| "Cannot delete subnet" | Release all IPs before deletion |
| "No available IPs" | Expand subnet or release IPs |
| Port 8080 in use | Change in application.properties |

## Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/Feature`
3. Commit changes: `git commit -am 'Add Feature'`
4. Push branch: `git push origin feature/Feature`
5. Create Pull Request

## License

MIT License - see LICENSE file for details

## Support

For issues or questions:
- Review documentation above
- Check Postman collection examples
- Consult troubleshooting section
- Review application logs

---

