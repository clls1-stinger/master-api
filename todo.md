# TODO Frontend React - Master API

## 🤖 System Prompt de Inicio

**CONTEXTO**: Eres Claude 4 Sonnet, un asistente de IA especializado en desarrollo de software. Vas a desarrollar paso a paso un Sistema de Gestión Personal Integral usando React + TypeScript + Redux Toolkit + Material-UI.

**METODOLOGÍA**: Desarrollo secuencial y verificado. Cada tarea debe completarse al 100% antes de continuar a la siguiente. Sigue estrictamente el orden establecido en las fases.

**CRITERIOS DE CALIDAD**:
- ✅ Funcionalidad completa y probada
- ✅ Código limpio y bien documentado
- ✅ Testing exhaustivo (>80% cobertura)
- ✅ UI/UX responsive y accesible
- ✅ Performance optimizada
- ✅ Seguridad implementada

**PROCESO POR TAREA**:
1. **Implementar** - Desarrollar la funcionalidad completa
2. **Probar** - Ejecutar tests unitarios e integración
3. **Verificar** - Validar criterios de aceptación
4. **Documentar** - Actualizar documentación técnica
5. **Marcar** - Confirmar completitud antes de continuar

**IMPORTANTE**: 
- ❌ NO avances a la siguiente tarea sin completar la actual
- 📋 Usa las convenciones de código establecidas
- 🔧 Consulta la sección de troubleshooting si encuentras problemas
- 📊 Actualiza las métricas de progreso tras cada tarea
- 🔄 Aplica criterios de rollback si es necesario

---

## Descripción del Proyecto
Desarrollo de un frontend React completo para la Master API, un sistema de gestión personal que incluye tareas, notas, hábitos y categorías con funcionalidades avanzadas de seguimiento y organización.

## Metodología de Desarrollo Automatizado
Cada tarea debe ser implementada y verificada por un LLM siguiendo estos pasos:
1. **Implementación**: Crear/modificar archivos según especificaciones
2. **Testing**: Escribir y ejecutar tests unitarios e integración
3. **Verificación**: Confirmar funcionalidad mediante pruebas manuales
4. **Documentación**: Actualizar documentación de componentes
5. **Marcado**: Marcar tarea como completada ✅

## Stack Tecnológico
- **Frontend**: React 18+ con TypeScript
- **Estado**: Redux Toolkit + RTK Query
- **UI**: Material-UI (MUI) v5
- **Routing**: React Router v6
- **HTTP**: Axios con interceptores
- **Formularios**: React Hook Form + Yup
- **Testing**: Jest + React Testing Library
- **Build**: Vite
- **Linting**: ESLint + Prettier

## Configuración de Entorno
### Variables de Entorno Requeridas
```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENV=development
REACT_APP_VERSION=1.0.0
REACT_APP_DEBUG=true
```

### Comandos de Desarrollo
```bash
npm run dev          # Servidor de desarrollo
npm run build        # Build de producción
npm run test         # Ejecutar tests
npm run test:coverage # Tests con cobertura
npm run lint         # Linting
npm run type-check   # Verificación de tipos
npm run analyze      # Análisis del bundle
npm run doctor       # Verificar configuración
npm run clean        # Limpiar cache y node_modules
```

## Convenciones de Código
### Naming Conventions
- **Componentes**: PascalCase (UserProfile.tsx)
- **Hooks**: camelCase con prefijo 'use' (useAuth.ts)
- **Tipos**: PascalCase con sufijo según tipo (UserType, AuthState)
- **Archivos**: kebab-case para utilidades (api-client.ts)
- **Constantes**: UPPER_SNAKE_CASE (API_ENDPOINTS)
- **Variables**: camelCase (userName, isLoading)

### Estructura de Archivos
- Un componente por archivo
- Index files para exports
- Tests junto al archivo correspondiente (.test.tsx)
- Tipos en carpeta types/ o junto al componente
- Estilos en archivos separados cuando sea necesario

### Comentarios y Documentación
- JSDoc para funciones públicas
- Comentarios inline para lógica compleja
- README por feature cuando sea necesario

## Estructura del Proyecto
```
src/
├── components/           # Componentes reutilizables
│   ├── common/          # Componentes comunes
│   ├── forms/           # Componentes de formularios
│   └── ui/              # Componentes UI básicos
├── pages/               # Páginas principales
├── features/            # Features por dominio
│   ├── auth/           # Autenticación
│   ├── tasks/          # Gestión de tareas
│   ├── notes/          # Gestión de notas
│   ├── habits/         # Gestión de hábitos
│   ├── categories/     # Gestión de categorías
│   ├── files/          # Gestión de archivos
│   └── dashboard/      # Dashboard principal
├── store/              # Redux store
├── services/           # Servicios API
├── hooks/              # Custom hooks
├── utils/              # Utilidades
├── types/              # Tipos TypeScript
└── constants/          # Constantes
```

---

# FASES DE DESARROLLO

## FASE 1: Configuración Inicial y Base del Proyecto

### Tarea 1.1: Configuración del Proyecto Base
- [ ] **Setup**: Inicializar proyecto con Vite + React + TypeScript
- [ ] **Dependencias**: Instalar todas las dependencias del stack
- [ ] **Configuración**: ESLint, Prettier, tsconfig.json
- [ ] **Variables de Entorno**: Configurar .env y .env.example
- [ ] **Scripts NPM**: Configurar todos los scripts de desarrollo
- [ ] **Estructura**: Crear estructura de carpetas completa
- [ ] **Testing**: Configurar Jest y React Testing Library
- [ ] **Verificación**: Proyecto ejecuta sin errores y todos los comandos funcionan

### Tarea 1.2: Configuración de Redux Store
- [ ] **Store**: Configurar Redux Toolkit store
- [ ] **RTK Query**: Configurar baseQuery con Axios
- [ ] **Interceptores**: Configurar interceptores para auth y errores
- [ ] **Types**: Definir tipos base para el estado global
- [ ] **Testing**: Tests para configuración del store
- [ ] **Verificación**: Store funciona correctamente

### Tarea 1.3: Tipos TypeScript y Modelos de Datos
- [ ] **User Types**: 
  ```typescript
  interface User {
    id: number;
    username: string;
    email: string;
    createdAt: string;
    lastLogin: string;
  }
  
  interface AuthResponse {
    token: string;
    user: User;
  }
  
  interface LoginRequest {
    username: string;
    password: string;
  }
  
  interface RegisterRequest {
    username: string;
    email: string;
    password: string;
  }
  ```
- [ ] **Task Types**:
  ```typescript
  interface Task {
    id: number;
    title: string;
    description?: string;
    creation: string;
    userId: number;
    categories?: Category[];
    notes?: Note[];
    habits?: Habit[];
  }
  
  interface TaskHistory {
    historyId: number;
    versionId: number;
    taskId: number;
    title: string;
    description?: string;
    creation: string;
    timestamp: string;
  }
  ```
- [ ] **Note Types**:
  ```typescript
  interface Note {
    id: number;
    title: string;
    note: string;
    creation: string;
    userId: number;
    categories?: Category[];
    tasks?: Task[];
    habits?: Habit[];
  }
  
  interface NoteHistory {
    historyId: number;
    versionId: number;
    noteId: number;
    title: string;
    noteContent: string;
    creation: string;
    timestamp: string;
  }
  ```
- [ ] **Habit Types**:
  ```typescript
  interface Habit {
    id: number;
    name: string;
    creation: string;
    trackingType: 'BOOLEAN' | 'QUANTITY';
    dailyGoal?: number;
    unit?: string;
    description?: string;
    active: boolean;
    color?: string;
    lastTrackedDate?: string;
    todayCompleted: boolean;
    todayQuantity?: number;
    currentStreak: number;
    bestStreak: number;
    userId: number;
    categories?: Category[];
    tasks?: Task[];
    notes?: Note[];
  }
  
  interface HabitStats {
    habitId: number;
    totalDays: number;
    completedDays: number;
    completionRate: number;
    currentStreak: number;
    bestStreak: number;
    averageQuantity?: number;
  }
  
  interface HabitTrackRequest {
    quantity?: number; // Para QUANTITY type
  }
  ```
