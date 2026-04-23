---
title: Android Mobile App with Jetpack Compose and Bounded Contexts
date: 2025-10-26
tags:
  - project-structure
  - architecture
  - android
  - jetpack-compose
  - mobile
  - bounded-contexts
  - ddd
stack:
  - Kotlin
  - Jetpack Compose
  - Coroutines
  - Room Database
  - Retrofit
  - Navigation Compose
principles:
  - "[[SWE/02 - Concepts & Permanent Notes/0201 - Software Architecture/020102 - Architectural Styles/Clean Architecture]]"
  - "[[Domain-Driven Design]]"
  - "[[MVVM Pattern]]"
  - "[[What is exactly the Repository Pattern about]]"
  - "[[Bounded Context Pattern]]"
---

## 1. Philosophy & Guiding Principles

This structure is a **simplified** approach combining **[[SWE/02 - Concepts & Permanent Notes/0201 - Software Architecture/020102 - Architectural Styles/Clean Architecture]]** with **[[Domain-Driven Design]]** organized by **[[Bounded Contexts]]**. It removes unnecessary complexity while maintaining clear separation of concerns and alignment with backend DDD architecture.

- **Domain-centric with Bounded Contexts:** Each bounded context encapsulates its own business rules, organized within architectural layers.
- **Dependency Rule:** Dependencies flow inwards. Presentation depends on domain, data implements domain interfaces, but domain depends on nothing.
- **Context Isolation:** Each bounded context has its own domain models, use cases, and data models within each layer.
- **Minimal Shared Kernel:** Only essential shared abstractions and UI components to avoid duplication while maintaining independence.
- **Unidirectional Data Flow:** State flows down from ViewModel to UI, events flow up from UI to ViewModel.
- **Pragmatic Simplicity:** No anti-corruption layers, domain events, or event buses unless specifically needed.
- **Testability First:** Each context and layer can be tested independently with minimal mocking.

---

## 2. Folder Structure Tree

```text
com.yourcompany.yourapp/
│
├── 📁 shared/                          # Shared kernel (minimal)
│   ├── 📁 domain/
│   │   ├── 📄 Result.kt              # Result wrapper for success/error
│   │   └── 📄 UseCase.kt             # Base use case interface (optional)
│   ├── 📁 ui/
│   │   ├── 📁 components/            # Reusable composables
│   │   │   ├── 📄 CustomButton.kt
│   │   │   ├── 📄 LoadingIndicator.kt
│   │   │   ├── 📄 ErrorView.kt
│   │   │   └── 📄 EmptyStateView.kt
│   │   └── 📁 theme/                 # Material 3 theming
│   │       ├── 📄 Color.kt
│   │       ├── 📄 Type.kt
│   │       ├── 📄 Shape.kt
│   │       └── 📄 Theme.kt
│   └── 📁 util/
│       ├── 📄 Constants.kt
│       └── 📄 Extensions.kt
│
├── 📁 domain/                        # Domain layer by bounded context
│   ├── 📁 sales/
│   │   ├── 📁 model/
│   │   │   ├── 📄 Order.kt
│   │   │   ├── 📄 Invoice.kt
│   │   │   └── 📄 Payment.kt
│   │   ├── 📁 repository/
│   │   │   ├── 📄 OrderRepository.kt
│   │   │   └── 📄 InvoiceRepository.kt
│   │   └── 📁 usecase/
│   │       ├── 📄 CreateOrderUseCase.kt
│   │       ├── 📄 GetOrdersUseCase.kt
│   │       └── 📄 ProcessPaymentUseCase.kt
│   │
│   ├── 📁 inventory/
│   │   ├── 📁 model/
│   │   │   ├── 📄 Product.kt
│   │   │   └── 📄 Stock.kt
│   │   ├── 📁 repository/
│   │   │   └── 📄 ProductRepository.kt
│   │   └── 📁 usecase/
│   │       ├── 📄 GetProductsUseCase.kt
│   │       └── 📄 UpdateStockUseCase.kt
│   │
│   └── 📁 auth/
│       ├── 📁 model/
│       │   ├── 📄 User.kt
│       │   └── 📄 Session.kt
│       ├── 📁 repository/
│       │   └── 📄 AuthRepository.kt
│       └── 📁 usecase/
│           ├── 📄 LoginUseCase.kt
│           └── 📄 LogoutUseCase.kt
│
├── 📁 data/                          # Data layer by bounded context
│   ├── 📁 sales/
│   │   ├── 📁 repository/
│   │   │   └── 📄 OrderRepositoryImpl.kt
│   │   ├── 📁 remote/
│   │   │   ├── 📄 SalesApi.kt
│   │   │   └── 📁 dto/
│   │   │       ├── 📄 OrderDto.kt
│   │   │       └── 📄 InvoiceDto.kt
│   │   ├── 📁 local/                 # Optional: for offline support
│   │   │   ├── 📄 OrderDao.kt
│   │   │   └── 📄 OrderEntity.kt
│   │   └── 📁 mapper/
│   │       └── 📄 OrderMapper.kt
│   │
│   ├── 📁 inventory/
│   │   ├── 📁 repository/
│   │   │   └── 📄 ProductRepositoryImpl.kt
│   │   ├── 📁 remote/
│   │   │   ├── 📄 InventoryApi.kt
│   │   │   └── 📁 dto/
│   │   │       └── 📄 ProductDto.kt
│   │   ├── 📁 local/
│   │   │   ├── 📄 ProductDao.kt
│   │   │   └── 📄 ProductEntity.kt
│   │   └── 📁 mapper/
│   │       └── 📄 ProductMapper.kt
│   │
│   └── 📁 auth/
│       ├── 📁 repository/
│       │   └── 📄 AuthRepositoryImpl.kt
│       ├── 📁 remote/
│       │   ├── 📄 AuthApi.kt
│       │   └── 📁 dto/
│       │       └── 📄 LoginDto.kt
│       └── 📁 local/
│           └── 📄 SessionPreferences.kt
│
├── 📁 presentation/                  # Presentation layer by feature/context
│   ├── 📁 sales/
│   │   ├── 📁 orders/
│   │   │   ├── 📄 OrderListScreen.kt
│   │   │   ├── 📄 OrderListViewModel.kt
│   │   │   └── 📄 OrderListState.kt
│   │   ├── 📁 checkout/
│   │   │   ├── 📄 CheckoutScreen.kt
│   │   │   ├── 📄 CheckoutViewModel.kt
│   │   │   └── 📄 CheckoutState.kt
│   │   └── 📁 navigation/
│   │       └── 📄 SalesNavGraph.kt
│   │
│   ├── 📁 inventory/
│   │   ├── 📁 products/
│   │   │   ├── 📄 ProductListScreen.kt
│   │   │   ├── 📄 ProductDetailScreen.kt
│   │   │   ├── 📄 ProductViewModel.kt
│   │   │   └── 📄 ProductState.kt
│   │   └── 📁 navigation/
│   │       └── 📄 InventoryNavGraph.kt
│   │
│   └── 📁 auth/
│       ├── 📁 login/
│       │   ├── 📄 LoginScreen.kt
│       │   ├── 📄 LoginViewModel.kt
│       │   └── 📄 LoginState.kt
│       └── 📁 navigation/
│           └── 📄 AuthNavGraph.kt
│
├── 📁 di/                            # Dependency Injection
│   ├── 📄 AppModule.kt
│   ├── 📄 SharedModule.kt
│   ├── 📄 SalesModule.kt
│   ├── 📄 InventoryModule.kt
│   └── 📄 AuthModule.kt
│
└── 📁 navigation/                    # Global navigation
    ├── 📄 AppNavGraph.kt
    └── 📄 Screen.kt
```

