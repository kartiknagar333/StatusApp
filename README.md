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
- **AboutApp - WhatsAppTab - InstagramTab**
<div align="center">
  <img src="https://github.com/user-attachments/assets/b96df537-b13b-466c-9f40-37548e5c4ed1" alt="Home" width="300" height="650"/>
  <img src="https://github.com/user-attachments/assets/446dd616-3666-41b5-9233-e9d187a77897" alt="Home" width="300" height="650" hspace="20" />
  <img src="https://github.com/user-attachments/assets/80712aef-ed04-48f1-8c5b-369fa9935c9f" alt="Home" width="300" height="650"/>
</div>
<br>

- **Choose Wallpaper Site - Search Photo**
<div align="center">
  <img src="https://github.com/user-attachments/assets/c92d2971-3fdb-4234-8790-d119252afd1f" alt="Home" width="300" height="650"/>
  <img src="https://github.com/user-attachments/assets/517428ba-a435-419c-81de-e7de656d6eb9" alt="Home" width="300" height="650" hspace="20" />
  <img src="https://github.com/user-attachments/assets/7df9d312-3675-4a12-9405-36198fcfcd7c" alt="Home" width="300" height="650"/>
</div>
<br>

- **LoadingScreen - SettingsScreen - DownloadScreen**
<div align="center">
   <img src="https://github.com/user-attachments/assets/0458f1c4-733c-4c6f-8a6e-a732374df47b" alt="Home" width="300" height="650"/>
  <img src="https://github.com/user-attachments/assets/c227585e-ca3e-4280-9646-56fdfd43da84" alt="Home" width="300" height="650" hspace="20" />
   <img src="https://github.com/user-attachments/assets/3bef581e-ace0-41af-9f89-47031635860f" alt="Home" width="300" height="650"/>
</div>

