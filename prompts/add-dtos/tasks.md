# Tasks: Implement DTOs and MapStruct Mapping for Transaction API

1. Update Build Dependencies (pom.xml)
   1. Add MapStruct dependencies:
      - compileOnly: org.mapstruct:mapstruct
      - annotationProcessor: org.mapstruct:mapstruct-processor
   2. Ensure Lombok remains:
      - org.projectlombok:lombok (optional)
      - maven-compiler-plugin configured with both Lombok and MapStruct under <annotationProcessorPaths>.
   3. Validate Maven builds successfully: mvn -q -DskipTests package

2. Create DTO Class
   1. Package: ua.ardas.udemy_ai.models
   2. Class: TransactionDTO
   3. Lombok annotations: @Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor
   4. Fields (mirror API surface):
      - Long id
      - BigDecimal amount
      - String currency
      - String description
      - String status (enum name values)
      - OffsetDateTime createdAt
      - OffsetDateTime updatedAt
   5. Optional (do not change controller error contract yet): add javax/jakarta validation annotations if desired (e.g., @NotNull for amount, currency).

3. Add MapStruct Mapper
   1. Package: ua.ardas.udemy_ai.mappers
   2. Interface: TransactionMapper
   3. Annotation: @Mapper(componentModel = "spring")
   4. Methods:
      - TransactionDTO toDto(ua.ardas.udemy_ai.entities.Transaction entity)
      - ua.ardas.udemy_ai.entities.Transaction toEntity(TransactionDTO dto)
   5. Mapping details:
      - status: map between entity enum and DTO string via enum name; MapStruct handles this by default. If needed, define @Mapping with qualifiedByName methods.
      - createdAt, updatedAt: copy as-is.

4. Update Controller to Use DTOs
   1. File: ua.ardas.udemy_ai.controllers.TransactionController
   2. Inject TransactionMapper.
   3. Endpoints:
      - GET /api/transactions/{id}: return ResponseEntity<TransactionDTO>
        - Map entity from service.getById to DTO (mapper.toDto)
        - Preserve 404 when absent.
      - GET /api/transactions: return List<TransactionDTO>
        - Map each entity to DTO (stream + mapper.toDto)
      - POST /api/transactions: accept @RequestBody TransactionDTO
        - Map DTO -> entity
        - Call service.create(entity)
        - Map created entity -> DTO
        - Return 201 Created with Location: /api/transactions/{id}
   4. Do not change service signatures; service continues to use entities.

5. Testing Updates
   1. Controller tests (ua.ardas.udemy_ai.controllers.TransactionControllerTest):
      - Keep TransactionService mocked (returns entities).
      - Make mapper available in the test context:
        - Option A: @Import(MapStruct-generated TransactionMapperImpl.class) if available, or 
        - Option B: Provide a @TestConfiguration bean that returns Mappers.getMapper(TransactionMapper.class) (static factory).
      - Update request/response bodies to use DTOs implicitly via controller; JSON field names and values should remain the same.
      - Verify:
        - GET by id: 200 with expected JSON (fields: id, amount, currency, description, status, createdAt, updatedAt)
        - GET list: array with same fields
        - POST: 201 with Location header and body matching created resource
   2. Repository tests remain unchanged.
   3. Run all tests: mvn -q test

6. Serialization and Format Checks
   1. Ensure BigDecimal serializes as numbers (Spring Boot defaults suffice).
   2. Ensure OffsetDateTime serializes ISO-8601 (jackson-datatype-jsr310 auto-configured by Spring Boot).
   3. Ensure status in DTO is uppercase string enum name.

7. Acceptance Criteria Verification
   1. TransactionDTO exists in ua.ardas.udemy_ai.models with required fields and Lombok.
   2. TransactionMapper exists in ua.ardas.udemy_ai.mappers and is Spring-managed.
   3. TransactionController exclusively uses DTOs for request/response bodies.
   4. API behavior preserved:
      - GET /api/transactions/{id}: 200 with body when found; 404 otherwise.
      - GET /api/transactions: 200 with array of items.
      - POST /api/transactions: 201 with Location header and created resource in body.
   5. Maven build succeeds and tests pass.

8. Documentation (Optional but Recommended)
   1. Update .junie/guidelines.md to note DTO usage and MapStruct in the stack.
   2. Update requirements.md if implementation nuances differ.

9. Housekeeping
   1. Ensure code style and imports are organized.
   2. Commit changes with a clear message, e.g., "feat(api): introduce TransactionDTO and MapStruct mapping; update controller/tests".