---

## 3. Shared Directory Breakdown

### **`/shared`**: Shared Kernel (Minimal)

Contains only essential shared code that all contexts need:

- **`domain/`**: Core abstractions used across all contexts
  - `Result.kt`: Sealed class for handling success/error results
  - `UseCase.kt`: Optional base interface for use cases
- **`ui/`**: Shared UI components and theming
  - `components/`: Reusable Composables (buttons, loading indicators, error views)
  - `theme/`: Material 3 theme configuration
- **`util/`**: Common utilities and extension functions

**Rule**: Keep this as small as possible. When in doubt, duplicate rather than share.

### **`/domain`**: Domain Layer by Bounded Context

Pure business logic organized by bounded context, independent of any framework:

- **`{context}/model/`**: Domain entities for this context (e.g., Order, Product, User)
- **`{context}/repository/`**: Repository interfaces (contracts) for data operations
- **`{context}/usecase/`**: Business rules and workflows specific to this context

**Key Points**:

- No Android dependencies
- No knowledge of how data is stored or presented
- Each context is completely independent

### **`/data`**: Data Layer by Bounded Context

Implements repository interfaces and handles data operations:

- **`{context}/repository/`**: Concrete implementations of repository interfaces
- **`{context}/remote/`**: API services and DTOs for network calls
  - `{Context}Api.kt`: Retrofit service interface
  - `dto/`: Data Transfer Objects matching API structure
- **`{context}/local/`**: Optional local persistence for offline support
  - DAOs for Room database
  - Entity classes for database tables
- **`{context}/mapper/`**: Converts between DTOs/Entities and Domain models

**Simplification**: If contexts need data from other contexts, they call the API again. The backend handles consistency.

### **`/presentation`**: Presentation Layer by Feature/Context

UI implementation using Jetpack Compose and MVVM pattern:

- **`{context}/{feature}/`**: Each feature has its own folder
  - `*Screen.kt`: Composable functions for UI
  - `*ViewModel.kt`: Manages UI state and orchestrates use cases
  - `*State.kt`: Data class representing UI state
- **`{context}/navigation/`**: Navigation graph for this context's screens

**Pattern**: Simple state management without sealed class events unless needed for complex flows.

