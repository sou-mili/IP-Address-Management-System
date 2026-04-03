#  IP Address Management System (IPAM)

##  Overview

The **IP Address Management System (IPAM)** is a comprehensive REST API application built with **Spring Boot** designed to simplify IP address allocation, subnet management, and network administration. This system automates critical network management operations including IPv4 address allocation, deallocation, subnet creation, and utilization tracking with built-in validation and conflict detection mechanisms.

### Core Capabilities
- **Automated Subnet Management**: Create subnets using CIDR notation with automatic network calculations
- **Dynamic IP Allocation**: Allocate IPs individually, sequentially, or in bulk with intelligent next-available detection
- **Resource Tracking**: Real-time utilization metrics and comprehensive device metadata storage
- **Data Integrity**: Duplicate prevention, CIDR validation, and subnet range enforcement
- **Safe Operations**: Transactional consistency and safeguards against concurrent allocation conflicts

---

## 🛠️ Technical Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.5.13 |
| **Persistence** | Spring Data JPA | Latest |
| **Database** | SQLite | Latest |
| **ORM** | Hibernate Community Dialects | Latest |
| **Build Tool** | Maven | 3.x |
| **Additional** | Lombok (Code Generation) | Latest |

---

## 📊 Data Models

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

**Properties**:
- `cidr`: IPv4 CIDR notation (e.g., 192.168.1.0/24)
- `networkAddress`: Network address (auto-calculated)
- `broadcastAddress`: Broadcast address (auto-calculated)
- `firstIp`: First usable IP address (auto-calculated, network + 1)
- `lastIp`: Last usable IP address (auto-calculated, broadcast - 1)
- `totalIps`: Total IP addresses in subnet (auto-calculated)
- `description`: Administrative description for subnet documentation

### IPAddress Entity
Represents an individual IPv4 address within a subnet with device metadata.

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

**Properties**:
- `ip`: IPv4 address (string format)
- `allocated`: Boolean flag indicating allocation status
- `hostname`: Device hostname for identification
- `macAddress`: Media Access Control address (48-bit identifier)
- `deviceType`: Device classification (e.g., Laptop, Server, Printer)
- `owner`: Device owner or assigned user
- `subnet`: Foreign key reference to parent Subnet entity

---

##  Core Features

### 🔹 Subnet Management
- **CIDR Validation**: Enforces valid IPv4 CIDR notation (e.g., `/8` to `/32`)
- **Automatic Calculations**: Computes network address, broadcast address, and usable IP ranges
- **Metadata Update**: Update subnet description without affecting IP allocations
- **Utilization Metrics**: Real-time statistics on allocated, free, and total IPs
- **Safe Deletion**: Prevents deletion of subnets with allocated IPs
- **Pagination**: Efficient retrieval of large subnet collections

### 🔹 IP Address Management
- **Targeted Allocation**: Allocate specific IP addresses with duplicate prevention
- **Sequential Allocation**: Automatic allocation of next available IP
- **Bulk Operations**: Allocate multiple IPs in single transaction
- **Device Metadata**: Store and retrieve device information (hostname, MAC, type, owner)
- **Rapid Deallocation**: Release IPs back to available pool
- **Subnet Filtering**: Query all IPs within a specific subnet

### 🔹 Validation & Safety Mechanisms
- **Duplicate Prevention**: Detect and prevent re-allocation of already-assigned IPs
- **Range Validation**: Ensure allocated IPs fall within subnet boundaries
- **CIDR Format Enforcement**: Validate subnet CIDR format at creation
- **Referential Integrity**: Prevent subnet deletion with active IP allocations
- **State Management**: Track allocation status with boolean flag

### 🔹 Error Handling & HTTP Status Codes
| Status | Scenario |
|--------|----------|
| `200 OK` | Successful operation |
| `201 Created` | Resource successfully created |
| `400 Bad Request` | Invalid CIDR format, malformed request body |
| `404 Not Found` | Subnet or IP address not found by ID |
| `409 Conflict` | Duplicate IP allocation, subnet deletion with allocated IPs |
| `500 Internal Server Error` | Unexpected server error |

---

---

## ⚙️ Setup & Installation

### Prerequisites
- **Java Development Kit (JDK) 21** or higher
- **Apache Maven 3.6.0** or higher
- **SQLite** (included with the application; no separate installation required)
- **Git** (for cloning the repository)

### Step 1: Clone the Repository
```bash
git clone <your-repository-url>
cd ipam
```

### Step 2: Build the Project
Maven will download all required dependencies and compile the application:
```bash
mvn clean install
```

