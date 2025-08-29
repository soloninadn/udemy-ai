# Requirements: Introduce DTOs and MapStruct Mapping for Transaction API

## 1. Overview
This change introduces Data Transfer Objects (DTOs) for the Transaction REST API and uses MapStruct to map between the existing JPA entity (ua.ardas.udemy_ai.entities.Transaction) and DTOs. The controller layer must expose and accept DTOs, keeping the service and repository layers working with entities.

## 2. Goals
- Decouple API models from persistence models by introducing TransactionDTO.
- Update TransactionController to use DTOs for request and response bodies.
- Provide a MapStruct mapper for conversion between Transaction and TransactionDTO.
- Preserve existing API behavior (status codes, headers, and JSON field names) while switching to DTOs.

## 3. Non‑Goals
- No business logic changes to service/repository layers.
- No introduction of new endpoints or fields beyond those already present in the Transaction entity.
- No change to database schema or entity mappings.

## 4. Current State (for reference)
- Entity: ua.ardas.udemy_ai.entities.Transaction
  - Fields: id, amount, currency, description, status, createdAt, updatedAt
- REST Controller: ua.ardas.udemy_ai.controllers.TransactionController
  - Endpoints: GET /api/transactions, GET /api/transactions/{id}, POST /api/transactions
  - Currently uses the entity directly as request/response body
- Service: ua.ardas.udemy_ai.services.TransactionService (+ Impl)
- Tests: Repository (@DataJpaTest) and Controller (@WebMvcTest)

## 5. Changes Required

### 5.1 DTO Definition
- Package: ua.ardas.udemy_ai.models
- Class: TransactionDTO
- Lombok: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
- Fields (match the entity’s surface used in API):
  - Long id
  - BigDecimal amount
  - String currency (ISO 4217, e.g., "USD")
  - String description
  - String status (enum name values: PENDING, COMPLETED, FAILED, CANCELED)
  - OffsetDateTime createdAt
  - OffsetDateTime updatedAt
- Notes:
  - Keep names and types aligned with the current JSON payloads used in tests (BigDecimal as numbers; dates as ISO-8601 strings).
  - Optional: Add bean validation annotations where appropriate (e.g., @NotNull for amount/currency) without changing controller error contract yet.

### 5.2 MapStruct Mapper
- Package: ua.ardas.udemy_ai.mappers
- Interface: TransactionMapper
- Annotations:
  - @Mapper(componentModel = "spring")
- Methods:
  - TransactionDTO toDto(Transaction entity)
  - Transaction toEntity(TransactionDTO dto)
- Mapping specifics:
  - status mapping: entity.status (enum) ↔ dto.status (String). MapStruct can map via name; define explicit mapping if needed.
  - createdAt/updatedAt should be copied as-is.

### 5.3 Controller Update
- Update ua.ardas.udemy_ai.controllers.TransactionController to:
  - Inject TransactionMapper.
  - GET /api/transactions/{id}: return ResponseEntity<TransactionDTO> using mapper.toDto if found; 404 otherwise.
  - GET /api/transactions: return List<TransactionDTO>, mapping each entity to DTO.
  - POST /api/transactions: accept TransactionDTO as request body, map to entity, call service.create, then map created entity back to DTO.
  - Keep response status codes and Location header unchanged (Location: /api/transactions/{id}).

### 5.4 Service Layer
- No changes to signatures or behavior.
- Service continues to operate on entities.

### 5.5 Dependencies (pom.xml)
- Add MapStruct and annotation processors:
  - compileOnly org.mapstruct:mapstruct
  - annotationProcessor org.mapstruct:mapstruct-processor
  - Ensure Lombok and MapStruct processors co-exist in maven-compiler-plugin annotationProcessorPaths.

### 5.6 JSON and Serialization Considerations
- BigDecimal should serialize as numbers (existing tests use numeric matchers).
- Dates (OffsetDateTime) serialize in ISO-8601; ensure Jackson Java Time module is available via Spring Boot defaults.
- Enum status exposed as uppercase string in DTO.

### 5.7 Testing Updates
- Update TransactionControllerTest to work with DTO-based API:
  - Mock service as before (still returns entities).
  - Configure the test context to include TransactionMapper (use MapStruct-generated implementation via @Import or a @MapperScan, or instantiate via Mappers.getMapper if not using Spring context).
  - Adjust request/response JSON expectations to still match current field names and values (they should remain unchanged).
- Repository tests remain unchanged.
- All tests must pass: mvn test.

## 6. Acceptance Criteria
- A new class ua.ardas.udemy_ai.models.TransactionDTO exists per section 5.1.
- A new mapper ua.ardas.udemy_ai.mappers.TransactionMapper exists per section 5.2 and is Spring-managed.
- TransactionController uses DTOs for all request and response bodies and compiles.
- Existing API behavior is preserved:
  - GET by id returns 200 with JSON matching previous fields when found; 404 otherwise.
  - GET list returns 200 with array of items matching previous fields.
  - POST returns 201, sets Location header to /api/transactions/{id}, and returns created resource in body.
- Maven build succeeds with MapStruct integrated.
- Controller tests updated and passing; repository tests continue to pass.

## 7. Backward Compatibility
- Field names in JSON remain identical to the entity-backed responses used previously.
- No changes to endpoint paths or HTTP status codes.