### **`/di`**: Dependency Injection

Hilt modules organized by layer and context:

- `AppModule.kt`: Application-level dependencies
- `SharedModule.kt`: Shared kernel dependencies (Retrofit, Room, etc.)
- `{Context}Module.kt`: Context-specific dependencies

### **`/navigation`**: Global Navigation

Coordinates navigation between contexts:

- `AppNavGraph.kt`: Root navigation graph coordinating all contexts
- `Screen.kt`: Sealed class defining all screen routes

---

## 4. Data Flow & Architecture Patterns

### Request Flow (User Action → Data)

```
User Interaction (Composable)
        ↓
    ViewModel (manages state)
        ↓
    Use Case (business logic)
        ↓
Repository Interface (domain contract)
        ↓
Repository Implementation (data layer)
        ↓
API Service (Retrofit) / DAO (Room)
        ↓
    Backend API / Local Database
```

### Response Flow (Data → UI)

```
Backend API / Local Database
        ↓
API Service / DAO
        ↓
Mapper (DTO/Entity → Domain Model)
        ↓
Repository Implementation
        ↓
Use Case (processes result)
        ↓
ViewModel (updates State)
        ↓
Composable (recomposes with new state)
```

### Cross-Context Data Access

When one context needs data from another:

```
Context A Use Case
        ↓
Calls Backend API for Context B data
        ↓
Backend handles consistency and relationships
        ↓
Context A receives data from its own API endpoint
```

**No direct context-to-context communication in the mobile app.**

### Key Patterns Applied

1. **[[SWE/02 - Concepts & Permanent Notes/0201 - Software Architecture/020102 - Architectural Styles/Clean Architecture]]**: Three-layer separation (Domain → Data → Presentation) within each context
2. **[[Domain-Driven Design]]**: Business logic organized by bounded contexts matching backend
3. **[[MVVM Pattern]]**: Presentation layer follows Model-View-ViewModel
4. **[[What is exactly the Repository Pattern about]]**: Abstraction over data sources
5. **[[Dependency Injection]]**: Hilt for dependency management
6. **[[Unidirectional Data Flow]]**: State flows down, events flow up

---

## 5. Code Examples

### Shared Layer Example

```kotlin
// shared/domain/Result.kt
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
}

// Extension functions for common operations
suspend inline fun <T> runCatching(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}
```

### Domain Layer Example (Sales Context)

```kotlin
// domain/sales/model/Order.kt
data class Order(
    val id: String,
    val customerId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalAmount: Double,
    val createdAt: Long
) {
    fun canBeCancelled(): Boolean {
        return status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED)
    }

    fun calculateTotal(): Double {
        return items.sumOf { it.price * it.quantity }
    }
}

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double
)

enum class OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}

// domain/sales/repository/OrderRepository.kt
interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
    suspend fun getOrderById(id: String): Result<Order>
    suspend fun createOrder(order: Order): Result<Order>
    suspend fun cancelOrder(id: String): Result<Unit>
}

// domain/sales/usecase/CreateOrderUseCase.kt
class CreateOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        customerId: String,
        items: List<OrderItem>
    ): Result<Order> {
        if (items.isEmpty()) {
            return Result.Error(IllegalArgumentException("Order must have at least one item"))
        }

        val order = Order(
            id = "", // Backend generates ID
            customerId = customerId,
            items = items,
            status = OrderStatus.PENDING,
            totalAmount = items.sumOf { it.price * it.quantity },
            createdAt = System.currentTimeMillis()
        )

        return orderRepository.createOrder(order)
    }
}
```

### Data Layer Example (Sales Context)

```kotlin
// data/sales/remote/SalesApi.kt
interface SalesApi {
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): OrderDto

    @POST("orders")
    suspend fun createOrder(@Body order: OrderDto): OrderDto

    @PUT("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String)
}

// data/sales/remote/dto/OrderDto.kt
data class OrderDto(
    val id: String,
    val customerId: String,
    val items: List<OrderItemDto>,
    val status: String,
    val totalAmount: Double,
    val createdAt: Long
)

data class OrderItemDto(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double
)

// data/sales/mapper/OrderMapper.kt
class OrderMapper {
    fun toDomain(dto: OrderDto): Order = Order(
        id = dto.id,
        customerId = dto.customerId,
        items = dto.items.map { toDomain(it) },
        status = OrderStatus.valueOf(dto.status),
        totalAmount = dto.totalAmount,
        createdAt = dto.createdAt
    )

    private fun toDomain(dto: OrderItemDto): OrderItem = OrderItem(
        productId = dto.productId,
        productName = dto.productName,
        quantity = dto.quantity,
        price = dto.price
    )

    fun toDto(order: Order): OrderDto = OrderDto(
        id = order.id,
        customerId = order.customerId,
        items = order.items.map { toDto(it) },
        status = order.status.name,
        totalAmount = order.totalAmount,
        createdAt = order.createdAt
    )

    private fun toDto(item: OrderItem): OrderItemDto = OrderItemDto(
        productId = item.productId,
        productName = item.productName,
        quantity = item.quantity,
        price = item.price
    )
}

// data/sales/repository/OrderRepositoryImpl.kt
class OrderRepositoryImpl(
    private val api: SalesApi,
    private val mapper: OrderMapper,
    private val orderDao: OrderDao? = null // Optional for offline support
) : OrderRepository {

    override suspend fun getOrders(): Result<List<Order>> = runCatching {
        val dtos = api.getOrders()
        dtos.map { mapper.toDomain(it) }
    }

    override suspend fun getOrderById(id: String): Result<Order> = runCatching {
        // Try local cache first (if implemented)
        orderDao?.getOrderById(id)?.let {
            return Result.Success(mapper.toDomain(it))
        }

        // Fetch from API
        val dto = api.getOrderById(id)
        mapper.toDomain(dto)
    }

    override suspend fun createOrder(order: Order): Result<Order> = runCatching {
        val dto = mapper.toDto(order)
        val createdDto = api.createOrder(dto)
        mapper.toDomain(createdDto)
    }

    override suspend fun cancelOrder(id: String): Result<Unit> = runCatching {
        api.cancelOrder(id)
    }
}
```