- [ ] **Category Types**:
  ```typescript
  interface Category {
    id: number;
    name: string;
    description?: string;
    creation: string;
    userId: number;
    tasks?: Task[];
    notes?: Note[];
    habits?: Habit[];
  }
  
  interface CategoryHistory {
    historyId: number;
    versionId: number;
    categoryId: number;
    name: string;
    description?: string;
    creation: string;
    timestamp: string;
  }
  ```
- [ ] **File Types**:
  ```typescript
  interface FileAttachment {
    id: number;
    originalFileName: string;
    fileType: string;
    fileSize: number;
    uploadDate: string;
    entityType: 'TASK' | 'NOTE' | 'HABIT' | 'CATEGORY';
    entityId: number;
    userId: number;
  }
  
  interface FileUploadRequest {
    file: File;
    entityType: string;
    entityId: number;
  }
  ```
- [ ] **Dashboard Types**:
  ```typescript
  interface DashboardStats {
    totalTasks: number;
    completedTasks: number;
    totalNotes: number;
    totalHabits: number;
    activeHabits: number;
    todayCompletedHabits: number;
    currentStreaks: number;
    totalCategories: number;
  }
  
  interface HabitDashboard {
    date: string;
    habits: Array<{
      id: number;
      name: string;
      trackingType: 'BOOLEAN' | 'QUANTITY';
      completed: boolean;
      quantity?: number;
      dailyGoal?: number;
      unit?: string;
      color?: string;
    }>;
  }
  ```
- [ ] **API Response Types**:
  ```typescript
  interface ApiResponse<T> {
    data: T;
    message?: string;
    success: boolean;
  }
  
  interface ApiError {
    message: string;
    status: number;
    timestamp: string;
  }
  
  interface PaginatedResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
  }
  ```
- [ ] **Testing**: Tests para todos los tipos TypeScript
- [ ] **Verificación**: Tipos compilan sin errores y cubren toda la API

### Tarea 1.4: Configuración de Servicios API Base
- [ ] **API Client**: Configurar cliente Axios base
  ```typescript
  const apiClient = axios.create({
    baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
  });
  ```
- [ ] **Interceptores**: Configurar interceptores de request/response
  - Request: Agregar token de autorización
  - Response: Manejo de errores globales
  - Refresh token automático
- [ ] **Error Handling**: Sistema de manejo de errores
- [ ] **Testing**: Tests para configuración de API
- [ ] **Verificación**: Cliente API funciona correctamente

---

## FASE 2: Sistema de Autenticación

### Tarea 2.1: Servicios de Autenticación
- [ ] **Auth API Service**: Implementar servicios de autenticación
  - `POST /auth/login` - Inicio de sesión
  - `POST /auth/register` - Registro de usuario
- [ ] **Token Management**: Gestión de tokens JWT
  - Almacenamiento seguro en localStorage
  - Validación de expiración
  - Refresh automático
- [ ] **Auth Guards**: Guards de autenticación para rutas
- [ ] **Testing**: Tests para servicios de auth
- [ ] **Verificación**: Login y registro funcionan correctamente

### Tarea 2.2: Redux Auth Slice
- [ ] **Auth Slice**: Slice de Redux para autenticación
  ```typescript
  interface AuthState {
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    error: string | null;
  }
  ```
- [ ] **Auth Actions**: Acciones de autenticación
  - login, logout, register
  - setUser, setToken, clearAuth
- [ ] **Auth Selectors**: Selectores para estado de auth
- [ ] **Testing**: Tests para auth slice
- [ ] **Verificación**: Estado de auth se maneja correctamente

### Tarea 2.3: Componentes de Autenticación
- [ ] **LoginForm**: Formulario de inicio de sesión
  - Validación con React Hook Form + Yup
  - Manejo de errores
  - Loading states
- [ ] **RegisterForm**: Formulario de registro
  - Validación completa
  - Confirmación de contraseña
  - Términos y condiciones
- [ ] **AuthLayout**: Layout para páginas de auth
- [ ] **ProtectedRoute**: Componente para rutas protegidas
- [ ] **Testing**: Tests para componentes de auth
- [ ] **Verificación**: Formularios funcionan y validan correctamente

---

## FASE 3: Layout y Navegación

### Tarea 3.1: Layout Principal
- [ ] **AppLayout**: Layout principal de la aplicación
  - Header con navegación
  - Sidebar colapsible
  - Main content area
  - Footer
- [ ] **Navigation**: Sistema de navegación
  - Menu principal
  - Breadcrumbs
  - User menu
- [ ] **Responsive Design**: Adaptación a diferentes pantallas
- [ ] **Testing**: Tests para layout
- [ ] **Verificación**: Layout responsive y funcional

### Tarea 3.2: Routing
- [ ] **Router Setup**: Configuración de React Router
- [ ] **Route Definitions**: Definición de todas las rutas
  ```typescript
  const routes = [
    { path: '/', element: <Dashboard /> },
    { path: '/tasks', element: <TasksPage /> },
    { path: '/notes', element: <NotesPage /> },
    { path: '/habits', element: <HabitsPage /> },
    { path: '/categories', element: <CategoriesPage /> },
    // ... más rutas
  ];
  ```
- [ ] **Lazy Loading**: Carga perezosa de componentes
- [ ] **Route Guards**: Protección de rutas
- [ ] **Testing**: Tests para routing
- [ ] **Verificación**: Navegación funciona correctamente

---

## FASE 4: Dashboard Principal

### Tarea 4.1: Servicios del Dashboard
- [ ] **Dashboard API**: Implementar servicios del dashboard
  - `GET /dashboard-stats` - Estadísticas generales
  - `GET /habits/dashboard/today` - Dashboard de hábitos del día
- [ ] **RTK Query**: Configurar queries para dashboard
- [ ] **Caching**: Configurar cache para datos del dashboard
- [ ] **Testing**: Tests para servicios del dashboard
- [ ] **Verificación**: APIs del dashboard funcionan

### Tarea 4.2: Widgets del Dashboard
- [ ] **StatsWidget**: Widget de estadísticas generales
  - Total de tareas, notas, hábitos
  - Progreso del día
  - Gráficos simples
- [ ] **HabitsWidget**: Widget de hábitos del día
  - Lista de hábitos de hoy
  - Botones de completar/cantidad
  - Progreso visual
- [ ] **TasksWidget**: Widget de tareas pendientes
  - Tareas del día
  - Tareas vencidas
  - Quick actions
- [ ] **NotesWidget**: Widget de notas recientes
  - Últimas notas creadas
  - Acceso rápido
- [ ] **Testing**: Tests para widgets
- [ ] **Verificación**: Widgets muestran datos correctamente

### Tarea 4.3: Calendario de Hábitos
- [ ] **HabitCalendar**: Calendario mensual de hábitos
  - Vista mensual
  - Indicadores de completado
  - Navegación entre meses
  - Tooltips con detalles
- [ ] **Calendar Navigation**: Navegación del calendario
- [ ] **Habit Tracking**: Marcado de hábitos desde calendario
- [ ] **Testing**: Tests para calendario
- [ ] **Verificación**: Calendario funciona correctamente

---

## FASE 5: Gestión de Categorías

