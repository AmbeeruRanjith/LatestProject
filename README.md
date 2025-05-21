# 🌾 AgriBazzar – Agricultural Marketplace App

AgriBazzar is a feature-rich Android application designed to revolutionize agricultural commerce by connecting farmers directly with customers. It provides a digital platform where farmers can list and sell their products, customers can browse and purchase items, and admins can manage and monitor the marketplace.

---

## 💡 Why AgriBazzar?

In today's digital age, agriculture still relies heavily on middlemen, often leading to the exploitation of farmers and higher prices for consumers. AgriBazzar aims to solve this by:

- Empowering farmers to reach customers directly
- Eliminating intermediaries
- Ensuring fair pricing and transparency
- Promoting digital inclusion in rural communities

This project is not just a technical solution—it's a step toward social impact.

---

## 🚀 Features

- 👨‍🌾 **Farmer Panel**: Upload products, set prices, track orders, update inventory
- 🛒 **Customer Panel**: Browse categories, search products, add to cart/wishlist, place orders
- 🛠️ **Admin Panel**: Add/update/delete products, remove users, view dashboard analytics
- 🔄 Real-time data updates with Firebase
- 📸 Optimized image handling via Cloudinary (ideal for low-speed internet)

---

## 👥 User Roles

### 👑 Admin
- Upload and manage product listings
- Monitor marketplace activity
- Manage users

### 👨‍🌾 Farmer
- List products like rice, turmeric, dal, honey
- Set pricing, manage stock, view customer orders

### 🧑‍🤝‍🧑 Customer
- Browse by category
- Search specific products
- Add to cart or wishlist
- Place and track orders

---

## ⚙️ Tech Stack

- **Platform**: Android Studio
- **Language**: Kotlin
- **Backend**: Firebase Realtime Database
- **Image Hosting**: Cloudinary
- **Networking**: Retrofit + OkHttp
- **UI**: Material Design Components
- **Min SDK**: Android 5.0 (Lollipop)

---

## 🧩 Key Libraries & Tools

- **Firebase Realtime DB** – Syncs user, product, and cart data in real time
- **Cloudinary API** – Uploads and optimizes product images
- **Retrofit (v2.9.0)** – For structured HTTP requests
- **OkHttp (v4.9.3)** – With logging interceptor for network debugging
- **Glide / Picasso** – Efficient image loading
- **RecyclerView + CardView** – Dynamic, scrollable UI components
- **CircleImageView** – For profile images

---

## 📂 Database Structure

```plaintext
/Users/       -> User profiles and roles (admin, farmer, customer)
/Products/    -> Product listings with images and metadata
/Cart/        -> Shopping cart data per customer
/Wishlist/    -> Customer favorite products
```

## Roadmap (Future Enhancements)
- ✅ Payment gateway integration

- ✅ Order delivery tracking

- ⏳ Chat system for buyers and sellers

- ⏳ Machine learning-based product recommendations

## 👨‍💻 Developed By
- Ajay Vasan – 12200749

- Saksham Gupta – 12202014

- Ambeeru Ranjith – 12200717

## 🧪 Testing & Feedback
Initial user testing across all three roles showed strong usability and performance even in low-bandwidth environments. Feedback was overwhelmingly positive, especially from rural stakeholders.

📷 Screenshots
- **Admin Panel**
- ![image](https://github.com/user-attachments/assets/62bda0b7-ac29-4b2e-b45a-18caa35207ff)

- **Customer Experinece**
- ![image](https://github.com/user-attachments/assets/dfc9d89b-2aed-4dae-8d9d-b3415ba47a3b)