This command:
- Removes any previous build artifacts (`clean`)
- Downloads all dependencies from Maven Central Repository
- Compiles Java source code
- Runs unit tests
- Creates a JAR file in the `target/` directory

### Step 3: Run the Application
Start the Spring Boot application:
```bash
mvn spring-boot:run
```

**Expected Output**:
```
Started IpamApplication in 3.456 seconds
Listening on port 8080
SQLite database 'ipam.db' initialized
```

The application will start on `http://localhost:8080` and create a SQLite database file named `ipam.db` in the project root directory.

### Step 4: Verify Installation
Test the service by making a request to:
```bash
curl http://localhost:8080/subnet
```

Expected response: An empty JSON array `[]` (no subnets created yet)

---

##  RESTful API Endpoints

### 🔹 Subnet Management Endpoints

#### 1. Create a New Subnet
Create a subnet using CIDR notation. Network details are automatically calculated.

**Request:**
```
POST /subnet
Content-Type: application/json
```

**Request Body:**
```json
{
  "cidr": "192.168.1.0/24",
  "description": "Office Network Segment"
}
```

**Response (201 Created):**
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

**Error Scenarios:**
- `400 Bad Request`: Invalid CIDR format (e.g., "192.168.1.0/33")
- `400 Bad Request`: Missing required fields

---

#### 2. Retrieve All Subnets
Fetch all subnets in the system (non-paginated).

**Request:**
```
GET /subnet
```

**Response (200 OK):**
```json
[
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
]
```

---

#### 3. Retrieve Subnets with Pagination
Fetch subnets with pagination support for large datasets.

**Request:**
```
GET /subnet/page?page=0&size=10
```

**Query Parameters:**
- `page`: Zero-indexed page number (default: 0)
- `size`: Number of results per page (default: 20)