### Tarea 5.1: Servicios de Categorías
- [ ] **Categories API**: Implementar todos los endpoints
  - `GET /categories` - Lista de categorías del usuario
  - `GET /categories/{id}` - Categoría por ID
  - `POST /categories` - Crear nueva categoría
  - `PUT /categories/{id}` - Actualizar categoría completa
  - `PATCH /categories/{id}` - Actualización parcial
  - `DELETE /categories/{id}` - Eliminar categoría
  - `GET /categories/search/by-name` - Buscar por nombre
- [ ] **Relationship APIs**: Endpoints de relaciones
  - `GET /categories/{id}/tasks` - Tareas de una categoría
  - `GET /categories/{id}/notes` - Notas de una categoría
  - `GET /categories/{id}/habits` - Hábitos de una categoría
  - `POST /categories/{categoryId}/tasks/{taskId}` - Agregar tarea
  - `POST /categories/{categoryId}/notes/{noteId}` - Agregar nota
  - `POST /categories/{categoryId}/habits/{habitId}` - Agregar hábito
  - `DELETE /categories/{categoryId}/tasks/{taskId}` - Quitar tarea
  - `DELETE /categories/{categoryId}/notes/{noteId}` - Quitar nota
  - `DELETE /categories/{categoryId}/habits/{habitId}` - Quitar hábito
- [ ] **History APIs**: Endpoints de historial
  - `GET /categories/{id}/history` - Historial de versiones
  - `GET /categories/{id}/history/{versionId}` - Versión específica
- [ ] **Testing**: Tests para todos los servicios
- [ ] **Verificación**: Todos los endpoints funcionan correctamente

### Tarea 5.2: Redux Categories Slice
- [ ] **Categories Slice**: Slice de Redux para categorías
  ```typescript
  interface CategoriesState {
    categories: Category[];
    selectedCategory: Category | null;
    isLoading: boolean;
    error: string | null;
    searchResults: Category[];
  }
  ```
- [ ] **Categories Actions**: Acciones CRUD completas
- [ ] **Categories Selectors**: Selectores optimizados
- [ ] **Testing**: Tests para categories slice
- [ ] **Verificación**: Estado de categorías se maneja correctamente

### Tarea 5.3: Componentes de Categorías
- [ ] **CategoriesList**: Lista de categorías con filtros
- [ ] **CategoryForm**: Formulario crear/editar categoría
- [ ] **CategoryCard**: Tarjeta de categoría con acciones
- [ ] **CategorySearch**: Búsqueda de categorías
- [ ] **CategoryRelations**: Gestión de relaciones
- [ ] **Testing**: Tests para componentes
- [ ] **Verificación**: CRUD de categorías funciona completamente

---

## FASE 6: Gestión de Tareas

### Tarea 6.1: Servicios de Tareas
- [ ] **Tasks API**: Implementar todos los endpoints
  - `GET /tasks` - Lista de tareas del usuario
  - `GET /tasks/{id}` - Tarea por ID
  - `POST /tasks` - Crear nueva tarea
  - `PUT /tasks/{id}` - Actualizar tarea completa
  - `PATCH /tasks/{id}` - Actualización parcial
  - `DELETE /tasks/{id}` - Eliminar tarea
- [ ] **Task Relationships**: Endpoints de relaciones
  - `GET /tasks/{id}/categories` - Categorías de una tarea
  - `GET /tasks/{id}/notes` - Notas relacionadas
  - `GET /tasks/{id}/habits` - Hábitos relacionados
  - `POST /tasks/{taskId}/categories/{categoryId}` - Agregar categoría
  - `DELETE /tasks/{taskId}/categories/{categoryId}` - Quitar categoría
- [ ] **Task History**: Endpoints de historial
  - `GET /tasks/{id}/history` - Historial de versiones
  - `GET /tasks/{id}/history/{versionId}` - Versión específica
- [ ] **Testing**: Tests para servicios de tareas
- [ ] **Verificación**: Todos los endpoints de tareas funcionan

### Tarea 6.2: Redux Tasks Slice
- [ ] **Tasks Slice**: Slice de Redux para tareas
  ```typescript
  interface TasksState {
    tasks: Task[];
    selectedTask: Task | null;
    isLoading: boolean;
    error: string | null;
    filters: {
      category?: number;
      completed?: boolean;
      search?: string;
    };
  }
  ```
- [ ] **Tasks Actions**: Acciones CRUD y filtros
- [ ] **Tasks Selectors**: Selectores con filtros
- [ ] **Testing**: Tests para tasks slice
- [ ] **Verificación**: Estado de tareas se maneja correctamente

### Tarea 6.3: Componentes de Tareas
- [ ] **TasksList**: Lista de tareas con filtros y ordenamiento
- [ ] **TaskForm**: Formulario crear/editar tarea
- [ ] **TaskCard**: Tarjeta de tarea con acciones rápidas
- [ ] **TaskFilters**: Filtros avanzados de tareas
- [ ] **TaskDetails**: Vista detallada de tarea
- [ ] **TaskHistory**: Historial de cambios
- [ ] **Testing**: Tests para componentes de tareas
- [ ] **Verificación**: CRUD completo de tareas funciona

---

## FASE 7: Gestión de Notas

### Tarea 7.1: Servicios de Notas
- [ ] **Notes API**: Implementar todos los endpoints
  - `GET /notes` - Lista de notas del usuario
  - `GET /notes/{id}` - Nota por ID
  - `POST /notes` - Crear nueva nota
  - `PUT /notes/{id}` - Actualizar nota completa
  - `PATCH /notes/{id}` - Actualización parcial
  - `DELETE /notes/{id}` - Eliminar nota
- [ ] **Note Relationships**: Endpoints de relaciones
  - `GET /notes/{id}/categories` - Categorías de una nota
  - `GET /notes/{id}/tasks` - Tareas relacionadas
  - `GET /notes/{id}/habits` - Hábitos relacionados
  - `POST /notes/{noteId}/tasks/{taskId}` - Relacionar tarea
  - `POST /notes/{noteId}/habits/{habitId}` - Relacionar hábito
  - `POST /notes/{noteId}/categories/{categoryId}` - Agregar categoría
  - `DELETE /notes/{noteId}/tasks/{taskId}` - Quitar relación tarea
  - `DELETE /notes/{noteId}/habits/{habitId}` - Quitar relación hábito
  - `DELETE /notes/{noteId}/categories/{categoryId}` - Quitar categoría
- [ ] **Note History**: Endpoints de historial
  - `GET /notes/{id}/history` - Historial de versiones
  - `GET /notes/{id}/history/{versionId}` - Versión específica
- [ ] **Testing**: Tests para servicios de notas
- [ ] **Verificación**: Todos los endpoints de notas funcionan

### Tarea 7.2: Redux Notes Slice
- [ ] **Notes Slice**: Slice de Redux para notas
  ```typescript
  interface NotesState {
    notes: Note[];
    selectedNote: Note | null;
    isLoading: boolean;
    error: string | null;
    filters: {
      category?: number;
      search?: string;
      dateRange?: { start: string; end: string };
    };
  }
  ```
- [ ] **Notes Actions**: Acciones CRUD completas
- [ ] **Notes Selectors**: Selectores con filtros
- [ ] **Testing**: Tests para notes slice
- [ ] **Verificación**: Estado de notas se maneja correctamente

### Tarea 7.3: Componentes de Notas
- [ ] **NotesList**: Lista de notas con vista grid/list
- [ ] **NoteForm**: Formulario crear/editar nota
- [ ] **NoteCard**: Tarjeta de nota con preview
- [ ] **NoteEditor**: Editor de texto enriquecido
- [ ] **NoteFilters**: Filtros de notas
- [ ] **NoteDetails**: Vista detallada de nota
- [ ] **NoteHistory**: Historial de cambios
- [ ] **Testing**: Tests para componentes de notas
- [ ] **Verificación**: CRUD completo de notas funciona

---

## FASE 8: Gestión de Hábitos

