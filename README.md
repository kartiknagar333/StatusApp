# StatusApp
## Features
- #### 📲 Get WhatsApp status & Instagram Feed-Reel photo & video
- #### 💬 Chat with any WhatsApp phone number without saving number
- #### 📥 Save & Download Photo - Video of Instagram Feed post & Reel
- #### 🖼️ Set Direct Wallpaper from Internet High-Quality Wallpaper Site


## Architecture
- The app follows the MVVM (Model–View–ViewModel) architectural pattern, along with several supporting best practices:
- View (Activities/Fragments)Displays data and handles user interactions via data binding and observers.
- ViewModelExposes UI state via LiveData or StateFlow, handles UI events, and coordinates with the domain layer. Survives configuration changes.
- RepositoryActs as a single source of truth for data, mediating between local (Room database, file storage) and remote sources (WhatsApp directories, Instagram APIs, Pexels/Unsplash REST endpoints via Retrofit).
- RoomManages local persistence of saved statuses, chats, and media metadata with DAO interfaces and entities.
- Retrofit + OkHttpHandles network operations for Instagram, Pexels, and Unsplash APIs, with JSON serialization via Moshi/Gson.
- CoroutinesProvides structured concurrency for background tasks, ensuring non-blocking I/O and simplified threading.
- Data Binding / View BindingBinds UI components directly to data sources, reducing boilerplate view lookups.
- Single Activity (optional)If implemented, uses a single-activity host with multiple Fragments for each feature area (WhatsApp, Instagram, Gallery).


## Screenshots
- **AboutApp**
<div align="center">
  <img src="https://github.com/user-attachments/assets/b96df537-b13b-466c-9f40-37548e5c4ed1" alt="Home" width="380" height="720" />
</div>
<br>

