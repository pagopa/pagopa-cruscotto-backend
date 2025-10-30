# KPI Framework - Modern Architecture with Composition over Inheritance

## Overview

This new KPI framework implements modern software engineering principles to create a reusable, extensible, and maintainable architecture for all KPI calculations.

## Architecture Principles

### 1. **Composition over Inheritance**
- Uses composition and dependency injection instead of deep inheritance hierarchies
- Each KPI processor composes strategies, aggregators, and evaluators
- Flexible and testable design

### 2. **Generics for Type Safety**
- Type-safe interfaces using Java generics: `KpiProcessor<R, D, A>`
- Compile-time type checking for KPI Result, Detail Result, and Analytic Data types
- No runtime casting errors

### 3. **Strategy Pattern**
- `KpiEvaluationStrategy<T>`: Pluggable evaluation logic
- `StandardThresholdEvaluationStrategy`: Standard threshold + tolerance fallback
- `ToleranceBasedEvaluationStrategy`: Percentage-based tolerance evaluation

### 4. **Data Aggregation Abstraction**
- `DataAggregator<T, R>`: Generic data aggregation interface
- Composable aggregation strategies for different data types
- Reusable across multiple KPIs

## Core Components

### Framework Interfaces

```java
// Main processor interface
public interface KpiProcessor<R extends KpiResult, D extends KpiDetailResult, A extends KpiAnalyticData>

// Marker interfaces for type safety
public interface KpiResult
public interface KpiDetailResult  
public interface KpiAnalyticData

// Strategy interfaces
public interface KpiEvaluationStrategy<T extends Number>
public interface DataAggregator<T, R>
```

### Base Classes

```java
// Abstract base providing common functionality
public abstract class AbstractKpiProcessor<R, D, A> implements KpiProcessor<R, D, A>

// Execution context with all necessary data
public class KpiExecutionContext
```

## KPI B.6 Implementation Example

### Business Rules
- **Objective**: Payment options must be active on all active stations
- **Evaluation**: Percentage compliance with tolerance threshold
- **Data Source**: Station configuration data

### Implementation Structure

```
kpi/b6/
├── KpiB6Processor.java                    # Main processor
├── aggregation/
│   └── StationPaymentOptionsAggregator.java  # Data aggregation logic
└── evaluation/
    └── (uses ToleranceBasedEvaluationStrategy)  # Pluggable evaluation
```

### Key Features

1. **Type-Safe Processing**
   ```java
   public class KpiB6Processor extends AbstractKpiProcessor<KpiB6ResultDTO, KpiB6DetailResultDTO, KpiB6AnalyticDataDTO>
   ```

2. **Composition-Based Design**
   ```java
   private final StationPaymentOptionsAggregator aggregator;
   private final ToleranceBasedEvaluationStrategy toleranceEvaluationStrategy;
   ```

3. **Reusable Components**
   - `ToleranceBasedEvaluationStrategy`: Can be reused by other KPIs
   - `StationPaymentOptionsAggregator`: Focused, single-responsibility aggregation
   - `AbstractKpiProcessor`: Common functionality shared across KPIs

## Extension to Other KPIs

### Step 1: Create KPI-Specific DTOs
```java
// Implement marker interfaces
public class KpiXResultDTO implements KpiResult { ... }
public class KpiXDetailResultDTO implements KpiDetailResult { ... }
public class KpiXAnalyticDataDTO implements KpiAnalyticData { ... }
```

### Step 2: Create Aggregator (if needed)
```java
@Component
public class KpiXDataAggregator implements DataAggregator<InputType, OutputType> {
    // KPI-specific aggregation logic
}
```

### Step 3: Create Processor
```java
@Component  
public class KpiXProcessor extends AbstractKpiProcessor<KpiXResultDTO, KpiXDetailResultDTO, KpiXAnalyticDataDTO> {
    
    public KpiXProcessor(KpiXDataAggregator aggregator, KpiEvaluationStrategy<Number> evaluationStrategy) {
        super(evaluationStrategy);
        this.aggregator = aggregator;
    }
    
    // Implement abstract methods
}
```

### Step 4: Create Job (optional)
```java
@Component
public class KpiXJob extends QuartzJobBean {
    private final KpiXProcessor processor;
    // Use processor in executeInternal()
}
```

## Benefits of This Architecture

### 1. **Reusability**
- Evaluation strategies shared across KPIs
- Common aggregation patterns reused
- Base functionality in `AbstractKpiProcessor`

### 2. **Testability**
- Each component can be tested in isolation
- Mock injection for dependencies
- Strategy pattern enables behavior verification

### 3. **Maintainability**
- Single responsibility principle
- Clear separation of concerns
- Type safety prevents runtime errors

### 4. **Extensibility**
- New KPIs follow the same pattern
- New evaluation strategies can be added
- Framework evolves without breaking existing KPIs

## Migration Strategy

### Phase 1: Framework Foundation ✅
- Core interfaces and base classes
- KPI B.6 as reference implementation
- Evaluation strategies

### Phase 2: Gradual Migration
- Refactor existing KPIs (B.1, B.2, etc.) to use framework
- Extract common patterns into reusable components
- Maintain backward compatibility

### Phase 3: Framework Enhancement
- Add monitoring and metrics
- Performance optimizations
- Advanced evaluation strategies

## Usage Example

```java
// In a job or service
KpiExecutionContext context = KpiExecutionContext.builder()
    .instance(instance)
    .instanceModule(instanceModule)
    .configuration(configuration)
    .additionalParameters(dataMap)
    .build();

// Process any KPI using the orchestrator
OutcomeStatus outcome = kpiOrchestrator.processKpi(context);
```

This architecture provides a solid foundation for scalable, maintainable KPI processing while demonstrating modern Java development practices.