**Response (200 OK):**
```json
{
  "content": [
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
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

#### 4. Retrieve Subnet by ID
Fetch a specific subnet by its unique identifier.

**Request:**
```
GET /subnet/{id}
```

**Path Parameters:**
- `id`: Subnet ID (Long)

**Response (200 OK):**
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

**Error Scenarios:**
- `404 Not Found`: Subnet with specified ID does not exist

---

#### 5. Update Subnet Metadata
Update the description of an existing subnet. Network calculations are preserved and cannot be modified after creation.

**Request:**
```
PUT /subnet/{id}
Content-Type: application/json
```

**Path Parameters:**
- `id`: Subnet ID (Long)

**Request Body:**
```json
{
  "description": "Updated Office Network - Building A"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "cidr": "192.168.1.0/24",
  "networkAddress": "192.168.1.0",
  "broadcastAddress": "192.168.1.255",
  "firstIp": "192.168.1.1",
  "lastIp": "192.168.1.254",
  "totalIps": 256,
  "description": "Updated Office Network - Building A"
}
```

**Error Scenarios:**
- `404 Not Found`: Subnet with specified ID does not exist

---

#### 6. Get Subnet Utilization
Retrieve real-time utilization statistics for a subnet (total, allocated, and available IPs).

**Request:**
```
GET /subnet/utilization/{id}
```

**Path Parameters:**
- `id`: Subnet ID (Long)

**Response (200 OK):**
```
Total: 256, Used: 45, Free: 211
```

**Error Scenarios:**
- `404 Not Found`: Subnet with specified ID does not exist

---

#### 7. Delete Subnet
Delete a subnet from the system. Deletion is only permitted if no IPs are currently allocated within the subnet.

**Request:**
```
DELETE /subnet/{id}
```

**Path Parameters:**
- `id`: Subnet ID (Long)

**Response (200 OK):**
```json
"Subnet deleted successfully"
```

**Error Scenarios:**
- `404 Not Found`: Subnet with specified ID does not exist
- `409 Conflict`: Cannot delete subnet with allocated IPs. Message: "Cannot delete subnet. IPs are still allocated."

---

### 🔹 IP Address Management Endpoints

#### 1. Allocate Specific IP Address
Allocate a specific IP address to a device. The system validates that the IP exists within the subnet range and is not already allocated.

**Request:**
```
POST /ip/allocate-specific
Content-Type: application/json
```

**Request Body:**
```json
{
  "subnetId": 1,
  "ip": "192.168.1.100",
  "hostname": "workstation-01",
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "deviceType": "Laptop",
  "owner": "John Doe"
}
```

**Request Parameters:**
- `subnetId`: ID of the target subnet (Long, required)
- `ip`: IPv4 address to allocate (String, required)
- `hostname`: Device hostname (String, optional)
- `macAddress`: MAC address (String, optional)
- `deviceType`: Device classification (String, optional)
- `owner`: Device owner/user name (String, optional)

**Response (200 OK):**
```json
{
  "id": 42,
  "ip": "192.168.1.100",
  "allocated": true,
  "hostname": "workstation-01",
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "deviceType": "Laptop",
  "owner": "John Doe",
  "subnet": {
    "id": 1,
    "cidr": "192.168.1.0/24",
    "networkAddress": "192.168.1.0",
    "broadcastAddress": "192.168.1.255",
    "firstIp": "192.168.1.1",
    "lastIp": "192.168.1.254",
    "totalIps": 256,
    "description": "Office Network Segment"
  }
}
```

**Error Scenarios:**
- `404 Not Found`: Subnet does not exist
- `409 Conflict`: IP already allocated. Message: "IP already allocated"
- `400 Bad Request`: IP is outside subnet range. Message: "IP not in subnet range"

---

#### 2. Allocate Next Available IP
Automatically allocate the next available (unallocated) IP address from a subnet.

**Request:**
```
POST /ip/allocate-next/{subnetId}
```

**Path Parameters:**
- `subnetId`: ID of the target subnet (Long, required)

**Response (200 OK):**
```json
{
  "id": 43,
  "ip": "192.168.1.101",
  "allocated": true,
  "hostname": null,
  "macAddress": null,
  "deviceType": null,
  "owner": null,
  "subnet": {
    "id": 1
  }
}
```

**Error Scenarios:**
- `404 Not Found`: Subnet does not exist
- `500 Internal Server Error`: No available IPs in the subnet. Message: "No available IPs"

---

#### 3. Bulk IP Allocation
Allocate multiple consecutive IP addresses in a single operation.

**Request:**
```
POST /ip/bulk/{subnetId}/{count}
```

**Path Parameters:**
- `subnetId`: ID of the target subnet (Long, required)
- `count`: Number of IPs to allocate (Integer, required)

**Response (200 OK):**
```json
[
  {
    "id": 44,
    "ip": "192.168.1.102",
    "allocated": true,
    "hostname": null,
    "macAddress": null,
    "deviceType": null,
    "owner": null,
    "subnet": { "id": 1 }
  },
  {
    "id": 45,
    "ip": "192.168.1.103",
    "allocated": true,
    "hostname": null,
    "macAddress": null,
    "deviceType": null,
    "owner": null,
    "subnet": { "id": 1 }
  }
]
```

**Error Scenarios:**
- `404 Not Found`: Subnet does not exist
- `500 Internal Server Error`: Insufficient available IPs for requested count

---

#### 4. Retrieve All IP Addresses
Fetch all allocated and unallocated IP addresses in the system.

**Request:**
```
GET /ip
```

**Response (200 OK):**
```json
[
  {
    "id": 42,
    "ip": "192.168.1.100",
    "allocated": true,
    "hostname": "workstation-01",
    "macAddress": "AA:BB:CC:DD:EE:FF",
    "deviceType": "Laptop",
    "owner": "John Doe",
    "subnet": { "id": 1 }
  },
  {
    "id": 43,
    "ip": "192.168.1.101",
    "allocated": true,
    "hostname": null,
    "macAddress": null,
    "deviceType": null,
    "owner": null,
    "subnet": { "id": 1 }
  }
]
```

---

#### 5. Retrieve IPs by Subnet
Fetch all IP addresses (allocated and unallocated) within a specific subnet.

**Request:**
```
GET /ip/subnet/{subnetId}
```

**Path Parameters:**
- `subnetId`: ID of the target subnet (Long, required)

**Response (200 OK):**
```json
[
  {
    "id": 42,
    "ip": "192.168.1.100",
    "allocated": true,
    "hostname": "workstation-01",
    "macAddress": "AA:BB:CC:DD:EE:FF",
    "deviceType": "Laptop",
    "owner": "John Doe",
    "subnet": { "id": 1 }
  },
  {
    "id": 43,
    "ip": "192.168.1.101",
    "allocated": false,
    "hostname": null,
    "macAddress": null,
    "deviceType": null,
    "owner": null,
    "subnet": { "id": 1 }
  }
]
```

---

#### 6. Release IP Address
Deallocate an assigned IP address and return it to the available pool.

**Request:**
```
PUT /ip/release/{id}
```

**Path Parameters:**
- `id`: IP address record ID (Long, required)

**Response (200 OK):**
```json
"IP Released Successfully"
```

**Error Scenarios:**
- `404 Not Found`: IP record with specified ID does not exist

---

## 📝 Implementation Details

### CIDR Calculation Algorithm
The system uses bit masking to calculate subnet properties:

1. **Network Address**: IP AND subnet mask
2. **Broadcast Address**: Network Address OR inverted mask
3. **First Usable IP**: Network Address + 1
4. **Last Usable IP**: Broadcast Address - 1
5. **Total IPs**: 2^(32 - prefix length)

Example for `/24` network:
- Prefix length: 24
- Mask: 255.255.255.0
- Total IPs: 2^(32-24) = 256

### IP Allocation Strategy
1. **Sequential Search**: Scans from first to last usable IP
2. **Duplicate Prevention**: Checks existing allocations before assignment
3. **Range Validation**: Ensures IP falls within subnet boundaries
4. **Atomic Operations**: Uses database transactions to prevent concurrent conflicts

### Data Persistence
- **Database**: SQLite (lightweight, file-based, no server required)
- **Location**: `ipam.db` in project root directory
- **Auto Schema**: Hibernate DDL set to `update` (automatically creates/updates tables)
- **SQL Logging**: Enabled in development mode (see `application.properties`)

---

## 🧪 Common Use Cases & Workflows

### Workflow 1: Set Up a New Network
```bash
# 1. Create a subnet
curl -X POST http://localhost:8080/subnet \
  -H "Content-Type: application/json" \
  -d '{
    "cidr": "10.0.0.0/24",
    "description": "Production Network"
  }'

