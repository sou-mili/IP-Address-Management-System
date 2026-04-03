# 📌 IP Address Management System (IPAM)

## 🚀 Overview
The **IP Address Management System (IPAM)** is a backend application built using **Spring Boot** that helps manage IP address allocation and subnet tracking.

It provides:
- Subnet creation using CIDR notation
- Automatic network calculations
- IP allocation & deallocation
- Conflict detection
- Utilization tracking

---

## 🧠 Key Features

### 🔹 Network Management
- Create subnet using CIDR (e.g., `192.168.1.0/24`)
- Automatic calculation:
  - Network Address
  - Broadcast Address
  - First & Last usable IP
  - Total IPs
- Pagination support
- Subnet utilization (Used / Free IPs)
- Update subnet metadata
- Safe delete (only if no IPs allocated)

---

### 🔹 IP Address Management
- Allocate specific IP
- Allocate next available IP
- Bulk IP allocation
- Release IP
- View IPs by subnet
- Store metadata (hostname, MAC, device type, owner)

---

### 🔹 Validation & Conflict Detection
- Prevent duplicate IP allocation
- Validate CIDR format
- Ensure IP belongs to subnet
- Prevent subnet deletion with allocated IPs

---

### 🔹 Error Handling
- 400 → Bad Request  
- 404 → Not Found  
- 409 → Conflict  
- 500 → Internal Server Error  

---

## 🛠️ Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- SQLite
- Maven
- Postman

---

## ⚙️ Setup Instructions

### 1. Clone the Repository
```bash
git clone <your-repo-link>
cd ipam