### Presentation Layer Example (Sales Context)

```kotlin
// presentation/sales/orders/OrderListState.kt
data class OrderListState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// presentation/sales/orders/OrderListViewModel.kt
@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase
) : ViewModel() {

    var state by mutableStateOf(OrderListState())
        private set

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            getOrdersUseCase()
                .onSuccess { orders ->
                    state = state.copy(
                        orders = orders,
                        isLoading = false
                    )
                }
                .onError { error ->
                    state = state.copy(
                        error = error.message ?: "Unknown error",
                        isLoading = false
                    )
                }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            cancelOrderUseCase(orderId)
                .onSuccess {
                    loadOrders() // Refresh list
                }
                .onError { error ->
                    state = state.copy(
                        error = "Failed to cancel order: ${error.message}"
                    )
                }
        }
    }
}

// presentation/sales/orders/OrderListScreen.kt
@Composable
fun OrderListScreen(
    viewModel: OrderListViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                actions = {
                    IconButton(onClick = viewModel::loadOrders) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCheckout) {
                Icon(Icons.Default.Add, "New Order")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    ErrorView(
                        message = state.error,
                        onRetry = viewModel::loadOrders,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.orders.isEmpty() -> {
                    EmptyStateView(
                        message = "No orders found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    OrderList(
                        orders = state.orders,
                        onOrderClick = onNavigateToDetail,
                        onCancelOrder = viewModel::cancelOrder
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderList(
    orders: List<Order>,
    onOrderClick: (String) -> Unit,
    onCancelOrder: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(orders) { order ->
            OrderItem(
                order = order,
                onClick = { onOrderClick(order.id) },
                onCancel = { onCancelOrder(order.id) }
            )
        }
    }
}

@Composable
private fun OrderItem(
    order: Order,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = order.status.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = getStatusColor(order.status)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${order.items.size} items",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "$${order.totalAmount}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (order.canBeCancelled()) {
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel Order")
                }
            }
        }
    }
}
```

### Dependency Injection Example

```kotlin
// di/SharedModule.kt
@Module
@InstallIn(SingletonComponent::class)
object SharedModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.yourapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
}

// di/SalesModule.kt
@Module
@InstallIn(SingletonComponent::class)
object SalesModule {

    @Provides
    @Singleton
    fun provideSalesApi(retrofit: Retrofit): SalesApi {
        return retrofit.create(SalesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderMapper(): OrderMapper {
        return OrderMapper()
    }

    @Provides
    @Singleton
    fun provideOrderRepository(
        api: SalesApi,
        mapper: OrderMapper,
        database: AppDatabase
    ): OrderRepository {
        return OrderRepositoryImpl(
            api = api,
            mapper = mapper,
            orderDao = database.orderDao()
        )
    }

    @Provides
    fun provideGetOrdersUseCase(
        repository: OrderRepository
    ): GetOrdersUseCase {
        return GetOrdersUseCase(repository)
    }

    @Provides
    fun provideCreateOrderUseCase(
        repository: OrderRepository
    ): CreateOrderUseCase {
        return CreateOrderUseCase(repository)
    }
}
```

### Navigation Example