# Response returns subnet ID: 1

# 2. Verify subnet creation
curl http://localhost:8080/subnet/1

# 3. Check utilization
curl http://localhost:8080/subnet/utilization/1
```

### Workflow 2: Allocate IPs to Devices
```bash
# 1. Allocate specific IP to a device
curl -X POST http://localhost:8080/ip/allocate-specific \
  -H "Content-Type: application/json" \
  -d '{
    "subnetId": 1,
    "ip": "10.0.0.100",
    "hostname": "server-01",
    "macAddress": "00:1A:2B:3C:4D:5E",
    "deviceType": "Server",
    "owner": "Admin Team"
  }'

# 2. Bulk allocate IPs for offices
curl -X POST http://localhost:8080/ip/bulk/1/50

# 3. View all allocated IPs
curl http://localhost:8080/ip/subnet/1
```

### Workflow 3: Release and Reuse IPs
```bash
# 1. Release an IP
curl -X PUT http://localhost:8080/ip/release/42

# 2. Next available IP can now use that slot
curl -X POST http://localhost:8080/ip/allocate-next/1
```

### Workflow 4: Network Expansion
```bash
# 1. Create new subnet
curl -X POST http://localhost:8080/subnet \
  -H "Content-Type: application/json" \
  -d '{
    "cidr": "10.0.1.0/24",
    "description": "Expansion Network"
  }'

# 2. Bulk allocate IPs for new devices
curl -X POST http://localhost:8080/ip/bulk/2/100

# 3. Monitor utilization
curl http://localhost:8080/subnet/utilization/2
```

---

## 🔍 Troubleshooting

### Issue: "Invalid CIDR format"
**Cause**: CIDR notation is incorrect  
**Solution**: Ensure format is `XXX.XXX.XXX.XXX/PREFIX` where PREFIX is 0-32  
**Example**: `192.168.1.0/24` ✅ | `192.168.1.0/33` ❌

### Issue: "IP already allocated"
**Cause**: Attempting to allocate an IP that's already in use  
**Solution**: Use `allocate-next` endpoint instead of `allocate-specific`, or release the IP first  
**Command**: `PUT /ip/release/{id}`

### Issue: "IP not in subnet range"
**Cause**: IP address falls outside the subnet's usable range  
**Solution**: Verify the IP is between `firstIp` and `lastIp` of the subnet  
**Example**: For `192.168.1.0/24`, valid range is `192.168.1.1` to `192.168.1.254`

### Issue: "Cannot delete subnet. IPs are still allocated."
**Cause**: Subnet has active IP allocations  
**Solution**: Release all IPs in the subnet before deletion  
**Steps**:
1. Get all IPs: `GET /ip/subnet/{subnetId}`
2. Release each allocated IP: `PUT /ip/release/{id}`
3. Delete subnet: `DELETE /subnet/{id}`

### Issue: "No available IPs"
**Cause**: All usable IPs in subnet are allocated  
**Solution**: either expand to larger subnet or release existing IPs  
**Check utilization**: `GET /subnet/utilization/{id}`

---

## 📊 Database Schema

### Subnet Table
```sql
CREATE TABLE subnet (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  cidr VARCHAR(255) NOT NULL,
  network_address VARCHAR(255),
  broadcast_address VARCHAR(255),
  first_ip VARCHAR(255),
  last_ip VARCHAR(255),
  total_ips INT,
  description VARCHAR(255)
);
```

### IPAddress Table
```sql
CREATE TABLE ip_address (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ip VARCHAR(255) NOT NULL,
  allocated BOOLEAN DEFAULT FALSE,
  hostname VARCHAR(255),
  mac_address VARCHAR(255),
  device_type VARCHAR(255),
  owner VARCHAR(255),
  subnet_id BIGINT REFERENCES subnet(id)
);
```

---

## 🔧 Advanced Configuration

### Modify Database Location
Edit `application.properties`:
```properties
spring.datasource.url=jdbc:sqlite:/custom/path/ipam.db
```

### Enable SQL Query Logging
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Change Server Port
```properties
server.port=9090
```

### Disable Auto Schema Creation
```properties
spring.jpa.hibernate.ddl-auto=validate
```

---

## 📋 Testing with Curl

### Create Subnet
```bash
curl -X POST http://localhost:8080/subnet \
  -H "Content-Type: application/json" \
  -d '{
    "cidr": "172.16.0.0/16",
    "description": "Test Network"
  }'
