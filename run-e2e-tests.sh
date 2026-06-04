#!/bin/bash

# ============================================
# Employee Management API - E2E Test Script
# Run API tests with Docker
# ============================================

set -e

echo "======================================"
echo "Employee Management API - E2E Tests"
echo "======================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed. Please install Docker first.${NC}"
    exit 1
fi

echo -e "${YELLOW}1. Stopping any existing containers...${NC}"
docker-compose down 2>/dev/null || true

echo -e "${YELLOW}2. Building Docker image...${NC}"
docker-compose build

echo -e "${YELLOW}3. Starting containers...${NC}"
docker-compose up -d

# Wait for app to start
echo -e "${YELLOW}4. Waiting for application to start (30 seconds)...${NC}"
sleep 30

# Check if app is running
if ! curl -f http://localhost:8080/api/employees > /dev/null 2>&1; then
    echo -e "${RED}❌ Application failed to start${NC}"
    docker-compose logs app
    docker-compose down
    exit 1
fi

echo -e "${GREEN}✅ Application is running${NC}"

# Run tests
echo -e "${YELLOW}5. Running E2E tests...${NC}"
mvn clean test -Dtest=EmployeeE2ETest -Dspring.profiles.active=test

TEST_RESULT=$?

# Cleanup
echo -e "${YELLOW}6. Cleaning up...${NC}"
docker-compose down

if [ $TEST_RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ All E2E tests passed!${NC}"
    exit 0
else
    echo -e "${RED}❌ E2E tests failed!${NC}"
    exit 1
fi