```kotlin
// navigation/Screen.kt
sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("auth/login")
    object Register : Screen("auth/register")

    // Sales
    object OrderList : Screen("sales/orders")
    object OrderDetail : Screen("sales/orders/{orderId}") {
        fun createRoute(orderId: String) = "sales/orders/$orderId"
    }
    object Checkout : Screen("sales/checkout")

    // Inventory
    object ProductList : Screen("inventory/products")
    object ProductDetail : Screen("inventory/products/{productId}") {
        fun createRoute(productId: String) = "inventory/products/$productId"
    }
}

// navigation/AppNavGraph.kt
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Auth context navigation
        authNavGraph(navController)

        // Sales context navigation
        salesNavGraph(navController)

        // Inventory context navigation
        inventoryNavGraph(navController)
    }
}

// presentation/sales/navigation/SalesNavGraph.kt
fun NavGraphBuilder.salesNavGraph(navController: NavController) {
    navigation(
        startDestination = Screen.OrderList.route,
        route = "sales"
    ) {
        composable(Screen.OrderList.route) {
            OrderListScreen(
                onNavigateToDetail = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            OrderDetailScreen(
                orderId = orderId ?: "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onOrderCreated = {
                    navController.navigate(Screen.OrderList.route) {
                        popUpTo("sales") { inclusive = false }
                    }
                }
            )
        }
    }
}
```

---

## 6. Key Trade-offs

### Pros

- ✅ **Clear Context Boundaries**: Aligned with backend DDD, reducing cognitive load
- ✅ **High Testability**: Each context and layer can be tested independently
- ✅ **Maintainability**: Clear separation makes it easy to locate and modify code
- ✅ **Scalability**: Easy to add new contexts or features within contexts
- ✅ **Team Collaboration**: Two developers can work on different contexts with minimal conflicts
- ✅ **Framework Independence**: Core business logic is not tied to Android or Compose
- ✅ **Simplified vs Full DDD**: Removed anti-corruption layers, domain events, and event buses for pragmatism

### Cons

- ❌ **Some Boilerplate**: Still requires interfaces, implementations, and mappers
- ❌ **Learning Curve**: Team needs to understand Clean Architecture and bounded contexts
- ❌ **More Files**: Organized structure means more files than a simple layered approach
- ❌ **Potential Duplication**: Similar concepts across contexts (intentional for independence)

---

## 7. When to Use This Structure

### ✅ Use this structure when:

- Building a medium-scale application with distinct business domains
- Your backend uses DDD with bounded contexts
- Working with a small team (2-3 developers)
- Long-term maintenance is expected (2+ years)
- You want Clean Architecture benefits without excessive complexity
- Business logic varies significantly between domains
- You need to maintain consistency with backend domain models

### ❌ Consider simpler alternatives when:

- Building a simple CRUD app with minimal business logic
- Working solo on a very small project (< 10 screens)
- Tight deadlines for MVP or prototype
- The application has a single cohesive domain
- The team is completely unfamiliar with Clean Architecture
- You need to ship quickly and technical debt is acceptable

---

## 8. Testing Strategy

### Unit Tests

**Domain Layer (Per Context)**

Test pure business logic without any Android dependencies:

```kotlin
class CreateOrderUseCaseTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var useCase: CreateOrderUseCase

    @Before
    fun setup() {
        orderRepository = mockk()
        useCase = CreateOrderUseCase(orderRepository)
    }

    @Test
    fun `should create order with valid items`() = runTest {
        // Arrange
        val items = listOf(
            OrderItem("1", "Product 1", 2, 10.0),
            OrderItem("2", "Product 2", 1, 20.0)
        )
        val expectedOrder = Order(
            id = "123",
            customerId = "customer1",
            items = items,
            status = OrderStatus.PENDING,
            totalAmount = 40.0,
            createdAt = System.currentTimeMillis()
        )
        coEvery { orderRepository.createOrder(any()) } returns Result.Success(expectedOrder)

        // Act
        val result = useCase("customer1", items)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(40.0, (result as Result.Success).data.totalAmount)
    }

    @Test
    fun `should return error when items list is empty`() = runTest {
        // Act
        val result = useCase("customer1", emptyList())

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IllegalArgumentException)
    }
}
```

**Data Layer (Per Context)**

Test repository implementations with mocked data sources:

```kotlin
class OrderRepositoryImplTest {
    private lateinit var api: SalesApi
    private lateinit var mapper: OrderMapper
    private lateinit var repository: OrderRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        mapper = OrderMapper()
        repository = OrderRepositoryImpl(api, mapper)
    }

    @Test
    fun `should fetch orders from API and map to domain`() = runTest {
        // Arrange
        val dtos = listOf(
            OrderDto("1", "customer1", emptyList(), "PENDING", 100.0, 123L)
        )
        coEvery { api.getOrders() } returns dtos

        // Act
        val result = repository.getOrders()

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.size)
    }
}
```

**Presentation Layer (Per Context)**

Test ViewModel logic and state management:

```kotlin
class OrderListViewModelTest {
    private lateinit var getOrdersUseCase: GetOrdersUseCase
    private lateinit var viewModel: OrderListViewModel

    @Before
    fun setup() {
        getOrdersUseCase = mockk()
        viewModel = OrderListViewModel(getOrdersUseCase)
    }

    @Test
    fun `should update state to loading when loading orders`() = runTest {
        // Arrange
        coEvery { getOrdersUseCase() } coAnswers {
            delay(100) // Simulate network delay
            Result.Success(emptyList())
        }

        // Act
        viewModel.loadOrders()

        // Assert
        assertTrue(viewModel.state.isLoading)
    }

    @Test
    fun `should update state with orders on success`() = runTest {
        // Arrange
        val orders = listOf(
            Order("1", "customer1", emptyList(), OrderStatus.PENDING, 100.0, 123L)
        )
        coEvery { getOrdersUseCase() } returns Result.Success(orders)

        // Act
        viewModel.loadOrders()
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.state.isLoading)
        assertEquals(1, viewModel.state.orders.size)
        assertNull(viewModel.state.error)
    }
}
```