### Tarea 8.1: Servicios de Hábitos
- [ ] **Habits API**: Implementar todos los endpoints
  - `GET /habits` - Lista de hábitos del usuario
  - `GET /habits/active` - Hábitos activos
  - `GET /habits/inactive` - Hábitos inactivos
  - `GET /habits/{id}` - Hábito por ID
  - `POST /habits` - Crear nuevo hábito
  - `PUT /habits/{id}` - Actualizar hábito completo
  - `PATCH /habits/{id}` - Actualización parcial
  - `DELETE /habits/{id}` - Eliminar hábito
  - `PUT /habits/{id}/toggle-active` - Activar/desactivar
- [ ] **Habit Tracking**: Endpoints de seguimiento
  - `POST /habits/{id}/track/complete` - Marcar completado (BOOLEAN)
  - `POST /habits/{id}/track/quantity` - Registrar cantidad (QUANTITY)
  - `GET /habits/{id}/stats` - Estadísticas del hábito
- [ ] **Habit Relationships**: Endpoints de relaciones
  - `GET /habits/{id}/categories` - Categorías del hábito
  - `GET /habits/{id}/tasks` - Tareas relacionadas
  - `GET /habits/{id}/notes` - Notas relacionadas
  - `POST /habits/{habitId}/categories/{categoryId}` - Agregar categoría
  - `POST /habits/{habitId}/tasks/{taskId}` - Relacionar tarea
  - `POST /habits/{habitId}/notes/{noteId}` - Relacionar nota
  - `DELETE /habits/{habitId}/categories/{categoryId}` - Quitar categoría
  - `DELETE /habits/{habitId}/tasks/{taskId}` - Quitar relación tarea
  - `DELETE /habits/{habitId}/notes/{noteId}` - Quitar relación nota
- [ ] **Habit History**: Endpoints de historial
  - `GET /habits/{id}/history` - Historial de versiones
  - `GET /habits/{id}/history/{versionId}` - Versión específica
- [ ] **Testing**: Tests para servicios de hábitos
- [ ] **Verificación**: Todos los endpoints de hábitos funcionan

### Tarea 8.2: Redux Habits Slice
- [ ] **Habits Slice**: Slice de Redux para hábitos
  ```typescript
  interface HabitsState {
    habits: Habit[];
    activeHabits: Habit[];
    selectedHabit: Habit | null;
    todayHabits: HabitDashboard | null;
    isLoading: boolean;
    error: string | null;
    filters: {
      active?: boolean;
      trackingType?: 'BOOLEAN' | 'QUANTITY';
      category?: number;
    };
  }
  ```
- [ ] **Habits Actions**: Acciones CRUD y tracking
- [ ] **Habits Selectors**: Selectores optimizados
- [ ] **Testing**: Tests para habits slice
- [ ] **Verificación**: Estado de hábitos se maneja correctamente

### Tarea 8.3: Componentes de Hábitos
- [ ] **HabitsList**: Lista de hábitos con filtros
- [ ] **HabitForm**: Formulario crear/editar hábito
- [ ] **HabitCard**: Tarjeta de hábito con tracking
- [ ] **HabitTracker**: Componente de seguimiento
- [ ] **HabitStats**: Estadísticas y gráficos
- [ ] **HabitFilters**: Filtros de hábitos
- [ ] **HabitHistory**: Historial de cambios
- [ ] **Testing**: Tests para componentes de hábitos
- [ ] **Verificación**: CRUD y tracking de hábitos funciona

---

## FASE 9: Gestión de Archivos

### Tarea 9.1: Servicios de Archivos
- [ ] **Files API**: Implementar endpoints de archivos
  - `POST /api/files/upload/{entityType}/{entityId}` - Subir archivo a entidad
  - `GET /api/files/download/{fileId}` - Descargar archivo
  - `GET /api/files/entity/{entityType}/{entityId}` - Archivos de entidad
  - `DELETE /api/files/{fileId}` - Eliminar archivo
- [ ] **File Upload**: Sistema de subida de archivos
  - Validación de tipos
  - Límites de tamaño
  - Progress tracking