```

### Get All Subnets
```bash
curl http://localhost:8080/subnet
```

### Allocate Specific IP
```bash
curl -X POST http://localhost:8080/ip/allocate-specific \
  -H "Content-Type: application/json" \
  -d '{
    "subnetId": 1,
    "ip": "172.16.0.10",
    "hostname": "test-host",
    "macAddress": "11:22:33:44:55:66",
    "deviceType": "Test",
    "owner": "Test User"
  }'
```

### View Subnet Utilization
```bash
curl http://localhost:8080/subnet/utilization/1
```

### Release IP
```bash
curl -X PUT http://localhost:8080/ip/release/1
```

---

##  Project Structure

```
ipam/
├── src/
│   ├── main/
│   │   ├── java/com/ipam/
│   │   │   ├── IpamApplication.java          # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   ├── IPController.java         # IP REST endpoints
│   │   │   │   └── SubnetController.java     # Subnet REST endpoints
│   │   │   ├── service/
│   │   │   │   ├── IPService.java            # IP business logic
│   │   │   │   └── SubnetService.java        # Subnet business logic
│   │   │   ├── model/
│   │   │   │   ├── IPAddress.java            # IP entity
│   │   │   │   └── Subnet.java               # Subnet entity
│   │   │   ├── repository/
│   │   │   │   ├── IPRepository.java         # IP data access
│   │   │   │   └── SubnetRepository.java     # Subnet data access
│   │   │   ├── dto/
│   │   │   │   ├── IpRequestDTO.java         # IP request DTO
│   │   │   │   └── IpResponseDTO.java        # IP response DTO
│   │   │   ├── util/
│   │   │   │   └── IPUtils.java              # CIDR calculations & conversions
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # Security configuration
│   │   │   │   └── SwaggerConfig.java        # API documentation config
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── application.properties         # App configuration
│   │       ├── static/                        # Static assets
│   │       └── templates/                     # HTML templates
│   └── test/
│       └── java/com/ipam/
│           └── IpamApplicationTests.java      # Integration tests
├── pom.xml                                    # Maven configuration
├── Readme.md                                  # This file
├── HELP.md                                    # Help documentation
└── ipam-postman-collection.json              # Postman API collection
```

---

##  Postman Collection

A Postman collection is included in the project: `ipam-postman-collection.json`

**Import Steps:**
1. Open Postman
2. Click "Import" → "Upload Files"
3. Select `ipam-postman-collection.json`
4. All endpoints will be pre-configured and ready to test

---

##  Contributing

To contribute to this project:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit changes (`git commit -am 'Add YourFeature'`)
4. Push to branch (`git push origin feature/YourFeature`)
5. Open a Pull Request

---

##  License

This project is licensed under the MIT License - see the LICENSE file for details.

---

##  Author & Support

For issues, questions, or suggestions:
- Check existing documentation at the top of this file
- Review the troubleshooting section
- Check the Postman collection for endpoint examples
- Review application logs for detailed error messages

---

**Last Updated**: April 3, 2026  
**Version**: 0.0.1-SNAPSHOT