### Integration Tests

**Repository with Real Database**

Test repositories with in-memory Room database:

```kotlin
@RunWith(AndroidJUnit4::class)
class OrderRepositoryIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var orderDao: OrderDao
    private lateinit var repository: OrderRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        orderDao = database.orderDao()
        repository = OrderRepositoryImpl(
            api = mockk(),
            mapper = OrderMapper(),
            orderDao = orderDao
        )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `should cache order locally after fetching from API`() = runTest {
        // Test caching logic
    }
}
```

**API Integration with MockWebServer**

Test API services with mock responses:

```kotlin
class SalesApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: SalesApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(SalesApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should parse orders response correctly`() = runTest {
        // Arrange
        val json = """
            [
                {
                    "id": "1",
                    "customerId": "customer1",
                    "items": [],
                    "status": "PENDING",
                    "totalAmount": 100.0,
                    "createdAt": 123
                }
            ]
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(json))

        // Act
        val orders = api.getOrders()

        // Assert
        assertEquals(1, orders.size)
        assertEquals("1", orders[0].id)
    }
}
```

### UI Tests

**Composable Testing**

Test individual screens using Compose Testing API:

```kotlin
@RunWith(AndroidJUnit4::class)
class OrderListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `should display loading indicator when loading`() {
        // Arrange
        val viewModel = mockk<OrderListViewModel>(relaxed = true)
        every { viewModel.state } returns OrderListState(isLoading = true)

        // Act
        composeTestRule.setContent {
            OrderListScreen(
                viewModel = viewModel,
                onNavigateToDetail = {},
                onNavigateToCheckout = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun `should display orders when loaded`() {
        // Arrange
        val orders = listOf(
            Order("1", "customer1", emptyList(), OrderStatus.PENDING, 100.0, 123L)
        )
        val viewModel = mockk<OrderListViewModel>(relaxed = true)
        every { viewModel.state } returns OrderListState(orders = orders)

        // Act
        composeTestRule.setContent {
            OrderListScreen(
                viewModel = viewModel,
                onNavigateToDetail = {},
                onNavigateToCheckout = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Order #1").assertIsDisplayed()
    }

    @Test
    fun `should display error message when error occurs`() {
        // Arrange
        val viewModel = mockk<OrderListViewModel>(relaxed = true)
        every { viewModel.state } returns OrderListState(error = "Network error")

        // Act
        composeTestRule.setContent {
            OrderListScreen(
                viewModel = viewModel,
                onNavigateToDetail = {},
                onNavigateToCheckout = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    }
}
```

### End-to-End Tests

Test complete user flows across contexts:

```kotlin
@RunWith(AndroidJUnit4::class)
class OrderFlowE2ETest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun `user can create and view order`() {
        // Login
        onView(withId(R.id.email_input)).perform(typeText("user@example.com"))
        onView(withId(R.id.password_input)).perform(typeText("password"))
        onView(withId(R.id.login_button)).perform(click())

        // Navigate to products
        onView(withId(R.id.products_tab)).perform(click())

        // Add product to cart
        onView(withText("Product 1")).perform(click())
        onView(withId(R.id.add_to_cart_button)).perform(click())

        // Go to checkout
        onView(withId(R.id.cart_button)).perform(click())
        onView(withId(R.id.checkout_button)).perform(click())

        // Verify order created
        onView(withText("Order #")).check(matches(isDisplayed()))
    }
}
```

---

## 9. Related Concepts

- [[SWE/02 - Concepts & Permanent Notes/0201 - Software Architecture/020102 - Architectural Styles/Clean Architecture]]
- [[Domain-Driven Design]]
- [[Bounded Context Pattern]]
- [[MVVM Pattern]]
- [[What is exactly the Repository Pattern about]]
- [[Use Case Pattern]]
- [[Dependency Injection]]
- [[SOLID Principles]]
- [[Dependency Inversion Principle]]
- [[Mapper Pattern]]
- [[Unidirectional Data Flow]]
- [[Jetpack Compose]]
- [[Kotlin Coroutines]]
- [[State Management in Compose]]
- [[Separation of Concerns]]

---

## 10. Additional Resources

### Official Documentation

- [Guide to app architecture - Android Developers](https://developer.android.com/topic/architecture)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Domain-Driven Design Reference - Eric Evans](https://www.domainlanguage.com/ddd/reference/)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)

### Recommended Libraries

- **Hilt**: Dependency Injection - recommended by Google for Android
- **Retrofit**: Type-safe HTTP client for remote data sources
- **Room**: Local database persistence with SQLite
- **Kotlin Coroutines**: Asynchronous programming
- **Navigation Compose**: Type-safe navigation for Compose
- **Coil**: Image loading for Compose
- **MockK**: Mocking library for Kotlin
- **Turbine**: Testing library for Flows

### Books & Articles

- "Domain-Driven Design" by Eric Evans
- "Implementing Domain-Driven Design" by Vaughn Vernon
- "Clean Architecture" by Robert C. Martin
- [Modularization in Android](https://developer.android.com/topic/modularization)
- [Now in Android - Architecture Learning Journey](https://github.com/android/nowinandroid/blob/main/docs/ArchitectureLearningJourney.md)

### Community Examples

- [Now in Android](https://github.com/android/nowinandroid) - Google's official sample app
- [Android Architecture Samples](https://github.com/android/architecture-samples) - Various architecture approaches

---

## 11. Migration Notes

### From Monolithic Architecture

If you're starting with a single-package monolithic structure:

**Phase 1: Prepare (Week 1)**

1. Identify your bounded contexts by analyzing features
2. Map existing classes to domain/data/presentation layers
3. Document dependencies between features
4. Create the `/shared` folder with shared utilities

**Phase 2: Extract Shared Layer (Week 2)**

1. Create `/shared/domain/Result.kt`
2. Move shared extensions to `/shared/util/`
3. Extract reusable UI components to `/shared/ui/components/`
4. Set up theming in `/shared/ui/theme/`

**Phase 3: Organize First Context (Weeks 3-4)**

1. Choose the most isolated context (often Auth)
2. Create folder structure: `/domain/auth/`, `/data/auth/`, `/presentation/auth/`
3. Move domain models to `/domain/auth/model/`
4. Extract repository interfaces to `/domain/auth/repository/`
5. Create use cases in `/domain/auth/usecase/`
6. Move API and DTOs to `/data/auth/remote/`
7. Implement repositories in `/data/auth/repository/`
8. Organize screens in `/presentation/auth/`
9. Create DI module in `/di/AuthModule.kt`
10. Test thoroughly before proceeding

**Phase 4: Migrate Remaining Contexts (Weeks 5-8)**

1. Repeat Phase 3 for each remaining context
2. One context at a time to avoid breaking the build
3. Update navigation as you migrate each context
4. Refactor cross-context dependencies to use API calls

**Phase 5: Refine and Optimize (Week 9+)**

1. Review and minimize the `/shared` shared kernel
2. Ensure no direct context-to-context dependencies
3. Complete test coverage for all layers
4. Document architecture decisions
5. Create developer onboarding guide

### From Traditional Clean Architecture

If you're migrating from a layer-first structure (all domain, all data, all presentation):

**Week 1: Planning**

1. Identify bounded contexts in collaboration with backend team
2. Map existing domain models to contexts
3. Document which features belong to which context

**Week 2-3: Restructure Domain Layer**

1. Create `/domain/{context}/` folders
2. Move models to appropriate context folders
3. Group repository interfaces by context
4. Organize use cases by context

**Week 4-5: Restructure Data Layer**

1. Create `/data/{context}/` folders
2. Move repository implementations to context folders
3. Organize APIs and DTOs by context
4. Update mappers to be context-specific

**Week 6-7: Restructure Presentation Layer**

1. Create `/presentation/{context}/` folders
2. Organize screens by context and feature
3. Group ViewModels with their screens
4. Create navigation graphs per context

**Week 8: Update DI and Navigation**

1. Split DI modules by context
2. Update global navigation to coordinate contexts
3. Test cross-context navigation flows

### Key Migration Principles

- **Never break the build**: Keep the app functional during migration
- **One context at a time**: Don't try to migrate everything simultaneously
- **Test continuously**: Add tests before and after each migration step
- **Incremental commits**: Commit working code frequently
- **Team alignment**: Ensure all team members understand the new structure

### Common Migration Pitfalls

1. **Sharing too much**: Resist adding too much to `/shared`, prefer duplication
2. **Context coupling**: Ensure contexts don't import from each other
3. **Navigation complexity**: Plan global navigation carefully
4. **DI confusion**: Keep context dependencies clearly separated
5. **Over-engineering**: Don't add patterns you don't need (events, ACLs, etc.)

### Checklist Before Migration

- [ ] Backend team confirms bounded context boundaries
- [ ] Team understands Clean Architecture principles
- [ ] Development environment is set up with required libraries
- [ ] Test suite exists for critical functionality
- [ ] Git branching strategy is defined
- [ ] Rollback plan is documented
- [ ] Team capacity is available (not during major releases)

### Post-Migration Validation

- [ ] All tests passing
- [ ] No direct imports between contexts
- [ ] DI modules properly separated
- [ ] Navigation working correctly
- [ ] Build times acceptable
- [ ] Team understands new structure
- [ ] Documentation updated
- [ ] Code review process adapted

---

## 12. Best Practices & Guidelines

### Naming Conventions

**Packages**: Use lowercase with underscores only when necessary

```
domain/sales/model/
data/inventory/remote/dto/
presentation/auth/login/
```

**Classes**: Follow standard Kotlin conventions

- Domain models: `Order`, `Product`, `User`
- DTOs: `OrderDto`, `ProductDto`
- Entities: `OrderEntity`, `ProductEntity`
- Use Cases: `GetOrdersUseCase`, `CreateOrderUseCase`
- ViewModels: `OrderListViewModel`, `ProductDetailViewModel`
- Screens: `OrderListScreen`, `LoginScreen`

**Files**: Match class names

```
Order.kt
OrderDto.kt
OrderMapper.kt
OrderRepository.kt
OrderRepositoryImpl.kt
```

### Code Organization Rules

1. **Context Independence**: Never import from another context

   ```kotlin
   // ❌ BAD: Sales context importing from Inventory
   import com.yourapp.domain.inventory.model.Product

   // ✅ GOOD: Call API to get product data
   suspend fun getProductInfo(productId: String): ProductInfo
   ```

2. **Layer Dependencies**: Follow the dependency rule strictly

   ```kotlin
   // ✅ GOOD: Presentation → Domain
   class OrderViewModel(private val getOrders: GetOrdersUseCase)

   // ✅ GOOD: Data → Domain
   class OrderRepositoryImpl : OrderRepository

   // ❌ BAD: Domain → Data
   class CreateOrderUseCase(private val api: SalesApi) // Never!
   ```

3. **Mapper Responsibility**: Keep mappers simple and focused

   ```kotlin
   // ✅ GOOD: Simple transformation
   fun toDomain(dto: OrderDto): Order = Order(...)

   // ❌ BAD: Business logic in mapper
   fun toDomain(dto: OrderDto): Order {
       if (dto.total < 0) throw InvalidOrderException() // No!
       return Order(...)
   }
   ```

4. **Use Case Single Responsibility**: One use case = one business operation

   ```kotlin
   // ✅ GOOD: Focused use case
   class CreateOrderUseCase(private val repo: OrderRepository) {
       suspend operator fun invoke(items: List<OrderItem>): Result<Order>
   }

   // ❌ BAD: Too many responsibilities
   class OrderManagementUseCase {
       fun create()
       fun update()
       fun delete()
       fun list()
   }
   ```

### Performance Considerations

**Caching Strategy**:

```kotlin
class OrderRepositoryImpl(
    private val api: SalesApi,
    private val dao: OrderDao,
    private val cacheTimeout: Long = 5 * 60 * 1000 // 5 minutes
) : OrderRepository {

    override suspend fun getOrders(): Result<List<Order>> = runCatching {
        // Check cache first
        val cached = dao.getOrdersWithTimestamp()
        if (cached != null && !cached.isExpired(cacheTimeout)) {
            return Result.Success(cached.orders)
        }

        // Fetch from API
        val orders = api.getOrders().map { mapper.toDomain(it) }

        // Update cache
        dao.insertOrders(orders, System.currentTimeMillis())

        orders
    }
}
```

**Pagination**:

```kotlin
@Paging
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getOrdersPaged(): PagingSource<Int, OrderEntity>
}

class GetOrdersUseCase(
    private val repository: OrderRepository
) {
    operator fun invoke(): Flow<PagingData<Order>> {
        return repository.getOrdersPaged()
    }
}
```

### Error Handling

**Consistent Error Handling**:

```kotlin
// Domain layer - define domain exceptions
sealed class OrderException(message: String) : Exception(message) {
    class InvalidItems : OrderException("Order must have at least one item")
    class InsufficientStock : OrderException("Insufficient stock for order")
}

// Use case - throw domain exceptions
class CreateOrderUseCase {
    suspend operator fun invoke(items: List<OrderItem>): Result<Order> {
        if (items.isEmpty()) {
            return Result.Error(OrderException.InvalidItems())
        }
        // ...
    }
}

// ViewModel - handle errors appropriately
class OrderViewModel {
    fun createOrder(items: List<OrderItem>) {
        viewModelScope.launch {
            createOrderUseCase(items)
                .onSuccess { order ->
                    state = state.copy(success = true)
                }
                .onError { error ->
                    val message = when (error) {
                        is OrderException.InvalidItems -> "Please add items to your order"
                        is OrderException.InsufficientStock -> "Some items are out of stock"
                        else -> "An error occurred. Please try again"
                    }
                    state = state.copy(error = message)
                }
        }
    }
}
```

### Security Considerations

**Token Management**:

```kotlin
// Store tokens securely
class SessionPreferences(private val encryptedPrefs: SharedPreferences) {
    fun saveToken(token: String) {
        encryptedPrefs.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }
}

// Add interceptor for authentication
class AuthInterceptor(private val sessionPrefs: SessionPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionPrefs.getToken()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

Remember: **This structure is a starting point, not a rigid framework**. Adapt it to your specific needs and context. The goal is to have a maintainable, testable codebase that grows with your application.