- [ ] **File Management**: Gestión de archivos
- [ ] **Testing**: Tests para servicios de archivos
- [ ] **Verificación**: Subida y descarga de archivos funciona
    categories?: Category[];
    tasks?: Task[];
    habits?: Habit[];
  }
  ```
- [ ] **Habit Types**:
  ```typescript
  interface Habit {
    id: number;
    name: string;
    description?: string;
    type: 'BOOLEAN' | 'QUANTITY';
    targetQuantity?: number;
    unit?: string;
    frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY';
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
    userId: number;
    categories?: Category[];
    tasks?: Task[];
    notes?: Note[];
  }
  ```
- [ ] **Category Types**:
  ```typescript
  interface Category {
    id: number;
    name: string;
    description?: string;
    color: string;
    createdAt: string;
    updatedAt: string;
    userId: number;
  }
  ```
- [ ] **File Types**:
  ```typescript
  interface FileAttachment {
    id: number;
    filename: string;
    originalName: string;
    mimeType: string;
    size: number;
    entityType: string;
    entityId: number;
    uploadedAt: string;
    userId: number;
  }
  ```
- [ ] **Dashboard Types**:
  ```typescript
  interface DashboardStats {
    taskCount: number;
    noteCount: number;
    habitCount: number;
    categoryCount: number;
  }
  ```
- [ ] **Testing**: Tests para validación de tipos
- [ ] **Verificación**: Todos los tipos están correctamente definidos

---

## FASE 2: Sistema de Autenticación

### Tarea 2.1: Servicios de Autenticación
- [ ] **Auth API**: Crear servicio RTK Query para autenticación
  - `POST /auth/register` - Registro de usuario
  - `POST /auth/login` - Inicio de sesión
- [ ] **Token Management**: Gestión de tokens JWT
- [ ] **Auth Slice**: Slice de Redux para estado de autenticación
- [ ] **Interceptores**: Configurar interceptores para tokens
- [ ] **Testing**: Tests para servicios de auth
- [ ] **Verificación**: API de auth funciona correctamente

### Tarea 2.2: Componentes de Autenticación
- [ ] **LoginForm**: Formulario de inicio de sesión
  - Validación con Yup
  - Manejo de errores
  - Loading states
- [ ] **RegisterForm**: Formulario de registro
  - Validación de contraseñas
  - Confirmación de email
  - Términos y condiciones
- [ ] **AuthLayout**: Layout para páginas de auth
- [ ] **ProtectedRoute**: Componente para rutas protegidas
- [ ] **Testing**: Tests para componentes de auth
- [ ] **Verificación**: Flujo de autenticación completo

### Tarea 2.3: Páginas de Autenticación
- [ ] **LoginPage**: Página de inicio de sesión
- [ ] **RegisterPage**: Página de registro
- [ ] **ForgotPasswordPage**: Página de recuperación (si aplica)
- [ ] **Routing**: Configurar rutas de autenticación
- [ ] **Redirects**: Manejar redirecciones post-login
- [ ] **Testing**: Tests de integración para páginas
- [ ] **Verificación**: Navegación entre páginas funciona

---

## FASE 3: Layout y Navegación Principal

### Tarea 3.1: Componentes de Layout
- [ ] **AppBar**: Barra superior con navegación
  - Logo/título de la aplicación
  - Menú de usuario
  - Botón de logout
  - Notificaciones (placeholder)
- [ ] **Sidebar**: Navegación lateral
  - Dashboard
  - Tareas
  - Notas
  - Hábitos
  - Categorías
  - Configuración
- [ ] **MainLayout**: Layout principal de la aplicación
- [ ] **Breadcrumbs**: Navegación de migas de pan
- [ ] **Testing**: Tests para componentes de layout
- [ ] **Verificación**: Layout responsive y funcional

### Tarea 3.2: Sistema de Navegación
- [ ] **Router Setup**: Configurar React Router v6
- [ ] **Route Guards**: Protección de rutas
- [ ] **Navigation Hooks**: Custom hooks para navegación
- [ ] **Active States**: Estados activos en navegación
- [ ] **Mobile Navigation**: Navegación para móviles
- [ ] **Testing**: Tests para sistema de navegación
- [ ] **Verificación**: Navegación fluida entre secciones

---

## FASE 4: Dashboard Principal

### Tarea 4.1: Servicios del Dashboard
- [ ] **Dashboard API**: Crear servicio RTK Query
  - `GET /dashboard-stats` - Estadísticas generales
  - `GET /habits/dashboard/today` - Dashboard de hábitos del día
- [ ] **Stats Slice**: Slice para estadísticas del dashboard
- [ ] **Real-time Updates**: Actualización automática de stats
- [ ] **Testing**: Tests para servicios del dashboard
- [ ] **Verificación**: Datos del dashboard se cargan correctamente

### Tarea 4.2: Componentes del Dashboard
- [ ] **StatsCard**: Tarjeta de estadísticas reutilizable
- [ ] **HabitCalendar**: Calendario mensual de hábitos
  - Vista mensual con días
  - Indicadores de hábitos completados
  - Navegación entre meses
  - Click en día para ver detalles
- [ ] **DashboardGrid**: Grid responsive para widgets
- [ ] **LoadingStates**: Estados de carga para dashboard
- [ ] **Testing**: Tests para componentes del dashboard
- [ ] **Verificación**: Dashboard se renderiza correctamente

### Tarea 4.3: Widgets del Dashboard
- [ ] **TodayHabitsWidget**: Hábitos del día actual
  - Lista de hábitos del día
  - Botones para marcar completado
  - Input numérico para hábitos QUANTITY
  - Progreso visual
- [ ] **HabitsStatsWidget**: Estadísticas de hábitos
  - Racha actual
  - Porcentaje de completado
  - Gráficos simples
- [ ] **TasksSummaryWidget**: Resumen de tareas
  - Tareas pendientes
  - Tareas vencidas
  - Tareas completadas hoy
- [ ] **RecentNotesWidget**: Notas recientes
  - Últimas 5 notas
  - Preview del contenido
  - Link a nota completa
- [ ] **QuickActionsWidget**: Acciones rápidas
  - Crear tarea rápida
  - Crear nota rápida
  - Marcar hábito completado
- [ ] **Testing**: Tests para todos los widgets
- [ ] **Verificación**: Widgets interactivos y funcionales

### Tarea 4.4: Integración de APIs del Dashboard
- [ ] **Habit Tracking**: Integración con APIs de seguimiento
  - `POST /habits/{id}/track/complete` - Marcar hábito completado
  - `POST /habits/{id}/track/quantity` - Actualizar cantidad de hábito
  - `GET /habits/{id}/stats` - Estadísticas de hábito específico
- [ ] **Real-time Updates**: Actualización en tiempo real
- [ ] **Error Handling**: Manejo de errores en tracking
- [ ] **Optimistic Updates**: Actualizaciones optimistas
- [ ] **Testing**: Tests de integración con APIs
- [ ] **Verificación**: Tracking de hábitos funciona en tiempo real

---

## FASE 5: Gestión de Categorías

### Tarea 5.1: Servicios de Categorías
- [ ] **Categories API**: Crear servicio RTK Query completo
  - `GET /categories` - Lista de categorías del usuario
  - `GET /categories/{id}` - Categoría por ID
  - `POST /categories` - Crear nueva categoría
  - `PUT /categories/{id}` - Actualizar categoría completa
  - `PATCH /categories/{id}` - Actualización parcial
  - `DELETE /categories/{id}` - Eliminar categoría
  - `GET /categories/search/by-name` - Buscar por nombre
- [ ] **Categories Slice**: Slice de Redux para categorías
- [ ] **Cache Management**: Gestión de caché para categorías
- [ ] **Testing**: Tests para servicios de categorías
- [ ] **Verificación**: CRUD completo de categorías funciona

### Tarea 5.2: Componentes de Categorías
- [ ] **CategoryForm**: Formulario para crear/editar categorías
  - Campo nombre (requerido)
  - Campo descripción (opcional)
  - Selector de color
  - Validación con Yup
- [ ] **CategoryCard**: Tarjeta para mostrar categoría
  - Nombre y descripción
  - Indicador de color
  - Acciones (editar, eliminar)
  - Contador de elementos asociados
- [ ] **CategoryList**: Lista de categorías
  - Grid responsive
  - Filtros y búsqueda
  - Paginación
- [ ] **CategorySelector**: Selector para asignar categorías
- [ ] **ColorPicker**: Selector de colores para categorías
- [ ] **Testing**: Tests para componentes de categorías
- [ ] **Verificación**: Componentes de categorías funcionan correctamente

### Tarea 5.3: Páginas de Categorías
- [ ] **CategoriesPage**: Página principal de categorías
- [ ] **CategoryDetailPage**: Página de detalle de categoría
  - Información de la categoría
  - Tareas asociadas
  - Notas asociadas
  - Hábitos asociados
- [ ] **CreateCategoryPage**: Página para crear categoría
- [ ] **EditCategoryPage**: Página para editar categoría
- [ ] **Testing**: Tests para páginas de categorías
- [ ] **Verificación**: Navegación entre páginas funciona

### Tarea 5.4: Relaciones de Categorías
- [ ] **Category Relations API**: APIs para relaciones
  - `GET /categories/{id}/tasks` - Tareas de una categoría
  - `GET /categories/{id}/notes` - Notas de una categoría
  - `GET /categories/{id}/habits` - Hábitos de una categoría
  - `POST /categories/{categoryId}/tasks/{taskId}` - Agregar tarea
  - `POST /categories/{categoryId}/notes/{noteId}` - Agregar nota
  - `POST /categories/{categoryId}/habits/{habitId}` - Agregar hábito
  - `DELETE /categories/{categoryId}/tasks/{taskId}` - Quitar tarea
  - `DELETE /categories/{categoryId}/notes/{noteId}` - Quitar nota
  - `DELETE /categories/{categoryId}/habits/{habitId}` - Quitar hábito
- [ ] **Relation Components**: Componentes para gestionar relaciones
- [ ] **Testing**: Tests para relaciones de categorías
- [ ] **Verificación**: Relaciones funcionan correctamente

---

## FASE 6: Gestión de Tareas

### Tarea 6.1: Servicios de Tareas
- [ ] **Tasks API**: Crear servicio RTK Query completo
  - `GET /tasks` - Lista de tareas del usuario
  - `GET /tasks/{id}` - Tarea por ID
  - `POST /tasks` - Crear nueva tarea
  - `PUT /tasks/{id}` - Actualizar tarea completa
  - `PATCH /tasks/{id}` - Actualización parcial
  - `DELETE /tasks/{id}` - Eliminar tarea
- [ ] **Task Relations API**: APIs para relaciones
  - `GET /tasks/{id}/categories` - Categorías de una tarea
  - `GET /tasks/{id}/notes` - Notas relacionadas
  - `GET /tasks/{id}/habits` - Hábitos relacionados
  - `POST /tasks/{taskId}/categories/{categoryId}` - Agregar categoría
  - `DELETE /tasks/{taskId}/categories/{categoryId}` - Quitar categoría
- [ ] **Task History API**: APIs para historial
  - `GET /tasks/{id}/history` - Historial de versiones
  - `GET /tasks/{id}/history/{versionId}` - Versión específica
- [ ] **Tasks Slice**: Slice de Redux para tareas
- [ ] **Testing**: Tests para servicios de tareas
- [ ] **Verificación**: CRUD completo de tareas funciona

### Tarea 6.2: Componentes de Tareas
- [ ] **TaskForm**: Formulario para crear/editar tareas
  - Campo título (requerido)
  - Campo descripción (opcional)
  - Selector de prioridad (LOW, MEDIUM, HIGH)
  - Selector de fecha de vencimiento
  - Checkbox de completado
  - Selector de categorías
  - Validación con Yup
- [ ] **TaskCard**: Tarjeta para mostrar tarea
  - Título y descripción
  - Indicador de prioridad
  - Fecha de vencimiento
  - Estado de completado
  - Categorías asignadas
  - Acciones (editar, eliminar, completar)
- [ ] **TaskList**: Lista de tareas
  - Vista de lista y grid
  - Filtros por estado, prioridad, categoría
  - Ordenamiento por fecha, prioridad
  - Búsqueda por título
  - Paginación
- [ ] **TaskFilters**: Componente de filtros
- [ ] **TaskStats**: Estadísticas de tareas
- [ ] **Testing**: Tests para componentes de tareas
- [ ] **Verificación**: Componentes de tareas funcionan correctamente

### Tarea 6.3: Páginas de Tareas
- [ ] **TasksPage**: Página principal de tareas
  - Lista de tareas con filtros
  - Botón para crear nueva tarea
  - Estadísticas rápidas
- [ ] **TaskDetailPage**: Página de detalle de tarea
  - Información completa de la tarea
  - Notas relacionadas
  - Hábitos relacionados
  - Historial de cambios
- [ ] **CreateTaskPage**: Página para crear tarea
- [ ] **EditTaskPage**: Página para editar tarea
- [ ] **Testing**: Tests para páginas de tareas
- [ ] **Verificación**: Navegación entre páginas funciona

---

## FASE 7: Gestión de Notas

### Tarea 7.1: Servicios de Notas
- [ ] **Notes API**: Crear servicio RTK Query completo
  - `GET /notes` - Lista de notas del usuario
  - `GET /notes/{id}` - Nota por ID
  - `POST /notes` - Crear nueva nota
  - `PUT /notes/{id}` - Actualizar nota completa
  - `PATCH /notes/{id}` - Actualización parcial
  - `DELETE /notes/{id}` - Eliminar nota
- [ ] **Note Relations API**: APIs para relaciones
  - `GET /notes/{id}/categories` - Categorías de una nota
  - `GET /notes/{id}/tasks` - Tareas relacionadas
  - `GET /notes/{id}/habits` - Hábitos relacionados
  - `POST /notes/{noteId}/tasks/{taskId}` - Relacionar tarea
  - `POST /notes/{noteId}/habits/{habitId}` - Relacionar hábito
  - `POST /notes/{noteId}/categories/{categoryId}` - Agregar categoría
  - `DELETE /notes/{noteId}/tasks/{taskId}` - Quitar relación tarea
  - `DELETE /notes/{noteId}/habits/{habitId}` - Quitar relación hábito
  - `DELETE /notes/{noteId}/categories/{categoryId}` - Quitar categoría
- [ ] **Note History API**: APIs para historial
  - `GET /notes/{id}/history` - Historial de versiones
  - `GET /notes/{id}/history/{versionId}` - Versión específica
- [ ] **Notes Slice**: Slice de Redux para notas
- [ ] **Testing**: Tests para servicios de notas
- [ ] **Verificación**: CRUD completo de notas funciona

### Tarea 7.2: Componentes de Notas
- [ ] **NoteForm**: Formulario para crear/editar notas
  - Campo título (requerido)
  - Editor de contenido (rich text)
  - Selector de categorías
  - Validación con Yup
- [ ] **NoteCard**: Tarjeta para mostrar nota
  - Título y preview del contenido
  - Fecha de creación/actualización
  - Categorías asignadas
  - Acciones (editar, eliminar, ver)
- [ ] **NoteList**: Lista de notas
  - Vista de lista y grid
  - Filtros por categoría
  - Búsqueda por título y contenido
  - Ordenamiento por fecha
  - Paginación
- [ ] **NoteEditor**: Editor de texto enriquecido
- [ ] **NotePreview**: Vista previa de nota
- [ ] **Testing**: Tests para componentes de notas
- [ ] **Verificación**: Componentes de notas funcionan correctamente

### Tarea 7.3: Páginas de Notas
- [ ] **NotesPage**: Página principal de notas
  - Lista de notas con filtros
  - Botón para crear nueva nota
  - Búsqueda global
- [ ] **NoteDetailPage**: Página de detalle de nota
  - Contenido completo de la nota
  - Tareas relacionadas
  - Hábitos relacionados
  - Historial de cambios
- [ ] **CreateNotePage**: Página para crear nota
- [ ] **EditNotePage**: Página para editar nota
- [ ] **Testing**: Tests para páginas de notas
- [ ] **Verificación**: Navegación entre páginas funciona

---

## FASE 8: Gestión de Hábitos

### Tarea 8.1: Servicios de Hábitos
- [ ] **Habits API**: Crear servicio RTK Query completo
  - `GET /habits` - Lista de hábitos del usuario
  - `GET /habits/active` - Hábitos activos
  - `GET /habits/inactive` - Hábitos inactivos
  - `GET /habits/{id}` - Hábito por ID
  - `POST /habits` - Crear nuevo hábito
  - `PUT /habits/{id}` - Actualizar hábito completo
  - `PATCH /habits/{id}` - Actualización parcial
  - `DELETE /habits/{id}` - Eliminar hábito
  - `PUT /habits/{id}/toggle-active` - Activar/desactivar
- [ ] **Habit Tracking API**: APIs para seguimiento
  - `POST /habits/{id}/track/complete` - Marcar completado (BOOLEAN)
  - `POST /habits/{id}/track/quantity` - Registrar cantidad (QUANTITY)
  - `GET /habits/{id}/stats` - Estadísticas del hábito
- [ ] **Habit Relations API**: APIs para relaciones
  - `GET /habits/{id}/categories` - Categorías del hábito
  - `GET /habits/{id}/tasks` - Tareas relacionadas
  - `GET /habits/{id}/notes` - Notas relacionadas
  - `POST /habits/{habitId}/categories/{categoryId}` - Agregar categoría
  - `POST /habits/{habitId}/tasks/{taskId}` - Relacionar tarea
  - `POST /habits/{habitId}/notes/{noteId}` - Relacionar nota
  - `DELETE /habits/{habitId}/categories/{categoryId}` - Quitar categoría
  - `DELETE /habits/{habitId}/tasks/{taskId}` - Quitar relación tarea
  - `DELETE /habits/{habitId}/notes/{noteId}` - Quitar relación nota
- [ ] **Habit History API**: APIs para historial
  - `GET /habits/{id}/history` - Historial de versiones
  - `GET /habits/{id}/history/{versionId}` - Versión específica
- [ ] **Habits Slice**: Slice de Redux para hábitos
- [ ] **Testing**: Tests para servicios de hábitos
- [ ] **Verificación**: CRUD completo de hábitos funciona

### Tarea 8.2: Componentes de Hábitos
- [ ] **HabitForm**: Formulario para crear/editar hábitos
  - Campo nombre (requerido)
  - Campo descripción (opcional)
  - Selector de tipo (BOOLEAN, QUANTITY)
  - Campo cantidad objetivo (para QUANTITY)
  - Campo unidad (para QUANTITY)
  - Selector de frecuencia (DAILY, WEEKLY, MONTHLY)
  - Checkbox activo/inactivo
  - Selector de categorías
  - Validación con Yup
- [ ] **HabitCard**: Tarjeta para mostrar hábito
  - Nombre y descripción
  - Tipo y frecuencia
  - Estado activo/inactivo
  - Progreso actual
  - Botones de tracking
  - Acciones (editar, eliminar, activar/desactivar)
- [ ] **HabitList**: Lista de hábitos
  - Vista de lista y grid
  - Filtros por tipo, frecuencia, estado
  - Búsqueda por nombre
  - Ordenamiento
  - Paginación
- [ ] **HabitTracker**: Componente para tracking
  - Botón para hábitos BOOLEAN
  - Input numérico para hábitos QUANTITY
  - Feedback visual
- [ ] **HabitStats**: Estadísticas de hábito
  - Racha actual
  - Porcentaje de completado
  - Gráfico de progreso
- [ ] **HabitCalendar**: Calendario de hábito específico
- [ ] **Testing**: Tests para componentes de hábitos
- [ ] **Verificación**: Componentes de hábitos funcionan correctamente

### Tarea 8.3: Páginas de Hábitos
- [ ] **HabitsPage**: Página principal de hábitos
  - Lista de hábitos con filtros
  - Botón para crear nuevo hábito
  - Vista de hábitos activos/inactivos
- [ ] **HabitDetailPage**: Página de detalle de hábito
  - Información completa del hábito
  - Estadísticas detalladas
  - Calendario de seguimiento
  - Tareas y notas relacionadas
  - Historial de cambios
- [ ] **CreateHabitPage**: Página para crear hábito
- [ ] **EditHabitPage**: Página para editar hábito
- [ ] **Testing**: Tests para páginas de hábitos
- [ ] **Verificación**: Navegación entre páginas funciona

---

## FASE 9: Gestión de Archivos

### Tarea 9.1: Servicios de Archivos
- [ ] **Files API**: Crear servicio RTK Query para archivos
  - `POST /api/files/upload/{entityType}/{entityId}` - Subir archivo a entidad
  - `GET /api/files/download/{fileId}` - Descargar archivo
  - `GET /api/files/entity/{entityType}/{entityId}` - Archivos de entidad
  - `DELETE /api/files/{fileId}` - Eliminar archivo
- [ ] **File Upload**: Manejo de subida de archivos
  - Progress tracking
  - Validación de tipos
  - Límites de tamaño
- [ ] **Files Slice**: Slice de Redux para archivos
- [ ] **Testing**: Tests para servicios de archivos
- [ ] **Verificación**: Subida y descarga de archivos funciona

### Tarea 9.2: Componentes de Archivos
- [ ] **FileUpload**: Componente de subida de archivos
  - Drag & drop
  - Selector de archivos
  - Preview de archivos
  - Progress bar
- [ ] **FileList**: Lista de archivos
  - Información del archivo
  - Acciones (descargar, eliminar)
  - Filtros por tipo
- [ ] **FilePreview**: Preview de archivos
  - Imágenes
  - PDFs
  - Otros tipos
- [ ] **FileManager**: Gestor de archivos por entidad
- [ ] **Testing**: Tests para componentes de archivos
- [ ] **Verificación**: Componentes de archivos funcionan

---

## FASE 10: Funcionalidades Avanzadas

### Tarea 10.1: Búsqueda Global
- [ ] **Search API**: Implementar búsqueda global
- [ ] **SearchBar**: Barra de búsqueda global
- [ ] **SearchResults**: Página de resultados
- [ ] **SearchFilters**: Filtros de búsqueda
- [ ] **Testing**: Tests para búsqueda
- [ ] **Verificación**: Búsqueda funciona en todas las entidades

### Tarea 10.2: Notificaciones
- [ ] **Notification System**: Sistema de notificaciones
- [ ] **Toast Notifications**: Notificaciones toast
- [ ] **Notification Center**: Centro de notificaciones
- [ ] **Testing**: Tests para notificaciones
- [ ] **Verificación**: Notificaciones funcionan correctamente

### Tarea 10.3: Configuración de Usuario
- [ ] **User Settings**: Página de configuración
- [ ] **Profile Management**: Gestión de perfil
- [ ] **Password Change**: Cambio de contraseña
- [ ] **Preferences**: Preferencias de usuario
- [ ] **Testing**: Tests para configuración
- [ ] **Verificación**: Configuración funciona correctamente

---

## FASE 11: Optimización y Testing Final

### Tarea 11.1: Optimización de Performance
- [ ] **Code Splitting**: Implementar code splitting
- [ ] **Lazy Loading**: Carga perezosa de componentes
- [ ] **Memoization**: Optimizar re-renders
- [ ] **Bundle Analysis**: Análisis del bundle
- [ ] **Testing**: Tests de performance
- [ ] **Verificación**: Performance optimizada

### Tarea 11.2: Testing Integral
- [ ] **Unit Tests**: Completar tests unitarios
- [ ] **Integration Tests**: Tests de integración
- [ ] **E2E Tests**: Tests end-to-end
- [ ] **Coverage**: Cobertura de tests > 80%
- [ ] **Testing**: Ejecutar suite completa de tests
- [ ] **Verificación**: Todos los tests pasan

### Tarea 11.3: Documentación Final
- [ ] **Component Documentation**: Documentar componentes
- [ ] **API Documentation**: Documentar servicios
- [ ] **User Guide**: Guía de usuario
- [ ] **Developer Guide**: Guía de desarrollador
- [ ] **Testing**: Verificar documentación
- [ ] **Verificación**: Documentación completa

---

# CONSIDERACIONES TÉCNICAS

## Estado Global (Redux)
- **auth**: Estado de autenticación
- **tasks**: Estado de tareas
- **notes**: Estado de notas
- **habits**: Estado de hábitos
- **categories**: Estado de categorías
- **files**: Estado de archivos
- **dashboard**: Estado del dashboard
- **ui**: Estado de la interfaz (loading, modals, etc.)

## Manejo de Errores
- Interceptores de Axios para errores HTTP
- Error boundaries para errores de React
- Notificaciones de error consistentes
- Logging de errores

## Seguridad
- Validación de inputs en frontend
- Sanitización de datos
- Manejo seguro de tokens
- Protección contra XSS

## Responsive Design
- Mobile-first approach
- Breakpoints de Material-UI
- Componentes adaptativos
- Touch-friendly interfaces

## Performance
- Lazy loading de rutas
- Memoización de componentes
- Optimización de re-renders
- Caching inteligente con RTK Query

## Configuración de API
```typescript
const API_CONFIG = {
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
};
```

## Troubleshooting Común
### Problemas de Desarrollo
- **Error de CORS**: Verificar configuración de proxy en vite.config.ts
- **Tests fallando**: Verificar setup de testing environment
- **TypeScript errors**: Ejecutar `npm run type-check`
- **Bundle size**: Analizar con `npm run analyze`
- **Performance issues**: Verificar con React DevTools Profiler
- **Memory leaks**: Verificar cleanup de useEffect y subscripciones

### Comandos de Diagnóstico
```bash
npm run doctor       # Verificar configuración completa
npm run clean        # Limpiar cache y node_modules
npm run type-check   # Verificar tipos TypeScript
npm run lint:fix     # Corregir errores de linting automáticamente
npm run test:debug   # Ejecutar tests en modo debug
```

### Soluciones Rápidas
- **Reinstalar dependencias**: `npm run clean && npm install`
- **Limpiar cache de Vite**: `rm -rf node_modules/.vite`
- **Resetear TypeScript**: `npx tsc --build --clean`
- **Verificar puertos**: `netstat -ano | findstr :3000`

---

# CRITERIOS DE ACEPTACIÓN DETALLADOS

## Criterios Generales por Tarea
Cada tarea debe cumplir TODOS los siguientes criterios antes de ser marcada como completada ✅:

### 1. ✅ **Funcionalidad Completa**
- [ ] Implementación 100% funcional según especificaciones
- [ ] Todos los endpoints de API integrados correctamente
- [ ] Manejo de estados de loading, success y error
- [ ] Validación de datos en frontend
- [ ] Integración completa con Redux store

### 2. ✅ **Testing Exhaustivo**
- [ ] Tests unitarios con cobertura > 80%
- [ ] Tests de integración para flujos principales
- [ ] Tests de componentes con React Testing Library
- [ ] Tests de servicios API con mocks
- [ ] Tests de Redux slices y selectors
- [ ] Todos los tests pasan sin errores

### 3. ✅ **UI/UX de Calidad**
- [ ] Diseño responsive (mobile, tablet, desktop)
- [ ] Interfaz intuitiva y consistente
- [ ] Feedback visual para acciones del usuario
- [ ] Estados de loading y error bien manejados
- [ ] Navegación fluida y lógica
- [ ] Cumplimiento de Material Design guidelines

### 4. ✅ **Performance Optimizada**
- [ ] Tiempo de carga inicial < 3 segundos
- [ ] Tiempo de respuesta de acciones < 1 segundo
- [ ] Lazy loading implementado donde corresponde
- [ ] Memoización de componentes costosos
- [ ] Optimización de re-renders
- [ ] Bundle size optimizado

### 5. ✅ **Accesibilidad (WCAG 2.1 AA)**
- [ ] Navegación por teclado funcional
- [ ] Etiquetas ARIA apropiadas
- [ ] Contraste de colores adecuado
- [ ] Textos alternativos para imágenes
- [ ] Focus management correcto
- [ ] Screen reader compatible

### 6. ✅ **Calidad de Código**
- [ ] Código limpio y bien estructurado
- [ ] Comentarios y documentación JSDoc
- [ ] Convenciones de naming consistentes
- [ ] No hay console.logs en producción
- [ ] ESLint y Prettier configurados y sin errores
- [ ] TypeScript sin errores de tipos

### 7. ✅ **Seguridad**
- [ ] Validación de inputs contra XSS
- [ ] Sanitización de datos
- [ ] Manejo seguro de tokens
- [ ] No exposición de datos sensibles
- [ ] Protección contra CSRF

### 8. ✅ **Verificación Manual**
- [ ] Pruebas manuales en diferentes navegadores
- [ ] Pruebas en diferentes dispositivos
- [ ] Flujos de usuario completos funcionando
- [ ] Integración con backend verificada
- [ ] Manejo de casos edge testado

## Criterios Específicos por Tipo de Tarea

### Para Servicios API:
- [ ] Todos los endpoints implementados según documentación
- [ ] Manejo correcto de errores HTTP
- [ ] Interceptores configurados correctamente
- [ ] Tipos TypeScript para requests/responses
- [ ] Retry logic para fallos de red
- [ ] Timeout configurado apropiadamente

### Para Redux Slices:
- [ ] Estado inicial bien definido
- [ ] Reducers puros sin side effects
- [ ] Actions creators tipados
- [ ] Selectors memoizados
- [ ] RTK Query configurado correctamente
- [ ] Middleware configurado si es necesario

### Para Componentes React:
- [ ] Props tipadas con TypeScript
- [ ] Hooks utilizados correctamente
- [ ] Manejo de efectos secundarios
- [ ] Cleanup de subscripciones
- [ ] Memoización cuando es necesario
- [ ] Error boundaries implementados

### Para Formularios:
- [ ] Validación en tiempo real
- [ ] Mensajes de error claros
- [ ] Manejo de estados de submit
- [ ] Reset de formularios
- [ ] Accesibilidad de formularios
- [ ] Prevención de doble submit

## Métricas de Calidad

### Cobertura de Tests
- **Mínimo requerido**: 80%
- **Objetivo**: 90%+
- **Crítico**: 100% para funciones de negocio

### Performance
- **First Contentful Paint**: < 1.5s
- **Largest Contentful Paint**: < 2.5s
- **Time to Interactive**: < 3s
- **Cumulative Layout Shift**: < 0.1

### Bundle Size
- **Initial Bundle**: < 500KB gzipped
- **Chunk Size**: < 200KB gzipped
- **Total Assets**: < 2MB

### Accesibilidad
- **Lighthouse Score**: > 90
- **axe-core violations**: 0
- **Keyboard navigation**: 100% funcional

---

## PROCESO DE VERIFICACIÓN

### Antes de marcar como completada ✅:
1. **Auto-verificación**: El LLM debe revisar cada criterio
2. **Testing automatizado**: Ejecutar suite completa de tests
3. **Verificación manual**: Probar funcionalidad manualmente
4. **Code review**: Revisar calidad del código
5. **Performance check**: Verificar métricas de performance
6. **Accessibility audit**: Verificar accesibilidad
7. **Documentation**: Actualizar documentación si es necesario

### Checklist de Finalización:
- [ ] ✅ Todos los criterios generales cumplidos
- [ ] ✅ Criterios específicos del tipo de tarea cumplidos
- [ ] ✅ Métricas de calidad dentro de rangos aceptables
- [ ] ✅ Tests pasando al 100%
- [ ] ✅ Verificación manual exitosa
- [ ] ✅ Documentación actualizada
- [ ] ✅ Commit con mensaje descriptivo realizado

## Criterios de Rollback
### Protocolo de Fallos
Si una tarea falla después de 3 intentos:
1. **Documentar** el problema específico en detalle
2. **Revisar** dependencias y prerequisitos de la tarea
3. **Considerar** implementación alternativa o simplificada
4. **Evaluar** impacto en tareas posteriores
5. **Rollback** a estado anterior estable si es crítico
6. **Reportar** el issue para revisión manual

### Estrategias de Recuperación
- **Checkpoint automático** antes de cada fase
- **Backup de configuración** antes de cambios mayores
- **Versionado de commits** con mensajes descriptivos
- **Logs detallados** de cada operación realizada

## Tracking de Progreso
### Por Fase
- [ ] **Fase 1**: 0/4 tareas (0%) - Configuración Inicial
- [ ] **Fase 2**: 0/3 tareas (0%) - Autenticación
- [ ] **Fase 3**: 0/2 tareas (0%) - Layout y Navegación
- [ ] **Fase 4**: 0/3 tareas (0%) - Dashboard Principal
- [ ] **Fase 5**: 0/3 tareas (0%) - Gestión de Categorías
- [ ] **Fase 6**: 0/3 tareas (0%) - Gestión de Tareas
- [ ] **Fase 7**: 0/3 tareas (0%) - Gestión de Notas
- [ ] **Fase 8**: 0/3 tareas (0%) - Gestión de Hábitos
- [ ] **Fase 9**: 0/3 tareas (0%) - Gestión de Archivos
- [ ] **Fase 10**: 0/3 tareas (0%) - Features Avanzadas
- [ ] **Fase 11**: 0/3 tareas (0%) - Optimización y Testing Final

### Métricas Globales
- **Total de tareas**: 45
- **Completadas**: 0
- **Progreso general**: 0%
- **Tiempo estimado**: 45-60 horas
- **Última actualización**: [Fecha automática]

### Hitos Importantes
- [ ] **Milestone 1**: Configuración base completa (Fase 1)
- [ ] **Milestone 2**: Autenticación funcional (Fase 2)
- [ ] **Milestone 3**: CRUD básico implementado (Fases 5-8)
- [ ] **Milestone 4**: Aplicación completa y optimizada (Fase 11)

---

**NOTA CRÍTICA**: 
- Este TODO debe seguirse **SECUENCIALMENTE**
- **NO** pasar a la siguiente fase hasta completar la actual
- **CADA** tarea debe cumplir **TODOS** los criterios antes de ✅
- El LLM debe **DOCUMENTAR** qué verificó en cada tarea
- En caso de fallos, **SEGUIR** protocolo de rollback
- Mantener **TRAZABILIDAD** de cambios y decisiones tomadas
- **ACTUALIZAR** métricas de progreso después de cada tarea